/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.visualapi;

import java.awt.Point;

/**
 *
 * @author markos
 */
public class VisTransition extends VisNode {

    private float flux;

    public VisTransition() {
        super();
        flux = 0;
    }

    public VisTransition(String name, String sid, Point location, String xmlSid) {
        super(name, sid, location, xmlSid);
        flux = 0;
    }

    public VisTransition(String name, String sid, Point location, String xmlSid, float flux) {
        super(name, sid, location, xmlSid);
        this.flux = flux;
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

    /*flux is computation of amkfba for certain task
     */
    public float getFlux() {
        return flux;
    }

    public void setFlux(float flux) {
        this.flux = flux;
    }
    
}
