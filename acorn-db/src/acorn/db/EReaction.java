/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.db;

import java.lang.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;
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
import javax.persistence.Table;
import static javax.persistence.GenerationType.IDENTITY;

/**
 *
 * @author lukasz
 */
@Entity
@Table(name = "EREACTION")
@NamedQueries({@NamedQuery(name = "EReaction.findById", query = "SELECT e FROM EReaction e WHERE e.id = :id"),
@NamedQuery(name = "EReaction.findBySid", query = "SELECT e FROM EReaction e WHERE e.sid = :sid"),
@NamedQuery(name = "EReaction.findByName", query = "SELECT e FROM EReaction e WHERE e.name = :name"),
@NamedQuery(name = "EReaction.findByReversible", query = "SELECT e FROM EReaction e WHERE e.reversible = :reversible"),
@NamedQuery(name = "EReaction.findByGenes", query = "SELECT e FROM EReaction e WHERE e.genes = :genes"),
@NamedQuery(name = "EReaction.findByMetabolism", query = "SELECT e FROM EReaction e WHERE e.metabolism = :metabolism")
})
public class EReaction implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Column(name = "SID", nullable = false)
    private String sid;
    @Column(name = "NAME", nullable = false)
    private String name;
    @Column(name = "REVERSIBLE", nullable = false)
    private boolean reversible;
    @Column(name = "GENES", length=1000, nullable = false)
    private String genes;
    @JoinColumn(name = "METABOLISM", referencedColumnName = "ID", nullable = false)
    @ManyToOne
    private EMetabolism metabolism;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reaction")
    private Collection<EBounds> eBoundsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reaction")
    private Collection<EReactant> eReactantCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reaction")
    private Collection<EProduct> eProductCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reaction")
    private Collection<EFbaData> eFbaDataCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reaction")
    private Collection<ERscanData> eRscanDataCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reaction")
    private Collection<EKgeneData> eKgeneDataCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reaction")
    private Collection<EfbaResultElement> efbaResultElementCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reaction")
    private Collection<EfvaResultElement> efvaResultElementCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reaction")
    private Collection<ErscanResultElement> erscanResultElementCollection;

    public EReaction() {
    }

    public EReaction(Integer id) {
        this.id = id;
    }

    public EReaction(Integer id, String sid, String name, boolean reversible, String genes) {
        this.id = id;
        this.sid = sid;
        this.name = name;
        this.reversible = reversible;
        this.genes = genes;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EReaction)) {
            return false;
        }
        EReaction other = (EReaction) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "acorn.db.EReaction[id=" + getId() + "]";
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

    public boolean getReversible() {
        return reversible;
    }

    public void setReversible(boolean reversible) {
        this.reversible = reversible;
    }

    public String getGenes() {
        return genes;
    }

    public void setGenes(String genes) {
        this.genes = genes;
    }

    public EMetabolism getMetabolism() {
        return metabolism;
    }

    public void setMetabolism(EMetabolism metabolism) {
        this.metabolism = metabolism;
    }

    public Collection<EBounds> getEBoundsCollection() {
        return eBoundsCollection;
    }

    public void setEBoundsCollection(Collection<EBounds> eBoundsCollection) {
        this.eBoundsCollection = eBoundsCollection;
    }

    public Collection<EReactant> getEReactantCollection() {
        return eReactantCollection;
    }

    public void setEReactantCollection(Collection<EReactant> eReactantCollection) {
        this.eReactantCollection = eReactantCollection;
    }

    public Collection<EProduct> getEProductCollection() {
        return eProductCollection;
    }

    public void setEProductCollection(Collection<EProduct> eProductCollection) {
        this.eProductCollection = eProductCollection;
    }

    public Collection<EFbaData> getEFbaDataCollection() {
        return eFbaDataCollection;
    }

    public void setEFbaDataCollection(Collection<EFbaData> eFbaDataCollection) {
        this.eFbaDataCollection = eFbaDataCollection;
    }

    public Collection<ERscanData> getERscanDataCollection() {
        return eRscanDataCollection;
    }

    public void setERscanDataCollection(Collection<ERscanData> eRscanDataCollection) {
        this.eRscanDataCollection = eRscanDataCollection;
    }

    public Collection<EKgeneData> getEKgeneDataCollection() {
        return eKgeneDataCollection;
    }

    public void setEKgeneDataCollection(Collection<EKgeneData> eKgeneDataCollection) {
        this.eKgeneDataCollection = eKgeneDataCollection;
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

    public LinkedList<String> getGenesList() {
        LinkedList<String> genesList = new LinkedList<String>();
        String[] genesArray;
        genesArray = genes.trim().split("[ \t]+");
        for (String gene : genesArray) {
            if (!(gene.equals("nogene") || gene.equals("(") || gene.equals(")") || gene.equals("OR") || gene.equals("AND"))) {
                genesList.add(gene);
            }
        }
        return genesList;
    }
    
    private String getCoefficientAsString(double c)
    {
        String ret = String.format(Locale.US, "%.6f", c);
        int end;
        end = ret.length()-1;
        while(ret.charAt(end) == '0')
            end --;
        
        if(ret.charAt(end) == '.')
            return ret.substring(0, end+2);
        else
            return ret.substring(0, end+1);
    }
    
    public String getReactionFormula()
    {
        String formula = new String("");
        Boolean first;
        
        first = true;
        for (EReactant reactant : this.getEReactantCollection()) {
            if (!first) formula += " + ";      
            formula +=  getCoefficientAsString(reactant.getStoichiometry()) + " " 
                    + reactant.getSpecies().getName();
            first = false;
        }
        formula += " = ";
        first = true;
        for (EProduct product : this.getEProductCollection()) {
            if (!first) formula += " + ";
            formula += getCoefficientAsString(product.getStoichiometry()) + " "
                    + product.getSpecies().getName();
            first = false;
        }
        return formula;
    }
    
    public String getDivedReactionFormula()
    {
        String formula = new String("");
        Boolean first;
        
        first = true;
        for (EReactant reactant : this.getEReactantCollection()) {     
            formula += "<div title=\"header=[Sbml Id] body=[" + reactant.getSpecies().getSid() + "]\">"
                    + (first ? "" : " + " ) + getCoefficientAsString(reactant.getStoichiometry()) + " " + reactant.getSpecies().getName() + " "
                    + "</div>";
            first = false;
        }
        formula += "&rarr; <br/>";
        first = true;
        for (EProduct product : this.getEProductCollection()) {
            formula += "<div title=\"header=[Sbml Id] body=[" + product.getSpecies().getSid() + "]\">"
                    + (first ? "" : " + " ) + getCoefficientAsString(product.getStoichiometry()) + " " + product.getSpecies().getName()
                    + "</div>";
            first = false;
        }
        return formula;
    }
    
    public String getGeneFormula() {
        if (metabolism.getGeneLink() == null) return genes;
        
        String geneFormula = new String();
        String[] genesArray = genesArray = genes.trim().split("[ \t]+");
        
        for (String gene : genesArray) {
            if (!(gene.equals("nogene") || gene.equals("(") || gene.equals(")") || gene.equals("OR") || gene.equals("AND"))) {
                geneFormula += " " + "<a href=\"" + String.format(metabolism.getGeneLink(), gene) + "\">" + gene + "</a>";
            } else {
                geneFormula += " " + gene;
            }
        }
        return geneFormula;
        
    }
}
