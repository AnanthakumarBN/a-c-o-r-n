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
@Table(name = "EMETABOLISM")
@NamedQueries({@NamedQuery(name = "EMetabolism.findById", query = "SELECT e FROM EMetabolism e WHERE e.id = :id"), 
@NamedQuery(name = "EMetabolism.findBySid", query = "SELECT e FROM EMetabolism e WHERE e.sid = :sid"), 
@NamedQuery(name = "EMetabolism.findByName", query = "SELECT e FROM EMetabolism e WHERE e.name = :name"), 
@NamedQuery(name = "EMetabolism.findByOrganism", query = "SELECT e FROM EMetabolism e WHERE e.organism = :organism"),
@NamedQuery(name = "EMetabolism.findByIDAndReactionSid", query = "SELECT r from EMetabolism m JOIN m.eReactionCollection r where m.id=:id and r.sid=:sid")
})
public class EMetabolism implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;
    
    @Column(name = "SID", nullable = false)
    private String sid;
    
    @Column(name = "NAME", nullable = false)
    private String name;
    
    @Column(name = "ORGANISM", nullable = false)
    private String organism;
    
    @Column(name = "GENELINK")
    private String geneLink;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "metabolism")
    private Collection<EModel> eModelCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "metabolism")
    private Collection<EReaction> eReactionCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "metabolism")
    private Collection<ECompartment> eCompartmentCollection;

    public EMetabolism() {
    }

    public EMetabolism(Integer id) {
        this.id = id;
    }

    public EMetabolism(Integer id, String sid, String name, String organism) {
        this.id = id;
        this.sid = sid;
        this.name = name;
        this.organism = organism;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EMetabolism)) {
            return false;
        }
        EMetabolism other = (EMetabolism) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "acorn.db.EMetabolism[id=" + getId() + "]";
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

    public String getOrganism() {
        return organism;
    }

    public void setOrganism(String organism) {
        this.organism = organism;
    }
    
    public String getGeneLink() {
        if (geneLink == null || geneLink.equals(""))
            return "http://www.google.pl/search?q=%s";
        else
            return geneLink;
    }

    public void setGeneLink(String geneLink) {
        this.geneLink = geneLink;
    }

    public Collection<EModel> getEModelCollection() {
        return eModelCollection;
    }

    public void setEModelCollection(Collection<EModel> eModelCollection) {
        this.eModelCollection = eModelCollection;
    }

    public Collection<EReaction> getEReactionCollection() {
        return eReactionCollection;
    }

    public void setEReactionCollection(Collection<EReaction> eReactionCollection) {
        this.eReactionCollection = eReactionCollection;
    }

    public Collection<ECompartment> getECompartmentCollection() {
        return eCompartmentCollection;
    }

    public void setECompartmentCollection(Collection<ECompartment> eCompartmentCollection) {
        this.eCompartmentCollection = eCompartmentCollection;
    }
}
