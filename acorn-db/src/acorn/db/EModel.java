/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.db;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import static javax.persistence.GenerationType.IDENTITY;

/**
 *
 * @author lukasz
 */
@Entity
@Table(name = "EMODEL")
@NamedQueries({@NamedQuery(name = "EModel.findById", query = "SELECT e FROM EModel e WHERE e.id = :id"), 
@NamedQuery(name = "EModel.findByName", query = "SELECT e FROM EModel e WHERE e.name = :name"), 
@NamedQuery(name = "EModel.findByOwner", query = "SELECT e FROM EModel e WHERE e.owner = :owner"), 
@NamedQuery(name = "EModel.findByDate", query = "SELECT e FROM EModel e WHERE e.date = :date"), 
@NamedQuery(name = "EModel.findByLastChange", query = "SELECT e FROM EModel e WHERE e.lastChange = :lastChange"), 
@NamedQuery(name = "EModel.findByReadOnly", query = "SELECT e FROM EModel e WHERE e.readOnly = :readOnly")})
public class EModel implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;
    
    @Column(name = "NAME", nullable = false)
    private String name;
    
    @Column(name = "DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    
    @Column(name = "LAST_CHANGE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastChange;
    
    @Column(name = "READ_ONLY", nullable = false)
    private boolean readOnly;
    
    @Column(name = "SHARED", nullable = false)
    private boolean shared;
    
    @JoinColumn(name = "PARENT", referencedColumnName = "ID")
    @ManyToOne
    private EModel parent;
    
    @JoinColumn(name = "METABOLISM", referencedColumnName = "ID", nullable = false)
    @ManyToOne
    private EMetabolism metabolism;
    
    @JoinColumn(name = "OWNER", referencedColumnName = "ID", nullable = false)
    @ManyToOne
    private EUser owner;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent")
    private Collection<EModel> eModelCollection;
    
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "model")
    private ETask task;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "model")
    private Collection<EBounds> eBoundsCollection;

    public EModel() {
    }

    public EModel(Integer id) {
        this.id = id;
    }

    public EModel(Integer id, String name, Date date, Date lastChange, boolean readOnly) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.lastChange = lastChange;
        this.readOnly = readOnly;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EModel)) {
            return false;
        }
        EModel other = (EModel) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "acorn.db.EModel[id=" + getId() + "]";
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getLastChange() {
        return lastChange;
    }

    public void setLastChange(Date lastChange) {
        this.lastChange = lastChange;
    }

    public boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public EModel getParent() {
        return parent;
    }

    public void setParent(EModel parent) {
        this.parent = parent;
    }

    public EMetabolism getMetabolism() {
        return metabolism;
    }

    public void setMetabolism(EMetabolism metabolism) {
        this.metabolism = metabolism;
    }

    public EUser getOwner() {
        return owner;
    }

    public void setOwner(EUser owner) {
        this.owner = owner;
    }

    public Collection<EModel> getEModelCollection() {
        return eModelCollection;
    }

    public void setEModelCollection(Collection<EModel> eModelCollection) {
        this.eModelCollection = eModelCollection;
    }

    public ETask getTask() {
        return task;
    }

    public void setTask(ETask task) {
        this.task = task;
    }

    public Collection<EBounds> getEBoundsCollection() {
        return eBoundsCollection;
    }

    public void setEBoundsCollection(Collection<EBounds> eBoundsCollection) {
        this.eBoundsCollection = eBoundsCollection;
    }
    
    public LinkedList<String> getGenesList(){
        LinkedList<String> result = new LinkedList<String>();
        Collection<EReaction> reactions = this.metabolism.getEReactionCollection();
        LinkedList<String> genes;
        for (EReaction reaction : reactions){
            genes = reaction.getGenesList();
            for (String gene : genes){
                if (!(result.contains(gene))){
                    result.add(gene);
                }
            }
        }
        return result;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }
}
