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
@Table(name = "ESPECIES")
@NamedQueries({@NamedQuery(name = "ESpecies.findById", query = "SELECT e FROM ESpecies e WHERE e.id = :id"), 
@NamedQuery(name = "ESpecies.findBySid", query = "SELECT e FROM ESpecies e WHERE e.sid = :sid"), 
@NamedQuery(name = "ESpecies.findByName", query = "SELECT e FROM ESpecies e WHERE e.name = :name"), 
@NamedQuery(name = "ESpecies.findByCharge", query = "SELECT e FROM ESpecies e WHERE e.charge = :charge"), 
@NamedQuery(name = "ESpecies.findByBoundaryCondition", query = "SELECT e FROM ESpecies e WHERE e.boundaryCondition = :boundaryCondition"),
@NamedQuery(name = "ESpecies.findByModelAndSid",
    query = "SELECT s FROM ESpecies s, EModel m WHERE s.compartment.metabolism.id = m.metabolism.id AND m.name = :modelName and s.sid =:sid"),
@NamedQuery(name = "ESpecies.findByModelName",
    query = "SELECT s FROM ESpecies s, EModel m WHERE s.compartment.metabolism.id = m.metabolism.id AND m.name = :modelName")})

public class ESpecies implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;
    
    @Column(name = "SID", nullable = false)
    private String sid;
    
    @Column(name = "NAME", nullable = false)
    private String name;
    
    @Column(name = "CHARGE", nullable = false)
    private int charge;
    
    @Column(name = "BOUNDARY_CONDITION", nullable = false)
    private boolean boundaryCondition;
    
    @JoinColumn(name = "COMPARTMENT", referencedColumnName = "ID")
    @ManyToOne
    private ECompartment compartment;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "species")
    private Collection<EReactant> eReactantCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "species")
    private Collection<EProduct> eProductCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "species")
    private Collection<EFbaData> eFbaDataCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "species")
    private Collection<ERscanData> eRscanDataCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "species")
    private Collection<EKgeneData> eKgeneDataCollection;
    

    public ESpecies() {
    }

    public ESpecies(Integer id) {
        this.id = id;
    }

    public ESpecies(Integer id, String sid, String name, int charge, boolean boundaryCondition) {
        this.id = id;
        this.sid = sid;
        this.name = name;
        this.charge = charge;
        this.boundaryCondition = boundaryCondition;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ESpecies)) {
            return false;
        }
        ESpecies other = (ESpecies) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "acorn.db.ESpecies[id=" + getId() + "]";
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public boolean getBoundaryCondition() {
        return boundaryCondition;
    }

    public void setBoundaryCondition(boolean boundaryCondition) {
        this.boundaryCondition = boundaryCondition;
    }

    public ECompartment getCompartment() {
        return compartment;
    }

    public void setCompartment(ECompartment compartment) {
        this.compartment = compartment;
    }

    public Collection<EReactant> getEReactantCollection() {
        return eReactantCollection;
    }

    public void setEReactantCollection(Collection<EReactant> eReactantCollection) {
        this.eReactantCollection = eReactantCollection;
    }

    public Collection<EProduct> getEProductCollection() {
        return eProductCollection;
    }

    public void setEProductCollection(Collection<EProduct> eProductCollection) {
        this.eProductCollection = eProductCollection;
    }

    public Collection<EFbaData> getEFbaDataCollection() {
        return eFbaDataCollection;
    }

    public void setEFbaDataCollection(Collection<EFbaData> eFbaDataCollection) {
        this.eFbaDataCollection = eFbaDataCollection;
    }

    public Collection<ERscanData> getERscanDataCollection() {
        return eRscanDataCollection;
    }

    public void setERscanDataCollection(Collection<ERscanData> eRscanDataCollection) {
        this.eRscanDataCollection = eRscanDataCollection;
    }

    public Collection<EKgeneData> getEKgeneDataCollection() {
        return eKgeneDataCollection;
    }

    public void setEKgeneDataCollection(Collection<EKgeneData> eKgeneDataCollection) {
        this.eKgeneDataCollection = eKgeneDataCollection;
    }

}
