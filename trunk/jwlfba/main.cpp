#include"MetabolicSimulation.h"
#include<sbml/SBMLTypes.h>
#include<iostream>
#include<cstdio>
using namespace std;

int main(int argc, char* argv[])
{
    SBMLDocument* document = readSBML(argv[1]);
    unsigned int errors = document->getNumErrors();

    cout << endl;
    cout << "  filename: " << argv[1] << endl;
    cout << "  error(s): " << errors  << endl;
    cout << endl;

    if (errors > 0) document->printErrors(cerr);

    MetabolicSimulation ms;

    ms.loadModel(document->getModel());
    ms.setObjective("R_biomass_SC4_bal");
    ms.runSimulation();

    printf("%lf\n", ms.getObjectiveFunctionValue());
}

/*
-1        1
-1        1
 1  -1  
       
        -17
*/

