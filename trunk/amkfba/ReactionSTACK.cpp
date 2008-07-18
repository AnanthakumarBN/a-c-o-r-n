#include <stdlib.h>
#include "ReactionSTACK.h"

ReactionSTACK::ReactionSTACK() 
{
    int i = 0;
    
	stackPointer = 0;
	for(i=0;i<STACKSIZE;i++) stack[i] = 0;
	
	/* AND operator */
	truthTable[1][0][0] = 0;
	truthTable[1][1][0] = 0;
	truthTable[1][0][1] = 0;
	truthTable[1][1][1] = 1;
	
	/* OR operator */
	truthTable[2][0][0] = 0;
	truthTable[2][1][0] = 1;
	truthTable[2][0][1] = 1;
	truthTable[2][1][1] = 1;
	
}

int ReactionSTACK::evaluate(int *expr, int *state) 
{
  int i,op1,op2;
  
  push(state[expr[1]]);
  push(state[expr[2]]);
  for(i=3;i<=expr[0];i++) 
  {
  	if(expr[i]<0) 
  	{
  	  op1 = pop();
  	  op2 = pop();
  	  push(truthTable[-1*expr[i]][op1][op2]);	
  	}
  	else 
  	{
  		push(state[expr[i]]);
  	}
  }
  
  return(pop());
}

