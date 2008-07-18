#define STACKSIZE 1000
#define TOKSIZE 100



class TokenSTACK 
{
 protected:
    char input[STACKSIZE][TOKSIZE];
    int N;
	char stack[STACKSIZE][TOKSIZE];
	int stackPointer;
	char output[STACKSIZE][TOKSIZE];
	int rpnN;
    
 public:
    void push(char *str) 
    {
        int i;
        
        stackPointer++;
    	for(i=0;str[i]!=0;i++) 
    	   stack[stackPointer][i] = str[i];	
        stack[stackPointer][i] = 0;
    }
    void addOutput(char *str) 
    {
        int i;
        
    	for(i=0;str[i]!=0;i++) 
    	   output[rpnN][i] = str[i];	
        output[rpnN][i] = 0;
        rpnN++;
    }
    void pop(char *str) 
    {
    	int i;
	
    	for(i=0;stack[stackPointer][i]!=0;i++) 
    	           str[i]=stack[stackPointer][i];
    	str[i]=0;
    	stackPointer--;
    }
    int size() 
    {
    	return(N);
    }
    int rpnSize() 
    {
    	 return(rpnN);
    }
    int getStackPointer()
    {
    	return(stackPointer);
    }
    void getRPN(char *str,int n) 
    {
    	int i;
    	for(i=0;output[n][i]!=0;i++) str[i] = output[n][i];
    	str[i] = 0;
    }
    void get(char *str,int n);
    int cmpr(char *str1, char *str2);
    TokenSTACK();
    void split(char *str);
    void splitNoBrackets(char *str);
    int extract(char *str, char *tag1, char *tag2);
    void parse();
};
