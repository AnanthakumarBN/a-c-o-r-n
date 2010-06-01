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

class SimulationController {
 private:
    ModelDatabase* model_database_;
    MetabolicSimulation* simulation_;
    string error_string_;
    bool using_amkfba_model_;

    bool DisableReactions(const set<string> reactions);
    bool DisableGenes(const set<string> genes);
    bool LoadBound(const string& line, Bound* bound);
    bool LoadBounds(const string& path, vector<Bound>* bounds);
    bool RunSimulation(const Model* model, const vector<Bound>& bounds,
            const OptimisationParameters& params);
    void Error(const string& err);
 public:
    const string& GetError() const;
    SimulationController();
    ~SimulationController();
    bool RunSimulation(const InputParameters& params);
    bool GetOptimal() const;
    double GetObjective() const;
    void GetFlux(vector<ReactionFlux>* flux) const;
};

#endif  // JWLFBA_SIMULATIONCONTROLLER_H_
