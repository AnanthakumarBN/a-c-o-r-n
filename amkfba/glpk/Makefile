OBJ=glpavl.o glpdmp.o glpiet.o glpinv.o glpios1.o glpios2.o glpios3.o glpipm.o glplib1a.o glplib1b.o glplib2.o glplib3.o glplpp1.o glplpp2.o glplpx1.o glplpx2.o glplpx3.o glplpx4.o glplpx5.o glplpx6a.o glplpx6b.o glplpx6d.o glplpx7.o glplpx8a.o glplpx8b.o glplpx8d.o glplpx8e.o glpluf.o glpmat.o glpmip1.o glpmip2.o glpmpl1.o glpmpl2.o glpmpl3.o glpmpl4.o glpqmd.o glprng.o glpspx1.o glpspx2.o glpstr.o glptsp.o
CFLAGS=
CPP=g++

all: $(OBJ)
	ar rc libglpkak.a $(OBJ)
	ranlib libglpkak.a

.cpp:
	$(CPP) $(CFLAGS) -c $<

clean:
	rm -f libglpkak.a *~ *.o *.bak core
