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
    vector<string> tokens;
    unsigned current_token_number;
    char separator;
    bool isSeparator(char c);
 public:
    StringTokenizer();
    string currentToken();
    bool currentDoubleToken(double* val);
    void nextToken();
    void moveToFirstToken();
    bool hasRemainingTokens();
    void parse(const string& str);
    void parse(const string& str, char separator);
};

#endif  // JWLFBA_STRINGTOKENIZER_H_
