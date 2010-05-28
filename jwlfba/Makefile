CPPFLAGS=-g
LIBS=-lsbml -lglpk

sources=MetabolicSimulation.cpp GeneExpression.cpp ModelBuilder.cpp
objects=$(sources:.cpp=.o)

%.o: %.cpp %.h
	g++ -Wall -c $< -o $@

jwlfba: $(objects)
	g++ $(LIBS) $(CPPFLAGS) main.cpp $^ -o $@

TEST_LIBS=-lboost_unit_test_framework

test_sources=test/StringTokenizerTest.cpp test/GeneExpressionTest.cpp test/main.cpp
test_objects=$(test_sources:.cpp=.o)

jwlfba_tests: $(objects) $(test_objects)
	g++ $(LIBS) $(TEST_LIBS) $(CPPFLAGS) $^ -o $@
