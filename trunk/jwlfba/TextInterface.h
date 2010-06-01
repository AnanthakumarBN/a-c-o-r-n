/*
 * Copyright 2010 Jakub Łącki
 */


#ifndef JWLFBA_TEXTINTERFACE_H_
#define JWLFBA_TEXTINTERFACE_H_

#include<map>
#include<set>
#include<string>
#include<vector>

using std::map;
using std::set;
using std::string;
using std::vector;
class InputParameters;

class TextInterface {
 private:
    void logError(const string& err);
    void runBatch();
    void runInteractive(const InputParameters& parameters);
 public:
    void run(const string& command_line);
};

#endif  // JWLFBA_TEXTINTERFACE_H_
