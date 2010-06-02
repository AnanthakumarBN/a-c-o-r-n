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

// Represents a gene association of a reaction, which is a logical expression,
// consisting of gene names, and/or operators and brackets.
// For example:
// GENE_ASSOCIATION: ( YML20FN and NNM13YB ) or YYMF

class GeneExpression {
 private:
    // The expression converted to reverse polish notation
    // for easier evaluation.
    vector<string> rpn_expression_;

    // Tokens corresponding to brackets.
    static const char* kLeftBracket;
    static const char* kRightBracket;

    // Classifiers for tokens.
    bool IsGeneName(const string& gene) const;
    bool IsOperatorToken(const string& token) const;

    // Returns true iff given gene is not disabled, that is it's logical
    // value is true.
    bool GeneValue(const string& token,
            const set<string>& disabled_genes) const;

    // Returns the value of applying operator oper to two values.
    bool Evaluate(bool val1, const string& oper, bool val2) const;

    // Load the given expression to rpn_expression_ field.
    bool TransformToRPN(StringTokenizer* tokenizer);

    // Auxilary function, which loads the expression, assuming that it
    // has to be a list of genes connected by 'exprected_operator'.
    // "" denotes any operator.
    // Returns true iff the expression is correct.
    bool TransformExpressionToRPN(StringTokenizer* tokenizer,
            string expected_operator);
 public:
    static const char* kAndToken;
    static const char* kOrToken;
    static const char* kGeneExpressionPrefix;

    // Returns true iff expr starts with kGeneExpressionPrefix
    bool LooksLikeGeneExpression(const string& expr) const;

    // Evaluates the expression for a given set of disabled genes.
    bool Evaluate(const set<string>& disabled_genes) const;

    // Initializes the object with the given gene expression. Returns true
    // iff the expression has correct syntax.
    bool LoadExpression(const string& expr);

    // Returns a list of all genes in the expression.
    void GetAllGenes(set<string>* genes);
};

#endif  // JWLFBA_GENEEXPRESSION_H_
