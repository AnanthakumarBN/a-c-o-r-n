/*
 * Copyright 2010 Jakub Łącki
 */

#include <iostream>
#include<string>
#include<vector>
#include"lib/pugixml.hpp"
#include"Bound.h"
#include"InputParameters.h"
#include"ReactionFlux.h"
#include"SimulationController.h"
#include"XMLTaskProcesser.h"

using std::string;
using pugi::xml_parse_result;
using pugi::xml_node;

void XMLTaskProcesser::Error(const string& err) {
    fprintf(stderr, "%s\n", err.c_str());
}

void XMLTaskProcesser::GetBounds(vector<Bound>* bounds) {
    Bound b;
    for (xml_node bound = doc_.child("fbaTask").child("listOfBounds").
            first_child(); bound; bound = bound.next_sibling("bound")) {
        b.reaction_id = bound.attribute("reactionId").value();
        b.lower_bound= bound.attribute("lowerBound").as_double();
        b.upper_bound = bound.attribute("upperBound").as_double();
        bounds -> push_back(b);
    }
}

OptimisationParameters XMLTaskProcesser::GetOptimisationParameters(
        xml_node simulation) {
    xml_node xml_params = simulation.child("simulationParameters");

    OptimisationParameters op;
    op.objective = xml_params.child("objective").child_value();
    // FIXME: this works only for a single gene and reaction

    const string& dg = xml_params.child("disableGene").child_value();
    if (!dg.empty())
        op.disabled_genes.insert(dg);

    const string& dr = xml_params.child("disableReaction").child_value();
    if (!dr.empty())
        op.disabled_reactions.insert(dr);
    op.minimize = xml_params.child("minimize").child_value() == string("true");

    return op;
}

string XMLTaskProcesser::GetStatus(const SimulationController& sc) {
    if (sc.GetOptimal())
        return "OPTIMAL";
    else if (sc.GetFeasible())
        return "FEASIBLE";
    else
        return "INFEASIBLE";
}

void XMLTaskProcesser::RunSimulation(xml_node simulation,
        const InputParameters& ip) {
    SimulationController sc;
    OptimisationParameters op;
    op = GetOptimisationParameters(simulation);

    InputParameters parameters = ip;

    parameters.set_print_flux(simulation.child("simulationParameters").
            child("printFlux").child_value() == string("true"));
    parameters.set_optimisation_parameters(op);

    // Delete any previous results
    simulation.remove_child("simulationResults");
    xml_node results = simulation.append_child("simulationResults");

    if (sc.RunSimulation(parameters)) {
        results.append_attribute("status") = GetStatus(sc).c_str();
        results.append_attribute("objectiveFunctionValue").
            set_value(sc.GetObjective());
    } else {
        results.append_attribute("status") = "ERROR";
        results.append_attribute("objectiveFunctionValue").set_value(0.0);
    }

    if (parameters.print_flux()) {
        vector<ReactionFlux> flux;
        sc.GetFlux(&flux);
        for (std::size_t i = 0; i < flux.size(); i++) {
            xml_node flux_node = results.append_child("flux");
            flux_node.append_attribute("reaction") = flux[i].reaction.c_str();
            flux_node.append_attribute("value").set_value(flux[i].flux);
        }
    }
}

void XMLTaskProcesser::Run(InputParameters parameters) {
    xml_parse_result result = doc_.load_file(parameters.xml_task().c_str());
    if (!result) {
        Error(result.description());
        return;
    }

    vector<Bound> bounds;
    GetBounds(&bounds);

    parameters.set_bounds(bounds);

    for (xml_node simulation = doc_.child("fbaTask").
            child("listOfSimulations").first_child(); simulation;
            simulation = simulation.next_sibling("simulation")) {
        RunSimulation(simulation, parameters);
    }
    doc_.save(std::cout);
}
