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
import javax.persistence.OneToMany;

/**
 *
 * @author Mateusza
 */
@Entity
public abstract class EVisArc implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String sid;
    @OneToMany(cascade=CascadeType.ALL, mappedBy="arc")
    private Collection <EVisArcpath> arcpathList;

    public EVisArc() {
    }

    public EVisArc(String sid) {
        this.sid = sid;
    }

    public EVisArc(String sid, Collection<EVisArcpath> arcpathList) {
        this.sid = sid;
        this.arcpathList = arcpathList;
    }

    public Collection<EVisArcpath> getArcpathList() {
        return arcpathList;
    }

    public void setArcpathList(Collection<EVisArcpath> arcpathList) {
        this.arcpathList = arcpathList;
    }
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
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
        if (!(object instanceof EVisArc)) {
            return false;
        }
        EVisArc other = (EVisArc) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "acorn.db.EVisArc[id=" + id + "]";
    }
}
