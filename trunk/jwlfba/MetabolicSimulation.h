/*
 * Copyright 2010 Jakub Łącki
 */

#ifndef JWLFBA_METABOLICSIMULATION_H_
#define JWLFBA_METABOLICSIMULATION_H_

#include<glpk/glpk.h>
#include<map>
#include<set>
#include<string>
#include<vector>

using std::string;
using std::set;
using std::map;
using std::pair;
using std::vector;

extern const char* kUpperBoundParameterId;
extern const char* kLowerBoundParameterId;
extern const double kUpperBoundUnlimited;
extern const double kLowerBoundUnlimited;
extern const double kBoundEpsilon;

class Model;
class Reaction;
class ListOfSpeciesReferences;
class GeneExpression;
class XMLNode;

class MetabolicSimulation {
 private:
    glp_prob* linear_problem;
    Model* model;
    map<string, int> species_row;

    set<string> all_genes;
    set<string> disabled_genes;
    vector<GeneExpression> genes;

    int internal_metabolites_count;
    vector<string> model_errors;

    void getStoichiometryData(int column,
            const ListOfSpeciesReferences& species, double scale,
            vector<pair<int, double> >* stoichiometry_data);
    void setColumnValues(int column,
            const vector<pair<int, double> >& stoichiometry_data);
    void setColumnBounds(int column, const Reaction& reaction);
    void findInternalMetabolites();
    void buildColumns();
    void boundRows();

    GeneExpression getGeneExpressionFromNotes(const XMLNode& notes);
    void getGenes();

    bool validateReactionBounds(const Reaction& reaction);
    bool validateSpeciesReferences(const ListOfSpeciesReferences& species);
    bool validateModel();
    void setObjectiveReaction(const string& objective);
    void setObjectiveSpecies(const string& objective);
    void setObjectiveForColumn(const string& sid, int column,
            const ListOfSpeciesReferences& species, double scale);
    void disableReaction(unsigned rnum);
 public:
    MetabolicSimulation();
    ~MetabolicSimulation();

    bool loadModel(const Model* model);

    bool disableReaction(const string& reaction_id);
    bool disableGene(const string& gene_id);
    bool setObjective(const string& objective);

    void runSimulation();
    string getStatus();
    double getObjectiveFunctionValue();
};

#endif  // JWLFBA_METABOLICSIMULATION_H_
