/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.visualapi;

/**
 *
 * @author markos
 */
public class VisPlace extends VisNode{

    public VisPlace() {
        super();
    }


    @Override
    public boolean isPlace(){
        return true;
    }

    @Override
    public boolean isTransition(){
        return false;
    }


    @Override
    public String toString(){
        return "Place: "+sid;
    }
}
