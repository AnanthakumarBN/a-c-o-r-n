/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.db;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author Mateusza
 */
@Entity
@NamedQueries({
    @NamedQuery(name="EVisPlace.getByName", query="select p from EVisPlace p where p.name=:name")
})
public class EVisPlace implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String sid;
    @ManyToOne
    private EVisualization visualization;
    private int x;
    private int y;
//    @OneToMany(cascade=CascadeType.ALL, mappedBy="source")
//    private Collection<EVisArcReactant> sourceArc;
//    @OneToMany(cascade=CascadeType.ALL, mappedBy="target")
//    private Collection<EVisArc> targetInArc;
//    
//    
    public EVisPlace() {
    }

    public EVisPlace(String name, String sid, EVisualization visualization, int x, int y) {
        this.name = name;
        this.sid = sid;
        this.visualization = visualization;
        this.x = x;
        this.y = y;
    }
//
//    public EVisPlace(String name, String sid, EVisualization visualization, int x, int y,  Collection<EVisArcReactant> rl) {
//        this.name = name;
//        this.sid = sid;
//        this.visualization = visualization;
//        this.x = x;
//        this.y = y;
//        this.sourceArc = rl;
//        
//    }

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
//        return sourceArc;
//    }
//
//    public void setSourceArc(Collection<EVisArcReactant> sourceArc) {
//        this.sourceArc = sourceArc;
//    }
//    
//    public void addArcReactant(EVisArcReactant arc){
//        Collection<EVisArcReactant> arcCol =  getSourceArc();
//        arcCol.add(arc);
//        setSourceArc(arcCol);
//    }

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

    public EVisualization getVisualization() {
        return visualization;
    }

    public void setVisualization(EVisualization visualization) {
        this.visualization = visualization;
    }

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
        if (!(object instanceof EVisPlace)) {
            return false;
        }
        EVisPlace other = (EVisPlace) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "acorn.db.EVisPlace[id=" + id + "]";
    }
}
