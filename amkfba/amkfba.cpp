#include <stdio.h>
#include <stdlib.h>
#include "SimKernel.h"

#define PATHLEN 1000

typedef struct 
{
  char program[20];
  char ifile[PATHLEN];
  char bfile[PATHLEN];
  char rbfile[PATHLEN];
  char comfile[PATHLEN];
  
  char objName[500];
  int bflag;
  int gwise;
  int koflag;
  int rbflag;
  int comfileflag;
  int objdir;
  int extmetflag;
  char kogene[PATHLEN];
  char extmet[PATHLEN];	
} COMLine;

int cmpr(char *str1, char *str2) 
{
	int i;
	for(i=0;str1[i]!=0;i++) 
		if(str1[i]!=str2[i]) return(0);
	if(str2[i]!=0) return(0);
	return(1);
}

void strcopy(char *str1, char *str2) 
{
	int i;
	for(i=0;str2[i]!=0;i++) str1[i] = str2[i];
	str1[i] = 0;
	
}

int length(char *str) 
{
    int i;
	for(i=0;str[i]!=0;i++); 
	return(i);
}

void stackTests() 
{
	char string[100] = "slowo 1 slowko field <GENES> (Rv1	OR Rv2 ) AND (Rv3 OR Rv4 OR Rv5) AND Rv6 </GENES> postfix postfix";
	char string1[100];
	int expr[12],result,i;
	ReactionSTACK *stack;
    TokenSTACK *parser;
    int state[100];
    
  parser = new TokenSTACK();
  parser->extract(string,"<GENES>","</GENES>");
  result = parser->size();
  for(i=0;i<parser->size();i++) 
  {
     parser->get(string,i);
     parser->push(string);
     parser->pop(string1);
     printf("%s\n",string1);	
  }
  printf("*********************\n");
  parser->parse();
  for(i=0;i<parser->rpnSize();i++) 
  {
     parser->getRPN(string1,i);
     printf("%s\n",string1);	
  }
  /*  ( 1 OR 2 ) AND ( 3 OR 4 OR 5 ) AND 6 */
  /*  1 2 OR 3 4 5 OR OR AND 6 AND */
  
  
  state[1] = 1;
  state[2] = 1;
  state[3] = 0;
  state[4] = 0;
  state[5] = 1;
  state[6] = 1;
  
  expr[0] = 11;
  expr[1] = 1;
  expr[2] = 2;
  expr[3] = -2;
  expr[4] = 3;
  expr[5] = 4;
  expr[6] = 5;
  expr[7] = -2;
  expr[8] = -2;
  expr[9] = -1;
  expr[10] = 6;
  expr[11] = -1;
  
  stack = new ReactionSTACK();
  result = stack->evaluate(expr,state);
  printf("Result\t%d\n",result);

}

int loadBoundsFile(char *path,SimKernelLPX *system) 
{
  int bnum,i,idx,max;
  char line[510];
  char *bfile;
  FILE *plik;
  TokenSTACK *parser;
  Bound bound;
  char buff[500];
  
  if((plik=fopen(path,"r"))==NULL) 
  {
  	fprintf(stderr,"Cannot open bounds file %s\n",path);
  	exit(0);
  }
  
  parser = new TokenSTACK();
  for(;;) 
  {
  	for(i=0;(line[i]=fgetc(plik))!='\n'|| i>500;i++) 
      if(feof(plik)!=0) 
      {
        line[i]=0;
        if((int)line[i-1]==10 || (int)line[i-1]==13) line[i-1]=0;
      	if(length(line)<2) 
      	{
      		delete parser;
      		return(1);
      	}
      	if(line[0]=='%') 
      	{
      		delete parser;
      		return(1);
      	}
      	parser->splitNoBrackets(line);
      	parser->get(buff,0);
      	if((idx = system->reactionIdx(buff))==(-1)) return(1);
      	parser->get(buff,1);
      	bound.lb = atof(buff);
      	parser->get(buff,2);
      	bound.ub = atof(buff);
      	if(bound.lb==bound.ub) bound.type = LPX_FX;
      	if(bound.lb!=bound.ub) bound.type = LPX_DB;
      	system->setReactionBound(idx,&bound);
      	system->setRbound(idx,&bound);
      	delete parser;
      	return(1);
      }	
     
     line[i]=0;
     if((int)line[i-1]==10 || (int)line[i-1]==13) line[i-1]=0;
     if(line[0]=='%') continue;
     parser->splitNoBrackets(line);
     parser->get(buff,0);
     if((idx = system->reactionIdx(buff))==(-1)) continue;
     parser->get(buff,1);
     bound.lb = atof(buff);
     parser->get(buff,2);
     bound.ub = atof(buff);
     if(bound.lb==bound.ub) bound.type = LPX_FX;
     if(bound.lb!=bound.ub) bound.type = LPX_DB;
     system->setReactionBound(idx,&bound);     
     system->setRbound(idx,&bound);
  }
  delete parser;
  return(1);
}

int loadRowBoundsFile(char *path,SimKernelLPX *system) 
{
  int bnum,i,idx,max;
  char line[510];
  char *bfile;
  FILE *plik;
  TokenSTACK *parser;
  Bound bound;
  char buff[500];
  
  if((plik=fopen(path,"r"))==NULL) 
  {
  	fprintf(stderr,"Cannot open bounds file %s\n",path);
  	exit(0);
  }
  
  parser = new TokenSTACK();
  for(;;) 
  {
  	for(i=0;(line[i]=fgetc(plik))!='\n'|| i>500;i++) 
      if(feof(plik)!=0) 
      {
        line[i]=0;
        if((int)line[i-1]==10 || (int)line[i-1]==13) line[i-1]=0;
      	if(length(line)<2) 
      	{
      		delete parser;
      		return(1);
      	}
      	if(line[0]=='%') 
      	{
      		delete parser;
      		return(1);
      	}
      	parser->splitNoBrackets(line);
      	parser->get(buff,0);
      	if((idx = system->substanceIdx(buff))==(-1)) return(1);
      	parser->get(buff,1);
      	bound.lb = atof(buff);
      	parser->get(buff,2);
      	bound.ub = atof(buff);
      	if(bound.lb==bound.ub) bound.type = LPX_FX;
      	if(bound.lb!=bound.ub) bound.type = LPX_DB;
      	system->setSbound(idx,&bound);
      	delete parser;
      	return(1);
      }	
     
     line[i]=0;
     if((int)line[i-1]==10 || (int)line[i-1]==13) line[i-1]=0;
     if(line[0]=='%') continue;
     parser->splitNoBrackets(line);
     parser->get(buff,0);
     if((idx = system->substanceIdx(buff))==(-1)) continue;
     parser->get(buff,1);
     bound.lb = atof(buff);
     parser->get(buff,2);
     bound.ub = atof(buff);
     if(bound.lb==bound.ub) bound.type = LPX_FX;
     if(bound.lb!=bound.ub) bound.type = LPX_DB;     
     system->setSbound(idx,&bound);
  }
  delete parser;
  return(1);
}

int loadGlistFile(char *path,SimKernelLPX *system) 
{
  int i;
  char line[510];
  FILE *plik;
  TokenSTACK *parser;
  char buff[500];
  
  if((plik=fopen(path,"r"))==NULL)
  {
  	fprintf(stderr,"Cannot open gene list file %s\n",path);
  	exit(0);
  }
  
  parser = new TokenSTACK();
  for(;;) 
  {
  	for(i=0;(line[i]=fgetc(plik))!='\n'|| i>500;i++) 
      if(feof(plik)!=0) 
      {
        line[i]=0;
      	return(1);
      }	
     
     line[i]=0;
     if((int)line[i-1]==10 || (int)line[i-1]==13) line[i-1]=0;
     if(line[0]=='%') continue;
     parser->splitNoBrackets(line);
     parser->get(buff,0);
     system->setEssential(buff);     	
   }
}

int loadComLine(char *path, char *argc[]) 
{

  int i,j;
  char line[510];
  FILE *plik;
  TokenSTACK *parser;
  char buff[500];
  int argn;
  
  if((plik=fopen(path,"r"))==NULL)
  {
  	fprintf(stderr,"Cannot load command line file %s\n",path);
  	exit(0);
  }
  
  parser = new TokenSTACK();
  argn=0;
  for(;;) 
  {
  	for(i=0;(line[i]=fgetc(plik))!='\n'|| i>500;i++) 
      if(feof(plik)!=0) 
      {
        line[i]=0;
        if((int)line[i-1]==10 || (int)line[i-1]==13) line[i-1]=0;
        if(line[0]=='%') continue;
        parser->splitNoBrackets(line);
        for(j=0;j<parser->size();j++)
        {
     	  argn++;
     	  argc[argn] = (char *)calloc(100,sizeof(char));
     	  parser->get(argc[argn],j);
        }
      	return(argn);
      }	
     
     line[i]=0;
     if((int)line[i-1]==10 || (int)line[i-1]==13) line[i-1]=0;
     if(line[0]=='%') continue;
     parser->splitNoBrackets(line);
     for(j=0;j<parser->size();j++)
     {
     	argn++;
     	argc[argn] = (char *)calloc(100,sizeof(char));
     	parser->get(argc[argn],j);
     }
     
  }

   return(argn);	
}

int loadInclGenesFile(char *path,SimKernelLPX *system) 
{
  int i;
  char line[510];
  FILE *plik;
  TokenSTACK *parser;
  char buff[500];
  
  if((plik=fopen(path,"r"))==NULL)
  {
  	fprintf(stderr,"Cannot open gene list file %s\n",path);
  	exit(0);
  }
  
  parser = new TokenSTACK();
  for(;;) 
  {
  	for(i=0;(line[i]=fgetc(plik))!='\n'|| i>500;i++) 
      if(feof(plik)!=0) 
      {
        line[i]=0;
      	return(1);
      }	
     
     line[i]=0;
     if((int)line[i-1]==10 || (int)line[i-1]==13) line[i-1]=0;
     if(line[0]=='%') continue;
     parser->splitNoBrackets(line);
     parser->get(buff,0);
     system->setIncluded(buff);     	
   }
}

void parseComLine(int argn, char *argc[100],COMLine *comline) 
{
	int i,gflag,varflag;
	
	gflag = 0;
	varflag=0;
	comline->gwise = 0;
	comline->bflag = 0;
	comline->koflag = 0;
	comline->rbflag = 0;
	comline->comfileflag = 0;
	comline->objdir = GLPKMAX;
	comline->extmetflag = 0;
	
	for(i=1;i<argn;i++) 
	{
		if(cmpr(argc[i],"-p")==1) strcopy(comline->program,argc[i+1]);
		if(cmpr(argc[i],"-i")==1) strcopy(comline->ifile,argc[i+1]);
		if(cmpr(argc[i],"-obj")==1) strcopy(comline->objName,argc[i+1]);
		if(cmpr(argc[i],"-gwise")==1) comline->gwise = 1;
		if(cmpr(argc[i],"-min")==1) comline->objdir = GLPKMIN;
		if(cmpr(argc[i],"-f")==1) 
		{
			strcopy(comline->comfile,argc[i+1]);
			comline->comfileflag=1;
		}
		if(cmpr(argc[i],"-bfile")==1) 
		{
		 strcopy(comline->bfile,argc[i+1]);
		 comline->bflag=1;	
		}
		if(cmpr(argc[i],"-rbfile")==1) 
		{
		 strcopy(comline->rbfile,argc[i+1]);
		 comline->rbflag=1;	
		}
		
		if(cmpr(argc[i],"-ko")==1) 
		{
			strcopy(comline->kogene,argc[i+1]);
			comline->koflag = 1;
		}
		
		if(cmpr(argc[i],"-metext")==1)
		{
			strcopy(comline->extmet,argc[i+1]);
			comline->extmetflag=1;
		}
		
		  
	}

}

int main(int argn,char** argc) 
{  
  int i;
  COMLine comline;
  SimKernelLPX *system;
  double Z;
  double bestvec[MAXCOLS];
  char *argm[1000];
  
  if(argn<2) 
  {
     printf("USAGE:%s -i input_file -p program -obj opt_target further options\n",argc[0]);
     printf("-i The name of the file in flux analyser like format\n");
     printf("-f The file containing the list of command line options (one option per line)\n");
     printf("-p The analysis program:\n");
     printf("    fba         - single FBA run\n");
     printf("    objvalue    - Compute objective function value and print it\n");
     printf("                  as a single number to stdout\n");
     printf("    objstat     - compute objective function value and status\n");
     printf("                  and print as one line output\n");
     printf("    metabolites - print metabolite list\n");
     printf("    genes       - print gene list\n");
     printf("    reactions4gene - print input file lines of reactions which \n");
     printf("                     are inactivated by the gene\n");
     printf("    fametlist   - print metabolite list in FluxAnalyser format\n");
     printf("    kogene      - knockout gene and run FBA\n");
     printf("    genes2reactions - print gene/reaction associations\n");
     printf("    g2rMatrix - print gene protein associations in sparse matrix format\n");
     printf("-obj Optimisation target name. It may be reaction or metabolite name\n");
     printf("-bfile flux (column) bounds file\n");
     printf("-rbfile rate (row) bounds file\n");
     printf("-ko gene name - the gene to be inactivated\n");
     printf("-min - compute minimal value of the objective function\n");
     printf("-metext - set metabolite as external");
     printf("\n"); 
     exit(0);
  }
  parseComLine(argn,argc,&comline);
  if(comline.comfileflag==1) 
  {
  	argn = loadComLine(comline.comfile,argm);
  	parseComLine(argn,argm,&comline);
  }
  
  if(cmpr(comline.program,"objvalue")==1) 
  {
  	system = new SimKernelLPX();
  	system->loadFALike(comline.ifile);
  	system->setObjective(comline.objName);
  	if(comline.bflag==1) loadBoundsFile(comline.bfile,system);
  	if(comline.rbflag==1) 
  	{
  		loadRowBoundsFile(comline.rbfile,system);
  	    system->reload();
  	}
  	if(comline.extmetflag==1) system->setExternalMetabolite(comline.extmet);
  	Z = system->optimisation(comline.objdir);
  	printf("%f\n",Z);
  }
  
  if(cmpr(comline.program,"objstat")==1) 
  {
  	system = new SimKernelLPX();
  	system->loadFALike(comline.ifile);
  	system->setObjective(comline.objName);
  	if(comline.bflag==1) loadBoundsFile(comline.bfile,system);
  	if(comline.rbflag==1) 
  	{
  		loadRowBoundsFile(comline.rbfile,system);
  	    system->reload();
  	}
  	if(comline.extmetflag==1) 
  	   system->setExternalMetabolite(comline.extmet);
  	Z = system->optimisation(comline.objdir);
  	printf("%f\t",Z);
  	i=system->getStatus();
  	if(i==LPX_OPT) printf("OPTIMAL\n");
  	if(i==LPX_FEAS) printf("FEASIBLE\n");
  	if(i==LPX_INFEAS) printf("INFEASIBLE\n");
  	if(i==LPX_NOFEAS) printf("NON-FEASIBLE\n");
  	if(i==LPX_UNBND) printf("UNBOUNDED\n");
  	if(i==LPX_UNDEF) printf("UNDEFINED\n");
  }
  
  if(cmpr(comline.program,"kogene")==1) 
  {
  	system = new SimKernelLPX();
  	system->loadFALike(comline.ifile);
  	system->loadGenes();
  	system->setObjective(comline.objName);
  	if(comline.bflag==1) loadBoundsFile(comline.bfile,system);
  	if(comline.rbflag==1) 
  	{
  		loadRowBoundsFile(comline.rbfile,system);
  	    system->reload();
  	}
  	if(comline.extmetflag==1) system->setExternalMetabolite(comline.extmet);
  	if(comline.koflag==0) 
  	{
  	  fprintf(stderr,"-ko must be specified for kogene analysis\n"); 
  	  exit(0);	
  	} 
  	system->setEssential(comline.kogene);
  	system->geneEssentialityTest();
  	Z = system->get_obj_value();
  	printf("%f\t",Z);
  	i=system->getStatus();
  	if(i==LPX_OPT) printf("OPTIMAL\n");
  	if(i==LPX_FEAS) printf("FEASIBLE\n");
  	if(i==LPX_INFEAS) printf("INFEASIBLE\n");
  	if(i==LPX_NOFEAS) printf("NON-FEASIBLE\n");
  	if(i==LPX_UNBND) printf("UNBOUNDED\n");
  	if(i==LPX_UNDEF) printf("UNDEFINED\n");
  }
  
  if(cmpr(comline.program,"fba")==1) 
  {
  	system = new SimKernelLPX();
  	system->loadFALike(comline.ifile);
  	system->setObjective(comline.objName);
  	if(comline.bflag==1) loadBoundsFile(comline.bfile,system);
  	if(comline.rbflag==1) 
  	{
  		loadRowBoundsFile(comline.rbfile,system);
  	    system->reload();
  	}
  	if(comline.extmetflag==1) system->setExternalMetabolite(comline.extmet);
  	Z = system->optimisation(comline.objdir);
  	system->printPrimalSolution();
  }
   
  if(cmpr(comline.program,"metabolites")==1) 
  {
  	system = new SimKernelLPX(); 
    system->loadFALike(comline.ifile);
    system->printMetaboliteList();
  }
  
  if(cmpr(comline.program,"fametlist")==1) 
  {
  	system = new SimKernelLPX(); 
    system->loadFALike(comline.ifile);
    system->printMetaboliteFaList();
  }
  
  if(cmpr(comline.program,"genes")==1) 
  {
  	system = new SimKernelLPX(); 
    system->loadFALike(comline.ifile);
    system->loadGenes();
    system->printGeneList();
  }
  
  if(cmpr(comline.program,"reactions")==1) 
  {
  	system = new SimKernelLPX(); 
    system->loadFALike(comline.ifile);
    system->printReactionList();
  }
  
  if(cmpr(comline.program,"genes2reactions")==1) 
  {
  	system = new SimKernelLPX(); 
    system->loadFALike(comline.ifile);
    system->loadGenes();
    system->printGeneReactionAssociation();
  }
  
  if(cmpr(comline.program,"g2rMatrix")==1) 
  {
  	system = new SimKernelLPX(); 
    system->loadFALike(comline.ifile);
    system->loadGenes();
    system->printGeneReactionMatrix();
  }
  
  if(cmpr(comline.program,"reactions4gene")==1) 
  {
    if(comline.koflag!=1) 
    {
    	fprintf(stderr,"-ko must be specified for this program\n");
    }
  	system = new SimKernelLPX(); 
    system->loadFALike(comline.ifile);
    system->loadGenes();
    system->printGeneReactionAssociation(comline.kogene);
  }
}

