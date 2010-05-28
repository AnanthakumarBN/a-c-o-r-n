/*
 * Copyright 2010 Jakub Łącki
 */

#ifndef JWLFBA_METABOLICSIMULATION_H_
#define JWLFBA_METABOLICSIMULATION_H_

#include<glpk/glpk.h>
#include<map>
#include<string>
#include<vector>

using std::string;
using std::map;
using std::pair;
using std::vector;

const char kUpperBoundParameterId[] = "UPPER_BOUND";
const char kLowerBoundParameterId[] = "LOWER_BOUND";
const double kUpperBoundUnlimited = 999999.0;
const double kLowerBoundUnlimited = -999999.0;

class Model;
class Reaction;
class ListOfSpeciesReferences;

class MetabolicSimulation {
 private:
    glp_prob* linear_problem;
    Model* model;
    map<string, int> species_row;
    int internal_metabolites_count;

    void addStoichiometry(int column, const ListOfSpeciesReferences& species,
            double scale, vector<pair<int, double> >* stoichiometry_data);
    void setColumnValues(int column,
            const vector<pair<int, double> >& stoichiometry_data);
    void setColumnBounds(int column, const Reaction& reaction);
    void findInternalMetabolites();
    void buildColumns();
    void boundRows();

    void validateModel();
    void setObjectiveReaction(const string& objective);
    void setObjectiveSpecies(const string& objective);
    void setObjectiveForColumn(const string& sid, int column,
            const ListOfSpeciesReferences& species, double scale);
 public:
    MetabolicSimulation();
    ~MetabolicSimulation();

    void loadModel(const Model* model);

    void disableReaction(const string& reaction_id);
    void disableGene(const string& gene_id);
    bool setObjective(const string& objective);

    void runSimulation();
    string getStatus();
    double getObjectiveFunctionValue();
};

#endif  // JWLFBA_METABOLICSIMULATION_H_
