/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package acorn.db;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "ETASK")
@NamedQueries({
@NamedQuery(name = "ETask.findById", query = "SELECT e FROM ETask e WHERE e.id = :id"), 
@NamedQuery(name = "ETask.findByName", query = "SELECT e FROM ETask e WHERE e.name = :name"), 
@NamedQuery(name = "ETask.findByDate", query = "SELECT e FROM ETask e WHERE e.date = :date"), 
@NamedQuery(name = "ETask.findByLastChange", query = "SELECT e FROM ETask e WHERE e.lastChange = :lastChange"),
@NamedQuery(name = "ETask.findByStatus", query = "SELECT e FROM ETask e WHERE e.status = :status"), 
@NamedQuery(name = "ETask.findByShared", query = "SELECT e FROM ETask e WHERE e.shared = :shared"),
@NamedQuery(name = "ETask.findByUserID", query = "SELECT t FROM ETask t, EModel m WHERE t.model = m AND m.owner = :id"),
@NamedQuery(name = "ETask.findByModel" , query = "SELECT t FROM ETask t where t.model = :model"),
@NamedQuery(name = "ETask.getFlux", query = "SELECT f.flux FROM ETask t, IN(t.efbaResultElementCollection) f where t=:task and f.reaction.sid = :reactionSid"),
@NamedQuery(name = "ETask.getFluxFVA", query = "SELECT f.minFlux, f.maxFlux FROM ETask t, IN(t.efvaResultElementCollection) f where t=:task and f.reaction.sid = :reactionSid")})

public class ETask implements Serializable {
    public static String statusDone = "done";
    public static String statusInProgress = "in progress";
    public static String statusQueued = "queued";
    public static String statusSysError = "system error";
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=IDENTITY)
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
    
    @Column(name = "STATUS", nullable = false)
    private String status;
    
    @Column(name = "INFO")
    private String info;
    
    @Column(name = "SHARED", nullable = false)
    private boolean shared;
    
    @JoinColumn(name = "METHOD", referencedColumnName = "ID", nullable = false)
    @ManyToOne
    private EMethod method;
    
    @JoinColumn(name = "MODEL", referencedColumnName = "ID", nullable = false)
    @OneToOne(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    private EModel model;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "task")
    private EMethodData methodData;
        
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "task")
    private ECommonResults commonResults;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "task")
    private Collection<EfbaResultElement> efbaResultElementCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "task")
    private Collection<EfvaResultElement> efvaResultElementCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "task")
    private Collection<ErscanResultElement> erscanResultElementCollection;
    
    public ETask() {
    }

    public ETask(Integer id) {
        this.id = id;
    }

    public ETask(Integer id, String name, Date date, Date lastChange, String status, boolean shared) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.lastChange = lastChange;
        this.status = status;
        this.shared = shared;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ETask)) {
            return false;
        }
        ETask other = (ETask) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "acorn.db.ETask[id=" + getId() + "]";
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

    public String getStatus() {
        double EPS = 0.00001;
        double prog;

        if(this.status.equals(ETask.statusSysError))
            return ETask.statusSysError;

        prog = getProgress();
        if (method.getIdent().equals(EMethod.fba) || method.getIdent().equals(EMethod.kgene)) {
          if (Math.abs(prog) < EPS) {
            return ETask.statusQueued;
          } else {
            return ETask.statusDone;
          }
        } else if (method.getIdent().equals(EMethod.fva) || method.getIdent().equals(EMethod.rscan)) {
          if (Math.abs(prog) < EPS) {
            return ETask.statusQueued;
          } else if (Math.abs(getProgress() - 1.0) > EPS) {
            return ETask.statusInProgress;
          } else {
            return ETask.statusDone;
          }
        }

        return "";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getProgress() {
        if (method.getIdent().equals(EMethod.fba) || method.getIdent().equals(EMethod.kgene)) {
          if (getCommonResults() == null) {
            return 0; 
          } else {
            return 1;
          }
        } else if (method.getIdent().equals(EMethod.fva)) {
          float allReactions = (float)getModel().getMetabolism().getEReactionCollection().size();
          float completedReactions = (float)getEfvaResultElementCollection().size();
          return completedReactions / allReactions;
        } else if (method.getIdent().equals(EMethod.rscan)) {
          float allReactions = (float)getModel().getMetabolism().getEReactionCollection().size();
          float completedReactions = (float)getErscanResultElementCollection().size();
          return completedReactions / allReactions;
        }

        return -1;
    }
    
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Boolean getShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public EMethod getMethod() {
        return method;
    }

    public void setMethod(EMethod method) {
        this.method = method;
    }

    public EModel getModel() {
        return model;
    }

    public void setModel(EModel model) {
        this.model = model;
    }

    public EMethodData getMethodData() {
        return methodData;
    }

    public void setEMethodDataCollection(EMethodData methodData) {
        this.setMethodData(methodData);
    }

    public ECommonResults getCommonResults() {
        return commonResults;
    }

    public void setCommonResults(ECommonResults commonResults) {
        this.commonResults = commonResults;
    }

    public Collection<EfbaResultElement> getEfbaResultElementCollection() {
        return efbaResultElementCollection;
    }

    public void setEfbaResultElementCollection(Collection<EfbaResultElement> efbaResultElementCollection) {
        this.efbaResultElementCollection = efbaResultElementCollection;
    }

    public Collection<EfvaResultElement> getEfvaResultElementCollection() {
        return efvaResultElementCollection;
    }

    public void setEfvaResultElementCollection(Collection<EfvaResultElement> efvaResultElementCollection) {
        this.efvaResultElementCollection = efvaResultElementCollection;
    }

    public Collection<ErscanResultElement> getErscanResultElementCollection() {
        return erscanResultElementCollection;
    }

    public void setErscanResultElementCollection(Collection<ErscanResultElement> erscanResultElementCollection) {
        this.erscanResultElementCollection = erscanResultElementCollection;
    }
    
    public boolean resultsAvailable() {
        return getStatus().equalsIgnoreCase(statusDone);
    }

    public boolean isInProgress() {
        return getStatus().equals(statusInProgress);
    }

    public void setMethodData(EMethodData methodData) {
        this.methodData = methodData;
    }
    
    //a hack to make owner user-aware delete button on web pages
    public Integer getOwnerId() {
        if (getModel().getOwner() == null) {
            return null;
        } else {
            return getModel().getOwner().getId();
        }
    }}
