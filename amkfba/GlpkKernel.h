/* 02/05/06 The interface to GLPK library */

extern "C" 
{
  #include "glpk/glpk.h"
}

#define GLPKMAX 1
#define GLPKMIN 0

class GlpkKernel 
{
    private:
      LPX *lp;
	public:
	  void glpkTest(char *path);
	  void read_mps(char *path);
	  void set_obj_dir(int d);
	  int simplex();
	  double get_obj_val();
	  void create_prob();
	  void delete_prob();
	  void set_prob_name(char *name);
	  void set_row_name(int i, char *name);
	  void set_col_name(int i, char *name);
	  int add_rows(int i);
	  int add_cols(int i);
	  void set_row_bnds(int i, int type, double lb, double ub);
	  void set_col_bnds(int i, int type, double lb, double ub);
	  void set_obj_coef(int j, double coef);
	  void set_mat_row(int i, int len, int ind[], double val[]);
	  int get_mat_row(int i, int ind[], double val[]);
	  int get_mat_col(int i, int ind[], double val[]);
	  int get_num_rows();
	  int get_num_cols();
	  double get_obj_coef(int i);
	  double get_col_prim(int i);
	  
	  double get_col_ub(int i);
	  double get_col_lb(int i);
	  double get_row_ub(int i);
	  double get_row_lb(int i);
	  int get_col_type(int i);
	  int get_row_type(int i);
	  int get_status();
	  int warm_up();
	  void adv_basis();
	  void std_basis();
	  void set_int_parm(int p,int val);
	  
	  int interior();
	  int ipt_status();
	  double ipt_obj_val();
	  double ipt_col_prim(int i);
	  
	  void load_matrix(int ne,int ia[],int ja[],double ar[]);
	  GlpkKernel();
	  ~GlpkKernel() 
	  {
	  	delete_prob();
	  }
};
