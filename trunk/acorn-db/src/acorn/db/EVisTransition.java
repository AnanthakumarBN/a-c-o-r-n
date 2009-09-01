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

/**
 *
 * @author Mateusza
 */
@Entity
public class EVisTransition extends EVisNode implements Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "ID", nullable = false)
    Long id;

    
    @ManyToOne
    @JoinColumn(name = "REACTION", referencedColumnName="ID")
    private EReaction reaction;

    @ManyToOne
    @JoinColumn(name = "VISUALIZATION", referencedColumnName="ID", nullable = false)
    private EVisualization visualization;

    public EVisTransition() {
    }

    public EVisTransition(String xmlSid, Point position, EReaction reaction, EVisualization visualization) {
        super(xmlSid, position);
        this.reaction = reaction;
        this.visualization = visualization;
    }
    
    public String getReactionName() {
        return reaction.getName();
    }

    public String getReactionSid(){
        return reaction.getSid();
    }

    
    @Override
    public String toString() {
        return "acorn.db.EVisTransition[id=" + id + "]";
    }
}
