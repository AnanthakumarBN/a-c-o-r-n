package acorn.sbmlParser;

import acorn.db.*;
import acorn.errorHandling.ErrorBean;
import acorn.userManagement.UserManager;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SbmlParser
 * @author lukasz
 */
public class SbmlParser extends DefaultHandler { 
    /* True if some kind of error occured */
    private boolean error;
    
    /* Error message */
    private String message;
    
    /* Structure of SBML document */
    int s[], v;
    
    /* Model and Metabolism - representation of model being added */
    private EModel model;
    private EMetabolism metabolism;
    
    /* Maps of Entities */
    private Map<String, ECompartment> compartment_map;
    private Map<String, ESpecies> species_map;
    
    /* Lists of Entities */
    private List<EBounds> bounds_list;
    private List<EReaction> reaction_list;
    private List<EReactant> reactant_list;
    private List<EProduct> product_list;
    
    /* Current reaction */
    private EReaction reaction;
    
    /* Current bounds */
    private EBounds bounds;
    
    /* Characters */
    String line, genes;
    
    /**
     * Creates a new instance of SbmlParser
     * @param name      - model name,
     * @param organism  - organism name.
     */
    public SbmlParser(String name, String organism, String geneLink) {
        super();

        assert name != null;
        assert organism != null;
        
        model = new EModel();
        model.setName(name);
        model.setEBoundsCollection(new LinkedList<EBounds>());
        
        metabolism = new EMetabolism();
        metabolism.setOrganism(organism);
        metabolism.setGeneLink(geneLink);
        metabolism.setEModelCollection(new LinkedList<EModel>());
        metabolism.setECompartmentCollection(new LinkedList<ECompartment>());
        metabolism.setEReactionCollection(new LinkedList<EReaction>());
    }
    
    /**
     * Print error message
     * @param msg - message to be printed
     */
    private void error(String message) {
        this.error = true;
        this.message = message;
    }
    
    @Override
    /**
     * Begin parsing
     */
    public void startDocument() {      
        assert model != null;
        assert metabolism != null;
        
        error = false;
        
        /* Initialization of data structures */
        s = new int[10]; for (int i = 0; i < 10; ++i) s[i] = 0; v = 0;

        compartment_map = new HashMap<String, ECompartment>();
        species_map = new HashMap<String, ESpecies>();
        
        bounds_list = new LinkedList<EBounds>();
        reaction_list = new LinkedList<EReaction>();
        reactant_list = new LinkedList<EReactant>();
        product_list = new LinkedList<EProduct>();
        
        reaction = null;
        bounds = null;
        
        line = null; genes = null;       
    }

    @Override
    /**
     * End parsing
     */
    public void endDocument() {
        assert model != null;
        assert metabolism != null;
        
        /* Nothing to do if error occured */
        if (isError()) return;
        
        /* Insert model to data base */
        try {
            EModelController mc = new EModelController();
            mc.addModel(metabolism, model, compartment_map, species_map, reaction_list, reactant_list, product_list, bounds_list);
        } 
        catch (Exception e) {
            ErrorBean.printMessage(e, null, "Unexpected error while adding model. Please contact the system Administrator.");
        }
    }
    
    /**
     * Get information about model
     * @param atts - model's SBML attributes
     */
    private void getModel(Attributes atts) {
        assert model != null;
        assert metabolism != null;
        
        String id = atts.getValue("id");
        String name = atts.getValue("name");
        
        if (id == null) error("Attribute 'id' required in 'model' tag.");
        if (name == null) error("Attribute 'name' required in 'model' tag.");
        
        metabolism.setSid(id);
        metabolism.setName(name);
       
        Date d = new Date();
        
        model.setDate(d);
        model.setLastChange(d);
        model.setReadOnly(true);
        model.setShared(true);
        model.setMetabolism(metabolism);
        model.setOwner(UserManager.getCurrentUser());
    }
    
    /**
     * Add new compartment
     * @param atts - compartment's SBML attributes
     */
    private void addCompartment(Attributes atts) { 
        assert model != null;
        assert metabolism != null;
        
        ECompartment c = new ECompartment();
        c.setECompartmentCollection(new LinkedList<ECompartment>());
        c.setESpeciesCollection(new LinkedList<ESpecies>());
        
        String id = atts.getValue("id"); 
        String outside = atts.getValue("outside");
        
        if (id == null) error("Attribute 'id' required in 'compartment' tag.");
        
        c.setSid(id);
        c.setMetabolism(metabolism);
        
        if (outside != null) {
            ECompartment p = compartment_map.get(outside);
            if (p == null) error("Compartment assigned to nonexisting compartment: " + outside);
            c.setOutside(p);
            p.getECompartmentCollection().add(c);
        } else {
            c.setOutside(null);
        }
                
        compartment_map.put(id, c);
    }
    
    /**
     * Add new species
     * @param atts - species SBML attributes
     */
    private void addSpecies(Attributes atts) {
        assert model != null;
        assert metabolism != null;
        
        ESpecies s = new ESpecies();
        s.setEReactantCollection(new LinkedList<EReactant>());
        s.setEProductCollection(new LinkedList<EProduct>());
        
        String id = atts.getValue("id"); 
        String name = atts.getValue("name");
        String compartment = atts.getValue("compartment");
        String charge = atts.getValue("charge");
        String boundaryCondition = atts.getValue("boundaryCondition");
        
        if (id == null) error("Attribute 'id' required in 'species' tag.");
        if (name == null) error("Attribute 'name' required in 'species' tag.");
        if (compartment == null) error("Attribute 'comaprtment' required in 'species' tag.");
        if (charge == null) error("Attribute 'charge' required in 'species' tag.");
        if (boundaryCondition == null) error("Attribute 'boundaryCondition' required in 'species' tag.");
        
        /* Look for compartment */
        ECompartment co = compartment_map.get(compartment);
        if (co == null) error("Species assigned to nonexisting compartment: " + compartment);
        
        /* Translate charge */
        Integer ch = new Integer(charge);                    
        
        /* Translate boundaryCondition */
        boolean bc = false;
        if (boundaryCondition.toLowerCase().equals("true")) {
            bc = true;
        } else if (boundaryCondition.toLowerCase().equals("false")) {
            bc = false;
        } else {
            error("Invalid 'boundaryCondition': " + boundaryCondition);
        }
        
        s.setSid(id);
        s.setName(name);
        s.setCompartment(co);
        s.setCharge(ch);
        s.setBoundaryCondition(bc);
        
        species_map.put(id, s);
    }
        
    /**
     * Start analysing new reaction
     * @param atts - reaction's SBML attributes
     */
    private void startReaction(Attributes atts) {
        assert model != null;
        assert metabolism != null;
        
        reaction = new EReaction();
        /*
        reaction.setEBoundsCollection(new LinkedList<EBounds>());
        reaction.setEReactantCollection(new LinkedList<EReactant>());
        reaction.setEProductCollection(new LinkedList<EProduct>());
        */
        
        bounds = new EBounds();

        String id = atts.getValue("id"); 
        String name = atts.getValue("name");
        String reversible = atts.getValue("reversible");
 
        if (id == null) error("Attribute 'id' required in 'reaction' tag.");
        if (name == null) error("Attribute 'name' required in 'reaction' tag.");
        if (reversible == null) error("Attribute 'reversible' required in 'reaction' tag.");

        /* Translate reversible */
        boolean r = false;
        if (reversible.toLowerCase().equals("true")) {
            r = true;
        } else if (reversible.toLowerCase().equals("false")) {
            r = false;
        } else {
            error("Invalid 'reversible': " + reversible);
        }
        
        reaction.setSid(id);
        reaction.setName(name);
        reaction.setReversible(r);
        reaction.setGenes("nogene");
        reaction.setMetabolism(metabolism);
        
        bounds.setModel(model);
        bounds.setReaction(reaction);
        bounds.setLowerBound(0);
        bounds.setUpperBound(0);
    }
    
    /**
     * Start analysing new comment
     */
    private void startGenes() {
        assert model != null;
        assert metabolism != null;
        
        if (reaction == null) error("Gene association outside 'reaction' tag.");;
        
        line = new String("");
        genes = new String("");
    }
    
    /**
     * Read genes from notes
     */
    private void endGenes() {
        assert model != null;
        assert metabolism != null;
        assert line != null;
        assert genes != null;
        
        if (reaction == null) error("Gene association outside 'reaction' tag.");
        
        if (line.startsWith("GENE_ASSOCIATION:")) {         
            genes = line.substring(18);
            if (!genes.equals("")) {
                if (genes.length() <= 1000) {
                    reaction.setGenes(genes.toUpperCase());
                } else {
                    error("Gene association too long.");
                }
            }
        }
        
        line = null; genes = null;
    }
    
    /**
     * Add new reactant
     * @param atts - reactant's SBML attributes
     */
    private void addReactant(Attributes atts) {
        assert model != null;
        assert metabolism != null;
       
        if (reaction == null) error("'reactant' outside 'reaction' tag.");
        
        EReactant r = new EReactant();
        
        String species = atts.getValue("species"); 
        String stoichiometry = atts.getValue("stoichiometry");
        
        if (species == null) error("Attribute 'species' required in 'reactant' tag.");
        if (stoichiometry == null) error("Attribute 'stoichiometry' required in 'reactant' tag.");
        
        /* Translate species */
        ESpecies sp = species_map.get(species);
        if (sp == null) error("Reactant assigned to nonexisting species: " + species);
        
        /* Translate stoichiometry */
        Float st = new Float(stoichiometry);
        
        r.setSpecies(sp);
        r.setReaction(reaction);
        r.setStoichiometry(st);
        
        reactant_list.add(r);
    }

    /**
     * Add new product
     * @param atts - product's SBML attributes
     */
    private void addProduct(Attributes atts) {
        assert model != null;
        assert metabolism != null;
        
        if (reaction == null) error("'product' outside 'reaction' tag.");
        
        EProduct p = new EProduct();
        
        String species = atts.getValue("species"); 
        String stoichiometry = atts.getValue("stoichiometry");
        
        if (species == null) error("Attribute 'species' required in 'product' tag.");
        if (stoichiometry == null) error("Attribute 'stoichiometry' required in 'product' tag.");
        
        /* Translate species */
        ESpecies sp = species_map.get(species);
        if (sp == null) error("Product assigned to nonexisting species: " + species);
        
        /* Translate stoichiometry */
        Float st = new Float(stoichiometry);
        
        p.setSpecies(sp);
        p.setReaction(reaction);
        p.setStoichiometry(st);
        
        product_list.add(p);
    }
    
    /**
     * Manage parameter
     * @param atts - bouonds SBML attributes
     */
    private void getBounds(Attributes atts) {
        assert model != null;
        assert metabolism != null;
        
        if (reaction == null) ;
        if (bounds == null) ;

        String id = atts.getValue("id");
        String value = atts.getValue("value");
        
        if (id == null) error("Attribute 'id' required in 'parameter' tag.");
        if (value == null) error("Attribute 'value' required in 'parameter' tag.");
        
        if (id.toUpperCase().equals("LOWER_BOUND")) {
            bounds.setLowerBound(new Float(value));
        } else if (id.toUpperCase().equals("UPPER_BOUND")) {
            bounds.setUpperBound(new Float(value));
        }
    }
    
    /**
     * Add new reaction
     */
    private void endReaction() {
        assert model != null;
        assert metabolism != null;
        
        /* We allow to add reactions without reactants, products or defined bounds */
        
        bounds_list.add(bounds); bounds = null;
        reaction_list.add(reaction); reaction = null;
    }
    
    @Override
    /**
     * Begin new element
     */
    public void startElement(String uri, String name, String qName, Attributes atts) {
        assert uri != null;
        assert name != null;
        assert qName != null;
        assert atts != null;
        
        assert model != null;
        assert metabolism != null;
        
        /* Nothing to do if error occured */
        if (isError()) return;
        
        switch (s[v]) {
            case 0:     /* root */
                if (name.equals("sbml")) {
                    s[++v] = 1;
                } else {
                    s[++v] = 0;
                }
                break;
                
            case 1:     /* sbml */
                if (name.equals("model")) {
                    s[++v] = 2;
                    getModel(atts);
                } else {
                    s[++v] = 1;
                }
                break;
                
            case 2:     /* model */
                if (name.equals("listOfCompartments")) {
                    s[++v] = 3;
                } else if (name.equals("listOfSpecies")) {
                    s[++v] = 5;
                } else if (name.equals("listOfReactions")) {
                    s[++v] = 7;
                } else {
                    s[++v] = 2;
                }
                break;
                
            case 3:     /* listOfCompartments */
                if (name.equals("compartment")) {
                    s[++v] = 4; 
                    addCompartment(atts);
                } else {
                    s[++v] = 3;
                }
                break;
                
            case 4:     /* compartment */
                s[++v] = 4;
                break;
                
            case 5:     /* listOfSpecies */
                if (name.equals("species")) {
                    s[++v] = 6; 
                    addSpecies(atts);
                } else {
                    s[++v] = 5;
                }
                break;
                
            case 6:    /* species */
                s[++v] = 6;
                break;
                
            case 7:    /* listOfReactions */
                if (name.equals("reaction")) {
                    s[++v] = 8; 
                    startReaction(atts);
                } else {
                    s[++v] = 7;
                }
                break;

            case 8:    /* reaction */
                if (name.equals("notes")) {
                    s[++v] = 9;
                } else if (name.equals("listOfReactants")) {
                    s[++v] = 11;
                } else if (name.equals("listOfProducts")) {
                    s[++v] = 13;
                } else if (name.equals("kineticLaw")) {
                    s[++v] = 15;
                } else {
                    s[++v] = 8;
                }
                break;
                
            case 9:    /* notes */
                if (name.equals("p")) {
                    s[++v] = 10;
                    startGenes();
                } else {
                    s[++v] = 9;
                }
                break;
                
            case 10:    /* p */
                s[++v] = 10;
                break;
               
            case 11:    /* listOfReactants */
                if (name.equals("speciesReference")) {
                    s[++v] = 12; 
                    addReactant(atts);
                } else {
                    s[++v] = 11;
                }
                break;
                
            case 12:    /* speciesReference - Reactant */
                s[++v] = 12;
                break;
                
            case 13:    /* listOfProducts */
                if (name.equals("speciesReference")) {
                    s[++v] = 14; 
                    addProduct(atts);
                } else {
                    s[++v] = 13;
                }
                break;
                
            case 14:    /* speciesReference - Product */
                s[++v] = 14;
                break;
                
            case 15:    /* kineticLaw */
                if (name.equals("listOfParameters")) {
                    s[++v] = 16;
                } else {
                    s[++v] = 15;
                }
                break;
                
            case 16:    /* listOfParameters */
                if (name.equals("parameter")) {
                    s[++v] = 17; 
                    getBounds(atts);
                } else {
                    s[++v] = 16;
                }
                break;
                
            case 17:    /* parameter */
                s[++v] = 17;
                break;
                
            default:
                /* nothing */
        }      
    }
    
    @Override
    /**
     * End element
     */
    public void endElement (String uri, String name, String qName) {
        assert uri != null;
        assert name != null;
        assert qName != null;
        
        assert model != null;
        assert metabolism != null;
        
        /* Nothing to do if error occured */
        if (isError()) return;
        
        switch (s[v]) {
            case 0:     /* root */                
            case 1:     /* sbml */
            case 2:     /* model */              
            case 3:     /* listOfCompartments */                
            case 4:     /* compartment */
            case 5:     /* listOfSpecies */
            case 6:     /* species */
            case 7:     /* listOfReactions */
                s[v--] = 0; break;
                
            case 8:     /* reaction */
                endReaction();
                
            case 9:     /* notes */
                s[v--] = 0; break;
                
            case 10:    /* p */
                endGenes();
                
            case 11:    /* listOfReactants */
            case 12:    /* speciesReference - Reactant */
            case 13:    /* listOfProducts */
            case 14:    /* speciesReference - Product */
            case 15:    /* kineticLaw */
            case 16:    /* listOfParameters */
            case 17:    /* parameter */
                s[v--] = 0; break;
                
            default:
                /* nothig */
        }   
    }
   
    @Override
    public void characters (char ch[], int start, int length) {
        
        assert model != null;
        assert metabolism != null;
        
        /* Nothing to do if error occured */
        if (isError()) return;
        
        if (s[v] == 10) { /* p */
            line = line + (new String(ch, start, length));
        }   
    }
    
    /**
     * Getters and setters
     */
    
    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
