/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package acorn.model;

import acorn.db.EVisualization;

/**
 *
 * @author Mateusza
 */
public class Visualization {
    
    private String index;
    private String sid;
    private String name;
    private EVisualization visualization;
    
   public Visualization(EVisualization vis, int ind){
        this.index = Integer.toString(ind);
        //index < 0 - makes the difference between species and reactions in radio button on parameters jsp page
        this.sid = vis.getName();
        this.name = vis.getName();
        this.visualization = vis;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EVisualization getVisualization() {
        return visualization;
    }

    public void setVisualization(EVisualization vis) {
        this.visualization = vis;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

}
