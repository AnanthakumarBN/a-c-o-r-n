#include <stdio.h>
#include <stdlib.h>
#include "ReactionSTACK.h"
#include "TokenSTACK.h"
#include "GlpkKernel.h"

#define MAXLINES 5000
#define MAXCHARS 20000
#define MAXROWS 5000
#define MAXCOLS 50000
#define LBMIN  -1e9
#define UBMAX  1e9
#define MAXGENES 10000
#define TP 1
#define FP 2
#define TN 3
#define FN 4
#define OPT_FAILED 1111


class GeneLPX 
{
	private:
	  char name[100];
	  int reactions[MAXCOLS];
	  int affectedReactions[MAXCOLS];
	  int rnum;
	  int affectedRNum;
	  int essential;
	  int included;
	  
	public:
	  GeneLPX() 
	  {
	  	rnum = 0;
	  	affectedRNum = 0;
	  	essential = 0;
	  	included = 0;
	  }
	  char *getName() 
	  {
	  	return(name);
	  }
	  void getName(char *str)
	  {
	  	int i;
	  	for(i=0;name[i]!=0;i++) str[i]=name[i];
	  	str[i]=0;
	  }
	  void setName(char *str) 
	  {
	  	int i;
	  	for(i=0;str[i]!=0;i++) name[i]=str[i];
	  	name[i]=0;
	  }
	  int getRNum() 
	  {
	  	return(rnum);
	  }
	  int getReaction(int i) 
	  {
	  	return(reactions[i]);
	  }
	  int getAffectedReactionsNum()
	  {
	  	return(affectedRNum);
	  }
	  int getAffectedReaction(int i)
	  {
	  	return(affectedReactions[i]);
	  }
	  void addReaction(int r)
	  {
	  	reactions[rnum] = r;
	  	rnum++;
	  }
	  void addAffectedReaction(int r)
	  {
	  	affectedReactions[affectedRNum]=r;
	  	affectedRNum++;
	  }
	  void setEssential() 
	  {
	  	essential = 1;
	  }
	  void setNonEssential()
	  {
	  	essential = 0;
	  }
	  void setIncluded()
	  {
	  	included = 1;
	  }
	  void setExcluded()
	  {
	  	included =0;
	  }
	  int isIncluded() 
	  {
	  	return(included);
	  }
	  int isEssential() 
	  {
	  	return(essential);
	  }
};

class ReactionLPX 
{
	private:
	  char name[100];
	  int genes[1000];
	  int gNum;
	  
	public:
	  char *getName() 
	  {
	  	return(name);
	  }
	  void getName(char *str)
	  {
	  	int i;
	  	for(i=0;name[i]!=0;i++) str[i]=name[i];
	  	str[i]=0;
	  }
	  void setName(char *str) 
	  {
	  	int i;
	  	for(i=0;str[i]!=0;i++) name[i]=str[i];
	  	name[i]=0;
	  }
	  int *getGenes() 
	  {
	  	return(genes);
	  }
	  void getGenes(int *vec) 
	  {
	    int i;
	  	for(i=0;i<=genes[0];i++) vec[i] = genes[i];
	  }
	  void setGenes(int *vec) 
	  {
	  	int i;
	  	for(i=0;i<=vec[0];i++) genes[i] = vec[i];
	  }
	  int getGNum() 
	  {
	  	return(gNum);
	  }
};

class SubstanceLPX 
{
    private:
	  char name[1000];
	  int external;
	  
	public:
	  char *getName() 
	  {
	  	return(name);
	  }
	  void getName(char *str)
	  {
	  	int i;
	  	for(i=0;name[i]!=0;i++) str[i]=name[i];
	  	str[i]=0;
	  }
	  void setName(char *str) 
	  {
	  	int i;
	  	for(i=0;str[i]!=0;i++) name[i]=str[i];
	  	name[i]=0;
	  }
	  int isExternal()
	  {
	  	return(external);
	  }
	  int classify() 
	  {
	    int i;
	  	for(i=2;name[i]!=0;i++);
	  	if(name[i-1]=='t' && name[i-2]=='x') 
	  	{
	  		external = 1;
	  		return(1);
	  	}
	  	external = 0;
	  	return(0);
	  }
};

class Bound 
{
   public:
	 int type;
	 double lb;
	 double ub;
	 Bound() 
	 {
	 	type = LPX_FX;
	 	lb = 0;
	 	ub = 0;
	 }
	 Bound(int t,double l, double u)
	 {
	 	type = t;
	 	lb = l;
	 	ub = u;
	 }
};

class FALikeFile 
{
	private:
	  char input[MAXLINES][MAXCHARS];
	  int size;
	  int ne, ia[MAXCOLS], ja[MAXCOLS];
	  double ar[MAXCOLS];
	  int getFile(FILE *plik);
	  int index(GeneLPX *genes[],char *str);
	  int index(ReactionLPX *reactions[],char *str);
	  int index(SubstanceLPX *substances[], char *str);
	  void newReaction(TokenSTACK *parser,GlpkKernel *system, ReactionLPX *reactions[], SubstanceLPX *substances[], Bound *rBounds[], Bound *sBounds[]);
      
           
	public:
	  void printSystem(GlpkKernel *system, GeneLPX *genes[], ReactionLPX *reactions[], SubstanceLPX *substances[]);
	  void Load(char *path, GlpkKernel *system, ReactionLPX *reactions[], SubstanceLPX *substances[], Bound *rBounds[], Bound *sBounds[]);
	  void reload(GlpkKernel *system, ReactionLPX *reactions[], SubstanceLPX *substances[]);
	  void LoadGenes(GeneLPX *genes[],ReactionLPX *reactions[]);
	  void PrintInput();
	  int sNum,gNum,rNum;
	  int cmpr(char *str1, char *str2);
	  int isnumber(char *str);
	  FALikeFile() 
	  {
	  	sNum=0; gNum=0; rNum=0; ne=0;
	  }
	  char* getInputLine(int i) 
	  {
	  	return(input[i]);
	  }
	  
};


class SimKernelLPX
{
	private:
	  FALikeFile *fafile;
	  GeneLPX *genes[MAXGENES];
	  int gNum;
	  ReactionLPX *reactions[MAXCOLS];
	  int rNum;
	  SubstanceLPX *substances[MAXROWS];
	  int sNum;
	  Bound *rBounds[MAXCOLS];
	  Bound *sBounds[MAXCOLS];
	  int resNum;
	  GlpkKernel *glpk;
	  char objName[1000];
	  int objIdx;
	  double doubleResults[MAXCOLS];
	  int intResults[MAXCOLS];
	  int intResults1[MAXCOLS];
	  int fpscanSize;
	  int status;
	  int retcode;
	  int inclgenelist;
	 
	  
	public:
	  int getResNum() 
	  {
	  	return(resNum);
	  }
	  void loadMPS(char *path);
	  void loadFALike(char *path);
	  void loadGenes();
	  double optimisation(int d);
	  void printFAFile();
	  void printReactionRPNs();
	  void printGeneEssentialityScan();
	  int setObjective(char *str);
	  void setObjective();
	  void setObjective(int i);
	  void setObjectiveFlux(int f);
	  int reactionIdx(char *str);
	  int substanceIdx(char *str);
	  int geneIdx(char *str);
	  int cmpr(char *str1, char *str2);
	  void setExternalMetabolite(char *mname);
	  void geneEssentialityTest();
	  
	  double optimisation(char *str, int d) 
	  {
	  	setObjective(str);
	  	return(optimisation(d));
	  }
	  void findAffectedReactions();
	  void getReactionBound(int i,Bound *b) 
	  {
	  	b->type = glpk->get_col_type(i);
	  	b->lb   = glpk->get_col_lb(i);
	  	b->ub   = glpk->get_col_ub(i);
	  }
	  void setReactionBound(int i,Bound *b) 
	  {
	  	glpk->set_col_bnds(i,b->type,b->lb,b->ub);
	    setRbound(i,b);
	  }
	  void setReactionBound(int i,int type,double lb, double ub) 
	  {
	  	glpk->set_col_bnds(i,type,lb,ub);
	  	rBounds[i]->type = type;
	  	rBounds[i]->lb = lb;
	  	rBounds[i]->ub = ub;
	  }
	  
	  void printPrimalSolution();
	  
	  int getStatus()
	  {
	  	return(status);
	  }
	  void setEssential(char *str);
	  void setIncluded(char *str);
	  void printMetaboliteList() 
	  {
	  	int i;
	  	for(i=1;i<=sNum;i++) printf("%s\n",substances[i]->getName());
	  }
	  
	  void printGeneList() 
	  {
	  	int i;
	  	for(i=1;i<=gNum;i++) printf("%s\n",genes[i]->getName());
	  }
	  
	  void printReactionList() 
	  {
	  	int i;
	  	for(i=1;i<=rNum;i++) printf("%s\n",reactions[i]->getName());
	  }
	  
	  void printMetaboliteFaList();
	  
	  SimKernelLPX();
	  void reload(); 
	  
	  double get_obj_value() 
	  {
	  	return(glpk->get_obj_val());
	  }
	  int simplex() 
	  {
	  	return(glpk->simplex());
	  }
	  
	  void setInclGeneList() 
	  {
	  	inclgenelist = 1;
	  }
	  void setRbound(int i,Bound *bound) 
	  {
	  	rBounds[i]->type = bound->type;
	  	rBounds[i]->lb = bound->lb;
	  	rBounds[i]->ub = bound->ub;
	  }
	  void createRbound(int i,int type,double lb, double ub) 
	  {
	  	rBounds[i] = new Bound(type,lb,ub);
	  }
	  void createSBound(int i,int type,double lb,double ub) 
	  {
	  	sBounds[i] = new Bound(type,lb,ub);
	  }
	  void setSbound(int i,Bound *bound) 
	  {
	  	sBounds[i]->type = bound->type;
	  	sBounds[i]->lb = bound->lb;
	  	sBounds[i]->ub = bound->ub;
	  }
	  void printSystem();
	  void printGeneReactionAssociation();
	  void printGeneReactionAssociation(char *gname);
	  void printGeneReactionMatrix();
};

