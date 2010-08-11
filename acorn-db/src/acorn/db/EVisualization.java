/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.db;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
    @NamedQuery(name = "EVisualization.getByNameForLogin", query = "select v from EVisualization v where v.name=:name and v.owner.login=:login"),
    @NamedQuery(name = "EVisualization.getByNameForGuest", query = "select v from EVisualization v where v.name=:name and v.owner IS NULL"),
    @NamedQuery(name = "EVisualization.getByModel", query = "select v from EVisualization v where v.model=:model"),
    @NamedQuery(name = "EVisualization.getByModelForLogin", query = "select v from EVisualization v where v.model=:model and v.owner.login=:login"),
    @NamedQuery(name = "EVisualization.getByModelForGuest", query = "select v from EVisualization v where v.model=:model and v.owner IS NULL"),
    @NamedQuery(name = "EVisualization.getByModelShared", query = "select v from EVisualization v where v.model=:model and v.shared=true"),
    @NamedQuery(name = "EVisualization.getPlaces", query = "select v.places from EVisualization v where v.id=:id"),
    @NamedQuery(name = "EVisualization.getTransitions", query = "select v.transitions from EVisualization v where v.id=:id"),
    @NamedQuery(name = "EVisualization.getArcResources", query = "select v.arcResource from EVisualization v where v.id=:id"),
    @NamedQuery(name = "EVisualization.getArcProducts", query = "select v.arcProduct from EVisualization v where v.id=:id"),
    @NamedQuery(name = "EVisualization.getAllVisualizations", query = "select v from EVisualization v")
})
public class EVisualization implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @JoinColumn(name = "OWNER", referencedColumnName = "ID", nullable = true)
    @ManyToOne(fetch = FetchType.EAGER)
    private EUser owner;
    @Column(name = "SHARED", nullable = false)
    private boolean shared;
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
        setName(name);
        setPlaces(places);
    }

    public EVisualization(String name, EUser owner) {
        setName(name);
        setOwner(owner);
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
    public static final char VIS_NAME_SEPARATOR = '.';
    public static final char VIS_NAME_SEPARATOR_DOT = '_';

    public String getQualifiedName() {
        if (getOwner() == null) {
            return VIS_NAME_SEPARATOR + getName();
        } else {
            return getOwner().getLogin() + VIS_NAME_SEPARATOR + getName();
        }
    }

    public String getQualifiedNameForDot() {
        if (getOwner() == null) {
            return VIS_NAME_SEPARATOR_DOT + getName();
        } else {
            return getOwner().getLogin() + VIS_NAME_SEPARATOR_DOT + getName();
        }
    }

    public String getQualifiedName(EUser forWho) {
        if (forWho == null) { //gest
            if (getOwner() == null) {
                return getName();
            } else {
                return getQualifiedName();
            }
        } else if (forWho.equals(getOwner())) {//the same user
            //we know that this is not quest
            return getName();
        } else {//different user
            //we know that this is not quest
            return getQualifiedName();
        }
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

    public EUser getOwner() {
        return owner;
    }

    public void setOwner(EUser owner) {
        this.owner = owner;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
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
