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
import javax.persistence.NamedQueries;

/**
 *
 * @author Mateusza
 */
@Entity
@NamedQueries({
})
public class EVisPlace extends EVisNode implements Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "ID", nullable = false)
    Long id;

//    private static final long serialVersionUID = 1L;
    @ManyToOne
    @JoinColumn(name = "SPECIES",  referencedColumnName="ID")
    private ESpecies species;

    @ManyToOne
    @JoinColumn(name = "VISUALIZATION", referencedColumnName="ID", nullable = false)
    private EVisualization visualization;

    public EVisPlace() {
    }

    public EVisPlace(String xmlSid, Point position, EVisualization vis, ESpecies species) {
        super(xmlSid, position);
        this.species = species;
        this.visualization = vis;
    }

    public EVisPlace(String xmlSid, double x, double y, EVisualization vis, ESpecies species) {
        super(xmlSid, x, y);
        this.species = species;
        this.visualization = vis;
    }


    public String getSpeciesName() {
        return species.getName();
    }

    public String getSpeciesSid(){
        return species.getSid();
    }

    @Override
    public String toString() {
        return "acorn.db.EVisSpecies[id=" + id + "]";
    }
}
