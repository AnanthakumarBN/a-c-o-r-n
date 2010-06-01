/*
 * Copyright 2010 Jakub Łącki
 */
#include"StringTokenizer.h"
#include<cassert>
#include<cctype>
#include<cmath>
#include<cstdlib>
#include<set>
#include<stack>
#include<string>
#include<vector>

using std::set;
using std::string;
using std::stack;
using std::vector;

bool StringTokenizer::isSeparator(char c) {
    if (separator == 0)
        return isspace(c);
    else
        return c == separator;
}

void StringTokenizer::parse(const string& str, char sep) {
    separator = sep;
    parse(str);
}

void StringTokenizer::parse(const string& str) {
    tokens.clear();

    string current;
    for (unsigned i = 0; i < str.size(); i++) {
        if (isSeparator(str[i]) && !current.empty()) {
            tokens.push_back(current);
            current = "";
        } else if (!isSeparator(str[i])) {
            current += str[i];
        }
    }
    if (!current.empty())
        tokens.push_back(current);
}

StringTokenizer::StringTokenizer() : current_token_number(0), separator(0) { }

void StringTokenizer::nextToken() {
    current_token_number++;
}

void StringTokenizer::moveToFirstToken() {
    current_token_number = 0;
}

bool StringTokenizer::hasRemainingTokens() {
    return current_token_number < tokens.size();
}

bool StringTokenizer::currentDoubleToken(double* val) {
    const string& token = currentToken();
    char* endptr;
    *val = strtod(token.c_str(), &endptr);
    if (isnan(*val) || isinf(*val) || endptr != token.c_str() + token.size())
        return false;

    return true;
}

string StringTokenizer::currentToken() {
    if (current_token_number < tokens.size())
        return tokens[current_token_number];
    else
        return "";
}

