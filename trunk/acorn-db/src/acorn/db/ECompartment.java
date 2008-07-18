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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import static javax.persistence.GenerationType.IDENTITY;

/**
 *
 * @author lukasz
 */
@Entity
@Table(name = "ECOMPARTMENT")
@NamedQueries({@NamedQuery(name = "ECompartment.findById", query = "SELECT e FROM ECompartment e WHERE e.id = :id"), 
@NamedQuery(name = "ECompartment.findBySid", query = "SELECT e FROM ECompartment e WHERE e.sid = :sid")})
public class ECompartment implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;
    
    @Column(name = "SID", nullable = false)
    private String sid;
    
    @JoinColumn(name = "OUTSIDE", referencedColumnName = "ID")
    @ManyToOne
    private ECompartment outside;
    
    @JoinColumn(name = "METABOLISM", referencedColumnName = "ID", nullable = false)
    @ManyToOne
    private EMetabolism metabolism;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "outside")
    private Collection<ECompartment> eCompartmentCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "compartment")
    private Collection<ESpecies> eSpeciesCollection;
    
    public ECompartment() {
    }

    public ECompartment(Integer id) {
        this.id = id;
    }

    public ECompartment(Integer id, String sid) {
        this.id = id;
        this.sid = sid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ECompartment)) {
            return false;
        }
        ECompartment other = (ECompartment) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "acorn.db.ECompartment[id=" + getId() + "]";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public ECompartment getOutside() {
        return outside;
    }

    public void setOutside(ECompartment outside) {
        this.outside = outside;
    }

    public EMetabolism getMetabolism() {
        return metabolism;
    }

    public void setMetabolism(EMetabolism metabolism) {
        this.metabolism = metabolism;
    }

    public Collection<ECompartment> getECompartmentCollection() {
        return eCompartmentCollection;
    }

    public void setECompartmentCollection(Collection<ECompartment> eCompartmentCollection) {
        this.eCompartmentCollection = eCompartmentCollection;
    }

    public Collection<ESpecies> getESpeciesCollection() {
        return eSpeciesCollection;
    }

    public void setESpeciesCollection(Collection<ESpecies> eSpeciesCollection) {
        this.eSpeciesCollection = eSpeciesCollection;
    }

}
