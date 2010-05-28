#include <boost/test/unit_test.hpp>
#include"../GeneExpression.h"

class StringTokenizerTestFixture {
};

BOOST_FIXTURE_TEST_SUITE(Test1, StringTokenizerTestFixture);

BOOST_AUTO_TEST_CASE(OneTokenTest) {
    StringTokenizer st;
    st.parse("abc");
    BOOST_CHECK_EQUAL(st.currentToken(), "abc");
    st.nextToken();
    BOOST_CHECK(!st.tokensRemaining());
}

BOOST_AUTO_TEST_CASE(WhitespacesTest) {
    StringTokenizer st;
    st.parse("  123\td ");
    BOOST_CHECK_EQUAL(st.currentToken(), "123");
    st.nextToken();
    BOOST_CHECK_EQUAL(st.currentToken(), "d");
    st.nextToken();
    BOOST_CHECK(!st.tokensRemaining());
}

BOOST_AUTO_TEST_CASE(MoveToFirstTest) {
    StringTokenizer st;
    st.parse("x y z");
    BOOST_CHECK_EQUAL(st.currentToken(), "x");
    st.nextToken();
    BOOST_CHECK_EQUAL(st.currentToken(), "y");
    st.moveToFirstToken();
    BOOST_CHECK_EQUAL(st.currentToken(), "x");
    st.nextToken();
    BOOST_CHECK_EQUAL(st.currentToken(), "y");
    st.nextToken();
    BOOST_CHECK_EQUAL(st.currentToken(), "z");
    st.nextToken();
    BOOST_CHECK(!st.tokensRemaining());
    st.moveToFirstToken();
    BOOST_CHECK_EQUAL(st.currentToken(), "x");
}

BOOST_AUTO_TEST_CASE(ReadingPastEndTest) {
    StringTokenizer st;
    st.parse("token1 token2");
    BOOST_CHECK(st.tokensRemaining());
    BOOST_CHECK_EQUAL(st.currentToken(), "token1");
    st.nextToken();
    BOOST_CHECK_EQUAL(st.currentToken(), "token2");
    BOOST_CHECK(st.tokensRemaining());
    st.nextToken();
    BOOST_CHECK_EQUAL(st.currentToken(), "");
}

BOOST_AUTO_TEST_CASE(EmptyString) {
    StringTokenizer st;
    st.parse("");
    BOOST_CHECK_EQUAL(st.currentToken(), "");
    BOOST_CHECK(!st.tokensRemaining());
}

BOOST_AUTO_TEST_SUITE_END();
