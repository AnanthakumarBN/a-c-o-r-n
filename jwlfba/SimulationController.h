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

class SimulationController {
 private:
    ModelDatabase* model_database;
    MetabolicSimulation* simulation;
    string error_string;

    bool disableReactions(const set<string> reactions);
    bool disableGenes(const set<string> genes);
    bool loadBound(const string& line, Bound* bound);
    bool loadBounds(const string& path, vector<Bound>* bounds);
    bool runSimulation(const Model* model, const vector<Bound>& bounds,
            const OptimisationParameters& params);
    void error(const string& err);
 public:
    const string& getError() const;
    SimulationController();
    ~SimulationController();
    bool runSimulation(const InputParameters& params);
};

#endif  // JWLFBA_SIMULATIONCONTROLLER_H_
