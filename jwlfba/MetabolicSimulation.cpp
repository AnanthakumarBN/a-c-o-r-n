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
#include"GeneExpression.h"

using std::pair;
using std::vector;

const char* MetabolicSimulation::kUpperBoundParameterId = "UPPER_BOUND";
const char* MetabolicSimulation::kLowerBoundParameterId = "LOWER_BOUND";
const double MetabolicSimulation::kBoundEpsilon = 1e-8;

MetabolicSimulation::MetabolicSimulation() : linear_problem(NULL),
    model(NULL), internal_metabolites_count(0) { }

MetabolicSimulation::~MetabolicSimulation() {
    if (linear_problem)
        glp_delete_prob(linear_problem);

    delete model;
}

void MetabolicSimulation::addError(const string& error) {
    model_errors.push_back(error);
}

const vector<string>& MetabolicSimulation::getErrors() {
    return model_errors;
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

void MetabolicSimulation::getStoichiometryData(
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

    if (upper_bound - lower_bound < kBoundEpsilon)
        glp_set_col_bnds(linear_problem, column+1, GLP_FX,
                lower_bound, lower_bound);
    else
        glp_set_col_bnds(linear_problem, column+1, GLP_DB,
                lower_bound, upper_bound);
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

    delete [] row_number;
    delete [] value;
}

void MetabolicSimulation::buildColumns() {
    const ListOfReactions& reactions = *model->getListOfReactions();

    for (unsigned column = 0; column < reactions.size(); column++) {
        const Reaction& reaction = *reactions.get(column);
        vector<pair<int, double> > stoichiometry;

        getStoichiometryData(*reaction.getListOfReactants(), -1.0,
                &stoichiometry);
        getStoichiometryData(*reaction.getListOfProducts(), 1.0,
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

bool MetabolicSimulation::validateReactionBounds(const Reaction& reaction) {
    if (!reaction.isSetKineticLaw()) {
        addError("Reaction '" + reaction.getId() + "' has no kinetic law");
        return false;
    }

    const ListOfParameters& parameters = *reaction.getKineticLaw()->
        getListOfParameters();

    bool is_upper_bound_set = false, is_lower_bound_set = false;

    for (unsigned i = 0; i < parameters.size(); i++) {
        if (parameters.get(i)->getId() == kUpperBoundParameterId)
            is_upper_bound_set = true;
        if (parameters.get(i)->getId() == kLowerBoundParameterId)
            is_lower_bound_set = true;
    }

    if (!is_upper_bound_set)
        addError("No upper bound set for reaction '" + reaction.getId() + "'");
    if (!is_lower_bound_set)
        addError("No lower bound set for reaction '" + reaction.getId() + "'");

    return is_upper_bound_set && is_lower_bound_set;
}

bool MetabolicSimulation::validateSpeciesReferences(
        const ListOfSpeciesReferences& species) {
    bool ret = true;
    for (unsigned i = 0; i < species.size(); i++) {
        const SpeciesReference& species_reference =
            *reinterpret_cast<const SpeciesReference*>(species.get(i));

        // TODO(me) check if species exists

        if (!species_reference.isSetSpecies()) {
            addError("Species reference has no 'species' attribute");
            ret = false;
        }
    }
    return ret;
}

bool MetabolicSimulation::validateModel() {
    bool ret = true;
    for (unsigned i = 0; i < model->getNumSpecies(); i++) {
        if (!model->getSpecies(i)->isSetId()) {
            addError("Species has no id");
            ret = false;
        }
    }

    for (unsigned i = 0; i < model->getNumReactions(); i++) {
        const Reaction& reaction = *model->getReaction(i);

        if (!validateReactionBounds(reaction))
            ret = false;

        if (!reaction.isSetId()) {
            addError("Reaction has no id");
            ret = false;
        }

        if (!validateSpeciesReferences(*reaction.getListOfReactants()) ||
                !validateSpeciesReferences(*reaction.getListOfProducts())) {
            ret = false;
        }
    }
    return ret;
}

GeneExpression MetabolicSimulation::getGeneExpressionFromNotes(
        const XMLNode& notes) {
    GeneExpression gexp;
    for (unsigned j = 0; j < notes.getNumChildren(); j++) {
        const XMLNode& note = notes.getChild(j);
        if (note.getNumChildren() == 1) {
            const string& note_string = note.getChild(0).toXMLString();
            if (gexp.looksLikeGeneExpression(note_string)) {
                gexp.loadExpression(note_string);
                break;
            }
        }
    }
    return gexp;
}

void MetabolicSimulation::getGenes() {
    for (unsigned i = 0; i < model->getNumReactions(); i++) {
        GeneExpression gexp;
        Reaction& reaction = *model->getReaction(i);
        if (reaction.isSetNotes()) {
            gexp = getGeneExpressionFromNotes(*reaction.getNotes());
        }
        genes.push_back(gexp);

        gexp.getAllGenes(&all_genes);
    }
}

bool MetabolicSimulation::loadModel(const Model* mod) {
    assert(linear_problem == NULL);
    assert(model == NULL);

    linear_problem = glp_create_prob();

    model = mod->clone();

    if (!validateModel())
        return false;

    findInternalMetabolites();

    glp_add_cols(linear_problem, model->getNumReactions());
    glp_add_rows(linear_problem, internal_metabolites_count);

    boundRows();
    buildColumns();

    getGenes();

    return true;
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
            glp_set_obj_coef(linear_problem, column+1,
                    scale*species_reference.getStoichiometry());
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
    assert(linear_problem != NULL);

    if (model->getReaction(objective) != NULL)
        setObjectiveReaction(objective);
    else if (model->getSpecies(objective) != NULL)
        setObjectiveSpecies(objective);
    else
        return false;

    return true;
}

void MetabolicSimulation::runSimulation() {
    assert(linear_problem != NULL);

    glp_set_obj_dir(linear_problem, GLP_MAX);
    if (glp_simplex(linear_problem, NULL) != 0)
        assert(false);  // TODO(me)
}

double MetabolicSimulation::getObjectiveFunctionValue() {
    assert(linear_problem);
    return glp_get_obj_val(linear_problem);
}


void MetabolicSimulation::disableReaction(unsigned rnum) {
    assert(rnum < model->getNumReactions());

    glp_set_col_bnds(linear_problem, rnum+1, GLP_FX, 0.0, 0.0);
}

bool MetabolicSimulation::disableReaction(const string& reaction_id) {
    for (unsigned i = 0; i < model->getNumReactions(); i++) {
        if (model->getReaction(i)->getId() == reaction_id) {
            disableReaction(i);
            return true;
        }
    }
    return false;
}

bool MetabolicSimulation::disableGene(const string& gene) {
    if (all_genes.find(gene) == all_genes.end())
       return false;

    disabled_genes.insert(gene);
    for (unsigned i = 0; i < model->getNumReactions(); i++) {
        if (!genes[i].evaluate(disabled_genes)) {
            disableReaction(i);
            return true;
        }
    }

    return true;
}
