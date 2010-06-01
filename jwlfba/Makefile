CPPFLAGS=-O2
LIBS=-lsbml -lglpk

sources=MetabolicSimulation.cpp GeneExpression.cpp ModelBuilder.cpp FileLineReader.cpp StringTokenizer.cpp SimulationController.cpp InputParameters.cpp

objects=$(sources:.cpp=.o)

%.o: %.cpp %.h
	g++ -Wall $(CPPFLAGS) -c $< -o $@

jwlfba: $(objects) main.cpp
	g++ $(LIBS) $(CPPFLAGS) $^ -o $@

TEST_LIBS=-lboost_unit_test_framework

test_sources=test/StringTokenizerTest.cpp test/GeneExpressionTest.cpp test/main.cpp
test_objects=$(test_sources:.cpp=.o)

jwlfba_tests: $(objects) $(test_objects)
	g++ $(LIBS) $(TEST_LIBS) $(CPPFLAGS) $^ -o $@
