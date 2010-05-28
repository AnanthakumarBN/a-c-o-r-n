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

    string createValidSBMLId(const string& sid);
    void addSpecies(Model* model, const string& species);
    bool getDouble(StringTokenizer* st, double* val);
    bool getStoichiometry(StringTokenizer* st, double* coefficient,
            string* species);
    bool addReactants(Model* model, StringTokenizer* st);
    bool addProducts(Model* model, StringTokenizer* st);
    bool addBounds(Reaction* reaction, StringTokenizer* st);
    bool addReaction(Model* model, StringTokenizer* st);
 public:
    string getError() { return error; }

    Model* loadFromAmkfbaFile(LineReader* reader);
    string getErrorDescription();
};

#endif  // JWLFBA_MODELDATABASE_H_
