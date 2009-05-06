/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package acorn.db;

import java.awt.Point;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author markos
 */
@Entity
@Table(name = "EVISNODE")
public abstract class EVisNode implements Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "ID", nullable = false)
    Long id;

    @Column(name = "XMLSID", nullable = false)
    String xmlSid;
    @Column(name = "X", nullable = false)
    double x;
    @Column(name = "Y", nullable = false)
    double y;

    public EVisNode() {
    }

    public EVisNode(String xmlSid, Point position) {
        this.xmlSid = xmlSid;
        this.x = position.getX();
        this.y = position.getY();
    }

    public EVisNode(String xmlSid, double x, double y) {
        this.xmlSid = xmlSid;
        this.x = x;
        this.y = y;
    }

    public Point getPosition(){
        return new Point((int)this.x, (int)this.y);
    }

    public void setPosition(Point p){
        x = p.x;
        y = p.y;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long Id) {
        this.id = Id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public String getXmlSid() {
        return xmlSid;
    }

    public void setXmlSid(String xmlSid) {
        this.xmlSid = xmlSid;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EVisNode)) {
            return false;
        }
        EVisNode other = (EVisNode) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
