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

bool GeneExpression::LooksLikeGeneExpression(const string& expr) const {
    if (expr.empty())
        return false;

    return expr.compare(0, sizeof(kGeneExpressionPrefix),
            kGeneExpressionPrefix, sizeof(kGeneExpressionPrefix)) == 0;
}

bool GeneExpression::LoadExpression(const string& expr) {
    StringTokenizer tokenizer;
    tokenizer.Parse(expr);

    if (tokenizer.CurrentToken() != kGeneExpressionPrefix)
       return false;

    tokenizer.NextToken();
    rpn_expression_.clear();
    return TransformToRPN(&tokenizer);
}

bool GeneExpression::IsGeneName(const string& gene) const {
    return !gene.empty() && gene != kAndToken && gene != kOrToken &&
        gene != kLeftBracket && gene != kRightBracket;
}

bool GeneExpression::IsOperatorToken(const string& token) const {
    return token == kAndToken || token == kOrToken;
}

bool GeneExpression::TransformExpressionToRPN(StringTokenizer* tokenizer,
        string expected_operator) {
    if (tokenizer->CurrentToken() == kLeftBracket) {
        tokenizer->NextToken();
        if (!TransformExpressionToRPN(tokenizer, "")
                || tokenizer->CurrentToken() != kRightBracket) {
            return false;
        }
        tokenizer->NextToken();
    } else if (IsGeneName(tokenizer->CurrentToken())) {
        rpn_expression_.push_back(tokenizer->CurrentToken());
        tokenizer->NextToken();
    } else {
        return false;
    }

    if (!tokenizer->HasRemainingTokens() ||
            tokenizer->CurrentToken() == kRightBracket)
       return true;

    if (!IsOperatorToken(tokenizer->CurrentToken()))
        return false;

    if (!expected_operator.empty() &&
            tokenizer->CurrentToken() != expected_operator)
        return false;
    else
        expected_operator = tokenizer->CurrentToken();

    tokenizer->NextToken();

    if (!TransformExpressionToRPN(tokenizer, expected_operator))
        return false;

    rpn_expression_.push_back(expected_operator);
    return true;
}

bool GeneExpression::TransformToRPN(StringTokenizer* tokenizer) {
    tokenizer->MoveToFirstToken();

    if (!tokenizer->HasRemainingTokens())
        return true;
    if (!TransformExpressionToRPN(tokenizer, ""))
        return false;
    return !tokenizer->HasRemainingTokens();
}

bool GeneExpression::GeneValue(const string& gene,
        const set<string>& disabledGenes) const {
    return disabledGenes.find(gene) == disabledGenes.end();
}

bool GeneExpression::Evaluate(bool val1, const string& oper,
        bool val2) const {
    if (oper == kAndToken)
        return val1 && val2;
    else if (oper == kOrToken)
        return val1 || val2;
    else
        assert(false);
}

bool GeneExpression::Evaluate(const set<string>& disabledGenes) const {
    stack<bool> rpn_stack;

    if (rpn_expression_.empty())
        return true;

    for (unsigned i = 0; i < rpn_expression_.size(); i++) {
        if (IsOperatorToken(rpn_expression_[i])) {
            assert(rpn_stack.size() >= 2);

            bool x, y;
            x = rpn_stack.top();
            rpn_stack.pop();
            y = rpn_stack.top();
            rpn_stack.pop();

            rpn_stack.push(Evaluate(x, rpn_expression_[i], y));
        } else {
            rpn_stack.push(GeneValue(rpn_expression_[i], disabledGenes));
        }
    }
    assert(rpn_stack.size() == 1);
    return rpn_stack.top();
}

void GeneExpression::GetAllGenes(set<string>* genes) {
    for (unsigned i = 0; i < rpn_expression_.size(); i++) {
        if (IsGeneName(rpn_expression_[i]))
            genes->insert(rpn_expression_[i]);
    }
}
