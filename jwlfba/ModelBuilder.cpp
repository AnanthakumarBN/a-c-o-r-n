/*
 * Copyright 2010 Jakub Łącki
 */

#include"ModelBuilder.h"
#include<sbml/SBMLTypes.h>
#include<string>
#include"GeneExpression.h"
#include"StringTokenizer.h"
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

const char* ModelBuilder::kLowerBoundParameterId = "LOWER_BOUND";
const char* ModelBuilder::kUpperBoundParameterId = "UPPER_BOUND";

const char* ModelBuilder::kCreatedFromAmkfbaFile = "CREATED_FROM_AMKFBA_FILE";

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
    const XMLNode* notes = const_cast<Model*>(model)->getNotes();

    return notes != NULL && notes->getNumChildren() > 0 &&
        notes->getChild(0).getCharacters() == kCreatedFromAmkfbaFile;
}

string ModelBuilder::DecodeSBMLId(const string& id) {
    string ret;
    unsigned i = 1;  // remove leading '_'

    while (i < id.size()) {
        if (id[i] == '_') {
            // two _ represent a _
            if (i+1 < id.size() && id[i+1] == '_') {
                ret += '_';
                i += 2;
            // otherwise _xy represents one character
            } else {
                if (i+2 < id.size()) {
                    ret += (id[i+1]-'a') + 16*(id[i+2]-'a');
                    i += 3;
                } else {
                    assert(false);  // incorrect id
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
    if (!st->CurrentDoubleToken(coefficient)) {
        Error("Bad coefficient");
        return false;
    }
    st->NextToken();

    if (!st->HasRemainingTokens())
        return false;

    *species_id = CreateValidSBMLId(st->CurrentToken());
    st->NextToken();
    return true;
}

void ModelBuilder::AddSpecies(Model* model, const string& species_id) {
    if (all_species_.find(species_id) != all_species_.end())
        return;

    Species* species = model->createSpecies();
    species->setId(species_id);

    unsigned len = strlen(kExternalMetaboliteSuffix);

    if (species_id.size() > len &&
            species_id.substr(species_id.size() - len) ==
            kExternalMetaboliteSuffix) {
        species->setBoundaryCondition(true);
    }
    all_species_.insert(species_id);
}

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

    if (!st->CurrentDoubleToken(&lower_bound)) {
        Error("Incorrect lower bound");
        return false;
    }
    st->NextToken();

    if (!st->CurrentDoubleToken(&upper_bound)) {
        Error("Incorrect upper bound");
        return false;
    }
    st->NextToken();

    KineticLaw* kinetic_law = reaction->createKineticLaw();
    Parameter* parameter = kinetic_law->createParameter();
    parameter->setId(kLowerBoundParameterId);
    parameter->setValue(lower_bound);

    parameter = kinetic_law->createParameter();
    parameter->setId(kUpperBoundParameterId);
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
        // amkfba format has different operators and uses uppercase AND/OR
        // contrary to lowercase operators in SBML
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
