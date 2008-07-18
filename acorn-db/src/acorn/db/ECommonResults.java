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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import static javax.persistence.GenerationType.IDENTITY;

/**
 *
 * @author lukasz
 */
@Entity
@Table(name = "ECOMMONRESULTS")
@NamedQueries({@NamedQuery(name = "ECommonResults.findById", query = "SELECT e FROM ECommonResults e WHERE e.id = :id"), 
@NamedQuery(name = "ECommonResults.findByStatus", query = "SELECT e FROM ECommonResults e WHERE e.status = :status"), 
@NamedQuery(name = "ECommonResults.findByGrowthRate", query = "SELECT e FROM ECommonResults e WHERE e.growthRate = :growthRate")})
public class ECommonResults implements Serializable {
    public static String statusOptimal = "Optimal";
    public static String statusUndefined = "Undefined";
    public static String statusFeasible = "Feasible";
    public static String statusInfeasible = "Infeasible";
    public static String statusNonFeasible = "Non feasible";
    public static String statusUnbounded = "Unbounded";
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;
    
    @Column(name = "STATUS", nullable = false)
    private String status;
    
    @Column(name = "GROWTH_RATE", nullable = false)
    private float growthRate;
    
    @JoinColumn(name = "TASK", referencedColumnName = "ID", nullable = false)
    @OneToOne
    private ETask task;

    public ECommonResults() {
    }

    public ECommonResults(Integer id) {
        this.id = id;
    }

    public ECommonResults(Integer id, String status, float growthRate) {
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
        if (!(object instanceof ECommonResults)) {
            return false;
        }
        ECommonResults other = (ECommonResults) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "acorn.db.ECommonResults[id=" + getId() + "]";
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

    public ETask getTask() {
        return task;
    }

    public void setTask(ETask task) {
        this.task = task;
    }

}
