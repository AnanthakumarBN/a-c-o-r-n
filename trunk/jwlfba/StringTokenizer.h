/*
 * Copyright 2010 Jakub Łącki
 */


#ifndef JWLFBA_STRINGTOKENIZER_H_
#define JWLFBA_STRINGTOKENIZER_H_

#include<vector>
#include<set>
#include<string>

using std::vector;
using std::set;
using std::string;


class StringTokenizer {
 private:
    vector<string> tokens_;
    unsigned current_token_number_;
    char separator_;
    bool IsSeparator(char c);
 public:
    StringTokenizer();
    string CurrentToken();
    bool CurrentDoubleToken(double* val);
    void NextToken();
    void MoveToFirstToken();
    bool HasRemainingTokens();
    void Parse(const string& str);
    void Parse(const string& str, char separator);
};

#endif  // JWLFBA_STRINGTOKENIZER_H_
