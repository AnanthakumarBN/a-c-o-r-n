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
@Table(name = "EPRODUCT")
@NamedQueries({@NamedQuery(name = "EProduct.findById", query = "SELECT e FROM EProduct e WHERE e.id = :id"), 
@NamedQuery(name = "EProduct.findByStoichiometry", query = "SELECT e FROM EProduct e WHERE e.stoichiometry = :stoichiometry"),
@NamedQuery(name = "EProduct.getReactionBySpecies",
        query="SELECT r1.reaction FROM EProduct r1 WHERE r1.species = :spec1"),
@NamedQuery(name = "EProduct.getReactionBy2Species",
        query = "SELECT r1.reaction FROM EProduct r1, EProduct r2 WHERE r1.reaction = r2.reaction and r1.species = :spec1 and r2.species = :spec2"),
@NamedQuery(name = "EProduct.getSpeciesByReaction",
        query = "SELECT p.species FROM EProduct p WHERE p.reaction=:reaction")
})
public class EProduct implements Serializable {
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

    public EProduct() {
    }

    public EProduct(Integer id) {
        this.id = id;
    }

    public EProduct(Integer id, double stoichiometry) {
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
        if (!(object instanceof EProduct)) {
            return false;
        }
        EProduct other = (EProduct) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "acorn.db.EProduct[id=" + getId() + "]";
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
