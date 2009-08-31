/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vislogicengine;

import acorn.data.buffer.DBSupporter;
import java.awt.Point;
import java.util.Collection;
import java.util.List;
import org.dbStructs.NameStruct;
import org.interfaces.LoadSaveInterface;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;
import org.structs.ComputationsVis;
import org.visualapi.VisNode;
import org.visualapi.VisPlace;
import org.visualapi.VisTransition;

/**
 *
 * @author markos
 */
public class VisLogic {

    private LoadSaveInterface graphProvider;
    private DBSupporter dbsupp;
    private Lookup.Result visCompResult;
    private static final int heightBetweenNodes = 100;
    private static final int widthBetweenNodes = 120;

    public VisLogic(LoadSaveInterface graphProvider) {
        this.graphProvider = graphProvider;
    }

    public VisLogic() {
    }

    public Result getVisCompResult() {
        return visCompResult;
    }

    public void setVisCompResult(Result visCompResult) {
        this.visCompResult = visCompResult;
    }

    /**
     * @return true if computations from SFBA should be on visualization (on right hand of transition sid)
     * , false in other case
     */
    public boolean computationsVisualized() {
        if (visCompResult == null) {
            return false;
        }
        Collection<ComputationsVis> c = (Collection<ComputationsVis>) visCompResult.allInstances();
        return !c.isEmpty();
    }

    public void setGraphProvider(LoadSaveInterface graphProvider) {
        this.graphProvider = graphProvider;
    }

    public void setDbsupp(DBSupporter dbsupp) {
        this.dbsupp = dbsupp;
    }

    /**
     *
     * @param node adds source nodes or target nodes for this node
     * @param sourceNodes - if true source nodes will be added (i.e. for reaction will be reactants),
     * if false target nodes will be added (i.e. for reaction will be products)
     */
    public void addNodes(VisNode node, boolean sourceNodes) {
        List<NameStruct> visualizedStructs = null;
        if (sourceNodes) {
            visualizedStructs = node.getSourceNodesStruct();
        } else {
            visualizedStructs = node.getTargetNodesStruct();
        }

        List<NameStruct> sourceStructs = null;
        if (node.isPlace()) {
            sourceStructs = dbsupp.getReactionsForSpecies(node.getStruct(), sourceNodes);
        } else {
            sourceStructs = dbsupp.getSpeciesForReaction(node.getStruct(), sourceNodes);
        }
        sourceStructs.removeAll(visualizedStructs);
        int nodesNumber = sourceStructs.size();
        int startLocation = -nodesNumber / 2;
        for (NameStruct struct : sourceStructs) {
            Point genPoint = node.getLocation();
            Point p = new Point(genPoint.x + startLocation * widthBetweenNodes, genPoint.y);
            if (sourceNodes) {
                p.y -= heightBetweenNodes;
            } else {
                p.y += heightBetweenNodes;
            }
            VisNode sourceNode = null;
            if (node.isPlace()) {
                sourceNode = new VisTransition(struct.getName(), struct.getSid(), p, "", dbsupp.getFlux(struct.getSid()));

                // for validation purpose - validation is by speciesForReaction list
                dbsupp.getSpeciesForReaction(struct, !sourceNodes);
            } else {
                sourceNode = new VisPlace(struct.getName(), struct.getSid(), p, "");
            }
            graphProvider.addNodeToScene(sourceNode, computationsVisualized());
            if (sourceNodes) {
                graphProvider.addNewEdge(sourceNode, node);
            } else {
                graphProvider.addNewEdge(node, sourceNode);
            }
            startLocation++;
        }
        graphProvider.validateNewGraph();
    }
}
