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
@Table(name = "EMETHOD")
@NamedQueries({@NamedQuery(name = "EMethod.findById", query = "SELECT e FROM EMethod e WHERE e.id = :id"), 
@NamedQuery(name = "EMethod.findByName", query = "SELECT e FROM EMethod e WHERE e.name = :name"), 
@NamedQuery(name = "EMethod.findByDescr", query = "SELECT e FROM EMethod e WHERE e.descr = :descr"),
@NamedQuery(name = "EMethod.findByIdent", query = "SELECT e FROM EMethod e WHERE e.ident = :ident")})
public class EMethod implements Serializable {
    /* Method identifiers: */
    public static String fba = "fba";
    public static String fva = "fva";
    public static String rscan = "rscan";
    public static String kgene = "kgene";
    
    /* IF YOU WANT TO ADD NEW METHOD -> put here your method's identifier */
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;
    
    @Column(name = "NAME", nullable = false)
    private String name;
    
    @Column(name = "IDENT", nullable = false)
    private String ident;
    
    @Column(name = "DESCR", nullable = false)
    private String descr;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "method")
    private Collection<ETask> eTaskCollection;

    public EMethod() {
    }

    public EMethod(Integer id) {
        this.id = id;
    }

    public EMethod(Integer id, String name, String descr) {
        this.id = id;
        this.name = name;
        this.descr = descr;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public Collection<ETask> getETaskCollection() {
        return eTaskCollection;
    }

    public void setETaskCollection(Collection<ETask> eTaskCollection) {
        this.eTaskCollection = eTaskCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EMethod)) {
            return false;
        }
        EMethod other = (EMethod) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "acorn.db.EMethod[id=" + id + "]";
    }

}
