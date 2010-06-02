#include <boost/test/unit_test.hpp>
#include<cstdio>
#include<string>
#include<set>
#include"../GeneExpression.h"

using std::string;
using std::set;

const string kPrefix = "GENE_ASSOCIATION: ";

class GeneExpressionTestFixture : public GeneExpression {
};

BOOST_FIXTURE_TEST_SUITE(Test2, GeneExpressionTestFixture);

BOOST_AUTO_TEST_CASE(EmptyGene) {
    GeneExpression exp;
    set<string> disabled;
    BOOST_CHECK(exp.LoadExpression(kPrefix));
    BOOST_CHECK(exp.Evaluate(disabled));
}

const char* kIncorrectDescriptions[] = {
    "(",
    ")",
    "and",
    "or",
    "ABC and XYZ or DEF",
    "( ABC",
    "ABC )",
    "and DEF",
    "( ABC or DEF)",
    "( ABC or DEF ) and ( XYZ and PQR ) or MM",
    "ABC or DEF or",
    "ABC DEF GHI"
};

BOOST_AUTO_TEST_CASE(IncorrectDescriptions) {
    GeneExpression exp;

    for (int i = 0; i < sizeof(kIncorrectDescriptions)/sizeof(char*); i++) {
        BOOST_CHECK(!exp.LoadExpression(kPrefix + kIncorrectDescriptions[i]));
    }
}

BOOST_AUTO_TEST_CASE(EvaluationTest) {
    set<string> disabled;
    GeneExpression exp;

    BOOST_CHECK(exp.LoadExpression(kPrefix + "ABC"));
    BOOST_CHECK(exp.Evaluate(disabled));

    BOOST_CHECK(exp.LoadExpression(kPrefix + "ABC or DEF"));
    BOOST_CHECK(exp.Evaluate(disabled));

    BOOST_CHECK(exp.LoadExpression(kPrefix + "PQR or XYZ"));
    BOOST_CHECK(exp.Evaluate(disabled));

    disabled.insert("ABC");

    BOOST_CHECK(exp.LoadExpression(kPrefix + "( ABC or DEF )"));
    BOOST_CHECK(exp.Evaluate(disabled));

    BOOST_CHECK(exp.LoadExpression(kPrefix + "( ABC and DEF )"));
    BOOST_CHECK(!exp.Evaluate(disabled));

    BOOST_CHECK(exp.LoadExpression(kPrefix + "( ABC and DEF ) or ABC"));
    BOOST_CHECK(!exp.Evaluate(disabled));

    BOOST_CHECK(exp.LoadExpression(kPrefix + "( ABC or PQR ) and ( ABC and DEF )"));
    BOOST_CHECK(!exp.Evaluate(disabled));

    BOOST_CHECK(exp.LoadExpression(kPrefix + "( ABC or PQR ) or ( ABC and DEF )"));
    BOOST_CHECK(exp.Evaluate(disabled));

    disabled.insert("DEF");

    BOOST_CHECK(exp.LoadExpression(kPrefix + "( ABC or DEF ) and ( PRS or PTM )"));
    BOOST_CHECK(!exp.Evaluate(disabled));

    BOOST_CHECK(exp.LoadExpression(kPrefix + 
                "( ABC or MNT or DEF ) and ( PRS or PTM ) and ( DEF or XYZ )"));
    BOOST_CHECK(exp.Evaluate(disabled));
}

BOOST_AUTO_TEST_SUITE_END();
