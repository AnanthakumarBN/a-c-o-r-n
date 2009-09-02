/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.db;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Markos
 */
@Entity
@Table(name="EVisArcReactant")
@NamedQueries({
    @NamedQuery(name="EVisArcReactant.getByReaction", query="select r from EVisArcReactant r where r.target_r.reaction = :reaction and r.visualization = :visualization")
})
public class EVisArcReactant extends EVisArc implements Serializable {

//    private static final long serialVersionUID = 1L;
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
    @ManyToOne
    private EVisPlace source_r;
    @ManyToOne
    private EVisTransition target_r;
    @ManyToOne
    private EVisualization visualization;
    
    public EVisArcReactant(String sid) {
        super(sid);
    }

    public EVisArcReactant(String sid, EVisualization visualization, EVisPlace source, EVisTransition target) {
        super(sid);
        this.source_r = source;
        this.target_r = target;
        this.visualization = visualization;
    }

    public EVisArcReactant(EVisPlace source_r, EVisTransition target_r, EVisualization visualization) {
        super();
        this.source_r = source_r;
        this.target_r = target_r;
        this.visualization = visualization;
    }



    public EVisArcReactant() {
    }

    public EVisPlace getSource() {
        return source_r;
    }

    public void setSource(EVisPlace source) {
        this.source_r = source;
    }

    public EVisTransition getTarget() {
        return target_r;
    }

    public void setTarget(EVisTransition target) {
        this.target_r = target;
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
        hash += (getId() != null ? (getId()).hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EVisArcReactant)) {
            return false;
        }
        EVisArcReactant other = (EVisArcReactant) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "acorn.db.EVisArcReactant[id=" + getId() + "]";
    }
}
