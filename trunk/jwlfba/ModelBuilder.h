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
    string error_description_;
    set<string> all_species_;

    static const char* kReactantsProductsSeparator;
    static const char* kReactionEndToken;
    static const char* kSpeciesSeparator;
    static const char* kBoundsStartToken;
    static const char* kExternalMetaboliteSuffix;
    static const char* kGenesBeginToken;
    static const char* kGenesEndToken;
    static const char* kAmkfbaAndToken;
    static const char* kAmkfbaOrToken;

    void AddSpecies(Model* model, const string& species);
    bool GetDouble(StringTokenizer* st, double* val);
    bool GetStoichiometry(StringTokenizer* st, double* coefficient,
            string* species);
    bool AddReactants(Model* model, StringTokenizer* st);
    bool AddProducts(Model* model, StringTokenizer* st);
    bool AddBounds(Reaction* reaction, StringTokenizer* st);
    bool AddReaction(Model* model, StringTokenizer* st);
    bool AddGenes(Reaction* reaction, StringTokenizer* st);
    bool IsValidSBMLDIdChar(char c) const;
    string EncodeChar(char c) const;
    string CreateValidSBMLId(const string& sid) const;
    void Error(const string& err);
 public:
    static string DecodeSBMLId(const string& id);
    static const char* kCreatedFromAmkfbaFile;
    string GetError() const;

    Model* LoadFromAmkfbaFile(LineReader* reader);
};

#endif  // JWLFBA_MODELBUILDER_H_
