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
    vector<string> rpn_expression_;
    static const char* kLeftBracket;
    static const char* kRightBracket;

    bool IsGeneName(const string& gene) const;
    bool IsOperatorToken(const string& token) const;

    bool GeneValue(const string& token,
            const set<string>& disabled_genes) const;
    bool Evaluate(bool val1, const string& oper, bool val2) const;
    bool TransformToRPN(StringTokenizer* tokenizer);
    bool TransformExpressionToRPN(StringTokenizer* tokenizer,
            string expected_operator);
 public:
    static const char* kAndToken;
    static const char* kOrToken;
    static const char* kGeneExpressionPrefix;
    bool LooksLikeGeneExpression(const string& expr) const;
    bool Evaluate(const set<string>& disabled_genes) const;
    bool LoadExpression(const string& expr);
    void GetAllGenes(set<string>* genes);
};

#endif  // JWLFBA_GENEEXPRESSION_H_
