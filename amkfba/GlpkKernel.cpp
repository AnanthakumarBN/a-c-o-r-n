#include <stdio.h>
#include <stdlib.h>
#include "GlpkKernel.h"


GlpkKernel::GlpkKernel() 
{
	lp = lpx_create_prob();
}

void GlpkKernel::read_mps(char *path)
{
	lp = lpx_read_mps(path);
}

void GlpkKernel::set_obj_dir(int d) 
{
	if(d==GLPKMAX) 
	{
		lpx_set_obj_dir(lp, LPX_MAX);
	}
	else 
	{
	    lpx_set_obj_dir(lp, LPX_MIN);	
	}
}

int GlpkKernel::simplex() 
{
	return(lpx_simplex(lp));
}

double GlpkKernel::get_obj_val() 
{
	return(lpx_get_obj_val(lp));
}

void GlpkKernel::delete_prob() 
{
	int i,nr;
	int num[10000];
	
	nr = get_num_rows();
	for(i=1;i<=nr;i++) 
	          num[i] = i;
	lpx_del_rows(lp,nr,num);
	nr = get_num_cols();
	for(i=1;i<=nr;i++) 
	            num[i] = i;
	lpx_del_cols(lp,nr,num);
	lpx_delete_prob(lp);
}

void GlpkKernel::create_prob()
{
	lp=lpx_create_prob();
}

void GlpkKernel::set_prob_name(char *name)
{
	lpx_set_prob_name(lp,name);
}

int GlpkKernel::add_rows(int i) 
{
	return(lpx_add_rows(lp,i));
}

int GlpkKernel::add_cols(int i) 
{
	return(lpx_add_cols(lp,i));
}

void GlpkKernel::set_row_name(int i, char *name) 
{
	lpx_set_row_name(lp,i,name);
}

void GlpkKernel::set_col_name(int i,char *name) 
{
	lpx_set_col_name(lp,i,name);
}

void GlpkKernel::set_row_bnds(int i, int type, double lb, double ub)
{
	lpx_set_row_bnds(lp,i,type,lb,ub);
}

void GlpkKernel::set_col_bnds(int i, int type, double lb, double ub)
{
	lpx_set_col_bnds(lp,i,type,lb,ub);
}

void GlpkKernel::load_matrix(int ne,int ia[],int ja[],double ar[]) 
{
	lpx_load_matrix(lp,ne,ia,ja,ar);
}

void GlpkKernel::set_obj_coef(int j, double coef) 
{
	lpx_set_obj_coef(lp,j,coef);
}

void GlpkKernel::set_mat_row(int i, int len, int ind[], double val[]) 
{
	lpx_set_mat_row(lp,i,len,ind,val);
}

int GlpkKernel::get_mat_row(int i, int ind[], double val[]) 
{
	return(lpx_get_mat_row(lp,i,ind,val));
}

int GlpkKernel::get_mat_col(int i, int ind[], double val[]) 
{
	return(lpx_get_mat_col(lp,i,ind,val));
}

int GlpkKernel::get_num_rows() 
{
	return(lpx_get_num_rows(lp));
}

double GlpkKernel::get_obj_coef(int i) 
{
	return(lpx_get_obj_coef(lp,i));
}

double GlpkKernel::get_col_ub(int i) 
{
	return(lpx_get_col_ub(lp,i));
}

double GlpkKernel::get_col_lb(int i)
{
	return(lpx_get_col_lb(lp,i));
}

double GlpkKernel::get_row_ub(int i)
{
    return(lpx_get_col_ub(lp,i));	
}

double GlpkKernel::get_row_lb(int i)
{
	return(lpx_get_row_lb(lp,i));
}

int GlpkKernel::get_num_cols() 
{
	return(lpx_get_num_cols(lp));
}

int GlpkKernel::get_col_type(int i) 
{
	return(lpx_get_col_type(lp,i));
}

int GlpkKernel::get_row_type(int i)
{
	return(lpx_get_row_type(lp,i));
}

int GlpkKernel::get_status() 
{
	return(lpx_get_status(lp));
}

double GlpkKernel::get_col_prim(int i)
{
	return(lpx_get_col_prim(lp,i));
}

int GlpkKernel::warm_up()
{
	return(lpx_warm_up(lp));
}

void GlpkKernel::adv_basis() 
{
	lpx_adv_basis(lp);
}

void GlpkKernel::std_basis()
{
	lpx_std_basis(lp);
}

void GlpkKernel::set_int_parm(int p,int val) 
{
	lpx_set_int_parm(lp,p,val);
}

int GlpkKernel::interior() 
{
	return(lpx_interior(lp));
}

int GlpkKernel::ipt_status() 
{
	return(lpx_ipt_status(lp));
}

double GlpkKernel::ipt_obj_val() 
{
    return(lpx_ipt_obj_val(lp));	
}

double GlpkKernel::ipt_col_prim(int i) 
{
	return(lpx_ipt_col_prim(lp,i));
}
void GlpkKernel::glpkTest(char *path) 
{
   double Z;
   
   lp = lpx_read_mps(path);
   lpx_set_obj_dir(lp, LPX_MAX);
   lpx_simplex(lp);
   Z = lpx_get_obj_val(lp);
   printf("\nZ = %g\n", Z);
      
   lpx_delete_prob(lp);
   	
}

