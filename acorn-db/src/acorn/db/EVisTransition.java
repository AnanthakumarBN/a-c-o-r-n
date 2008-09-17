/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.db;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author Mateusza
 */
@Entity
public class EVisTransition implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String sid;
    private int x;
    private int y;
    @ManyToOne
    private EVisualization visualization;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "source")
//    private Collection<EVisArcReactant> source;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "target")
//    private Collection<EVisArc> targetInArc;
    public EVisTransition() {
    }

    public EVisTransition(String name, String sid, int x, int y, EVisualization visualization) {
        this.name = name;
        this.sid = sid;
        this.x = x;
        this.y = y;
        this.visualization = visualization;
    }

//    public EVisTransition(String name, String sid, int x, int z, EVisualization evis, Collection<EVisArcReactant> rl) {
//        this.name = name;
//        this.sid = sid;
//        this.x = x;
//        this.y = y;
//        this.visualization = evis;
//        this.source =rl;
//    }

    
    public EVisualization getVisualization() {
        return visualization;
    }

    public void setVisualization(EVisualization visualization) {
        this.visualization = visualization;
    }

    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
//
//    public Collection<EVisArcReactant> getSourceArc() {
//        return source;
//    }
//
//    public void setSourceArc(Collection<EVisArcReactant> sourceInArc) {
//        this.source = sourceInArc;
//    }
//    public void addArcReactant(EVisArcReactant arc){
//        Collection<EVisArcReactant> arcCol =  getSourceArc();
//        arcCol.add(arc);
//        setSourceArc(arcCol);
//    }

//    public Collection<EVisArc> getTargetInArc() {
//        return targetInArc;
//    }
//
//    public void setTargetInArc(Collection<EVisArc> targetInArc) {
//        this.targetInArc = targetInArc;
//    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EVisTransition)) {
            return false;
        }
        EVisTransition other = (EVisTransition) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "acorn.db.EVisTransition[id=" + id + "]";
    }
}
