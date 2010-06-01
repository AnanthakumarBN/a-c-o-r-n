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
#include"StringTokenizer.h"

using std::set;
using std::string;
using std::stack;
using std::vector;

const char* GeneExpression::kAndToken = "and";
const char* GeneExpression::kOrToken = "or";
const char* GeneExpression::kLeftBracket = "(";
const char* GeneExpression::kRightBracket = ")";
const char* GeneExpression::kGeneExpressionPrefix = "GENE_ASSOCIATION:";

bool GeneExpression::looksLikeGeneExpression(const string& expr) const {
    if (expr.empty())
        return false;

    return expr.compare(0, sizeof(kGeneExpressionPrefix),
            kGeneExpressionPrefix, sizeof(kGeneExpressionPrefix)) == 0;
}

bool GeneExpression::loadExpression(const string& expr) {
    StringTokenizer tokenizer;
    tokenizer.parse(expr);

    if (tokenizer.currentToken() != kGeneExpressionPrefix)
       return false;

    tokenizer.nextToken();
    rpn_expression.clear();
    return transformToRPN(&tokenizer);
}

bool GeneExpression::isGeneName(const string& gene) const {
    return !gene.empty() && gene != kAndToken && gene != kOrToken &&
        gene != kLeftBracket && gene != kRightBracket;
}

bool GeneExpression::isOperatorToken(const string& token) const {
    return token == kAndToken || token == kOrToken;
}

bool GeneExpression::transformExpressionToRPN(StringTokenizer* tokenizer,
        string expected_operator) {
    if (tokenizer->currentToken() == kLeftBracket) {
        tokenizer->nextToken();
        if (!transformExpressionToRPN(tokenizer, "")
                || tokenizer->currentToken() != kRightBracket) {
            return false;
        }
        tokenizer->nextToken();
    } else if (isGeneName(tokenizer->currentToken())) {
        rpn_expression.push_back(tokenizer->currentToken());
        tokenizer->nextToken();
    } else {
        return false;
    }

    if (!tokenizer->hasRemainingTokens() ||
            tokenizer->currentToken() == kRightBracket)
       return true;

    if (!isOperatorToken(tokenizer->currentToken()))
        return false;

    if (!expected_operator.empty() &&
            tokenizer->currentToken() != expected_operator)
        return false;
    else
        expected_operator = tokenizer->currentToken();

    tokenizer->nextToken();

    if (!transformExpressionToRPN(tokenizer, expected_operator))
        return false;

    rpn_expression.push_back(expected_operator);
    return true;
}

bool GeneExpression::transformToRPN(StringTokenizer* tokenizer) {
    tokenizer->moveToFirstToken();

    if (!tokenizer->hasRemainingTokens())
        return true;
    if (!transformExpressionToRPN(tokenizer, ""))
        return false;
    return !tokenizer->hasRemainingTokens();
}

bool GeneExpression::geneValue(const string& gene,
        const set<string>& disabledGenes) const {
    return disabledGenes.find(gene) == disabledGenes.end();
}

bool GeneExpression::evaluate(bool val1, const string& oper,
        bool val2) const {
    if (oper == kAndToken)
        return val1 && val2;
    else if (oper == kOrToken)
        return val1 || val2;
    else
        assert(false);
}

bool GeneExpression::evaluate(const set<string>& disabledGenes) const {
    stack<bool> rpn_stack;

    if (rpn_expression.empty())
        return true;

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

void GeneExpression::getAllGenes(set<string>* genes) {
    for (unsigned i = 0; i < rpn_expression.size(); i++) {
        if (isGeneName(rpn_expression[i]))
            genes->insert(rpn_expression[i]);
    }
}
