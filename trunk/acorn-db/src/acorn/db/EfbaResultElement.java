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
@Table(name = "EFBARESULTELEMENT")
@NamedQueries({@NamedQuery(name = "EfbaResultElement.findById", query = "SELECT e FROM EfbaResultElement e WHERE e.id = :id"), 
@NamedQuery(name = "EfbaResultElement.findByFlux", query = "SELECT e FROM EfbaResultElement e WHERE e.flux = :flux")})
public class EfbaResultElement implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;
    
    @Column(name = "FLUX", nullable = false)
    private float flux;
    
    @JoinColumn(name = "REACTION", referencedColumnName = "ID")
    @ManyToOne
    private EReaction reaction;
    
    @JoinColumn(name = "TASK", referencedColumnName = "ID")
    @ManyToOne
    private ETask task;

    public EfbaResultElement() {
    }

    public EfbaResultElement(Integer id) {
        this.id = id;
    }

    public EfbaResultElement(Integer id, float flux) {
        this.id = id;
        this.flux = flux;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EfbaResultElement)) {
            return false;
        }
        EfbaResultElement other = (EfbaResultElement) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "acorn.db.EfbaResultElement[id=" + getId() + "]";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public float getFlux() {
        return flux;
    }

    public void setFlux(float flux) {
        this.flux = flux;
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
