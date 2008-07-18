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
@Table(name = "EFVARESULTELEMENT")
@NamedQueries({@NamedQuery(name = "EfvaResultElement.findById", query = "SELECT e FROM EfvaResultElement e WHERE e.id = :id"), 
@NamedQuery(name = "EfvaResultElement.findByMinFlux", query = "SELECT e FROM EfvaResultElement e WHERE e.minFlux = :minFlux"), 
@NamedQuery(name = "EfvaResultElement.findByMaxFlux", query = "SELECT e FROM EfvaResultElement e WHERE e.maxFlux = :maxFlux")})
public class EfvaResultElement implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;
    
    @Column(name = "MIN_FLUX", nullable = false)
    private float minFlux;
    
    @Column(name = "MAX_FLUX", nullable = false)
    private float maxFlux;
    
    @JoinColumn(name = "REACTION", referencedColumnName = "ID")
    @ManyToOne
    private EReaction reaction;
    
    @JoinColumn(name = "TASK", referencedColumnName = "ID")
    @ManyToOne
    private ETask task;

    public EfvaResultElement() {
    }

    public EfvaResultElement(Integer id) {
        this.id = id;
    }

    public EfvaResultElement(Integer id, float minFlux, float maxFlux) {
        this.id = id;
        this.minFlux = minFlux;
        this.maxFlux = maxFlux;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EfvaResultElement)) {
            return false;
        }
        EfvaResultElement other = (EfvaResultElement) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "acorn.db.EfvaResultElement[id=" + getId() + "]";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public float getMinFlux() {
        return minFlux;
    }

    public void setMinFlux(float minFlux) {
        this.minFlux = minFlux;
    }

    public float getMaxFlux() {
        return maxFlux;
    }

    public void setMaxFlux(float maxFlux) {
        this.maxFlux = maxFlux;
    }

    public EReaction getReaction() {
        return reaction;
    }

    public void setReaction(EReaction reaction) {
        this.reaction = reaction;
    }

    public ETask getTask() {
        return task;
    }

    public void setTask(ETask task) {
        this.task = task;
    }

}
