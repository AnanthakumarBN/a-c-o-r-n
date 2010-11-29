CPPFLAGS=-O2

sources=pugixml.cpp

objects=$(sources:.cpp=.o)

all: $(objects)
	
%.o: %.cpp
	g++ -Wall $(CPPFLAGS) -c $< -o $@

clean:
	rm -f *.o
