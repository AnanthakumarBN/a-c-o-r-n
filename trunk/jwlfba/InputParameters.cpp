/*
 * Copyright 2010 Jakub Łącki
 */

#include"InputParameters.h"
#include<cstring>
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

InputParameters::InputParameters() : interactive_mode(false),
    print_flux(false) { }

const char* kValidParameters[] = {
    "--disable-genes", "--disable-reactions", "--objective", "--model",
    "--amkfba-model", "--bounds-file", "--interactive", "--print-flux", "--min"
};

const char* kOptionPrefix = "--";

map<string, string> getParameterMap(const string& param) {
    StringTokenizer st;
    map<string, string> ret;

    st.parse(param);
    while (st.hasRemainingTokens()) {
        string key = st.currentToken();
        st.nextToken();
        if (st.hasRemainingTokens() && st.currentToken().substr(
                    0, strlen(kOptionPrefix)) != kOptionPrefix) {
            ret[key] = st.currentToken();
            st.nextToken();
        } else {
            ret[key] = "";
        }
    }
    return ret;
}

void parseSetValue(const string& csv_set, set<string>* out) {
    StringTokenizer st;
    st.parse(csv_set, ',');
    while (st.hasRemainingTokens()) {
        out->insert(st.currentToken());
        st.nextToken();
    }
}
/*
void InputParameters::processSetParameters(const map<string, string>& param_map,
        const vector<pair<string, set<string>* > >& set_parameters) {
    for (unsigned i = 0; i < set_parameters.size(); i++) {
        const map<string, string>::iterator& it = param_map->find(
                string(kOptionPrefix) + set_parameters[i].first);
        if (it != param_map.end()) {
            parseSetValue(it->second, set_parameters[i].second);
        }
    }
}

void InputParameters::processFlagParameters(const map<string, string>& param_map,
        const vector<pair<string, bool*> >& flag_parameters) {
    for (unsigned i = 0; i < flag_parameters.size(); i++) {
        const map<string, string>::iterator& it = param_map->find(
                string(kOptionPrefix) + set_parameters[i].first);
        if (it != param_map.end()) {
            *flag_parameters[i].second = true;
            if (it->second != "")
                addError(it->first + " takes no argument");
        }
    }
}

void InputParameters::processStringParameters(
        const map<string, string>& param_map,
        const vector<pair<string, string*> >& string_parameters) {
    for (unsigned i = 0; i < string_parameters.size(); i++) {
        const map<string, string>::iterator& it = param_map->find(
                string(kOptionPrefix) + string_parameters[i].first);
        if (it != param_map.end()) {
            *flag_parameters[i].second = it->second;
        }
    }
}
*/
void InputParameters::addError(const string& error) {
    errors.push_back(error);
}

const vector<string>& InputParameters::getErrors() const {
    return errors;
}

bool InputParameters::validateParametersMap(map<string, string>* param_map) {
    errors.clear();

    if (!param_map["--min"].empty())
        addError("--min takes no arguments");

    if (!param_map["--print-flux"].empty())
        addError("--print-flux takes no arguments");

    if (!param_map["--interactive"].empty())
        addError("--interactive takes no arguments");

    if (param_map["--objective"].empty())
        addError("No objective specified");

    if (param_map["--amkfba-model"].empty() && param_map["--model"].empty())
        addError("No model specified");

    if (!param_map["--amkfba-model"].empty() && !param_map["--model"].empty())
        addError("--akmfba-model and --model can't be set at the same time");

    for (map<string, string>::iterator it = param_map.begin();
            it != param_map.end(); ++it) {
        int index = -1;
        for (unsigned i = 0; i <
                sizeof(kValidParameters)/sizeof(kValidParameters[0]); i++) {
            if (it->first == kValidParameters[i])
                index = i;
        }
        if (index == -1) {
            addError(string("Invalid parameter '") + it->first + "'");
        }
    }
    return getErrors().size() == 0;
}

bool InputParameters::loadFromString(const string& input_parameters) {
    map<string, string> param_map = getParameterMap(input_parameters);

    if (!validateParametersMap(param_map))
        return false;

    if (param_map.find("--min") != param_map.end())
        optimisation_parameters.minimize = true;

    if (param_map.find("--print_flux") != param_map.end())
        print_flux = true;

    if (param_map.find("--interactive") != param_map.end())
        interactive_mode = true;

    optimisation_parameters.objective = param_map["--objective"];
    model_path = param_map["--model"];
    amkfba_model_path = param_map["--amkfba-model"];
    bounds_file_path = param_map["--bounds-file"];

    parseSetValue(param_map["--disable-genes"],
            &optimisation_parameters.disabled_genes);

    parseSetValue(param_map["--disable-reactions"],
            &optimisation_parameters.disabled_reactions);

    return true;
}


