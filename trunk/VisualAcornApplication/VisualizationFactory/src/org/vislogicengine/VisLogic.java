/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vislogicengine;

import acorn.data.buffer.DBSupporter;

import acorn.webservice.RepeatedVisualizationNameException_Exception;
import acorn.webservice.VisValidationException_Exception;
import acornwsclient.download.DBDataDownloader;
import java.awt.Point;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.dbStructs.ModelStruct;
import org.dbStructs.NameStruct;
import org.exceptions.BadKeyInBufferStruct;
import org.exceptions.VisValidationException;
import org.interfaces.LoadSaveInterface;
import org.interfaces.TopComponentHelperInterface;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;
import org.openide.util.NbPreferences;
import org.structs.ComputationsVis;
import org.usermanagement.UserManagementPanel;
import org.visualapi.VisEdge;
import org.visualapi.VisNode;
import org.visualapi.VisPlace;
import org.visualapi.VisTransition;

/**
 *
 * @author markos
 */
public class VisLogic {

    private LoadSaveInterface graphProvider;
    private static DBSupporter dbsupp;
    private Lookup.Result visCompResult;
    private static TopComponentHelperInterface tchelper;
    private static DBDataDownloader dataDownloader;
    private static final int heightBetweenNodes = 100;
    private static final int widthBetweenNodes = 120;
    private static String user;
    private static String MD5pass;
    private static boolean isUserSet = false;



    public VisLogic() {
    }

    public void clearVisualization() {
        graphProvider.clearVisualization();
    }

    public void eraseOldSaveNewVisualization(String visName) throws BadKeyInBufferStruct, VisValidationException, RepeatedVisualizationNameException_Exception, VisValidationException_Exception {
        String newName = isVisualizationNameProper(visName);
        this.validateVisualization();
        List<VisTransition> transitions = graphProvider.getTransitionsFromScene();
        List<VisPlace> places = graphProvider.getPlacesFromScene();
        List<VisEdge> edges = graphProvider.getEdgesFromScene();

        dbsupp.eraseOldSaveNewVisualization(newName, transitions, places, edges);

        graphProvider.nodesLocationAndControlPointsChanged();
    }

    public ModelStruct[] getModelsArray() {
        if(!VisLogic.isUserSet){
            return new ModelStruct[0];
        }
        return dbsupp.getModelsArray();
    }

    public List<NameStruct> getSuitableReactions(VisTransition visTransition) {
        isModelModified();
        return dbsupp.getSuitableReactions(visTransition);
    }

    public List<NameStruct> getSuitableSpecies(VisPlace visPlace) {
        isModelModified();
        return dbsupp.getSuitableSpecies(visPlace);
    }

    public void getVisualization(String visName) {
        Collection<ComputationsVis> c = (Collection<ComputationsVis>) visCompResult.allInstances();
        List<VisEdge> edges;

        if (!c.isEmpty()) {
            edges = dbsupp.getVisualization(visName);
            graphProvider.loadVisualization(edges, true);
        } else {
            edges = dbsupp.getVisualization(visName);
            graphProvider.loadVisualization(edges, false);
        }
    }

    public List<String> getVisualizationNames() {
        return dbsupp.getVisualizationNames();
    }

    public boolean isDoneTask() {
        return dbsupp.isDoneTask();
    }

    public boolean isFbaTask() {
        return dbsupp.isFbaTask();
    }

    public boolean isModelSet() {
        return dbsupp.getModelName() != null;
    }

    public void removeVisualization(String visName) {
        dbsupp.removeVisualization(visName);
    }

    public boolean setModel(ModelStruct model) {
        if (dbsupp.getModelList().contains(model)) {
            dbsupp.setModel(model);
            graphProvider.modelSet();
            graphProvider.clearVisualization();
            return true;
        }
        return false;
    }

    public void setSpeciesForReaction(NameStruct selectedStruct) {
        dbsupp.setSpeciesForReaction(selectedStruct);
    }

    public void validateVisualization() throws VisValidationException, BadKeyInBufferStruct {
        graphProvider.validateVisualizationGraph();
        List<VisTransition> transitionList = graphProvider.getTransitionsFromScene();
        dbsupp.validateVisualization(transitionList);
    }

    public void addcomputationsToTransitionsName(boolean addComp) {
        graphProvider.addComputationsToTransitionsLabel(addComp);
    }

    public void saveNewVisualization(String visName) throws VisValidationException, RepeatedVisualizationNameException_Exception, BadKeyInBufferStruct, BadKeyInBufferStruct, VisValidationException_Exception {
        String newName = isVisualizationNameProper(visName);

        this.validateVisualization();
        List<VisTransition> transitions = graphProvider.getTransitionsFromScene();
        List<VisPlace> places = graphProvider.getPlacesFromScene();
        List<VisEdge> edges = graphProvider.getEdgesFromScene();

        dbsupp.saveNewVisualization(newName, transitions, places, edges);

        graphProvider.nodesLocationAndControlPointsChanged();
    }

    public String isVisualizationNameProper(String visName) throws VisValidationException, VisValidationException {
        if (visName == null || visName.equals("")) {
            throw new VisValidationException("Write name for visualization");
        }
        if (!visName.matches("[ a-zA-Z_0-9]*")) {
            throw new VisValidationException("You can use only letters, digits, white spaces and underscores");
        }
        String newName = visName.replace(' ', '_');
        return newName;
    }

    public void setTchepler(TopComponentHelperInterface tchi) {
        tchelper = tchi;
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
        VisLogic.dbsupp = dbsupp;
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

        Collection<NameStruct> detachedReactions = isModelModified();
        if (detachedReactions != null) {
            if (detachedReactions.contains(new NameStruct(node.getName(), node.getSid())));
            return;
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

    public static void updateModelsList() {
        if (!VisLogic.isUserSet) {
            VisLogic.isUserSet = true;
            VisLogic.user = NbPreferences.forModule(UserManagementPanel.class).get("user", "");
            VisLogic.MD5pass = NbPreferences.forModule(UserManagementPanel.class).get("pass", "");
            dbsupp = new DBSupporter(VisLogic.user, VisLogic.MD5pass);
            VisLogic.dataDownloader = new DBDataDownloader(VisLogic.user, VisLogic.MD5pass);
        }
        dbsupp.updateModelsList();
        tchelper.updateModelsList(dbsupp.getModelsArray());
    }

    public Collection<NameStruct> isModelModified() {
        int modelId = dbsupp.getSelectedModelId();
        Date localModificationDate = dbsupp.getModelModificationDate();
        Date serverModificationDate = dataDownloader.getModelModificationDate(modelId);

        if (!localModificationDate.equals(serverModificationDate)) {
            dbsupp.setModelModificationDate(serverModificationDate);
            Collection<NameStruct> detachedReactions = dataDownloader.getDetachedReactions(modelId);

            graphProvider.removeDetachedTransitions(detachedReactions);
            dbsupp.removeDetachedTransitions(detachedReactions);

            String modelName = dbsupp.getModelName();
            String msg = "Model " + modelName + " has been updated.\n Some transitions from visualization may disapper.";
            NotifyMessage.displayInfromationMessage(msg);
            return detachedReactions;
        }
        return null;
    }

    public void nameVisNode(VisNode node, NameStruct struct) {
        node.setName(struct.getName());
        node.setSid(struct.getSid());
        node.setXmlSid("");

        if (node.isTransition()) {
            ((VisTransition) node).setFlux(dbsupp.getFlux(node.getSid()));
        }
    }
}
