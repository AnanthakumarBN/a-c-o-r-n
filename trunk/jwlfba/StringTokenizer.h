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

// Parses the given string into tokens.

class StringTokenizer {
 private:
    // The parsed list of tokens.
    vector<string> tokens_;

    // Index of the current token in the above vector.
    unsigned current_token_number_;

    // Separator for parsing tokens. 0 means any whitespace.
    char separator_;

    // Returns true iff c is a separator
    bool IsSeparator(char c);
 public:
    StringTokenizer();

    // Returns the current token or "" if we advanced past the last token.
    string CurrentToken();

    // Returns the current token as double. Returns false iff the current token
    // cannot be converted to a finite double value.
    bool CurrentDoubleToken(double* val);

    // Advances to next token.
    void NextToken();

    // Moves back to first token.
    void MoveToFirstToken();

    // Returns true if CurrentToken points to a valid token.
    bool HasRemainingTokens();

    // Loads the given string using any whitespace as separator.
    void Parse(const string& str);
    // Loads the given string using the given character as separator.
    void Parse(const string& str, char separator);
};

#endif  // JWLFBA_STRINGTOKENIZER_H_
