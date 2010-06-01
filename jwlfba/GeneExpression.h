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

class StringTokenizer;

class GeneExpression {
 private:
    vector<string> rpn_expression;
    static const char* kLeftBracket;
    static const char* kRightBracket;

    bool isGeneName(const string& gene) const;
    bool isOperatorToken(const string& token) const;

    bool evaluateExpression();
    bool geneValue(const string& token,
            const set<string>& disabled_genes) const;
    bool evaluate(bool val1, const string& oper, bool val2) const;
    bool transformToRPN(StringTokenizer* tokenizer);
    bool transformExpressionToRPN(StringTokenizer* tokenizer,
            string expected_operator);
 public:
    static const char* kAndToken;
    static const char* kOrToken;
    static const char* kGeneExpressionPrefix;
    bool looksLikeGeneExpression(const string& expr) const;
    bool evaluate(const set<string>& disabled_genes) const;
    bool loadExpression(const string& expr);
    void getAllGenes(set<string>* genes);
};

#endif  // JWLFBA_GENEEXPRESSION_H_
