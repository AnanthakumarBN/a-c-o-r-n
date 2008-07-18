/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package acorn.model;

import acorn.db.*;

/**
 *
 * @author marcin
 */
public class Species {
    
    private String index;
    private String sid;
    private String name;
    private ESpecies species;
    
    Species(ESpecies species, int ind){
        this.index = Integer.toString(-ind);
        //index < 0 - makes the difference between species and reactions in radio button on parameters jsp page
        this.sid = species.getSid();
        this.name = species.getName();
        this.species = species;
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

    public ESpecies getSpecies() {
        return species;
    }

    public void setSpecies(ESpecies species) {
        this.species = species;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

}
