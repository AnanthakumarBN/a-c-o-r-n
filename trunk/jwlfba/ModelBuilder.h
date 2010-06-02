/*
 * Copyright 2010 Jakub Łącki
 */

#ifndef JWLFBA_MODELBUILDER_H_
#define JWLFBA_MODELBUILDER_H_

#include<string>
#include<set>
#include<cstdio>
using std::string;
using std::set;

class Model;
class Reaction;
class StringTokenizer;
class LineReader;

// Builds a SBML Model from a given file in amkfba format.
// It is a file consisting of a number of lines in the following format:
// Coefficient ::= <double>
// LowerBound ::= <double>
// UpperBound ::= <double>
// ReactionId ::= <string>
// GeneExpression ::= <string>
// Stoichiometry ::= Coefficient <string>
// ListOfReactants ::= Stoichiometry ("+" Stoichiometry)+
// ListOfProducts ::= Stoichiometry ("+" Stoichiometry)+

// Reaction ::= ReactionId ListOfReactants "=" ListOfProducts "|" "#"
//      LowerBound UpperBound "<GENES>" GeneExpression "</GENES>"
//
// External metabolites are recognized by the fact that their id
// ends with "_xt".
//
// Note that because some strings are not valid SBML ids, they need to be
// encoded with a smaller character set. An id can be decoded
// with DecodeSBMLId.
// For a given Model it is posssible to check if its ids have been encoded
// using IsAmkfbaModel static method.

class ModelBuilder {
 private:
    // Description of the last syntax error encountered.
    string error_description_;

    // Set of all species in the model built during the parsing.
    set<string> all_species_;

    // Tokens appearing in the amkfba file format
    static const char* kReactantsProductsSeparator;
    static const char* kReactionEndToken;
    static const char* kSpeciesSeparator;
    static const char* kBoundsStartToken;
    static const char* kExternalMetaboliteSuffix;
    static const char* kGenesBeginToken;
    static const char* kGenesEndToken;
    static const char* kAmkfbaAndToken;
    static const char* kAmkfbaOrToken;

    static const char* kLowerBoundParameterId;
    static const char* kUpperBoundParameterId;

    // Adds species with a given id to the model.
    void AddSpecies(Model* model, const string& species);

    // Get a Stoichiometry element. Returns true iff it has a correct syntax.
    bool GetStoichiometry(StringTokenizer* st, double* coefficient,
            string* species);

    // Retrieves a list of reactants and adds them to the model. Returns true
    // iff their sytax is correct.
    bool AddReactants(Model* model, StringTokenizer* st);

    // Retrieves a list of products and adds them to the model. Returns true
    // iff their sytax is correct.
    bool AddProducts(Model* model, StringTokenizer* st);

    // Retrieves a lower and upper bound and adds them to the reaction.
    // Returns true iff their sytax is correct.
    bool AddBounds(Reaction* reaction, StringTokenizer* st);

    // Retrieves a reaction and adds it to the model. Returns true iff its
    // sytax is correct.
    bool AddReaction(Model* model, StringTokenizer* st);

    // Retrieves a gene expression and adds it to the model. Returns true iff
    // its sytax is correct.
    bool AddGenes(Reaction* reaction, StringTokenizer* st);

    // Checks whether the character can be used in a SBML id
    static bool IsValidSBMLDIdChar(char c);

    // Encodes char to a string of valid SBML characters
    static string EncodeChar(char c);

    // Reports a sytnax error.
    void Error(const string& err);
 public:
    // Encodes a given string to a string, which can be used as a SBML id.
    static string CreateValidSBMLId(const string& sid);

    // Decodes a string coded with the above function.
    static string DecodeSBMLId(const string& id);

    // Value set in the notes property of the model, if it has been created
    // by this builder.
    static const char* kCreatedFromAmkfbaFile;

    // Returns true if the model has been created by this builder.
    static bool IsAmkfbaModel(const Model* model);

    // Returns the last syntax error encountered.
    string GetError() const;

    // Returns a model built from the given description. Caller is responsible
    // for deallocating it.
    Model* LoadFromAmkfbaFile(LineReader* reader);
};

#endif  // JWLFBA_MODELBUILDER_H_
