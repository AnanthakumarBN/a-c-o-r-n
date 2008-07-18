/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package acorn.model;

import java.util.List;

/**
 *
 * @author sg236027
 */
public class Params {
    
    
    public Params(){
    }

    public void setReactionTarget(String val){}
    
    public void setSpeciesTarget(String val){}
    
    public void setGene(String val){}
    
    /* IF YOU WANT TO ADD NEW METHOD -> put here setMYVARIABLE() function
     * if your method's parameters are not only reaction, species or gene
     * but also MYVARIABLE (it is required by JSP Page connected with method) */
    
    /* IF YOU WANT TO ADD NEW METHOD -> create MYMETHODParams class
     * (see FBAParams, FVAParams, KGENEParams, RSCANParams) */
     
    
    public boolean isEmpty(){
        return true;
    }
}
