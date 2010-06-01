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
    OptimisationParameters optimisation_parameters;
    string model_path;
    string amkfba_model_path;
    string bounds_file_path;
    bool interactive_mode;
    bool print_flux;

    vector<string> errors;
    map<string, string> getParameterMap(const string& param);
    bool validateParametersMap(const map<string, string>* param_map);
    void addError(const string& error);
 public:
    InputParameters();
    const vector<string>& getErrors();
    string getModelPath() const { return model_path; }
    string getAmkfbaModelPath() const { return amkfba_model_path; }
    string getBoundsFilePath() const { return bounds_file_path; }
    bool getInteractiveMode() const { return interactive_mode; }
    const OptimisationParameters& getOptimisationParameters() const {
        return optimisation_parameters;
    }
    const vector<string>& getErrors() const;
    bool loadFromString(const string& input_parameters);
};

#endif  // JWLFBA_INPUTPARAMETERS_H_
