/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package acorn.db;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import static javax.persistence.GenerationType.IDENTITY;

/**
 *
 * @author lukasz
 */
@Entity
@Table(name = "EREACTANT")
@NamedQueries({
@NamedQuery(name = "EReactant.findById", query = "SELECT e FROM EReactant e WHERE e.id = :id"),
@NamedQuery(name = "EReactant.findByStoichiometry", query = "SELECT e FROM EReactant e WHERE e.stoichiometry = :stoichiometry"),
@NamedQuery(name = "EReactant.getReactionBySpecies",
        query="SELECT r1.reaction FROM EReactant r1 WHERE r1.species = :spec1"),
@NamedQuery(name = "EReactant.getReactionBy2Species", 
        query = "SELECT r1.reaction FROM EReactant r1, EReactant r2 WHERE r1.reaction = r2.reaction and r1.species = :spec1 and r2.species = :spec2"),
@NamedQuery(name= "EReactant.getSpeciesByReaction",
        query="SELECT r.species FROM EReactant r WHERE r.reaction=:reaction")
})
public class EReactant implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;
    
    @Column(name = "STOICHIOMETRY", nullable = false)
    private double stoichiometry;
    
    @JoinColumn(name = "REACTION", referencedColumnName = "ID", nullable = false)
    @ManyToOne
    private EReaction reaction;
    
    @JoinColumn(name = "SPECIES", referencedColumnName = "ID", nullable = false)
    @ManyToOne
    private ESpecies species;

    public EReactant() {
    }

    public EReactant(Integer id) {
        this.id = id;
    }

    public EReactant(Integer id, double stoichiometry) {
        this.id = id;
        this.stoichiometry = stoichiometry;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EReactant)) {
            return false;
        }
        EReactant other = (EReactant) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "acorn.db.EReactant[id=" + getId() + "]";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getStoichiometry() {
        return stoichiometry;
    }

    public void setStoichiometry(double stoichiometry) {
        this.stoichiometry = stoichiometry;
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

}
