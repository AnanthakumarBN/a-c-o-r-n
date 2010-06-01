/*
 * Copyright 2010 Jakub Łącki
 */

#include"TextInterface.h"
#include<string>
#include"InputParameters.h"
#include"SimulationController.h"

using std::string;

const int kMaxCommandLineLength = 50000;

void TextInterface::logError(const string& err) {
    fprintf(stderr, "%s\n", err.c_str());
}

void TextInterface::run(const string& command_line) {
    InputParameters parameters;
    if (!parameters.loadFromString(command_line)) {
        logError(parameters.getErrors()[0]);
        return;
    }

    if (parameters.getInteractiveMode())
        runInteractive(parameters);
    else
        runBatch();
}

void TextInterface::runBatch(const InputParameters& parameters) {
    SimulationController sc;
    if (!sc.runSimulation(parameters)) {
        logError(sc.getError());
        return;
    }

    // wyniki
}

void TextInterface::runInteractive() {
    char command[kMaxCommandLineLength];
    while (fgets(command, kMaxCommandLineLength, stdin)) {
        InputParameters parameters;
        SimulationController sc;
        if (!parameters.loadFromString(command)) {
            logError(parameters.getErrors()[0]);
            continue;
        }

        if (!sc.runSimulation(parameters)) {
            logError(sc.getError());
            continue;
        }

        // wyniki
    }
}
