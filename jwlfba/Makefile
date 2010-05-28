CPPFLAGS=-g
LIBS=-lsbml -lglpk

sources=MetabolicSimulation.cpp GeneExpression.cpp main.cpp 
objects=$(sources:.cpp=.o)

%.o: %.cpp %.h
	g++ -Wall -c $< -o $@

jwlfba: $(objects)
	g++ $(LIBS) $(CPPFLAGS) $^ -o $@
