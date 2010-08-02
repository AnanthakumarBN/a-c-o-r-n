/*
 * Copyright 2010 Jakub Łącki
 */


#ifndef JWLFBA_SIMULATIONCONTROLLER_H_
#define JWLFBA_SIMULATIONCONTROLLER_H_

#include<string>
#include<set>
#include<vector>

using std::string;
using std::set;
using std::vector;

class InputParameters;
class ModelDatabase;
class Model;
class MetabolicSimulation;
class OptimisationParameters;
struct Bound;
struct ReactionFlux;

// Responsible for running a Simulation for a given set of parameters
// and retrieving the results.
// Note that it only supports running one simulation at a time.

class SimulationController {
 private:
    // ModelDatabase used to get necessary models.
    ModelDatabase* model_database_;

    // Simulation we are currently working with.
    MetabolicSimulation* simulation_;

    // Description of the error encoutered.
    string error_string_;

    // True if the model we're using has been build with a ModelBuilder.
    bool using_amkfba_model_;

    // Functions for loading the parameters of the simulation. Return true
    // if the parameters are valid (i.e. exist in the model).
    bool DisableReactions(const set<string> reactions);
    bool DisableGenes(const set<string> genes);

    // Retrieves a Bound from a given line, which should be in the format:
    // reaction_id lower_bound upper_bound <whatever>
    // Returns true iff the format is correct.
    bool LoadBound(const string& line, Bound* bound);

    // Gets the vector of bounfs from the file. Returns true iff the file
    // has been successfully parsed.
    bool LoadBounds(const string& path, vector<Bound>* bounds);

    // Runs the simulation with a given set of parameters. Returns true iff
    // the operation has been successful.
    bool RunSimulation(const Model* model, const vector<Bound>& bounds,
            const OptimisationParameters& params);

    // Stores an error in the error_string_ field.
    void Error(const string& err);
 public:
    // Retrieves the description of the error encountered.
    const string& GetError() const;
    SimulationController();
    ~SimulationController();

    // Runs the simulation for a given set of parameters. Returns true iff
    // the simulation has been successful.
    bool RunSimulation(const InputParameters& params);

    // Return the results of the simulation.
    bool GetOptimal() const;
    bool GetFeasible() const;
    double GetObjective() const;
    void GetFlux(vector<ReactionFlux>* flux) const;
};

#endif  // JWLFBA_SIMULATIONCONTROLLER_H_
