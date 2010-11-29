/*
 * Copyright 2010 Jakub Łącki
 */


#ifndef JWLFBA_INPUTPARAMETERS_H_
#define JWLFBA_INPUTPARAMETERS_H_

#include<map>
#include<set>
#include<string>
#include<vector>
#include"Bound.h"

using std::map;
using std::set;
using std::string;
using std::vector;

// The list of all command line parameters for a simulation

struct OptimisationParameters {
    bool minimize;
    set<string> disabled_genes;
    set<string> disabled_reactions;
    string objective;

    OptimisationParameters();
};

// Responsible for parsing command line and loading all option.

class InputParameters {
 private:
    // Simulation parameters
    OptimisationParameters optimisation_parameters_;

    // Path to XML model file. '-' denotes stdin
    string model_path_;

    // Path to model file in amkfba format. '-' denotes stdin
    string amkfba_model_path_;

    // Path to file describing bounds.
    string bounds_file_path_;

    // Alternatively, a list of bounds
    vector<Bound> bounds_;

    // True if we should run in interactive mode, in which comands are read
    // from standard input in a loop.
    bool interactive_mode_;

    // The path to XML file containing description of the task
    string xml_task_;

    // Determines if the flux through each reaction should be printed after
    // finding the solution.
    bool print_flux_;

    // Syntax errors in the parameters.
    vector<string> errors_;

    // Returns a value for a given key in a map or "" if the value
    // is undefined. It has an advantage over [] operator, since it can
    // be used with a const map.
    string GetMapValue(const map<string, string>& mp, const string& key);

    // Parses the command line into a option->value map
    map<string, string> GetParametersMap(const string& param) const;

    // Returns true iff we recognize all parameters from the param_map
    bool CheckForUnknownParameters(const map<string, string>& param_map);

    // Returns true iff the set of parameters is correct.
    bool ValidateParametersMap(const map<string, string>& param_map);

    // Parses a comma separated string into set of values.
    void ParseSetValue(const string& csv_values, set<string>* out);

    // Reports a syntax error.
    void AddError(const string& error);
 public:
    InputParameters();
    string model_path() const { return model_path_; }
    string amkfba_model_path() const { return amkfba_model_path_; }
    string bounds_file_path() const { return bounds_file_path_; }
    vector<Bound> bounds() const { return bounds_; }
    void set_bounds(const vector<Bound>& b) { bounds_ = b; }

    bool interactive_mode() const { return interactive_mode_; }
    string xml_task() const { return xml_task_; }
    bool print_flux() const { return print_flux_; }
    void set_print_flux(bool pf) { print_flux_ = pf; }
    const OptimisationParameters& optimisation_parameters() const {
        return optimisation_parameters_;
    }

    void set_optimisation_parameters(const OptimisationParameters& op) {
        optimisation_parameters_ = op;
    }

    // Returns the vector of sytnax errors.
    const vector<string>& GetErrors() const;

    // Loads parameters from a string. Returns true iff the parameters
    // are valid.
    bool LoadFromString(const string& input_parameters);
};

#endif  // JWLFBA_INPUTPARAMETERS_H_
