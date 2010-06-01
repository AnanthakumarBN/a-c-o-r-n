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
class SimulationController;

class TextInterface {
 private:
    void LogError(const string& err);
    void RunBatch(const InputParameters& parameters);
    void RunInteractive();
    void ShowResults(const SimulationController& sc, bool print_flux) const;
 public:
    void Run(const string& command_line);
};

#endif  // JWLFBA_TEXTINTERFACE_H_
