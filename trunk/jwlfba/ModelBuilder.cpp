/*
 * Copyright 2010 Jakub Łącki
 */

#include"ModelBuilder.h"
#include<sbml/SBMLTypes.h>
#include<string>
#include"GeneExpression.h"
#include"StringTokenizer.h"
#include"MetabolicSimulation.h"
#include"LineReader.h"

using std::string;

const char* ModelBuilder::kReactantsProductsSeparator = "=";
const char* ModelBuilder::kReactionEndToken = "|";
const char* ModelBuilder::kSpeciesSeparator = "+";
const char* ModelBuilder::kBoundsStartToken = "#";
const char* ModelBuilder::kExternalMetaboliteSuffix = "_xt";
const char* ModelBuilder::kGenesBeginToken = "<GENES>";
const char* ModelBuilder::kGenesEndToken = "</GENES>";
const char* ModelBuilder::kAmkfbaAndToken = "AND";
const char* ModelBuilder::kAmkfbaOrToken = "OR";

const char* ModelBuilder::kCreatedFromAmkfbaFile = "CREATED_FROM_AMKFBA_FILE";

bool ModelBuilder::getDouble(StringTokenizer* st, double* val) {
    if (sscanf(st->currentToken().c_str(), "%lf", val) != 1) {
        error("Expected a floating point number");
        return false;
    }

    st->nextToken();
    return true;
}

bool ModelBuilder::isValidSBMLDIdChar(char c) const {
    return isalnum(c) || c == '_';
}

string ModelBuilder::encodeChar(char c) const {
    string ret = "_";
    ret += 'a' + c % 16;
    ret += 'a' + c / 16;
    return ret;
}

string ModelBuilder::createValidSBMLId(const string& sid) const {
    string ret;
    ret += '_';
    for (unsigned i = 0; i < sid.size(); i++) {
        if (isValidSBMLDIdChar(sid[i]) && sid[i] != '_')
            ret += sid[i];
        else if (sid[i] == '_')
            ret += "__";
        else
            ret += encodeChar(sid[i]);
    }
    return ret;
}

string ModelBuilder::decodeSBMLId(const string& id) {
    string ret;
    unsigned i = 1;  // remove leading '_'

    while (i < id.size()) {
        if (id[i] == '_') {
            if (i+1 < id.size() && id[i+1] == '_') {
                ret += '_';
                i += 2;
            } else {
                if (i+2 < id.size()) {  // TODO(me): a jesli nie?
                    ret += (id[i+1]-'a') + 16*(id[i+2]-'a');
                    i += 3;
                }
            }
        } else {
            ret += id[i];
            i++;
        }
    }
    return ret;
}

bool ModelBuilder::getStoichiometry(StringTokenizer* st, double* coefficient,
        string* species) {
    if (!getDouble(st, coefficient)) {
        error("Bad coefficient");
        return false;
    }

    if (!st->hasRemainingTokens())
        return false;

    *species = createValidSBMLId(st->currentToken());
    st->nextToken();
    return true;
}

// TODO(kuba) - ladniej!

void ModelBuilder::addSpecies(Model* model, const string& species_id) {
    if (species.find(species_id) != species.end())
        return;

    Species* species = model->createSpecies();
    species->setId(species_id);
    if (species_id.size() > 3 &&
            species_id.substr(species_id.size() - 3) == "_xt") {
        species->setBoundaryCondition(true);
    }
}

// TODO(kuba): przepisz to ladniej

bool ModelBuilder::addReactants(Model* model, StringTokenizer* st) {
    do {
        double coefficient;
        string species;
        if (!getStoichiometry(st, &coefficient, &species)) {
            error("Reactant expected");
            return false;
        }
        SpeciesReference* species_reference = model->createReactant();
        species_reference->setSpecies(species);
        species_reference->setStoichiometry(coefficient);

        addSpecies(model, species);

        if (st->currentToken() == kSpeciesSeparator)
            st->nextToken();
    } while (st->currentToken() != kReactantsProductsSeparator);
    st->nextToken();
    return true;
}

bool ModelBuilder::addProducts(Model* model, StringTokenizer* st) {
    do {
        double coefficient;
        string species;
        if (!getStoichiometry(st, &coefficient, &species)) {
            error("Product expected");
            return false;
        }
        SpeciesReference* species_reference = model->createProduct();
        species_reference->setSpecies(species);
        species_reference->setStoichiometry(coefficient);

        addSpecies(model, species);

        if (st->currentToken() == kSpeciesSeparator)
            st->nextToken();
    } while (st->currentToken() != kReactionEndToken);
    st->nextToken();
    return true;
}

bool ModelBuilder::addBounds(Reaction* reaction, StringTokenizer* st) {
    double lower_bound, upper_bound;

    if (!getDouble(st, &lower_bound) || !getDouble(st, &upper_bound)) {
        error("Incorrect bounds");
        return false;
    }

    KineticLaw* kinetic_law = reaction->createKineticLaw();
    Parameter* parameter = kinetic_law->createParameter();
    parameter->setId(MetabolicSimulation::kLowerBoundParameterId);
    parameter->setValue(lower_bound);

    parameter = kinetic_law->createParameter();
    parameter->setId(MetabolicSimulation::kUpperBoundParameterId);
    parameter->setValue(upper_bound);

    return true;
}

bool ModelBuilder::addGenes(Reaction* reaction, StringTokenizer* st) {
    string genes = GeneExpression::kGeneExpressionPrefix;

    if (st->currentToken() != kGenesBeginToken) {
        error(string("Expected '") + kGenesBeginToken + "'");
        return false;
    }
    st->nextToken();

    while (st->hasRemainingTokens() && st->currentToken() != kGenesEndToken) {
        genes += ' ';
        if (st->currentToken() == kAmkfbaOrToken)
            genes += GeneExpression::kOrToken;
        else if (st->currentToken() == kAmkfbaAndToken)
            genes += GeneExpression::kAndToken;
        else
            genes += st->currentToken();
        st->nextToken();
    }
    genes += ' ';

    if (st->currentToken() != kGenesEndToken) {
        error(string("Expected '") + kGenesEndToken + "'");
        return false;
    }

    XMLNode* ptr = XMLNode::convertStringToXMLNode(string("<p>") +
            genes + "</p>");
    reaction->setNotes(ptr);

    return true;
}

bool ModelBuilder::addReaction(Model* model, StringTokenizer* st) {
    if (!st->hasRemainingTokens()) {
        error("Reaction id expected");
        return false;
    }

    Reaction* reaction = model->createReaction();
    reaction->setId(createValidSBMLId(st->currentToken()));

    st->nextToken();

    if (!addReactants(model, st))
        return false;
    if (!addProducts(model, st))
        return false;

    if (st->currentToken() != kBoundsStartToken) {
        error(string("Expected '") + kBoundsStartToken + "'");
        return false;
    }
    st->nextToken();

    if (!addBounds(reaction, st))
        return false;

    if (!addGenes(reaction, st))
        return false;

    return true;
}

void ModelBuilder::error(const string& err) {
    error_description = err;
}

string ModelBuilder::getError() const {
    return error_description;
}

Model* ModelBuilder::loadFromAmkfbaFile(LineReader* reader) {
    SBMLDocument* sbmlDoc = new SBMLDocument(2, 1);
    Model* model = sbmlDoc->createModel();

    model->setNotes(kCreatedFromAmkfbaFile);

    while (reader->hasRemainingLines()) {
        StringTokenizer st;
        string line = reader->readLine();
        st.parse(line);

        if (!addReaction(model, &st))
            return NULL;

        reader->nextLine();
    }
    return model;
}
