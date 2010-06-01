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
    glp_prob* linear_problem_;

    // libSBML object that stores the model
    Model* model_;

    // Maps internal metabolites (and only them) to the numbers of rows
    // (0-based) in the constraint matrix.
    map<string, int> species_row_;

    map<string, int> reactions_map_;

    // all genes in the model
    set<string> all_genes_;

    // set of genes that we consider inactive
    set<string> disabled_genes_;

    // GeneExpression for each reaction in the model
    vector<GeneExpression> genes_;

    int internal_metabolites_count_;

    // list of errors found when validating the model
    vector<string> model_errors_;

    // Returns a list of <row_number, stoichiometry_coefficient*scale> for each
    // species from the list, which is an internal species. Scaling is used,
    // because we want the products to have a negative coefficient.
    void GetStoichiometryData(const ListOfSpeciesReferences& species,
            double scale, vector<pair<int, double> >* stoichiometry_data);

    // Sets the constraints in the LP matrix according to the second parameter.
    void SetColumnValues(int column,
            const vector<pair<int, double> >& stoichiometry_data);

    // Sets the bounds for a given column using lower and upper bounds for
    // the flux through the reaction
    void SetColumnBounds(int column, const Reaction& reaction);

    // Builds the species_row map and stores the count in
    // internal_metabolites_count
    void FindInternalMetabolites();

    void BuildReactionsMap();

    // Sets the entries in the LP matrix and sets colum bounds
    void BuildColumns();

    // Sets the bounds for rows (all equal to 0)
    void BoundRows();

    // Extracts a GeneExpression object from the notes tag.
    GeneExpression GetGeneExpressionFromNotes(const XMLNode& notes) const;

    // Fills all_genes and genes fields
    void GetGenes();

    // Returns true iff the reaction has both bounds set
    bool ValidateReactionBounds(const Reaction& reaction);

    // Returns true iff all species references are valid
    bool ValidateSpeciesReferences(const ListOfSpeciesReferences& species);

    // Returns true iff the model is valid and can be used for simulation
    // Note that only properties important to running simulations are checked.
    bool ValidateModel();

    // Sets the given reaction as the simulation target.
    void SetObjectiveReaction(const string& objective);

    // Sets the given species as the simulation target.
    void SetObjectiveSpecies(const string& objective);

    // Sets the weight of species with id=sid in the objective function to its
    // coefficient in the given list of species, multiplied by scale. Does
    // nothing if the species does not belong to the list.
    void SetObjectiveForColumn(const string& sid, int column,
            const ListOfSpeciesReferences& species, double scale);

    // Sets the lower and upper bounds for the flux through the reaction to 0
    void DisableReaction(unsigned rnum);

    void ApplyBound(const Bound& bound, KineticLaw* kl);

    bool ApplyBounds(const vector<Bound>& bounds);

    // Records an error in the model.
    void AddError(const string& error);
 public:
    MetabolicSimulation();
    ~MetabolicSimulation();

    // The ids of the parameters in a SBML model, which specify the lower
    // and upper bounds on the flux through a reaction.
    static const char* kUpperBoundParameterId;
    static const char* kLowerBoundParameterId;

    // Builds a LP problem for a given SBML Model object
    // Creates a copy of the given parameter.
    bool LoadModel(const Model* model, const vector<Bound>& bounds);

    // Returns the list of validation errors, meaningful only after
    // validateModel has been run (executed from loadModel)
    const vector<string>& GetErrors() const;


    // Sets the lower and upper bounds for the flux through the reaction to 0
    // Returns true iff reaction with given id exists
    bool DisableReaction(const string& reaction_id);

    // Disables the given gene and consequently all reactions that
    // whose gene expressions became false.
    // Reutrns false iff the given gene does not exist.
    bool DisableGene(const string& gene_id);

    // Sets the objective to a reaction of species with the given id.
    // If the parameter is both a reaction and species id, reaction
    // gets disabled.
    // Returns true iff the parameter is an id of existing species or reaction.
    bool SetObjective(const string& objective);

    // TODO(me)
    void SetMaximize(bool maximize);

    // Executes the optimisation.
    bool RunSimulation();

    string GetStatus();

    // Get the value of the objective function.
    double GetObjectiveFunctionValue();
};

#endif  // JWLFBA_METABOLICSIMULATION_H_
