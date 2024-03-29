/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package acorn.db;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import static javax.persistence.InheritanceType.JOINED;
import javax.persistence.Table;

/**
 *
 * @author lukasz
 */
@Entity
@Table(name="EKGENEDATA")
public class EKgeneData extends EMethodData implements Serializable{
    @JoinColumn(name = "REACTION", referencedColumnName = "ID")
    @ManyToOne
    private EReaction reaction;
    
    @JoinColumn(name = "SPECIES", referencedColumnName = "ID")
    @ManyToOne
    private ESpecies species;
    
    @Column(name = "GENE", nullable = false)
    private String gene;

    public EKgeneData() {
    }
       
    public EReaction getReaction() {
        return reaction;
    }

    public void setReaction(EReaction reaction) {
        this.reaction = reaction;
    }

    public ESpecies getSpecies() {
        return species;
    }

    public void setSpecies(ESpecies species) {
        this.species = species;
    }

    public String getGene() {
        return gene;
    }

    public void setGene(String gene) {
        this.gene = gene;
    }
    
    public String getObjFunctionSid()
    {
        if(species != null)
        {
            if(species.getBoundaryCondition())
                return species.getSid() + "_xt";
            else
                return species.getSid();
        }
        else if(reaction != null)
            return reaction.getSid();
        else
            return null;
    }
    
    public String getObjFunctionName()
    {
        if(species != null)
            return species.getName();
        else if(reaction != null)
            return reaction.getName();
        else
            return null;
    }

}