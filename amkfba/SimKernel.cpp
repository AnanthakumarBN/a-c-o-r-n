#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "SimKernel.h"


SimKernelLPX::SimKernelLPX() 
{
	glpk = new GlpkKernel();
	inclgenelist = 0;
}

void SimKernelLPX::loadMPS(char *path) 
{
	glpk->read_mps(path);
}

double SimKernelLPX::optimisation(int d) 
{
	glpk->set_obj_dir(d);
	if((retcode=glpk->simplex())!=LPX_E_OK) 
	{
			reload();
			retcode = glpk->simplex();
			if(retcode!=LPX_E_OK) 
	        {
              status = OPT_FAILED;		
	        }
	        else 
	        {
	          status = glpk->get_status();	
	        }
	        return(glpk->get_obj_val());
	}
	status = glpk->get_status();
	return(glpk->get_obj_val());
}

void SimKernelLPX::loadFALike(char *path) 
{
	fafile = new FALikeFile();
	fafile->Load(path,glpk,reactions,substances,rBounds,sBounds);
	sNum=fafile->sNum;
	rNum=fafile->rNum;
}

void SimKernelLPX::printFAFile() 
{
	fafile->PrintInput();
	printf("\n%s\n",substances[1]->getName());
}

void SimKernelLPX::findAffectedReactions() 
{
    int i,j,num, idx;
    int state[MAXGENES];
    ReactionSTACK *stack;
    int *vec;
    
    stack = new ReactionSTACK();
    
    for(i=1;i<=gNum;i++) state[i] = 1;
    
    for(i=1;i<=gNum;i++) 
    {
       num = genes[i]->getRNum();
       for(j=0;j<num;j++)
       {
        idx = genes[i]->getReaction(j);
        vec = reactions[idx]->getGenes();
        if(vec[0]==1) 
        {
          idx = genes[i]->getReaction(j);
          genes[i]->addAffectedReaction(idx); 
          continue;
        }
        state[i] = 0;
        idx = stack->evaluate(vec,state);
       	if(idx==0)
       	{
       		idx = genes[i]->getReaction(j);
       		genes[i]->addAffectedReaction(idx);
       	}
       	state[i] = 1;
       }
        
       	
    }
}

void SimKernelLPX::loadGenes() 
{
	fafile->LoadGenes(genes,reactions);
	gNum = fafile->gNum;
	findAffectedReactions();
}

void SimKernelLPX::printReactionRPNs() 
{
	int i,j;
	int vec[1000];
	
	for(i=1;i<=rNum;i++) 
	{
		printf("%s\t",reactions[i]->getName());
		reactions[i]->getGenes(vec);
		for(j=1;j<=vec[0];j++) 
		{
			if(vec[j]==(-1)) printf(" AND ");
			if(vec[j]==(-2)) printf(" OR ");
			if(vec[j]>=0) printf(" %s ",genes[vec[j]]->getName());
		}
		printf("\n");
	}
}



int SimKernelLPX::geneIdx(char *str) 
{
	int i;
	for(i=1;i<gNum;i++) 
	  if(cmpr(str,genes[i]->getName())==1) return(i);
	return(-1);
}

int SimKernelLPX::reactionIdx(char *str) 
{
	int i;
	for(i=1;i<=rNum;i++) 
	  if(cmpr(str,reactions[i]->getName())==1) return(i);
	return(-1);
}

int SimKernelLPX::substanceIdx(char *str) 
{
	int i;
	for(i=1;i<=sNum;i++) 
	  if(cmpr(str,substances[i]->getName())==1) return(i);
	return(-1);
}


int SimKernelLPX::cmpr(char *str1, char *str2) 
{
	int i;
	for(i=0;str1[i]!=0;i++) 
		if(str1[i]!=str2[i]) return(0);
	if(str2[i]!=0) return(0);
	return(1);
}

int SimKernelLPX::setObjective(char *str) 
{
	int i,id,len,k;
	int idx[MAXROWS];
	double val[MAXROWS];
	
	id = reactionIdx(str);
	if(id!=(-1))
	{
	   setObjectiveFlux(id);
	   return(id);	
	}
	id = substanceIdx(str);
	if(id==(-1))
	{
		fprintf(stderr,"Wrong optimisation target name\n");
		exit(0);
	}

    for(k=1;k<=rNum;k++) glpk->set_obj_coef(k,0.000000);
	len = glpk->get_mat_row(id,idx,val);
	for(i=1;i<=len;i++) glpk->set_obj_coef(idx[i],val[i]);
	glpk->set_row_bnds(id,LPX_FR,LBMIN,UBMAX);
	objIdx = id;
	return(id);	
	
}

void SimKernelLPX::setObjective()
{
	int i,id,len,k;
	int idx[MAXROWS];
	double val[MAXROWS];
	
	id = objIdx;
	for(k=1;k<=rNum;k++) glpk->set_obj_coef(k,0.000000);
	len = glpk->get_mat_row(id,idx,val);
	for(i=1;i<=len;i++) glpk->set_obj_coef(idx[i],val[i]);
	glpk->set_row_bnds(id,LPX_FR,LBMIN,UBMAX);


}

void SimKernelLPX::setObjective(int obj)
{
	int i,id,len,k;
	int idx[MAXROWS];
	double val[MAXROWS];
	
	objIdx = obj;
	id = objIdx;
	for(k=1;k<=rNum;k++) glpk->set_obj_coef(k,0.000000);
	len = glpk->get_mat_row(id,idx,val);
	for(i=1;i<=len;i++) glpk->set_obj_coef(idx[i],val[i]);
	glpk->set_row_bnds(id,LPX_FR,LBMIN,UBMAX);


}

void SimKernelLPX::setObjectiveFlux(int f)
{
   int k;
   for(k=1;k<=rNum;k++) glpk->set_obj_coef(k,0.000000);
   glpk->set_obj_coef(f,1.000000);
}

void SimKernelLPX::geneEssentialityTest() 
{
	int i,j,bnum,idx;
	Bound *bound;
	
	bound = new Bound(LPX_FX,0,0);
	for(i=1;i<=gNum;i++) 
	{
        if(genes[i]->isEssential()!=1) continue;
		bnum = genes[i]->getAffectedReactionsNum();
		for(j=0;j<bnum;j++)
		{
		   idx = genes[i]->getAffectedReaction(j);	
		   setReactionBound(idx,bound);
		}
	}
	optimisation(GLPKMAX);	
}


void SimKernelLPX::printPrimalSolution() 
{
	int i;
	
    if(status==LPX_OPT) 
    {
    	printf("Status:\tOPTIMAL\n");
    }
    else 
    {
    	printf("Status:\tUNDEFINED\n");
    }
    
    printf("Objective function value:\t%f\n",glpk->get_obj_val());
	for(i=1;i<=rNum;i++) 
	{
		printf("%s\t%f\t%s\n",reactions[i]->getName(),glpk->get_col_prim(i),fafile->getInputLine(i-1));
	}
}

void SimKernelLPX::setEssential(char *str) 
{
	int idx;
	
	if((idx = geneIdx(str))>0) genes[idx]->setEssential();
}

void SimKernelLPX::setIncluded(char *str) 
{
	int idx;
	
	if((idx = geneIdx(str))>0) genes[idx]->setIncluded();
}

void SimKernelLPX::printMetaboliteFaList()
{
	int i;
	
	for(i=1;i<=sNum;i++) 
	{
		printf("%s\t%s\t0.001\t",substances[i]->getName(),substances[i]->getName());
		if(glpk->get_row_lb(i)==0 && glpk->get_row_ub(i)==0) 
		{
			printf("0\n");
			continue;
		}
		if(glpk->get_row_lb(i)==0 && glpk->get_row_type(i)==LPX_FX) 
		{
		   printf("0\n");
		   continue;	
		}
		printf("1\n");
	}
}

void SimKernelLPX::reload()
{
        int i;
        GlpkKernel *glpkold;

        glpkold = glpk;
        // delete glpk;
        glpk = new GlpkKernel();
        fafile->reload(glpk,reactions,substances);

        for(i=1;i<=rNum;i++)
                setReactionBound(i,rBounds[i]->type,rBounds[i]->lb,rBounds[i]->ub);
        for(i=1;i<=sNum;i++)
            glpk->set_row_bnds(i,sBounds[i]->type,sBounds[i]->lb,sBounds[i]->ub);
        setObjective();
        delete glpkold;

}


void SimKernelLPX::printSystem() 
{
	int i,j,rnum;
	int vec[MAXCOLS];
	double dvec[MAXCOLS];
	int ne=0;
	
    rnum = glpk->get_num_cols();
    printf("Reactions: %d\n",rnum);
    printf("Substances: %d\n\n",glpk->get_num_rows());
    
    for(i=1;i<=rnum;i++) 
    {
    	ne = glpk->get_mat_col(i,vec,dvec);
    	for(j=1;j<=ne;j++) 
    	{
    		printf("%d\t%d\t%s\t%d\t%f\t%f\t%s\t%d\t%f\t%f\t%f\n",i,vec[j],reactions[i]->getName(),glpk->get_col_type(i),glpk->get_col_lb(i),glpk->get_col_ub(i),substances[vec[j]]->getName(),glpk->get_row_type(vec[j]),glpk->get_row_lb(vec[j]),glpk->get_row_ub(vec[j]),dvec[j]);
    	}
    }
	
}

void SimKernelLPX::printGeneReactionAssociation() 
{
	int i,j;
	
	for(i=1;i<=gNum;i++)
	{
	   for(j=0;j<genes[i]->getAffectedReactionsNum();j++)
	     printf("%s\t%s\n",genes[i]->getName(),fafile->getInputLine(genes[i]->getAffectedReaction(j)-1));	
	}
}

void SimKernelLPX::printGeneReactionAssociation(char *gname) 
{
	int i,j;
	
	for(i=1;i<=gNum;i++)
	{
	  if(cmpr(genes[i]->getName(),gname)==1)
	   for(j=0;j<genes[i]->getAffectedReactionsNum();j++)
	     printf("%s\t%s\n",genes[i]->getName(),fafile->getInputLine(genes[i]->getAffectedReaction(j)-1));	
	}
}

void SimKernelLPX::printGeneReactionMatrix() 
{
	int i,j;
	
	for(i=1;i<=gNum;i++)
	{
	   for(j=0;j<genes[i]->getAffectedReactionsNum();j++)
	     printf("%d\t%d\n",i,genes[i]->getAffectedReaction(j));	
	}
}

void SimKernelLPX::setExternalMetabolite(char *mname) 
{
    int idx;
    Bound bound;
    
	if((idx = substanceIdx(mname))==(-1)) 
	{
		fprintf(stderr,"%s is not metabolite name\n",mname);
		exit(0);
	}
	
	bound.type = LPX_DB;
	bound.lb = (-100000);
	bound.ub = 100000;
	
	setSbound(idx,&bound);
	glpk->set_row_bnds(idx,bound.type,bound.lb,bound.ub);
	
}
/*********************************** FALikeFile class ************************************************************/

int FALikeFile::getFile(FILE *plik) 
{
    int i;
    
    for(size=0;;size++) 
    {
     for(i=0;(input[size][i]=fgetc(plik))!='\n';i++) 
     {
      if(feof(plik)!=0) 
      {
        input[size][i]=0;
        if((int)input[size][i-1]==10 || (int)input[size][i-1]==13) input[size][i-1]=0;
        if(input[size][0]!='%') size++;
      	return(0);
      }	
      if(i>MAXCHARS) 
      {
        fprintf(stderr,"Line %d longer than max of %d  characters\n",size,MAXCHARS);
        exit(0);	
      }
         
     }
     if(size>MAXLINES) 
     {
        fprintf(stderr,"Number of lines greater than max of %d\n",MAXLINES);     
	    exit(0);
     }     
     input[size][i]=0;
     if((int)input[size][i-1]==10 || (int)input[size][i-1]==13) input[size][i-1]=0; 
     if(input[size][0]=='%') size--;
    }
    fclose(plik);    
    return(1);
}

void FALikeFile::PrintInput() 
{
	int i;
	
	for(i=0;i<size;i++) printf("%s\n",input[i]); 
	
}

int FALikeFile::index(GeneLPX *obj[], char *str) 
{
	int i;
	for(i=1;i<=gNum;i++) 
	  if(cmpr(str,obj[i]->getName())==1) return(i);
	return(-1);
}

int FALikeFile::index(ReactionLPX *obj[], char *str) 
{
	int i;
	for(i=1;i<=rNum;i++) 
	  if(cmpr(str,obj[i]->getName())==1) return(i);
	return(-1);
}

int FALikeFile::index(SubstanceLPX *obj[], char *str) 
{
	int i;
	for(i=1;i<=sNum;i++) 
	  if(cmpr(str,obj[i]->getName())==1) return(i);
	return(-1);
}


int FALikeFile::cmpr(char *str1, char *str2) 
{
	int i;
	for(i=0;str1[i]!=0;i++) 
		if(str1[i]!=str2[i]) return(0);
	if(str2[i]!=0) return(0);
	return(1);
}

void FALikeFile::newReaction(TokenSTACK *parser,GlpkKernel *system, ReactionLPX *reactions[], SubstanceLPX *substances[],Bound *rBounds[],Bound *sBounds[]) 
{
    char buff[1000],buff1[1000];
    char name[100];
    int idx = 0;
    int idx1 = 0;
    int i,j,flag, flag1;
    double stoch;
    double mult=(-1);
    
    // Get reaction name and check whether it is unique
    parser->get(buff,0);
    if(index(reactions,buff)>0) 
    {
        fprintf(stderr,"Reaction name %s is not unique\n",buff);
        fprintf(stderr,"Reaction:");
        for(i=0;i<parser->size();i++) 
        {
        	parser->get(buff,i);
        	fprintf(stderr,buff);
        }
        fprintf(stderr,"\n");
    	exit(0);	
    }
    
    // Create new ReactionLPX object and set reaction name
    rNum++;
    reactions[rNum] = new ReactionLPX();
    reactions[rNum]->setName(buff);
    system->add_cols(1);
    
    // Find position of reaction separator |
    for(i=1;i<size;i++) 
    {
      parser->get(buff,i);
      if(cmpr(buff,"|")==1) 
      {
      	idx = i;
      	break;
      }
    }
    if(idx==0) 
    {
    	fprintf(stderr,"\nNo | character in definition of reaction %s\n",reactions[rNum]->getName());
    	exit(0);
    }
    
    // Loop over all fields in reaction definition part of the input file line
    flag = 0;
    flag1 = 0;
    for(i=1;i<idx;i++) 
    {
      // get field
      parser->get(buff,i);
      
      // If character equal + reset flag,check formula syntax and skip +										
      if(cmpr(buff,"+")==1) 
      {
      	flag = 0;
      	if(flag>1) 
      	{
      		fprintf(stderr,"Wrong formula syntax (probably missing +) in reaction %s",reactions[rNum]->getName());
      		exit(0);
      	}
      	continue;
      }
      
      // change side of equation, reset flags, check formula syntax and skip =
      if(cmpr(buff,"=")==1)										 
      {
      	mult = 1; 
      	flag = 0;
      	flag1 = 1;
      	if(flag>1) 
      	{
      		fprintf(stderr,"Wrong formula syntax (probably missing + or =) in reaction %s",reactions[rNum]->getName());
      		exit(0);
      	}
      	continue;
      }
      
      // if field is number set current stoch coeficient
      if(isnumber(buff)==1) 									
      {
      	stoch=atof(buff);
      	stoch*=mult;
      	flag++;
      }
      else 														// else treat the field as substance name
      {
        if(flag==0) 											// check whether stoch coef was set
        {
        	stoch=mult;
        	flag++;
        }											
      	if((idx1=index(substances,buff))<0) 					// check whether the substance has been created before
      	{
      		sNum++;												// create new substance and set its index and name
      		substances[sNum] = new SubstanceLPX();
      		substances[sNum]->setName(buff);
      		system->add_rows(1);
      		if(substances[sNum]->classify()==1) 				// Check whether it is external metabolite
      		{
      		   system->set_row_bnds(sNum,LPX_FR,LBMIN,UBMAX);	// If yes make the row unbound
      		   sBounds[sNum] = new Bound(LPX_FR,LBMIN,UBMAX); 
      		}
      		else 
      		{
      			system->set_row_bnds(sNum,LPX_FX,0,0);			// If no bind the row to 0 (flux balance condition)
      			sBounds[sNum] = new Bound(LPX_FX,0,0);
      		}

      		idx1=sNum;
      	}
      	ne++; ia[ne] = idx1; ja[ne] = rNum; ar[ne] = stoch;		// create entry of constraint matrix
      }
    }
    
    // Check whether right side was defined for the formula.
    if(flag1==0) 
    {
      fprintf(stderr,"Only substrates are defined for reaction %s.",reactions[rNum]->getName());
      exit(0);
    }
    
    // Parse and set reaction bounds
    idx++; idx++;
    parser->get(buff,idx);
    idx++;
    parser->get(buff1,idx);
    system->set_col_bnds(rNum,LPX_DB,atof(buff),atof(buff1));
    rBounds[rNum] = new Bound(LPX_DB,atof(buff),atof(buff1));
}

void FALikeFile::Load(char *path, GlpkKernel *system, ReactionLPX *reactions[], SubstanceLPX *substances[],Bound *rBounds[],Bound *sBounds[])
{
	FILE *plik;
	int i,j;
	char buff[1000];
	char name[100];
	TokenSTACK *parser;

	if(strcmp(path, "-") == 0)
	    plik = stdin;
	else if((plik=fopen(path,"r"))==NULL) 
	{
		fprintf(stderr,"Cannot open %s\n",path);
		exit(0);
	}
	
    getFile(plik);
    
    parser = new TokenSTACK();
    for(i=0;i<size;i++)
    {
    	parser->splitNoBrackets(input[i]);
        newReaction(parser,system,reactions,substances,rBounds,sBounds);	
    }
    
    // Create the LPX data structure.
    for(i=1;i<=sNum;i++) 
    {
    	substances[i]->getName(buff);
    	for(j=0;j<255;j++) { name[j]=buff[j]; if(buff[j]==0) break;	}	  
    	system->set_row_name(i,name);
    }
    for(i=1;i<=rNum;i++) 
    {
    	reactions[i]->getName(buff);
    	for(j=0;j<255;j++) { name[j]=buff[j]; if(buff[j]==0) break;	}	  
    	system->set_col_name(i,name);
    }
    system->load_matrix(ne,ia,ja,ar);
}

void FALikeFile::reload(GlpkKernel *system, ReactionLPX *reactions[], SubstanceLPX *substances[])
{
	int i,j;
	char buff[1000];
	char name[100];
	
	system->add_cols(rNum);
	system->add_rows(sNum);
    for(i=1;i<=sNum;i++) 
    {
    	substances[i]->getName(buff);
    	for(j=0;j<255;j++) { name[j]=buff[j]; if(buff[j]==0) break;	}	  
    	system->set_row_name(i,name);
    }
    for(i=1;i<=rNum;i++) 
    {
    	reactions[i]->getName(buff);
    	for(j=0;j<255;j++) { name[j]=buff[j]; if(buff[j]==0) break;	}	  
    	system->set_col_name(i,name);
    }
    system->load_matrix(ne,ia,ja,ar);
}

void FALikeFile::LoadGenes(GeneLPX *genes[], ReactionLPX *reactions[]) 
{
	int i,idx,j;
	int vec[MAXGENES];
	char buff[1000];
	char name[100];
	TokenSTACK *parser;
	
	parser = new TokenSTACK();
	gNum=0;
    for(i=0;i<size;i++)
    {
    	parser->extract(input[i],"<GENES>","</GENES>");
    	for(j=0;j<parser->size();j++) 
    	{
    		parser->get(buff,j);
    		if(cmpr(buff,"AND")==1 || cmpr(buff,"OR")==1 || cmpr(buff,"(")==1 || cmpr(buff,")")==1) continue;
    		if((idx=index(genes,buff))>0) 
    		{
    		    genes[idx]->addReaction(i+1);
    			continue;
    		}
    		gNum++;
    		genes[gNum] = new GeneLPX(); 
    		genes[gNum]->setName(buff);
    		genes[gNum]->addReaction(i+1);
    	}
    	
    	parser->parse();
    	for(j=0;j<parser->rpnSize();j++) 
    	{
    		parser->getRPN(buff,j);
    		if(cmpr(buff,"AND")==1) 
    		{
    		 vec[j+1] = (-1);
    		 continue;	
    		}
    		if(cmpr(buff,"OR")==1) 
    		{
    		 vec[j+1] =  (-2);
    		 continue;	
    		}
    		vec[j+1] = index(genes,buff);
    	}
    	vec[0] = j;
    	reactions[i+1]->setGenes(vec);        	
    }

	
}

int FALikeFile::isnumber(char *str) 
{
	int i;
	if(cmpr("+",str)==1) return(0);
	for(i=0;str[i]!=0;i++) 
		if((int)str[i]<45 || (int)str[i]>57)
		 if((int)str[i]!=69 && (int)str[i]!=101 && (int)str[i]!=43) 
		                                   return(0);
	return(1);
}

void FALikeFile::printSystem(GlpkKernel *system, GeneLPX *genes[], ReactionLPX *reactions[], SubstanceLPX *substances[]) 
{
    int i;
    
    printf("Substances: %d\n",sNum);
    printf("Reactions:%d\n",rNum);
    for(i=1;i<=ne;i++) 
       printf("%d\t%d\t%f\t%s\t%s\n",ia[i],ja[i],ar[i],substances[ia[i]]->getName(),reactions[ja[i]]->getName());
 	   
}
