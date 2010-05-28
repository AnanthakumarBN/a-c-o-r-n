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

const char* kAndToken = "and";
const char* kOrToken = "or";
const char* kLeftBracket = "(";
const char* kRightBracket = ")";

class GeneExpression {
 private:
    vector<string> tokens;
    vector<string> rpn_expression;
    unsigned current_token_number;

    string currentToken();
    void nextToken();
    void moveToFirstToken();
    bool tokensRemaining();

    bool isGeneName(const string& gene);
    bool isOperatorToken(const string& token);

    bool evaluateExpression();
    bool geneValue(const string& token, const set<string>& disabled_genes);
    bool evaluate(bool val1, const string& oper, bool val2);
    bool transformToRPN();
    bool transformExpressionToRPN(string expected_operator);
 public:
    bool looksLikeGeneExpression(const string& expr);
    bool evaluate(const set<string>& disabled_genes);
    bool loadExpression(const string& expr);
};

#endif  // JWLFBA_GENEEXPRESSION_H_
