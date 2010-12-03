/*
 * Copyright 2010 Jakub Łącki
 */

#include"TextInterface.h"
#include<cstdio>
#include<string>
#include<vector>
#include"InputParameters.h"
#include"Bound.h"
#include"ReactionFlux.h"
#include"SimulationController.h"
#include"XMLTaskProcesser.h"

using std::string;
using std::vector;

// Maximum length of a command. Has to be relatively big, since it may
// hold up to three file paths.
const int kMaxCommandLineLength = 50000;

void TextInterface::LogError(const string& err) {
    fprintf(stderr, "%s\n", err.c_str());
}

void TextInterface::Run(const string& command_line) {
    InputParameters parameters;
    if (!parameters.LoadFromString(command_line)) {
        LogError(parameters.GetErrors()[0]);
        return;
    }

    if (parameters.interactive_mode())
        RunInteractive();
    else if (!parameters.xml_task().empty()) {
        XMLTaskProcesser xtp;
        xtp.Run(parameters);
    } else {
        RunBatch(parameters);
    }
}

void TextInterface::ShowResults(const SimulationController& sc,
        bool print_flux) const {
    if (sc.GetOptimal())
        printf("OPTIMAL\n");
    else if (sc.GetFeasible())
        printf("FEASIBLE\n");
    else
        printf("UNDEFINED\n");


    printf("%.6lf\n", sc.GetObjective());

    if (print_flux) {
        vector<ReactionFlux> rf;
        sc.GetFlux(&rf);
        for (unsigned i = 0; i < rf.size(); i++) {
            printf("%s %.6lf\n", rf[i].reaction.c_str(), rf[i].flux);
        }
    }
}

void TextInterface::RunBatch(const InputParameters& parameters) {
    SimulationController sc;
    if (!sc.RunSimulation(parameters)) {
        LogError(sc.GetError());
        return;
    }

    ShowResults(sc, parameters.print_flux());
}

void TextInterface::RunInteractive() {
    char command[kMaxCommandLineLength];
    while (fgets(command, kMaxCommandLineLength, stdin)) {
        InputParameters parameters;
        SimulationController sc;
        if (!parameters.LoadFromString(command)) {
            LogError(parameters.GetErrors()[0]);
            continue;
        }

        if (!sc.RunSimulation(parameters)) {
            LogError(sc.GetError());
            continue;
        }

        ShowResults(sc, parameters.print_flux());
    }
}