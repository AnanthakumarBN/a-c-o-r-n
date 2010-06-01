/*
 * Copyright 2010 Jakub Łącki
 */

#include"SimulationController.h"
#include<sbml/SBMLTypes.h>
#include<set>
#include<string>
#include<vector>
#include"ModelDatabase.h"
#include"MetabolicSimulation.h"
#include"InputParameters.h"
#include"FileLineReader.h"
#include"StringTokenizer.h"

using std::set;
using std::string;
using std::vector;

SimulationController::SimulationController() : simulation(NULL) {
    model_database = new ModelDatabase;
}

SimulationController::~SimulationController() {
    delete model_database;
    delete simulation;
}

bool SimulationController::disableReactions(const set<string> reactions) {
    for (set<string>::iterator it = reactions.begin();
            it != reactions.end(); ++it) {
        if (!simulation->disableReaction(*it)) {
            error("No such reaction '" + *it + "'");
            return false;
        }
    }
    return true;
}

bool SimulationController::disableGenes(const set<string> genes) {
    for (set<string>::iterator it = genes.begin();
            it != genes.end(); ++it) {
        if (!simulation->disableGene(*it)) {
            error("No such gene '" + *it + "'");
            return false;
        }
    }
    return true;
}

bool SimulationController::runSimulation(const Model* model,
        const vector<Bound>& bounds,
        const OptimisationParameters& optimisation_parameters) {
    simulation = new MetabolicSimulation;

    if (!simulation->loadModel(model, bounds)) {
        error("Error loading model: " + simulation->getErrors()[0]);
        return false;
    }

    if (!disableReactions(optimisation_parameters.disabled_reactions))
        return false;

    if (!disableGenes(optimisation_parameters.disabled_genes))
        return false;

    if (!simulation->setObjective(optimisation_parameters.objective)) {
        error("Bad objective '" + optimisation_parameters.objective + "'");
        return false;
    }

    if (!simulation->runSimulation()) {
        error("GLPK error while running simulation");
        return false;
    }

    return true;
}

bool SimulationController::loadBound(const string& line, Bound* bound) {
    StringTokenizer st;
    st.parse(line);
    bound->reaction_id = st.currentToken();
    st.nextToken();

    if (st.currentDoubleToken(&bound->lower_bound)) {
        error("Invalid lower bound");
        return false;
    }
    st.nextToken();

    if (st.currentDoubleToken(&bound->upper_bound)) {
        error("Invalid upper bound");
        return false;
    }
    return true;
}

bool SimulationController::loadBounds(const string& path,
        vector<Bound>* bounds) {
    FileLineReader fl;
    if (!fl.loadFile(path)) {
        error("Unable to open '" + path  + "'");
        return false;
    }

    while (fl.hasRemainingLines()) {
        Bound bd;
        if (!loadBound(fl.readLine(), &bd))
            return false;

        bounds->push_back(bd);

        fl.nextLine();
    }
    return true;
}

bool SimulationController::runSimulation(const InputParameters& params) {
    Model* model;
    assert(params.getModelPath().empty() !=
            params.getAmkfbaModelPath().empty());

    if (!params.getModelPath().empty()) {
        model = model_database->getModel(params.getModelPath());
    } else {
        model = model_database->getAmkfbaModel(params.getAmkfbaModelPath());
    }

    if (model == NULL) {
        error("Error reading model: " + model_database->getError());
        return false;
    }

    bool copied = false;

    vector<Bound> bounds;

    if (!loadBounds(params.getBoundsFilePath(), &bounds))
        return false;

    bool ret = runSimulation(model, bounds, params.getOptimisationParameters());

    if (copied)
        delete model;

    return ret;
}

void SimulationController::error(const string& err) {
    error_string = err;
}

const string& SimulationController::getError() const {
    return error_string;
}

