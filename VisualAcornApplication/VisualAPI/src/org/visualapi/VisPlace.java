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
public class VisPlace extends VisNode{

    public VisPlace() {
        super();
    }

    public VisPlace(String name, String sid, Point location, String xmlSid) {
        super(name, sid, location, xmlSid);
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
