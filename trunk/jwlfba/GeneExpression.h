/*
 * Copyright 2010 Jakub Łącki
 */


#ifndef JWLFBA_GENEEXPRESSION_H_
#define JWLFBA_GENEEXPRESSION_H_

#include<vector>
#include<set>
#include<string>

using std::vector;
using std::set;
using std::string;

extern const char* kAndToken;
extern const char* kOrToken;
extern const char* kLeftBracket;
extern const char* kRightBracket;

class StringTokenizer {
 private:
    vector<string> tokens;
    unsigned current_token_number;
 public:
    StringTokenizer();
    string currentToken();
    void nextToken();
    void moveToFirstToken();
    bool tokensRemaining();
    void parse(const string& str);
};

class GeneExpression {
 private:
    vector<string> rpn_expression;

    bool isGeneName(const string& gene);
    bool isOperatorToken(const string& token);

    bool evaluateExpression();
    bool geneValue(const string& token, const set<string>& disabled_genes);
    bool evaluate(bool val1, const string& oper, bool val2);
    bool transformToRPN(StringTokenizer* tokenizer);
    bool transformExpressionToRPN(StringTokenizer* tokenizer,
            string expected_operator);
 public:
    bool looksLikeGeneExpression(const string& expr);
    bool evaluate(const set<string>& disabled_genes);
    bool loadExpression(const string& expr);
};

#endif  // JWLFBA_GENEEXPRESSION_H_
