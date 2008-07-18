/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.model;

import acorn.db.*;
import javax.servlet.http.*;
import java.security.*;
import java.util.List;
import java.util.Date;
import java.util.concurrent.Semaphore;
import acorn.errorHandling.ErrorBean;

/**
 *
 * @author dl236088
 */
public class TaskBeanData {

    public Params parameters;
    public String reactionNameFilter;
    public String speciesNameFilter;
    public String genesNameFilter;
    public int reactionsStart;
    public int speciesStart;
    public int genesStart;
    public List<Condition> conditions;
    public List<Condition> originalConditions;
    public List<Condition> filteredConditions;
    public List<Species> species;
    public List<Species> filteredSpecies;
    public List<String> genes;
    public List<String> filteredGenes;
    public EModel model;
    private String selectedReaction;
    private String selectedSpecies;
    private String selectedGene;
    public String taskName;
    public String method;
    public Date lastUse;
    private Semaphore mutex;
    public String errorMessage;

    TaskBeanData(EModel m) {
        super();
        reactionsStart = 0;
        speciesStart = 0;
        genesStart = 0;
        errorMessage = "";
        lastUse = new Date();
        this.model = m;
        mutex = new Semaphore(1);
    }

    public void lock() {
        try {
            mutex.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unlock() {
        mutex.release();
    }

    public Date touch() {
        Date tmp = lastUse;
        lastUse = new Date();
        return tmp;
    }

    public EModel getModel() {
        return model;
    }

    public String getSelectedReaction() {
        return selectedReaction;
    }

    public void setSelectedReaction(String selectedReaction) {
        try {
            this.selectedReaction = selectedReaction;
            parameters.setReactionTarget(selectedReaction);
        } catch (Exception e) {
            ErrorBean.printStackTrace(e);
        }
    }

    public String getSelectedSpecies() {
        return selectedSpecies;
    }

    public void setSelectedSpecies(String selectedSpecies) {
        this.selectedSpecies = selectedSpecies;
        parameters.setSpeciesTarget(selectedSpecies);
    }

    public String getSelectedGene() {
        return selectedGene;
    }

    public void setSelectedGene(String selectedGene) {
        this.selectedGene = selectedGene;
        parameters.setGene(selectedGene);
    }
}
