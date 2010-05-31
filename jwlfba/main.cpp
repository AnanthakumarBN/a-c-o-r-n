#include"MetabolicSimulation.h"
#include"ModelBuilder.h"
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

/*    ModelBuilder mb;
    FileLineReader rd;
    rd.loadFile(argv[1]);
    Model* mod = mb.loadFromAmkfbaFile(&rd);*/
    Model* mod = document->getModel();

//    if (mod == NULL)
 //       printf("%s\n", mb.getError().c_str());

    MetabolicSimulation ms;

    bool r2, r1 = ms.loadModel(mod);
    for (int i=0; i<ms.getErrors().size(); i++)
        printf("%s\n", ms.getErrors()[i].c_str());

   // r2 = ms.setObjective("R_13DPGt");
    printf("%d %d\n", (int)r1, (int)r2);
    ms.runSimulation();

    printf("%lf\n", ms.getObjectiveFunctionValue());
}

