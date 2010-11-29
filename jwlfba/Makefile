CPPFLAGS=-O2
LIBS=-lsbml -lglpk

sources=MetabolicSimulation.cpp GeneExpression.cpp ModelBuilder.cpp FileLineReader.cpp StringTokenizer.cpp SimulationController.cpp InputParameters.cpp ModelDatabase.cpp TextInterface.cpp XMLTaskProcesser.cpp

objects=$(sources:.cpp=.o)

%.o: %.cpp %.h
	g++ -Wall -Wshadow $(CPPFLAGS) -c $< -o $@

jwlfba: $(objects) lib/pugixml.o main.cpp
	g++ $(LIBS) $(CPPFLAGS) $^ -o $@

lib/pugixml.o:
	$(MAKE) -C lib

clean:
	rm -f *.o
	$(MAKE) -C lib clean
	rm -f jwlfba
	rm -f jwlfba_tests

TEST_LIBS=-lboost_unit_test_framework

test_sources=test/StringTokenizerTest.cpp test/GeneExpressionTest.cpp test/main.cpp
test_objects=$(test_sources:.cpp=.o)

jwlfba_tests: $(objects) lib/pugixml.o $(test_objects)
	g++ $(LIBS) $(TEST_LIBS) $(CPPFLAGS) $^ -o $@
