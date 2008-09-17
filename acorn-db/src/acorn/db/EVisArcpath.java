/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.db;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author Mateusza
 */
@Entity
public class EVisArcpath implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int x;
    private int y;
    private String sid;
    private boolean curvePoint;
    @ManyToOne
    private EVisArc arc;
        
    public EVisArcpath(int x, int y, String sid, boolean curvePoint) {
        this.x = x;
        this.y = y;
        this.sid = sid;
        this.curvePoint = curvePoint;
    }

    public EVisArcpath(int x, int y, String sid, boolean curvePoint, EVisArc arc) {
        this.x = x;
        this.y = y;
        this.sid = sid;
        this.curvePoint = curvePoint;
        this.arc = arc;
    }

    public EVisArcpath() {
    }

    public EVisArc getArc() {
        return arc;
    }

    public void setArc(EVisArc arc) {
        this.arc = arc;
    }

    public boolean isCurvePoint() {
        return curvePoint;
    }

    public void setCurvePoint(boolean curvePoint) {
        this.curvePoint = curvePoint;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
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
        if (!(object instanceof EVisArcpath)) {
            return false;
        }
        EVisArcpath other = (EVisArcpath) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "acorn.db.EVisArcpath[id=" + id + "]";
    }
}
