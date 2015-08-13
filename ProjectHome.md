## Acorn ##

Acorn is a web server for constraint based modeling of genome scale metabolic reaction networks. After signing in users can run Flux Balance Analysis simulations on multiple models loaded by an administrator from SBML files. Initial conditions of computer simulations (reaction bounds, objective function) are stored and can be shared with other users. Models and results are displayed in tables with genes linked to genome information portals, but pathway visualisation will soon be implemented. The server is capable of handling multiple users and iterative FBA simulations by using clusters set up in GlassFish environment. Currently acorn supports the following simulation protocols:

  1. Single FBA optimisation
  1. Flux Variability Analysis
  1. Reaction Essentiality Scan
  1. Single gene knock-out

Acorn uses [GLPK](http://www.gnu.org/software/glpk/) library for linear programming and [libSML](http://sbml.org/Software/libSBML) library for SBML file and data streams.


## Using Acorn ##

An installation of Acorn with is publicly accessible at [University of Surrey](http://sysbio3.fhms.surrey.ac.uk:8080/acorn/homepage.jsf).

For instructions on how to deploy Acorn at you own servers see [Acorn\_Installation\_on\_Remote\_Server](Acorn_Installation_on_Remote_Server.md).

## Research paper ##
We published a research paper in the BMC Bioinformatics journal (input factor 3.43). It is available for open access [here](http://www.biomedcentral.com/1471-2105/12/196) and has qualified to be marked as 'Highly accessed'. For your convenience here is the [bibtex entry](http://dblp.uni-trier.de/rec/bibtex/journals/bmcbi/SrokaBGLLMABMK11) you can use to cite it.