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

bool ModelBuilder::GetDouble(StringTokenizer* st, double* val) {
    if (sscanf(st->CurrentToken().c_str(), "%lf", val) != 1) {
        Error("Expected a floating point number");
        return false;
    }

    st->NextToken();
    return true;
}

bool ModelBuilder::IsValidSBMLDIdChar(char c) {
    return isalnum(c) || c == '_';
}

string ModelBuilder::EncodeChar(char c) {
    string ret = "_";
    ret += 'a' + c % 16;
    ret += 'a' + c / 16;
    return ret;
}

string ModelBuilder::CreateValidSBMLId(const string& sid) {
    string ret;
    ret += '_';
    for (unsigned i = 0; i < sid.size(); i++) {
        if (IsValidSBMLDIdChar(sid[i]) && sid[i] != '_')
            ret += sid[i];
        else if (sid[i] == '_')
            ret += "__";
        else
            ret += EncodeChar(sid[i]);
    }
    return ret;
}

bool ModelBuilder::IsAmkfbaModel(const Model* model) {
    const XMLNode& notes = *(const_cast<Model*>(model)->getNotes());

    return notes.getNumChildren() > 0 &&
        notes.getChild(0).getCharacters() == kCreatedFromAmkfbaFile;
}

string ModelBuilder::DecodeSBMLId(const string& id) {
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

bool ModelBuilder::GetStoichiometry(StringTokenizer* st, double* coefficient,
        string* species_id) {
    if (!GetDouble(st, coefficient)) {
        Error("Bad coefficient");
        return false;
    }

    if (!st->HasRemainingTokens())
        return false;

    *species_id = CreateValidSBMLId(st->CurrentToken());
    st->NextToken();
    return true;
}

// TODO(kuba) - ladniej!

void ModelBuilder::AddSpecies(Model* model, const string& species_id) {
    if (all_species_.find(species_id) != all_species_.end())
        return;

    Species* species = model->createSpecies();
    species->setId(species_id);
    if (species_id.size() > 3 &&
            species_id.substr(species_id.size() - 3) == "_xt") {
        species->setBoundaryCondition(true);
    }
    all_species_.insert(species_id);
}

// TODO(kuba): przepisz to ladniej

bool ModelBuilder::AddReactants(Model* model, StringTokenizer* st) {
    do {
        double coefficient;
        string species;
        if (!GetStoichiometry(st, &coefficient, &species)) {
            Error("Reactant expected");
            return false;
        }
        SpeciesReference* species_reference = model->createReactant();
        species_reference->setSpecies(species);
        species_reference->setStoichiometry(coefficient);

        AddSpecies(model, species);

        if (st->CurrentToken() == kSpeciesSeparator)
            st->NextToken();
    } while (st->CurrentToken() != kReactantsProductsSeparator);
    st->NextToken();
    return true;
}

bool ModelBuilder::AddProducts(Model* model, StringTokenizer* st) {
    do {
        double coefficient;
        string species;
        if (!GetStoichiometry(st, &coefficient, &species)) {
            Error("Product expected");
            return false;
        }
        SpeciesReference* species_reference = model->createProduct();
        species_reference->setSpecies(species);
        species_reference->setStoichiometry(coefficient);

        AddSpecies(model, species);

        if (st->CurrentToken() == kSpeciesSeparator)
            st->NextToken();
    } while (st->CurrentToken() != kReactionEndToken);
    st->NextToken();
    return true;
}

bool ModelBuilder::AddBounds(Reaction* reaction, StringTokenizer* st) {
    double lower_bound, upper_bound;

    if (!GetDouble(st, &lower_bound) || !GetDouble(st, &upper_bound)) {
        Error("Incorrect bounds");
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

bool ModelBuilder::AddGenes(Reaction* reaction, StringTokenizer* st) {
    string genes = GeneExpression::kGeneExpressionPrefix;

    if (st->CurrentToken() != kGenesBeginToken) {
        Error(string("Expected '") + kGenesBeginToken + "'");
        return false;
    }
    st->NextToken();

    while (st->HasRemainingTokens() && st->CurrentToken() != kGenesEndToken) {
        genes += ' ';
        if (st->CurrentToken() == kAmkfbaOrToken)
            genes += GeneExpression::kOrToken;
        else if (st->CurrentToken() == kAmkfbaAndToken)
            genes += GeneExpression::kAndToken;
        else
            genes += st->CurrentToken();
        st->NextToken();
    }
    genes += ' ';

    if (st->CurrentToken() != kGenesEndToken) {
        Error(string("Expected '") + kGenesEndToken + "'");
        return false;
    }

    XMLNode* ptr = XMLNode::convertStringToXMLNode(string("<p>") +
            genes + "</p>");
    reaction->setNotes(ptr);

    return true;
}

bool ModelBuilder::AddReaction(Model* model, StringTokenizer* st) {
    if (!st->HasRemainingTokens()) {
        Error("Reaction id expected");
        return false;
    }

    Reaction* reaction = model->createReaction();
    reaction->setId(CreateValidSBMLId(st->CurrentToken()));

    st->NextToken();

    if (!AddReactants(model, st))
        return false;
    if (!AddProducts(model, st))
        return false;

    if (st->CurrentToken() != kBoundsStartToken) {
        Error(string("Expected '") + kBoundsStartToken + "'");
        return false;
    }
    st->NextToken();

    if (!AddBounds(reaction, st))
        return false;

    if (!AddGenes(reaction, st))
        return false;

    return true;
}

void ModelBuilder::Error(const string& err) {
    error_description_ = err;
}

string ModelBuilder::GetError() const {
    return error_description_;
}

Model* ModelBuilder::LoadFromAmkfbaFile(LineReader* reader) {
    SBMLDocument* sbmlDoc = new SBMLDocument(2, 1);
    Model* model = sbmlDoc->createModel();

    model->setNotes(kCreatedFromAmkfbaFile);

    while (reader->HasRemainingLines()) {
        StringTokenizer st;
        string line = reader->ReadLine();
        st.Parse(line);

        if (!AddReaction(model, &st))
            return NULL;

        reader->NextLine();
    }
    return model;
}
