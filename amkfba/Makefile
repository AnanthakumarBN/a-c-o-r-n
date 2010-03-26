CPP=g++
CPPFLAGS=-O2 -Wall
OBJ=amkfba.o GlpkKernel.o ReactionSTACK.o SimKernel.o TokenSTACK.o

all: $(OBJ)
	$(CPP) -o amkfba $(OBJ) -lglpk

%.o: %.c
	$(CPP) $(CPPFLAGS) -c $<

clean:
	rm -f amkfba *~ *.o *.bak core
