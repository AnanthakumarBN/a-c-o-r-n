/*
 * Copyright 2010 Jakub Łącki
 */

#ifndef JWLFBA_MODELBUILDER_H_
#define JWLFBA_MODELBUILDER_H_

#include<string>
#include<set>
#include<cstdio>
using std::string;
using std::set;

class Model;
class Reaction;
class StringTokenizer;
class LineReader;

class ModelBuilder {
 private:
    string error_description;
    set<string> species;

    static const char* kReactantsProductsSeparator;
    static const char* kReactionEndToken;
    static const char* kSpeciesSeparator;
    static const char* kBoundsStartToken;
    static const char* kExternalMetaboliteSuffix;
    static const char* kGenesBeginToken;
    static const char* kGenesEndToken;
    static const char* kAmkfbaAndToken;
    static const char* kAmkfbaOrToken;

    void addSpecies(Model* model, const string& species);
    bool getDouble(StringTokenizer* st, double* val);
    bool getStoichiometry(StringTokenizer* st, double* coefficient,
            string* species);
    bool addReactants(Model* model, StringTokenizer* st);
    bool addProducts(Model* model, StringTokenizer* st);
    bool addBounds(Reaction* reaction, StringTokenizer* st);
    bool addReaction(Model* model, StringTokenizer* st);
    bool addGenes(Reaction* reaction, StringTokenizer* st);
    bool isValidSBMLDIdChar(char c) const;
    string encodeChar(char c) const;
    string createValidSBMLId(const string& sid) const;
    void error(const string& err);
 public:
    static string decodeSBMLId(const string& id);
    static const char* kCreatedFromAmkfbaFile;
    string getError() const;

    Model* loadFromAmkfbaFile(LineReader* reader);
};

#endif  // JWLFBA_MODELBUILDER_H_
