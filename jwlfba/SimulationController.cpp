/*
 * Copyright 2010 Jakub Łącki
 */

#include"SimulationController.h"
#include<sbml/SBMLTypes.h>
#include<set>
#include<string>
#include<vector>
#include"Bound.h"
#include"ModelDatabase.h"
#include"ReactionFlux.h"
#include"ModelBuilder.h"
#include"MetabolicSimulation.h"
#include"InputParameters.h"
#include"FileLineReader.h"
#include"StringTokenizer.h"

using std::set;
using std::string;
using std::vector;

SimulationController::SimulationController() : simulation_(NULL),
    using_amkfba_model_(false) {
    model_database_ = new ModelDatabase;
}

SimulationController::~SimulationController() {
    delete model_database_;
    delete simulation_;
}

bool SimulationController::DisableReactions(const set<string> reactions) {
    for (set<string>::iterator it = reactions.begin();
            it != reactions.end(); ++it) {
        if (!simulation_->DisableReaction(*it)) {
            Error("No such reaction '" + *it + "'");
            return false;
        }
    }
    return true;
}

bool SimulationController::DisableGenes(const set<string> genes) {
    for (set<string>::iterator it = genes.begin();
            it != genes.end(); ++it) {
        if (!simulation_->DisableGene(*it)) {
            Error("No such gene '" + *it + "'");
            return false;
        }
    }
    return true;
}

bool SimulationController::RunSimulation(const Model* model,
        const vector<Bound>& bounds,
        const OptimisationParameters& optimisation_parameters) {
    simulation_ = new MetabolicSimulation;

    if (!simulation_->LoadModel(model, bounds)) {
        Error("Error Loading model: " + simulation_->GetErrors()[0]);
        return false;
    }

    if (!DisableReactions(optimisation_parameters.disabled_reactions))
        return false;

    if (!DisableGenes(optimisation_parameters.disabled_genes))
        return false;

    string objective = optimisation_parameters.objective;

    // Encode the objective for amkfba models.
    if (ModelBuilder::IsAmkfbaModel(model)) {
        using_amkfba_model_ = true;
        objective = ModelBuilder::CreateValidSBMLId(objective);
    }

    if (!simulation_->SetObjective(objective)) {
        Error("Bad objective '" + optimisation_parameters.objective + "'");
        return false;
    }

    simulation_->SetMaximize(!optimisation_parameters.minimize);

    if (!simulation_->RunSimulation()) {
        Error("GLPK error while Running simulation");
        return false;
    }

    return true;
}

bool SimulationController::LoadBound(const string& line, Bound* bound) {
    StringTokenizer st;
    st.Parse(line);
    bound->reaction_id = st.CurrentToken();

    st.NextToken();

    if (!st.CurrentDoubleToken(&bound->lower_bound)) {
        Error("Invalid lower bound");
        return false;
    }
    st.NextToken();

    if (!st.CurrentDoubleToken(&bound->upper_bound)) {
        Error("Invalid upper bound");
        return false;
    }
    return true;
}

bool SimulationController::LoadBounds(const string& path,
        vector<Bound>* bounds) {
    if (path == "")
        return true;

    FileLineReader fl;
    if (!fl.LoadFile(path)) {
        Error("Unable to open '" + path  + "'");
        return false;
    }

    while (fl.HasRemainingLines()) {
        Bound bd;
        if (!LoadBound(fl.ReadLine(), &bd))
            return false;

        bounds->push_back(bd);

        fl.NextLine();
    }
    return true;
}

bool SimulationController::RunSimulation(const InputParameters& params) {
    Model* model;
    assert(params.model_path().empty() !=
            params.amkfba_model_path().empty());

    if (!params.model_path().empty()) {
        model = model_database_->GetModel(params.model_path());
    } else {
        model = model_database_->GetAmkfbaModel(params.amkfba_model_path());
    }

    if (model == NULL) {
        Error("Error reading model: " + model_database_->GetError());
        return false;
    }

    vector<Bound> bounds;

    if (!LoadBounds(params.bounds_file_path(), &bounds))
        return false;

    // Encode the bounds for amkfba models
    if (ModelBuilder::IsAmkfbaModel(model)) {
        for (unsigned i = 0; i < bounds.size(); i++)
            bounds[i].reaction_id = ModelBuilder::CreateValidSBMLId(
                    bounds[i].reaction_id);
    }

    return RunSimulation(model, bounds, params.optimisation_parameters());
}

bool SimulationController::GetOptimal() const {
    return simulation_->GetOptimal();
}

double SimulationController::GetObjective() const {
    return simulation_->GetObjective();
}

void SimulationController::GetFlux(vector<ReactionFlux>* flux) const {
    simulation_->GetFlux(flux);
    if (using_amkfba_model_) {
        for (unsigned i = 0; i < flux->size(); i++) {
            (*flux)[i].reaction =
                ModelBuilder::DecodeSBMLId((*flux)[i].reaction);
        }
    }
}

void SimulationController::Error(const string& err) {
    error_string_ = err;
}

const string& SimulationController::GetError() const {
    return error_string_;
}

