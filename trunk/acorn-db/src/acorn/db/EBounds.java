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
@Table(name = "EBOUNDS")
@NamedQueries({
@NamedQuery(name = "EBounds.findById", query = "SELECT e FROM EBounds e WHERE e.id = :id"), 
@NamedQuery(name = "EBounds.findByLowerBound", query = "SELECT e FROM EBounds e WHERE e.lowerBound = :lowerBound"), 
@NamedQuery(name = "EBounds.findByUpperBound", query = "SELECT e FROM EBounds e WHERE e.upperBound = :upperBound"),
@NamedQuery(name = "EBounds.findByModel", query = "SELECT e FROM EBounds e WHERE e.model = :model"),
@NamedQuery(name = "EBounds.findByParent", query = "SELECT b FROM EBounds b WHERE b.model = :parent AND " +
                                                   "NOT EXISTS (SELECT c FROM EBounds c WHERE c.model = :model AND b.reaction = c.reaction)")})
public class EBounds implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;
    
    @Column(name = "LOWER_BOUND", nullable = false)
    private float lowerBound;
    
    @Column(name = "UPPER_BOUND", nullable = false)
    private float upperBound;
    
    @JoinColumn(name = "MODEL", referencedColumnName = "ID", nullable = false)
    @ManyToOne
    private EModel model;
    
    @JoinColumn(name = "REACTION", referencedColumnName = "ID", nullable = false)
    @ManyToOne
    private EReaction reaction;

    public EBounds() {
    }

    public EBounds(Integer id) {
        this.id = id;
    }

    public EBounds(Integer id, float lowerBound, float upperBound) {
        this.id = id;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EBounds)) {
            return false;
        }
        EBounds other = (EBounds) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "acorn.db.EBounds[id=" + getId() + "]";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public float getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(float lowerBound) {
        this.lowerBound = lowerBound;
    }

    public float getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(float upperBound) {
        this.upperBound = upperBound;
    }

    public EModel getModel() {
        return model;
    }

    public void setModel(EModel model) {
        this.model = model;
    }

    public EReaction getReaction() {
        return reaction;
    }

    public void setReaction(EReaction reaction) {
        this.reaction = reaction;
    }
    
    public EBounds copy() {
        EBounds b = new EBounds(getId());
        
        b.setLowerBound(lowerBound);
        b.setUpperBound(upperBound);
        b.setModel(model);
        b.setReaction(reaction);
        
        return b;
    }

}
