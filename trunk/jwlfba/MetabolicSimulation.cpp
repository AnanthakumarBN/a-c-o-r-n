/*
 * Copyright 2010 Jakub Łącki
 */

#include"MetabolicSimulation.h"
#include<glpk/glpk.h>
#include<sbml/SBMLTypes.h>
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

MetabolicSimulation::MetabolicSimulation() : linear_problem_(NULL),
    model_(NULL), internal_metabolites_count_(0) { }

MetabolicSimulation::~MetabolicSimulation() {
    if (linear_problem_)
        glp_delete_prob(linear_problem_);

    delete model_;
}

void MetabolicSimulation::AddError(const string& error) {
    model_errors_.push_back(error);
}

const vector<string>& MetabolicSimulation::GetErrors() const {
    return model_errors_;
}

void MetabolicSimulation::FindInternalMetabolites() {
    int species_count = 0;
    const ListOfSpecies* species = model_->getListOfSpecies();
    for (unsigned i = 0; i < species->size(); i++) {
        const Species& sp = *(species->get(i));
        if (!sp.getBoundaryCondition()) {
            species_row_[sp.getId()] = species_count++;
        }
    }

    internal_metabolites_count_ = species_count;
}

void MetabolicSimulation::GetStoichiometryData(
     const ListOfSpeciesReferences& species, double scale,
        vector<pair<int, double > >* stoichiometry_data) {
    for (unsigned i = 0; i < species.size(); i++) {
        const SpeciesReference& species_reference =
            *reinterpret_cast<const SpeciesReference*>(species.get(i));

        const string& sid = species_reference.getSpecies();

        if (species_row_.find(sid) != species_row_.end()) {
            // ignore external metabolites
            stoichiometry_data->push_back(
                    std::make_pair(species_row_[sid],
                        species_reference.getStoichiometry() * scale));
        }
    }
}

void MetabolicSimulation::SetColumnBounds(int column,
        const Reaction& reaction) {
    const ListOfParameters& parameters = *reaction.getKineticLaw()->
        getListOfParameters();

    double upper_bound = 0, lower_bound = 0;

    for (unsigned i = 0; i < parameters.size(); i++) {
        if (parameters.get(i)->getId() == kUpperBoundParameterId)
            upper_bound = parameters.get(i) -> getValue();
        if (parameters.get(i)->getId() == kLowerBoundParameterId)
            lower_bound = parameters.get(i) -> getValue();
    }

    if (upper_bound - lower_bound < kBoundEpsilon)
        glp_set_col_bnds(linear_problem_, column+1, GLP_FX,
                lower_bound, lower_bound);
    else
        glp_set_col_bnds(linear_problem_, column+1, GLP_DB,
                lower_bound, upper_bound);
}

void MetabolicSimulation::SetColumnValues(int column,
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
    glp_set_mat_col(linear_problem_, column+1, len, row_number, value);

    delete [] row_number;
    delete [] value;
}

void MetabolicSimulation::BuildColumns() {
    const ListOfReactions& reactions = *model_->getListOfReactions();

    for (unsigned column = 0; column < reactions.size(); column++) {
        const Reaction& reaction = *reactions.get(column);
        vector<pair<int, double> > stoichiometry;

        GetStoichiometryData(*reaction.getListOfReactants(), -1.0,
                &stoichiometry);
        GetStoichiometryData(*reaction.getListOfProducts(), 1.0,
                &stoichiometry);

        SetColumnValues(column, stoichiometry);

        SetColumnBounds(column, reaction);
    }
}

void MetabolicSimulation::BoundRows() {
    int row_count = glp_get_num_rows(linear_problem_);

    for (int row = 0; row < row_count; row++)
        glp_set_row_bnds(linear_problem_, row+1, GLP_FX, 0.0, 0.0);
}

bool MetabolicSimulation::ValidateReactionBounds(const Reaction& reaction) {
    if (!reaction.isSetKineticLaw()) {
        AddError("Reaction '" + reaction.getId() + "' has no kinetic law");
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
        AddError("No upper bound set for reaction '" + reaction.getId() + "'");
    if (!is_lower_bound_set)
        AddError("No lower bound set for reaction '" + reaction.getId() + "'");

    return is_upper_bound_set && is_lower_bound_set;
}

bool MetabolicSimulation::ValidateSpeciesReferences(
        const ListOfSpeciesReferences& species) {
    bool ret = true;
    for (unsigned i = 0; i < species.size(); i++) {
        const SpeciesReference& species_reference =
            *reinterpret_cast<const SpeciesReference*>(species.get(i));

        // TODO(me) check if species exists

        if (!species_reference.isSetSpecies()) {
            AddError("Species reference has no 'species' attribute");
            ret = false;
        }
    }
    return ret;
}

bool MetabolicSimulation::ValidateModel() {
    bool ret = true;
    for (unsigned i = 0; i < model_->getNumSpecies(); i++) {
        if (!model_->getSpecies(i)->isSetId()) {
            AddError("Species has no id");
            ret = false;
        }
    }

    for (unsigned i = 0; i < model_->getNumReactions(); i++) {
        const Reaction& reaction = *model_->getReaction(i);

        if (!ValidateReactionBounds(reaction))
            ret = false;

        if (!reaction.isSetId()) {
            AddError("Reaction has no id");
            ret = false;
        }

        if (!ValidateSpeciesReferences(*reaction.getListOfReactants()) ||
                !ValidateSpeciesReferences(*reaction.getListOfProducts())) {
            ret = false;
        }
    }
    return ret;
}

GeneExpression MetabolicSimulation::GetGeneExpressionFromNotes(
        const XMLNode& notes) const {
    GeneExpression gexp;
    for (unsigned j = 0; j < notes.getNumChildren(); j++) {
        const XMLNode& note = notes.getChild(j);
        if (note.getNumChildren() == 1) {
            const string& note_string = note.getChild(0).toXMLString();
            if (gexp.LooksLikeGeneExpression(note_string)) {
                gexp.LoadExpression(note_string);
                break;
            }
        }
    }
    return gexp;
}

void MetabolicSimulation::GetGenes() {
    for (unsigned i = 0; i < model_->getNumReactions(); i++) {
        GeneExpression gexp;
        Reaction& reaction = *model_->getReaction(i);
        if (reaction.isSetNotes()) {
            gexp = GetGeneExpressionFromNotes(*reaction.getNotes());
        }
        genes_.push_back(gexp);

        gexp.GetAllGenes(&all_genes_);
    }
}

void MetabolicSimulation::BuildReactionsMap() {
    for (unsigned i = 0; i < model_->getNumReactions(); i++) {
        reactions_map_[model_->getReaction(i)->getId()] = i;
    }
}

void MetabolicSimulation::ApplyBound(const Bound& bound, KineticLaw* kl) {
    ListOfParameters* parameters = kl->getListOfParameters();

    Parameter *upper_bound_parameter = NULL, *lower_bound_parameter = NULL;

    for (unsigned i = 0; i < parameters->size(); i++) {
        if (parameters->get(i)->getId() == kUpperBoundParameterId)
            upper_bound_parameter = parameters->get(i);
        if (parameters->get(i)->getId() == kLowerBoundParameterId)
            lower_bound_parameter = parameters->get(i);
    }

    if (upper_bound_parameter == NULL)
        upper_bound_parameter = kl->createParameter();

    if (lower_bound_parameter == NULL)
        lower_bound_parameter = kl->createParameter();

    lower_bound_parameter->setId(kLowerBoundParameterId);
    lower_bound_parameter->setValue(bound.lower_bound);

    upper_bound_parameter->setId(kUpperBoundParameterId);
    upper_bound_parameter->setValue(bound.upper_bound);
}

bool MetabolicSimulation::ApplyBounds(const vector<Bound>& bounds) {
    for (unsigned i = 0; i < bounds.size(); i++) {
        if (reactions_map_.find(bounds[i].reaction_id) == reactions_map_.end()) {
            AddError("Cannot set bounds for reaction '" +
                    bounds[i].reaction_id + "'");
            return false;
        }

        Reaction* reaction = model_->getReaction(
                reactions_map_[bounds[i].reaction_id]);

        KineticLaw* kl;
        if (reaction->isSetKineticLaw())
            kl = reaction->getKineticLaw();
        else
            kl = reaction->createKineticLaw();

        ApplyBound(bounds[i], kl);
    }
    return true;
}

bool MetabolicSimulation::LoadModel(const Model* mod,
        const vector<Bound>& bounds) {
    assert(linear_problem_ == NULL);
    assert(model_ == NULL);

    linear_problem_ = glp_create_prob();

    model_ = mod->clone();

    BuildReactionsMap();

    if (!ApplyBounds(bounds))
        return false;

    if (!ValidateModel())
        return false;

    FindInternalMetabolites();

    glp_add_cols(linear_problem_, model_->getNumReactions());
    glp_add_rows(linear_problem_, internal_metabolites_count_);

    BoundRows();
    BuildColumns();

    GetGenes();

    return true;
}

void MetabolicSimulation::SetObjectiveReaction(const string& rid) {
    for (unsigned column = 0; column < model_->getNumReactions(); column++) {
        if (model_->getReaction(column)->getId() == rid) {
            glp_set_obj_coef(linear_problem_, column+1, 1.0);
            return;
        }
    }
    assert(false);
}

void MetabolicSimulation::SetObjectiveForColumn(const string& sid, int column,
        const ListOfSpeciesReferences& species, double scale) {
    for (unsigned i = 0; i < species.size(); i++) {
        const SpeciesReference& species_reference =
            *reinterpret_cast<const SpeciesReference*>(species.get(i));

        if (species_reference.getSpecies() == sid)
            glp_set_obj_coef(linear_problem_, column+1,
                    scale*species_reference.getStoichiometry());
    }
}

void MetabolicSimulation::SetObjectiveSpecies(const string& sid) {
    for (unsigned column = 0; column < model_->getNumReactions(); column++) {
        const Reaction& reaction = *model_->getReaction(column);

        SetObjectiveForColumn(sid, column,
                *reaction.getListOfReactants(), -1.0);
        SetObjectiveForColumn(sid, column, *reaction.getListOfProducts(), 1.0);
    }
}

bool MetabolicSimulation::SetObjective(const string& objective) {
    assert(model_ != NULL);
    assert(linear_problem_ != NULL);

    if (model_->getReaction(objective) != NULL)
        SetObjectiveReaction(objective);
    else if (model_->getSpecies(objective) != NULL)
        SetObjectiveSpecies(objective);
    else
        return false;

    return true;
}

void MetabolicSimulation::SetMaximize(bool maximize) {
    if (maximize)
        glp_set_obj_dir(linear_problem_, GLP_MAX);
    else
        glp_set_obj_dir(linear_problem_, GLP_MIN);
}

bool MetabolicSimulation::RunSimulation() {
    assert(linear_problem_ != NULL);

    if (glp_simplex(linear_problem_, NULL) != 0)
        return false;

    return true;
}

double MetabolicSimulation::GetObjectiveFunctionValue() {
    assert(linear_problem_);
    return glp_get_obj_val(linear_problem_);
}


void MetabolicSimulation::DisableReaction(unsigned rnum) {
    assert(rnum < model_->getNumReactions());

    glp_set_col_bnds(linear_problem_, rnum+1, GLP_FX, 0.0, 0.0);
}

bool MetabolicSimulation::DisableReaction(const string& reaction_id) {
    for (unsigned i = 0; i < model_->getNumReactions(); i++) {
        if (model_->getReaction(i)->getId() == reaction_id) {
            DisableReaction(i);
            return true;
        }
    }
    return false;
}

bool MetabolicSimulation::DisableGene(const string& gene) {
    if (all_genes_.find(gene) == all_genes_.end())
       return false;

    disabled_genes_.insert(gene);
    for (unsigned i = 0; i < model_->getNumReactions(); i++) {
        if (!genes_[i].Evaluate(disabled_genes_)) {
            DisableReaction(i);
            return true;
        }
    }

    return true;
}
