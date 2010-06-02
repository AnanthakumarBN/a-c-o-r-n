#include <boost/test/unit_test.hpp>
#include"../StringTokenizer.h"

class StringTokenizerTestFixture {
};

BOOST_FIXTURE_TEST_SUITE(Test1, StringTokenizerTestFixture);

BOOST_AUTO_TEST_CASE(OneTokenTest) {
    StringTokenizer st;
    st.Parse("abc");
    BOOST_CHECK_EQUAL(st.CurrentToken(), "abc");
    st.NextToken();
    BOOST_CHECK(!st.HasRemainingTokens());
}

BOOST_AUTO_TEST_CASE(WhitespacesTest) {
    StringTokenizer st;
    st.Parse("  123\td ");
    BOOST_CHECK_EQUAL(st.CurrentToken(), "123");
    st.NextToken();
    BOOST_CHECK_EQUAL(st.CurrentToken(), "d");
    st.NextToken();
    BOOST_CHECK(!st.HasRemainingTokens());
}

BOOST_AUTO_TEST_CASE(MoveToFirstTest) {
    StringTokenizer st;
    st.Parse("x y z");
    BOOST_CHECK_EQUAL(st.CurrentToken(), "x");
    st.NextToken();
    BOOST_CHECK_EQUAL(st.CurrentToken(), "y");
    st.MoveToFirstToken();
    BOOST_CHECK_EQUAL(st.CurrentToken(), "x");
    st.NextToken();
    BOOST_CHECK_EQUAL(st.CurrentToken(), "y");
    st.NextToken();
    BOOST_CHECK_EQUAL(st.CurrentToken(), "z");
    st.NextToken();
    BOOST_CHECK(!st.HasRemainingTokens());
    st.MoveToFirstToken();
    BOOST_CHECK_EQUAL(st.CurrentToken(), "x");
}

BOOST_AUTO_TEST_CASE(ReadingPastEndTest) {
    StringTokenizer st;
    st.Parse("token1 token2");
    BOOST_CHECK(st.HasRemainingTokens());
    BOOST_CHECK_EQUAL(st.CurrentToken(), "token1");
    st.NextToken();
    BOOST_CHECK_EQUAL(st.CurrentToken(), "token2");
    BOOST_CHECK(st.HasRemainingTokens());
    st.NextToken();
    BOOST_CHECK_EQUAL(st.CurrentToken(), "");
}

BOOST_AUTO_TEST_CASE(EmptyString) {
    StringTokenizer st;
    st.Parse("");
    BOOST_CHECK_EQUAL(st.CurrentToken(), "");
    BOOST_CHECK(!st.HasRemainingTokens());
}

BOOST_AUTO_TEST_SUITE_END();
