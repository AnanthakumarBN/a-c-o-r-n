/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package acorn.model;

import acorn.db.EVisualization;
import acorn.userManagement.UserManager;

/**
 *
 * @author Mateusza
 */
public class Visualization {
    
    private String index;
    private String sid;
    private String qualifiedName;
    private EVisualization visualization;
    
   public Visualization(EVisualization vis, int ind){
        this.index = Integer.toString(ind);
        this.sid = vis.getId().toString();
        this.qualifiedName = vis.getQualifiedName(UserManager.getCurrentUser());
        this.visualization = vis;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(String name) {
        this.qualifiedName = name;
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
}
