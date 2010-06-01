/*
 * Copyright 2010 Jakub Łącki
 */


#ifndef JWLFBA_INPUTPARAMETERS_H_
#define JWLFBA_INPUTPARAMETERS_H_

#include<map>
#include<set>
#include<string>
#include<vector>

using std::map;
using std::set;
using std::string;
using std::vector;

struct OptimisationParameters {
    bool minimize;
    set<string> disabled_genes;
    set<string> disabled_reactions;
    string objective;

    OptimisationParameters();
};

class InputParameters {
 private:
    OptimisationParameters optimisation_parameters_;
    string model_path_;
    string amkfba_model_path_;
    string bounds_file_path_;
    bool interactive_mode_;
    bool print_flux_;

    vector<string> errors_;
    map<string, string> GetParameterMap(const string& param);
    bool ValidateParametersMap(const map<string, string>& param_map);
    void AddError(const string& error);
 public:
    InputParameters();
    string model_path() const { return model_path_; }
    string amkfba_model_path() const { return amkfba_model_path_; }
    string bounds_file_path() const { return bounds_file_path_; }
    bool interactive_mode() const { return interactive_mode_; }
    bool print_flux() const { return print_flux_; }
    const OptimisationParameters& optimisation_parameters() const {
        return optimisation_parameters_;
    }
    const vector<string>& GetErrors() const;
    bool LoadFromString(const string& input_parameters);
};

#endif  // JWLFBA_INPUTPARAMETERS_H_
