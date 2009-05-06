/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.db;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Mateusza
 */
@Entity
@Table(name="EVisArcProduct")
public class EVisArcProduct extends EVisArc implements Serializable {

//    private static final long serialVersionUID = 1L;
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
    @ManyToOne
    private EVisTransition source_p;
    @ManyToOne
    private EVisPlace target_p;
    @ManyToOne
    private EVisualization visualization;

    public EVisArcProduct() {
        super();
    }

    public EVisArcProduct(EVisTransition source, EVisPlace target, EVisualization visualization) {
        super();
        this.source_p = source;
        this.target_p = target;
        this.visualization = visualization;
    }

    public EVisArcProduct(String sid, EVisualization vis, EVisPlace place, EVisTransition transition) {
        super(sid);
        this.source_p = transition;
        this.target_p = place;
        this.visualization = vis;
    }

    public EVisTransition getSource_p() {
        return source_p;
    }

    public void setSource_p(EVisTransition source_p) {
        this.source_p = source_p;
    }

    public EVisPlace getTarget_p() {
        return target_p;
    }

    public void setTarget_p(EVisPlace target_p) {
        this.target_p = target_p;
    }

    public EVisTransition getSource() {
        return source_p;
    }

    public void setSource(EVisTransition source) {
        this.source_p = source;
    }

    public EVisPlace getTarget() {
        return target_p;
    }

    public void setTarget(EVisPlace target) {
        this.target_p = target;
    }

    public EVisualization getVisualization() {
        return visualization;
    }

    public void setVisualization(EVisualization visualization) {
        this.visualization = visualization;
    }

    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public void setId(Long id) {
         super.setId(id);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (super.getId() != null ? super.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EVisArcProduct)) {
            return false;
        }
        EVisArcProduct other = (EVisArcProduct) object;
        if ((getId() == null && other.getId() != null) || (getId() != null && !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "acorn.db.EVisArcProduct[id=" + getId() + "]";
    }
}
