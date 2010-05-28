/*
 * Copyright 2010 Jakub Łącki
 */

#include"MetabolicSimulation.h"
#include<glpk/glpk.h>
#include<sbml/Model.h>
#include<sbml/Species.h>
#include<utility>
#include<string>
#include<vector>
using std::pair;
using std::vector;

MetabolicSimulation::MetabolicSimulation() : linear_problem(NULL),
    model(NULL), internal_metabolites_count(0) { }

MetabolicSimulation::~MetabolicSimulation() {
    if (linear_problem)
        glp_delete_prob(linear_problem);

    delete model;
}

void MetabolicSimulation::findInternalMetabolites() {
    int species_count = 0;
    const ListOfSpecies* species = model->getListOfSpecies();
    for (unsigned i = 0; i < species->size(); i++) {
        const Species& sp = *(species->get(i));
        if (!sp.getBoundaryCondition()) {
            species_row[sp.getId()] = species_count++;
        }
    }

    internal_metabolites_count = species_count;
}

void MetabolicSimulation::addStoichiometry(int column,
        const ListOfSpeciesReferences& species, double scale,
        vector<pair<int, double > >* stoichiometry_data) {
    for (unsigned i = 0; i < species.size(); i++) {
        const SpeciesReference& species_reference =
            *reinterpret_cast<const SpeciesReference*>(species.get(i));

        const string& sid = species_reference.getSpecies();

        if (species_row.find(sid) != species_row.end()) {
            // ignore external metabolites
            stoichiometry_data->push_back(
                    std::make_pair(species_row[sid],
                        species_reference.getStoichiometry() * scale));
        }
    }
}

void MetabolicSimulation::setColumnBounds(int column,
        const Reaction& reaction) {
    const ListOfParameters& parameters = *reaction.getKineticLaw()->
        getListOfParameters();

    double upper_bound, lower_bound;

    for (unsigned i = 0; i < parameters.size(); i++) {
        if (parameters.get(i)->getId() == kUpperBoundParameterId)
            upper_bound = parameters.get(i) -> getValue();
        if (parameters.get(i)->getId() == kLowerBoundParameterId)
            lower_bound = parameters.get(i) -> getValue();
    }

    glp_set_col_bnds(linear_problem, column+1, GLP_DB, lower_bound,
            upper_bound);
}

void MetabolicSimulation::setColumnValues(int column,
        const vector<pair<int, double> >& stoichiometry_data) {
    int* row_number;
    double* value;

    int len = stoichiometry_data.size();

    row_number = new int[1+len];
    value = new double[1+len];

    for (int i = 0; i < len; i++) {
        row_number[i+1] = stoichiometry_data[i].first+1;
        value[i+1] = stoichiometry_data[i].second;
    }
    glp_set_mat_col(linear_problem, column+1, len, row_number, value);
}

void MetabolicSimulation::buildColumns() {
    const ListOfReactions& reactions = *model->getListOfReactions();

    for (unsigned column = 0; column < reactions.size(); column++) {
        const Reaction& reaction = *reactions.get(column);
        vector<pair<int, double> > stoichiometry;

        addStoichiometry(column, *reaction.getListOfReactants(), -1.0,
                &stoichiometry);
        addStoichiometry(column, *reaction.getListOfProducts(), 1.0,
                &stoichiometry);

        setColumnValues(column, stoichiometry);

        setColumnBounds(column, reaction);
    }
}

void MetabolicSimulation::boundRows() {
    int row_count = glp_get_num_rows(linear_problem);

    for (int row = 0; row < row_count; row++)
        glp_set_row_bnds(linear_problem, row+1, GLP_FX, 0.0, 0.0);
}

void MetabolicSimulation::loadModel(const Model* mod) {
    // TODO(me) check if not init'ed earlier
    linear_problem = glp_create_prob();

    model = mod->clone();

    findInternalMetabolites();

    glp_add_cols(linear_problem, model->getNumReactions());
    glp_add_rows(linear_problem, internal_metabolites_count);
    boundRows();
    buildColumns();
}

void MetabolicSimulation::setObjectiveReaction(const string& rid) {
    for (unsigned column = 0; column < model->getNumReactions(); column++) {
        if (model->getReaction(column)->getId() == rid) {
            glp_set_obj_coef(linear_problem, column+1, 1.0);
            return;
        }
    }
    assert(false);
}

void MetabolicSimulation::setObjectiveForColumn(const string& sid, int column,
        const ListOfSpeciesReferences& species, double scale) {
    for (unsigned i = 0; i < species.size(); i++) {
        const SpeciesReference& species_reference =
            *reinterpret_cast<const SpeciesReference*>(species.get(i));

        if (species_reference.getSpecies() == sid)
            glp_set_obj_coef(linear_problem, column+1, scale);
    }
}

void MetabolicSimulation::setObjectiveSpecies(const string& sid) {
    for (unsigned column = 0; column < model->getNumReactions(); column++) {
        const Reaction& reaction = *model->getReaction(column);

        setObjectiveForColumn(sid, column,
                *reaction.getListOfReactants(), -1.0);
        setObjectiveForColumn(sid, column, *reaction.getListOfProducts(), 1.0);
    }
}

bool MetabolicSimulation::setObjective(const string& objective) {
    assert(model != NULL);

    if (model->getReaction(objective) != NULL)
        setObjectiveReaction(objective);
    else if (model->getSpecies(objective) != NULL)
        setObjectiveSpecies(objective);
    else
        return false;

    return true;
}

void MetabolicSimulation::runSimulation() {
    glp_set_obj_dir(linear_problem, GLP_MAX);
    if (glp_simplex(linear_problem, NULL) != 0)
        assert(false);
}

double MetabolicSimulation::getObjectiveFunctionValue() {
    return glp_get_obj_val(linear_problem);
}

