#include"MetabolicSimulation.h"
#include"ModelBuilder.h"
#include<sbml/SBMLTypes.h>
#include<iostream>
#include<cstdio>
using namespace std;

int main(int argc, char* argv[])
{
/*    SBMLDocument* document = readSBML(argv[1]);
    unsigned int errors = document->getNumErrors();

    cout << endl;
    cout << "  filename: " << argv[1] << endl;
    cout << "  error(s): " << errors  << endl;
    cout << endl;

    if (errors > 0) document->printErrors(cerr);*/

    ModelBuilder mb;
    FileLineReader rd;
    rd.loadFile(argv[1]);
    Model* mod = mb.loadFromAmkfbaFile(&rd);

    if (mod == NULL)
        printf("%s\n", mb.getError().c_str());

    MetabolicSimulation ms;

    ms.loadModel(mod);
    ms.setObjective("BIOMASS2");
    ms.runSimulation();

    printf("%lf\n", ms.getObjectiveFunctionValue());
}

/*
-1        1
-1        1
 1  -1  
       
        -17
*/

