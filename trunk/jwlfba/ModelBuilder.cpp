/*
 * Copyright 2010 Jakub Łącki
 */

#include"ModelBuilder.h"
#include<string>
#include<sbml/SBMLTypes.h>
#include"GeneExpression.h"
#include"MetabolicSimulation.h"

using std::string;

const int kFileLineReaderBuffer = 1000;

LineReader::~LineReader() { }

FileLineReader::FileLineReader() : file(NULL) { }

FileLineReader::~FileLineReader() {
    if (file)
        fclose(file);
}

bool FileLineReader::loadFile(const string& path) {
    file = fopen(path.c_str(), "r");
    return  file != NULL;
}

void FileLineReader::fetchLine() {
    char buf[kFileLineReaderBuffer];
    bool newline_reached = false;

    if (!next_line.empty())
        return;

    while(fgets(buf, kFileLineReaderBuffer, file) && !newline_reached) {
        int len = strlen(buf);
        if (buf[len-1] == '\n') {
            buf[len-1] = '\0';
            newline_reached = true;
        } 
        next_line += buf;
    }
}

string FileLineReader::readLine() {
    fetchLine();
    return next_line;
}

void FileLineReader::nextLine() {
    next_line = "";
    fetchLine();
}

bool FileLineReader::hasRemainingLines() {
    fetchLine();
    return next_line != "";
}

const char* kReactantsProductsSeparator = "=";
const char* kReactionEndToken = "|";
const char* kSpeciesSeparator = "+";
const char* kBoundsStartToken = "#";

bool ModelBuilder::getDouble(StringTokenizer* st, double* val) {
    if (sscanf(st->currentToken().c_str(), "%lf", val) != 1) {
        error = "Expected a floating point number";
        return false;
    }

    st->nextToken();
    return true;
}

string ModelBuilder::createValidSBMLId(const string& sid) {
    string ret;
    ret += '_';
    for (unsigned i = 0; i < sid.size(); i++) {
        if (!isalnum(sid[i]) && sid[i] != '_')
            ret += '_';
        else
            ret += sid[i];
    }
    return ret;
}

bool ModelBuilder::getStoichiometry(StringTokenizer* st, double* coefficient,
        string* species) {

    if (!getDouble(st, coefficient)) {
        error = "Bad coefficient";
        return false;
    }
    
    if (!st->hasRemainingTokens())
        return false;

    *species = createValidSBMLId(st->currentToken());
    st->nextToken();
    return true;
}

void ModelBuilder::addSpecies(Model* model, const string& species_id) {
    if (species.find(species_id) != species.end())
        return;

    Species* species = model->createSpecies();
    species->setId(species_id);
}

// TODO(kuba): przepisz to ladniej

bool ModelBuilder::addReactants(Model* model, StringTokenizer* st) {
    do {
        double coefficient;
        string species;
        if (!getStoichiometry(st, &coefficient, &species)) {
            error = "Reactant expected";
            return false;
        }
        SpeciesReference* species_reference = model->createReactant();
        species_reference->setSpecies(species);
        species_reference->setStoichiometry(coefficient);
        
//        printf("%lf\n", coefficient);
        addSpecies(model, species);
        
        if (st->currentToken() == kSpeciesSeparator)
            st->nextToken();
    } while(st->currentToken() != kReactantsProductsSeparator);
    st->nextToken();
    return true;
}

bool ModelBuilder::addProducts(Model* model, StringTokenizer* st) {
    do {
        double coefficient;
        string species;
        if (!getStoichiometry(st, &coefficient, &species)) {
            error = "Product expected";
            return false;
        }
        SpeciesReference* species_reference = model->createProduct();
        species_reference->setSpecies(species);
        species_reference->setStoichiometry(coefficient);

        addSpecies(model, species);

        if (st->currentToken() == kSpeciesSeparator)
            st->nextToken();
    } while(st->currentToken() != kReactionEndToken);
    st->nextToken();
    return true;
}

bool ModelBuilder::addBounds(Reaction* reaction, StringTokenizer* st) {
    double lower_bound, upper_bound;

    if (!getDouble(st, &lower_bound) || !getDouble(st, &upper_bound)) {
        error = "Incorrect bounds";
        return false;
    }

    KineticLaw* kinetic_law = reaction->createKineticLaw();
    Parameter* parameter = kinetic_law->createParameter();
    parameter->setId(kLowerBoundParameterId);
    parameter->setValue(lower_bound);
    
    parameter = kinetic_law->createParameter();
    parameter->setId(kUpperBoundParameterId);
    parameter->setValue(upper_bound);

    return true;
}

bool ModelBuilder::addReaction(Model* model, StringTokenizer* st) {
    if (!st->hasRemainingTokens()) {
        error = "Reaction id expected";
        return false;
    }

    Reaction* reaction = model->createReaction();
    reaction->setId(createValidSBMLId(st->currentToken()));
    st -> nextToken();

    if (!addReactants(model, st))
        return false;
    if (!addProducts(model, st))
        return false;

    if (st->currentToken() != kBoundsStartToken) {
        error = string("Expected '") + kBoundsStartToken + "'";
        return false;
    }
    st->nextToken();

    if (!addBounds(reaction, st))
        return false;

    return true;
}

Model* ModelBuilder::loadFromAmkfbaFile(LineReader* reader) {
    SBMLDocument* sbmlDoc = new SBMLDocument(2, 4);
    Model* model = sbmlDoc->createModel();
    
    while(reader->hasRemainingLines()) {
        StringTokenizer st;
        string line = reader->readLine();
        st.parse(line);

        if (!addReaction(model, &st))
            return NULL;

        reader->nextLine();
    }
    return model;
}
