CPP=g++
CPPFLAGS=-O2
OBJ=amkfba.o GlpkKernel.o ReactionSTACK.o SimKernel.o TokenSTACK.o

LIBGLPK=glpk/libglpkak.a

all: $(LIBGLPK) $(OBJ)
	$(CPP) -o amkfba -L./glpk $(OBJ) -lglpkak

$(LIBGLPK):
	cd glpk; make

.cpp:
	$(CPP) $(CPPFLAGS) -c $<

clean:
	rm -f amkfba *~ *.o *.bak core
	cd glpk; make clean
