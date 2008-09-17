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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Mateusza
 */
@Entity
@Table(name = "EVISUALIZATION")
@NamedQueries({
    @NamedQuery(name = "EVisualization.getByName", query = "select v from EVisualization v where v.name=:name"),
    @NamedQuery(name = "EVisualization.getByModel", query = "select v from EVisualization v where v.model=:model"),
    @NamedQuery(name= "EVisualization.getPlaces", query="select v.places from EVisualization v where v.id=:id"),
    @NamedQuery(name= "EVisualization.getTransitions", query="select v.transitions from EVisualization v where v.id=:id"),
    @NamedQuery(name= "EVisualization.getArcResources", query="select v.arcResource from EVisualization v where v.id=:id"),
    @NamedQuery(name= "EVisualization.getArcProducts", query="select v.arcProduct from EVisualization v where v.id=:id")
})
public class EVisualization implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
//    @ManyToOne
//    private EModel model;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "visualization")
    private Collection<EVisPlace> places;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "visualization")
    private Collection<EVisTransition> transitions;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "visualization")
    private Collection<EVisArcReactant> arcResource;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "visualization")
    private Collection<EVisArcProduct> arcProduct;
    @ManyToOne
    private EModel model;

    public EVisualization() {
    }

    public EVisualization(String name, Collection<EVisPlace> places) {
        this.name = name;
        this.places = places;
    }

    public EVisualization(String name) {
        this.name = name;
    }

    public Collection<EVisArcProduct> getArcProduct() {
        return arcProduct;
    }

    public void setArcProduct(Collection<EVisArcProduct> arcProduct) {
        this.arcProduct = arcProduct;
    }

    public EModel getModel() {
        return model;
    }

    public void setModel(EModel model) {
        this.model = model;
    }

    public Collection<EVisArcReactant> getArcResource() {
        return arcResource;
    }

    public void setArcResource(Collection<EVisArcReactant> arcResource) {
        this.arcResource = arcResource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<EVisPlace> getPlaces() {
        return places;
    }

    public void setPlaces(Collection<EVisPlace> places) {
        this.places = places;
    }

    public Collection<EVisTransition> getTransitions() {
        return transitions;
    }

    public void setTransitions(Collection<EVisTransition> transitions) {
        this.transitions = transitions;
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
        if (!(object instanceof EVisualization)) {
            return false;
        }
        EVisualization other = (EVisualization) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "acorn.db.EVisualization[id=" + id + "]";
    }
}
