/*
 * Copyright 2010 Jakub Łącki
 */

#ifndef JWLFBA_MODELDATABASE_H_
#define JWLFBA_MODELDATABASE_H_

#include<string>
#include<set>
#include<cstdio>
using std::string;
using std::set;

class Model;
class Reaction;
class StringTokenizer;

class LineReader {
 public:
    virtual string readLine() = 0;
    virtual void nextLine() = 0;
    virtual bool hasRemainingLines() = 0;
    virtual ~LineReader();
};

class FileLineReader : public LineReader {
    FILE* file;
    string next_line;
    void fetchLine();
 public:
    FileLineReader();
    virtual ~FileLineReader();
    bool loadFile(const string& path);

    virtual string readLine();
    virtual void nextLine();
    virtual bool hasRemainingLines();
};

class ModelBuilder {
 private:
    string error;
    set<string> species;

    const static char* kReactantsProductsSeparator;
    const static char* kReactionEndToken;
    const static char* kSpeciesSeparator;
    const static char* kBoundsStartToken;
    const static char* kExternalMetaboliteSuffix;
    const static char* kGenesBeginToken;
    const static char* kGenesEndToken;
    const static char* kAmkfbaAndToken;
    const static char* kAmkfbaOrToken;

    void addSpecies(Model* model, const string& species);
    bool getDouble(StringTokenizer* st, double* val);
    bool getStoichiometry(StringTokenizer* st, double* coefficient,
            string* species);
    bool addReactants(Model* model, StringTokenizer* st);
    bool addProducts(Model* model, StringTokenizer* st);
    bool addBounds(Reaction* reaction, StringTokenizer* st);
    bool addReaction(Model* model, StringTokenizer* st);
    bool addGenes(Reaction* reaction, StringTokenizer* st);
    bool isValidSBMLDIdChar(char c);
    string encodeChar(char c);
    string createValidSBMLId(const string& sid);
 public:
    static string decodeSBMLId(const string& id);
    const static char* kCreatedFromAmkfbaFile;
    string getError() { return error; }

    Model* loadFromAmkfbaFile(LineReader* reader);
    string getErrorDescription();
};

#endif  // JWLFBA_MODELDATABASE_H_
