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

// Interface for running simulations.

class TextInterface {
 private:
    // Displays an error by printing it on stderr.
    void LogError(const string& err);

    // Runs in batch mode with the given parameters. One simulation is
    // performed and the results displayed.
    void RunBatch(const InputParameters& parameters);

    // Runs in interactive mode. Commands are read from standard input
    // in a loop and the simulations are ran one by one.
    void RunInteractive();

    // Prints the results of a simulation to the standard output.
    // Status and objective are always printed and the flux through
    // each reaction only when print_flux is set.
    void ShowResults(const SimulationController& sc, bool print_flux) const;
 public:
    // Runs the interface for a given command line.
    void Run(const string& command_line);
};

#endif  // JWLFBA_TEXTINTERFACE_H_
