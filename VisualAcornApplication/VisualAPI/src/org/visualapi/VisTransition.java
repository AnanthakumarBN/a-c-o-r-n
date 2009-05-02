/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.visualapi;

/**
 *
 * @author markos
 */
public class VisTransition extends VisNode {

    public VisTransition() {
        super();
    }

    @Override
    public boolean isPlace() {
        return false;
    }

    @Override
    public boolean isTransition() {
        return true;
    }
    

    @Override
    public String toString() {
        return "Transition: " + sid;
    }
}
