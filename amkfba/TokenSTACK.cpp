#include "TokenSTACK.h"
#define AND "AND"
#define OR "OR"

TokenSTACK::TokenSTACK() 
{
   stackPointer = 0;	
}

void TokenSTACK::split(char *str) 
{
	int i,j;
    
    N = 0;
    j = 0;
    for(i=0;str[i]!=0;i++) 
    {
      if(str[i]==' ' || str[i]=='\t') 
      {
        if(j>0) 
        {
            input[N][j] = 0; j=0;
        	N++;	
        }	
      	continue;
      }
      if(str[i]=='(' || str[i]==')') 
      {
        if(j>0) 
        {
          input[N][j] = 0; j=0;
      	  N++;	
        }     	
      	input[N][0] = str[i];
      	input[N][1] = 0;
      	N++;
      	continue;
      }
      if(str[i]=='+' || str[i]=='=') 
      {
        if(j>0) 
        {
         input[N][j] = 0; j=0;
      	 N++;
        }      	
      	input[N][0] = str[i];
      	input[N][1] = 0;
      	N++;
      	continue;
      }
      input[N][j] = str[i]; j++;	
    }
    input[N][j] = 0;
    N++;
}

void TokenSTACK::splitNoBrackets(char *str) 
{
	int i,j;
    
    N = 0;
    j = 0;
    for(i=0;str[i]!=0;i++) 
    {
      if(str[i]==' ' || str[i]=='\t') 
      {
        if(j>0) 
        {
            input[N][j] = 0; j=0;
        	N++;	
        }	
      	continue;
      }
     /* if(str[i]=='(' || str[i]==')') 
      {
        if(j>0) 
        {
          input[N][j] = 0; j=0;
      	  N++;	
        }     	
      	input[N][0] = str[i];
      	input[N][1] = 0;
      	N++;
      	continue;
      } */
      if(str[i]=='+' || str[i]=='=') 
      {
        if(j>0) 
        {
         input[N][j] = 0; j=0;
      	 N++;
        }      	
      	input[N][0] = str[i];
      	input[N][1] = 0;
      	N++;
      	continue;
      }
      input[N][j] = str[i]; j++;	
    }
    input[N][j] = 0;
    N++;
}

int TokenSTACK::extract(char *str, char *tag1, char *tag2) 
{
	int i,j,flag;
	int result = 0;
    
    N = 0;
    j = 0;
    flag = 0;
    for(i=0;str[i]!=0;i++) 
    {
      if(str[i]==' ' || str[i]=='\t') 
      {
        if(j>0) 
        {
            input[N][j] = 0; j=0;
            if(flag==0)
              if(cmpr(input[N],tag1)==1) {flag=1; continue; }
            if(flag==1)  
              if(cmpr(input[N],tag2)==1) return(1);
        	if(flag==1) N++;	
        }	
      	continue;
      }
      if(str[i]=='(' || str[i]==')') 
      {
        if(j>0) 
        {
          input[N][j] = 0; j=0;
          if(flag==0)
              if(cmpr(input[N],tag1)==1) {flag=1; continue; }
          if(flag==1)  
              if(cmpr(input[N],tag2)==1) return(1);
      	  if(flag==1) N++;	
        } 
        if(flag==1) 
        {
          input[N][0] = str[i];
      	  input[N][1] = 0;
      	  N++;
        }      	
      	continue;
      }
      if(str[i]=='+' || str[i]=='=') 
      {
        if(j>0) 
        {
         input[N][j] = 0; j=0;
         if(flag==0)
             if(cmpr(input[N],tag1)==1) {flag=1; continue; }
         if(flag==1)  
             if(cmpr(input[N],tag2)==1) return(1);
      	 if(flag==1) N++;
        }      	
      	if(flag==1) 
        {
          input[N][0] = str[i];
      	  input[N][1] = 0;
      	  N++;
        } 
      	continue;
      }
      input[N][j] = str[i]; j++;	
    }
    input[N][j] = 0;
    N++;
    
    return(result);
}

void TokenSTACK::get(char *str,int n) 
{
    	int i;
    	for(i=0;input[n][i]!=0;i++) str[i] = input[n][i];
    	str[i] = 0;
}

int TokenSTACK::cmpr(char *str1, char *str2) 
{
	int i;
	for(i=0;str1[i]!=0;i++) 
		if(str1[i]!=str2[i]) return(0);
	if(str2[i]!=0) return(0);
	return(1);
}
void TokenSTACK::parse() 
{
  int i;
  char buff[100];
  
  stackPointer = 0;
  rpnN = 0;
  for(i=0;i<N;i++) 
  {
    if(cmpr(input[i],"(")==1)  
    {
    	push(input[i]); 
    	continue; 
    }
    if(cmpr(input[i],"OR")==1)  
    {
    	push(input[i]); 
    	continue; 
    }
    if(cmpr(input[i],"AND")==1)  
    {
        if(cmpr(stack[stackPointer],"AND")==1)
        {
        	pop(buff);
        	addOutput(buff);
        	push(input[i]);
        	continue;
        }
    	push(input[i]); 
    	continue; 
    }
    if(cmpr(input[i],")")==1)  
    {
        while(cmpr(stack[stackPointer],"(")==0) 
        {
        	pop(buff);
        	addOutput(buff);
        } 
        pop(buff);
    	continue; 
    }
    addOutput(input[i]);	
  }
  while(stackPointer>0) 
  {
  	pop(buff);
  	addOutput(buff);
  }
}

    