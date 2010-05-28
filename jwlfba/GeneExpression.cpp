/*
 * Copyright 2010 Jakub Łącki
 */
#include"GeneExpression.h"
#include<cassert>
#include<cctype>
#include<set>
#include<stack>
#include<string>
#include<vector>

using std::set;
using std::string;
using std::stack;
using std::vector;

bool GeneExpression::loadExpression(const std::string& expr) {
    string current;
    for (unsigned i = 0; i < expr.size(); i++) {
        if (isspace(expr[i]) && !current.empty()) {
            tokens.push_back(current);
            current = "";
        } else if (!isspace(expr[i])) {
            current += expr[i];
        }
    }
    if (!current.empty())
        tokens.push_back(current);

    return transformToRPN();
}

void GeneExpression::nextToken() {
    current_token_number++;
}

void GeneExpression::moveToFirstToken() {
    current_token_number = 0;
}

bool GeneExpression::tokensRemaining() {
return current_token_number < tokens.size();
}

bool GeneExpression::isGeneName(const string& gene) {
    return !gene.empty() && gene != kAndToken && gene != kOrToken &&
        gene != kLeftBracket && gene != kRightBracket;
}

bool GeneExpression::isOperatorToken(const string& token) {
    return token == kAndToken || token == kOrToken;
}

bool GeneExpression::transformExpressionToRPN(string expected_operator) {
    if (currentToken() == kLeftBracket) {
        nextToken();
        if (!transformExpressionToRPN("") || currentToken() != kRightBracket)
            return false;
        nextToken();
    } else if (isGeneName(currentToken())) {
        rpn_expression.push_back(currentToken());
        nextToken();
    }

    if (!tokensRemaining() || currentToken() == kRightBracket)
       return true;

    if (expected_operator.empty() && currentToken() != expected_operator)
        return false;
    else
        expected_operator = currentToken();

    nextToken();

    if (!transformExpressionToRPN(expected_operator))
        return false;

    rpn_expression.push_back(expected_operator);
    nextToken();

    return true;
}

bool GeneExpression::transformToRPN() {
    moveToFirstToken();

    if (!tokensRemaining())
        return true;
    if (!transformExpressionToRPN(""))
        return false;
    return !tokensRemaining();
}

string GeneExpression::currentToken() {
    if (current_token_number < tokens.size())
        return tokens[current_token_number];
    else
        return "";
}

bool GeneExpression::geneValue(const string& gene,
        const set<string>& disabledGenes) {
    return disabledGenes.find(gene) == disabledGenes.end();
}

bool GeneExpression::evaluate(bool val1, const string& oper, bool val2) {
    if (oper == kAndToken)
        return val1 && val2;
    else if (oper == kOrToken)
        return val1 || val2;
    else
        assert(false);
}

bool GeneExpression::evaluate(const set<string>& disabledGenes) {
    stack<bool> rpn_stack;

    for (unsigned i = 0; i < rpn_expression.size(); i++) {
        if (isOperatorToken(rpn_expression[i])) {
            assert(rpn_stack.size() >= 2);

            bool x, y;
            x = rpn_stack.top();
            rpn_stack.pop();
            y = rpn_stack.top();
            rpn_stack.pop();

            rpn_stack.push(evaluate(x, rpn_expression[i], y));
        } else {
            rpn_stack.push(geneValue(rpn_expression[i], disabledGenes));
        }
    }
    assert(rpn_stack.size() == 1);
    return rpn_stack.top();
}
