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
@Table(name = "ERSCANRESULTELEMENT")
@NamedQueries({@NamedQuery(name = "ErscanResultElement.findById", query = "SELECT e FROM ErscanResultElement e WHERE e.id = :id"), 
@NamedQuery(name = "ErscanResultElement.findByStatus", query = "SELECT e FROM ErscanResultElement e WHERE e.status = :status"), 
@NamedQuery(name = "ErscanResultElement.findByGrowthRate", query = "SELECT e FROM ErscanResultElement e WHERE e.growthRate = :growthRate")})
public class ErscanResultElement implements Serializable {
    public static String statusOptimal = "optimal";
    public static String statusUndefined = "undefined";
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;
    
    @Column(name = "STATUS", nullable = false)
    private String status;
    
    @Column(name = "GROWTH_RATE", nullable = false)
    private float growthRate;
    
    @JoinColumn(name = "REACTION", referencedColumnName = "ID", nullable = false)
    @ManyToOne
    private EReaction reaction;
    
    @JoinColumn(name = "TASK", referencedColumnName = "ID", nullable = false)
    @ManyToOne
    private ETask task;

    public ErscanResultElement() {
    }

    public ErscanResultElement(Integer id) {
        this.id = id;
    }

    public ErscanResultElement(Integer id, String status, float growthRate) {
        this.id = id;
        this.status = status;
        this.growthRate = growthRate;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ErscanResultElement)) {
            return false;
        }
        ErscanResultElement other = (ErscanResultElement) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "acorn.db.ErscanResultElement[id=" + getId() + "]";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getGrowthRate() {
        return growthRate;
    }

    public void setGrowthRate(float growthRate) {
        this.growthRate = growthRate;
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
