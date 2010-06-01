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

bool StringTokenizer::IsSeparator(char c) {
    if (separator_ == 0)
        return isspace(c);
    else
        return c == separator_;
}

void StringTokenizer::Parse(const string& str, char sep) {
    separator_ = sep;
    Parse(str);
}

void StringTokenizer::Parse(const string& str) {
    tokens_.clear();

    string current;
    for (unsigned i = 0; i < str.size(); i++) {
        if (IsSeparator(str[i]) && !current.empty()) {
            tokens_.push_back(current);
            current = "";
        } else if (!IsSeparator(str[i])) {
            current += str[i];
        }
    }
    if (!current.empty())
        tokens_.push_back(current);
}

StringTokenizer::StringTokenizer() : current_token_number_(0), separator_(0) { }

void StringTokenizer::NextToken() {
    current_token_number_++;
}

void StringTokenizer::MoveToFirstToken() {
    current_token_number_ = 0;
}

bool StringTokenizer::HasRemainingTokens() {
    return current_token_number_ < tokens_.size();
}

bool StringTokenizer::CurrentDoubleToken(double* val) {
    const string& token = CurrentToken();
    char* endptr;
    *val = strtod(token.c_str(), &endptr);
    if (isnan(*val) || isinf(*val) || endptr != token.c_str() + token.size())
        return false;

    return true;
}

string StringTokenizer::CurrentToken() {
    if (current_token_number_ < tokens_.size())
        return tokens_[current_token_number_];
    else
        return "";
}

