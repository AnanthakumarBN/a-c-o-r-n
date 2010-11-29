package acorn.model;

import acorn.db.EBounds;
import acorn.db.ECompartment;
import acorn.db.EMetabolism;
import acorn.db.EModel;
import acorn.db.EProduct;
import acorn.db.EReactant;
import acorn.db.EReaction;
import acorn.db.ESpecies;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;

/**
 *
 * @author kuba
 * Yes, it is dumb not to use an XML processor, but I'm on a train and I don't
 * happen to have one.
 */
public class ModelExporter {
    private StringWriter stringWriter;
    private PrintWriter printWriter;
    int indentation;

    public ModelExporter() {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        indentation = 0;
    }

    void output(String s) {
        indent();
        printWriter.append(s);
    }

    void increaseIndent() {
        indentation++;
    }

    void decreaseIndent() {
        indentation--;
    }

    void indent() {
        for (int i = 0; i < indentation; i++) {
            printWriter.append('\t');
        }
    }

    void beginSBML() {
        // TODO: check if we are really level 2 version 2 compilant
        output("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<sbml xmlns=\"http://www.sbml.org/sbml/level2\" level=\"2\" "
                + "version=\"1\" xmlns:html=\"http://www.w3.org/1999/xhtml\">\n");
    }

    void beginModel(EMetabolism metabolism) {
        printWriter.printf("<model id=\"%s\" name=\"%s\">\n", metabolism.getSid(),
                metabolism.getName());
    }

    void beginElement(String element) {
        output("<" + element + ">\n");
        increaseIndent();
    }

    void endElement(String element) {
        decreaseIndent();
        output("</" + element + ">\n");
    }

    void writeCompartment(ECompartment compartment) {
        indent();

        if(compartment.getOutside() == null)
            printWriter.printf("<compartment id=\"%s\"/>\n",
                    compartment.getSid());
        else
            printWriter.printf("<compartment id=\"%s\" outside=\"%s\"/>\n",
                    compartment.getSid(), compartment.getOutside().getSid());
    }

    void writeCompartments(EMetabolism metabolism) {
        beginElement("listOfCompartments");
        for(ECompartment compartment : metabolism.getECompartmentCollection()) {
            writeCompartment(compartment);
        }
        endElement("listOfCompartments");
    }

    void writeOneSpecies(String sid, String name, String compartment,
            int charge, boolean boundaryCondition) {
        indent();
        printWriter.printf("<species id=\"%s\" name=\"%s\" compartment=\"%s\""
                + " charge=\"%d\" boundaryCondition=\"%s\"/>\n", sid, name,
                compartment, charge, boundaryCondition ? "true" : "false");
    }

    void writeAllSpecies(EMetabolism metabolism) {
        beginElement("listOfSpecies");
        Collection<ECompartment> compartments =
                metabolism.getECompartmentCollection();
        for (ECompartment comp : compartments) {
            Collection<ESpecies> species = comp.getESpeciesCollection();
            for (ESpecies spec : species) {
                writeOneSpecies(spec.getSid(), spec.getName(),
                        spec.getCompartment().getSid(), spec.getCharge(),
                        spec.getBoundaryCondition());
            }
        }
        endElement("listOfSpecies");
    }

    void writeReactionHeader(String id, String name, boolean reversible) {
        indent();
        printWriter.printf("<reaction id=\"%s\" name=\"%s\" "
                + "reversible=\"%s\">\n",
                id, name, reversible ? "true" : "false");
    }

    void writeGenes(String genes) {
        beginElement("notes");
        output("<html:p>GENE_ASSOCIATION: " + genes + "</html:p>\n");
        endElement("notes");
    }

    void writeSpeciesReference(String species, double stoichiometry) {
        indent();
        printWriter.printf("<speciesReference species=\"%s\""
                + " stoichiometry=\"%.6f\"/>\n", species, stoichiometry);
    }

    void writeReactants(EReaction reaction) {
        beginElement("listOfReactants");
        Collection<EReactant> reactants = reaction.getEReactantCollection();
        for (EReactant reactant : reactants) {
            writeSpeciesReference(reactant.getSpecies().getSid(),
                    reactant.getStoichiometry());
        }
        endElement("listOfReactants");
    }

    void writeProducts(EReaction reaction) {
        beginElement("listOfProducts");
        Collection<EProduct> products = reaction.getEProductCollection();
        for (EProduct product : products) {
            writeSpeciesReference(product.getSpecies().getSid(),
                    product.getStoichiometry());
        }
        endElement("listOfProducts");
    }

    void writeParameter(String parameterName, double value) {
        indent();
        printWriter.printf("<parameter id=\"%s\" value=\"%.6f\"/>\n",
                parameterName, value);
    }

    void writeParameters(EReaction reaction, EBounds bounds) {
        beginElement("kineticLaw");
        beginElement("listOfParameters");

        writeParameter("LOWER_BOUND", bounds.getLowerBound());
        writeParameter("UPPER_BOUND", bounds.getUpperBound());

        endElement("listOfParameters");
        endElement("kineticLaw");
    }

    void writeOneReaction(EReaction reaction, EBounds bounds) {
        writeReactionHeader(reaction.getSid(), reaction.getName(),
                reaction.getReversible());
        increaseIndent();

        writeGenes(reaction.getGenes());
        writeReactants(reaction);
        writeProducts(reaction);
        writeParameters(reaction, bounds);
        endElement("reaction");
    }

    void writeAllReactions(EModel model) {
        beginElement("listOfReactions");
        Collection<EBounds> bounds = model.getEBoundsCollection();
        for (EBounds bound : bounds) {
            EReaction reaction = bound.getReaction();
            writeOneReaction(reaction, bound);
        }
        endElement("listOfReactions");
    }

    String export(EModel model) {
        EMetabolism metabolism = model.getMetabolism();
        beginSBML();
        beginModel(metabolism);

        writeCompartments(metabolism);
        writeAllSpecies(metabolism);
        writeAllReactions(model);

        endElement("model");
        endElement("sbml");
        printWriter.flush();
        return stringWriter.toString();
    }
}
