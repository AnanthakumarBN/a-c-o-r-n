#define STACKSIZE 1000
#define OPNUM 3


class ReactionSTACK 
{
 protected:
	int stack[STACKSIZE];
	int stackPointer;
	int truthTable[OPNUM][2][2];
    
 public:
    void push(int n) 
    {
        stackPointer++;
    	stack[stackPointer] = n;
    }
    int pop() 
    {
    	int result;
	
    	result = stack[stackPointer];
    	stackPointer--;
    	return result;
    }
    ReactionSTACK();
    int evaluate(int *expr, int *state);
};

