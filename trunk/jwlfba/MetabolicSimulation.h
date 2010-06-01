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


class Model;
class Reaction;
class ListOfSpeciesReferences;
class GeneExpression;
class KineticLaw;
class XMLNode;


struct Bound {
    string reaction_id;
    double lower_bound, upper_bound;
};

// Given a SBML model and optimisation parameters (e.g. objective funciton),
// performs the simulation.

class MetabolicSimulation {
 private:
    // Bounds that are closer than that are considered equal.
    static const double kBoundEpsilon;

    // GLPK library object for running linear programming
    glp_prob* linear_problem;

    // libSBML object that stores the model
    Model* model;

    // Maps internal metabolites (and only them) to the numbers of rows
    // (0-based) in the constraint matrix.
    map<string, int> species_row;

    map<string, int> reactions_map;

    // all genes in the model
    set<string> all_genes;

    // set of genes that we consider inactive
    set<string> disabled_genes;

    // GeneExpression for each reaction in the model
    vector<GeneExpression> genes;

    int internal_metabolites_count;

    // list of errors found when validating the model
    vector<string> model_errors;

    // Returns a list of <row_number, stoichiometry_coefficient*scale> for each
    // species from the list, which is an internal species. Scaling is used,
    // because we want the products to have a negative coefficient.
    void getStoichiometryData(const ListOfSpeciesReferences& species,
            double scale, vector<pair<int, double> >* stoichiometry_data);

    // Sets the constraints in the LP matrix according to the second parameter.
    void setColumnValues(int column,
            const vector<pair<int, double> >& stoichiometry_data);

    // Sets the bounds for a given column using lower and upper bounds for
    // the flux through the reaction
    void setColumnBounds(int column, const Reaction& reaction);

    // Builds the species_row map and stores the count in
    // internal_metabolites_count
    void findInternalMetabolites();

    void buildReactionsMap();

    // Sets the entries in the LP matrix and sets colum bounds
    void buildColumns();

    // Sets the bounds for rows (all equal to 0)
    void boundRows();

    // Extracts a GeneExpression object from the notes tag.
    GeneExpression getGeneExpressionFromNotes(const XMLNode& notes) const;

    // Fills all_genes and genes fields
    void getGenes();

    // Returns true iff the reaction has both bounds set
    bool validateReactionBounds(const Reaction& reaction);

    // Returns true iff all species references are valid
    bool validateSpeciesReferences(const ListOfSpeciesReferences& species);

    // Returns true iff the model is valid and can be used for simulation
    // Note that only properties important to running simulations are checked.
    bool validateModel();

    // Sets the given reaction as the simulation target.
    void setObjectiveReaction(const string& objective);

    // Sets the given species as the simulation target.
    void setObjectiveSpecies(const string& objective);

    // Sets the weight of species with id=sid in the objective function to its
    // coefficient in the given list of species, multiplied by scale. Does
    // nothing if the species does not belong to the list.
    void setObjectiveForColumn(const string& sid, int column,
            const ListOfSpeciesReferences& species, double scale);

    // Sets the lower and upper bounds for the flux through the reaction to 0
    void disableReaction(unsigned rnum);

    void applyBounds(const Bound& bound, KineticLaw* kl);

    bool applyBounds(const vector<Bound>& bounds);

    // Records an error in the model.
    void addError(const string& error);
 public:
    MetabolicSimulation();
    ~MetabolicSimulation();

    // The ids of the parameters in a SBML model, which specify the lower
    // and upper bounds on the flux through a reaction.
    static const char* kUpperBoundParameterId;
    static const char* kLowerBoundParameterId;

    // Builds a LP problem for a given SBML Model object
    // Creates a copy of the given parameter.
    bool loadModel(const Model* model, const vector<Bound>& bounds);

    // Returns the list of validation errors, meaningful only after
    // validateModel has been run (executed from loadModel)
    const vector<string>& getErrors() const;


    // Sets the lower and upper bounds for the flux through the reaction to 0
    // Returns true iff reaction with given id exists
    bool disableReaction(const string& reaction_id);

    // Disables the given gene and consequently all reactions that
    // whose gene expressions became false.
    // Reutrns false iff the given gene does not exist.
    bool disableGene(const string& gene_id);

    // Sets the objective to a reaction of species with the given id.
    // If the parameter is both a reaction and species id, reaction
    // gets disabled.
    // Returns true iff the parameter is an id of existing species or reaction.
    bool setObjective(const string& objective);

    // TODO(me)
    void setMaximize(bool maximize);

    // Executes the optimisation.
    bool runSimulation();

    string getStatus();

    // Get the value of the objective function.
    double getObjectiveFunctionValue();
};

#endif  // JWLFBA_METABOLICSIMULATION_H_
