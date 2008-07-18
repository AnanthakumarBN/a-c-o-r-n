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
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import static javax.persistence.GenerationType.IDENTITY;

/**
 *
 * @author lukasz
 */
@Entity
@Table(name = "EUSER")
@NamedQueries({
@NamedQuery(name = "EUser.findById", query = "SELECT e FROM EUser e WHERE e.id = :id"), 
@NamedQuery(name = "EUser.findByName", query = "SELECT e FROM EUser e WHERE e.name = :name"), 
@NamedQuery(name = "EUser.findBySurname", query = "SELECT e FROM EUser e WHERE e.surname = :surname"), 
@NamedQuery(name = "EUser.findByLogin", query = "SELECT e FROM EUser e WHERE e.login = :login"), 
@NamedQuery(name = "EUser.findByPasswd", query = "SELECT e FROM EUser e WHERE e.passwd = :passwd"), 
@NamedQuery(name = "EUser.findByEmail", query = "SELECT e FROM EUser e WHERE e.email = :email"), 
@NamedQuery(name = "EUser.findByStatus", query = "SELECT e FROM EUser e WHERE e.status = :status"), 
@NamedQuery(name = "EUser.findByActivationCode", query = "SELECT e FROM EUser e WHERE e.activationCode = :activationCode"), 
@NamedQuery(name = "EUser.findByInstitution", query = "SELECT e FROM EUser e WHERE e.institution = :institution")})
public class EUser implements Serializable {
    public static String statusNormal = "normal";
    public static String statusAdmin = "admin";
    public static String statusInactive = "inactive";
    public static String statusBanned = "banned";
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;
    
    @Column(name = "NAME", nullable = false)
    private String name;
    
    @Column(name = "SURNAME", nullable = false)
    private String surname;
    
    @Column(name = "LOGIN", nullable = false, unique = true)
    private String login;
    
    @Column(name = "PASSWD", nullable = false)
    private String passwd;
    
    @Column(name = "EMAIL", nullable = false)
    private String email;
    
    @Column(name = "STATUS", nullable = false)
    private String status;
    
    @Column(name = "ACTIVATION_CODE", nullable = false)
    private String activationCode;
    
    @Column(name = "INSTITUTION")
    private String institution;
    
    @Column(name = "DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
    private Collection<EModel> eModelCollection;

    public EUser() {
    }

    public EUser(Integer id) {
        this.id = id;
    }

    public EUser(Integer id, String name, String surname, String login, String passwd, String email, String status, String activationCode) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.login = login;
        this.passwd = passwd;
        this.email = email;
        this.status = status;
        this.activationCode = activationCode;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EUser)) {
            return false;
        }
        EUser other = (EUser) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "acorn.db.EUser[id=" + getId() + "]";
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public Collection<EModel> getEModelCollection() {
        return eModelCollection;
    }

    public void setEModelCollection(Collection<EModel> eModelCollection) {
        this.eModelCollection = eModelCollection;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
