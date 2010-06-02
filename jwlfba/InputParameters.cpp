/*
 * Copyright 2010 Jakub Łącki
 */

#include"InputParameters.h"
#include<cstring>
#include<cstdio>
#include<map>
#include<string>
#include<set>
#include<vector>
#include"StringTokenizer.h"

using std::map;
using std::string;
using std::set;
using std::pair;
using std::vector;

// The default behavior is to compute maximum flux
OptimisationParameters::OptimisationParameters() : minimize(false) { }

InputParameters::InputParameters() : interactive_mode_(false),
    print_flux_(false) { }

const char* kValidParameters[] = {
    "--disable-genes", "--disable-reactions", "--objective", "--model",
    "--amkfba-model", "--bounds-file", "--interactive", "--print-flux", "--min"
};

const char* kOptionPrefix = "--";

map<string, string> InputParameters::GetParametersMap(
        const string& param) const {
    StringTokenizer st;
    map<string, string> ret;

    st.Parse(param);
    while (st.HasRemainingTokens()) {
        string key = st.CurrentToken();
        st.NextToken();
        if (st.HasRemainingTokens() && st.CurrentToken().substr(
                    0, strlen(kOptionPrefix)) != kOptionPrefix) {
            ret[key] = st.CurrentToken();
            st.NextToken();
        } else {
            ret[key] = "";
        }
    }
    return ret;
}

void InputParameters::ParseSetValue(const string& csv_set, set<string>* out) {
    StringTokenizer st;
    st.Parse(csv_set, ',');
    while (st.HasRemainingTokens()) {
        out->insert(st.CurrentToken());
        st.NextToken();
    }
}

void InputParameters::AddError(const string& error) {
    errors_.push_back(error);
}

const vector<string>& InputParameters::GetErrors() const {
    return errors_;
}

string InputParameters::GetMapValue(const map<string, string>& mp,
        const string& key) {
    map<string, string>::const_iterator it = mp.find(key);
    if (it == mp.end())
        return "";
    else
        return it->second;
}

bool InputParameters::CheckForUnknownParameters(
        const map<string, string>& param_map) {
    bool ret = true;
    for (map<string, string>::const_iterator it = param_map.begin();
            it != param_map.end(); ++it) {
        int index = -1;
        for (unsigned i = 0; i <
                sizeof(kValidParameters)/sizeof(kValidParameters[0]); i++) {
            if (it->first == kValidParameters[i])
                index = i;
        }
        if (index == -1) {
            ret = false;
            AddError(string("Invalid parameter '") + it->first + "'");
        }
    }
    return ret;
}

bool InputParameters::ValidateParametersMap(
        const map<string, string>& param_map) {
    errors_.clear();

    // Flag options take no arguments
    if (!GetMapValue(param_map, "--min").empty())
        AddError("--min takes no arguments");

    if (!GetMapValue(param_map, "--print-flux").empty())
        AddError("--print-flux takes no arguments");

    if (!GetMapValue(param_map, "--interactive").empty())
        AddError("--interactive takes no arguments");

    // These option have to have an argument
    if (GetMapValue(param_map, "--objective").empty())
        AddError("No objective specified");

    if (GetMapValue(param_map, "--amkfba-model").empty() &&
            GetMapValue(param_map, "--model").empty())
        AddError("No model specified");

    // Exactly one of those two has to be set.
    if (!GetMapValue(param_map, "--amkfba-model").empty() &&
            !GetMapValue(param_map, "--model").empty())
        AddError("--akmfba-model and --model can't be set at the same time");

    CheckForUnknownParameters(param_map);

    return GetErrors().size() == 0;
}

bool InputParameters::LoadFromString(const string& input_parameters) {
    map<string, string> param_map = GetParametersMap(input_parameters);

    if (param_map.find("--interactive") != param_map.end()) {
        interactive_mode_ = true;
        return true;
    }

    if (!ValidateParametersMap(param_map))
        return false;

    // Flag options
    if (param_map.find("--min") != param_map.end())
        optimisation_parameters_.minimize = true;

    if (param_map.find("--print-flux") != param_map.end())
        print_flux_ = true;

    // Parametrized options
    optimisation_parameters_.objective = param_map["--objective"];
    model_path_ = param_map["--model"];
    amkfba_model_path_ = param_map["--amkfba-model"];
    bounds_file_path_ = param_map["--bounds-file"];

    // Options taking sets as values
    ParseSetValue(param_map["--disable-genes"],
            &optimisation_parameters_.disabled_genes);

    ParseSetValue(param_map["--disable-reactions"],
            &optimisation_parameters_.disabled_reactions);

    return true;
}


