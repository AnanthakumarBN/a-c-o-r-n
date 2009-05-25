/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.data.buffer;

import acorn.webservice.RepeatedVisualizationNameException_Exception;
import acorn.webservice.VisValidationException_Exception;
import acorn.data.buffer.structures.BadKeyInBufferStruct;
import acorn.data.buffer.structures.SpeciesReactionBuffer;
import acornwsclient.download.DBDataDownloader;
import acornwsclient.upload.DBDataUploader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.dbStructs.NameStruct;
import org.exceptions.VisValidationException;
import org.openide.util.Lookup.Result;
import org.visualapi.VisPlace;
import org.visualapi.VisTransition;
import org.interfaces.LoadSaveInterface;
import org.visualapi.VisEdge;

import org.structs.ComputationsVis;
import org.openide.util.Lookup;
import org.visualapi.VisNode;

/**
 *
 * @author markos
 */
public class DBSupporter {

    private String modelName;
    private List<String> modelNameList;
    private DBDataDownloader dataProvider;
    private DBDataUploader dataUploader;
    private SpeciesReactionBuffer srBuffer;
    private List<NameStruct> allReactionList;
    private List<NameStruct> allSpeciesList;
    private NameStruct[] template = new NameStruct[0];
    private List<String> visualizationNames;
    private String savedVisualizationName;

//    private GraphModelScene scene;
    private LoadSaveInterface graphProvider;
    private boolean fbaTask;
    private boolean doneTask;
    private Lookup.Result visCompResult;


    public DBSupporter() {
        dataProvider = new DBDataDownloader();
        dataUploader = new DBDataUploader();
        modelNameList = dataProvider.getModels();
        srBuffer = new SpeciesReactionBuffer();
        modelName = null;
        updateVisualizationNames();
        savedVisualizationName = null;
        fbaTask = false;
        doneTask = false;
    }

    public boolean isDoneTask() {
        return doneTask;
    }

    public boolean isFbaTask() {
        return fbaTask;
    }

    public Result getVisCompResult() {
        return visCompResult;
    }

    public void setVisCompResult(Result visCompResult) {
        this.visCompResult = visCompResult;
    }

    public void clearVisualizatioon() {
        graphProvider.clearVisualization();
    }

    public LoadSaveInterface getGraphProvider() {
        return graphProvider;
    }

    public void setGraphProvider(LoadSaveInterface graphProvider) {
        this.graphProvider = graphProvider;
    }

//    public void setScene(GraphModelScene scene) {
//        this.scene = scene;
//    }
    /**
     * @param reactants reactants in returned reactions
     * @param products products in returned reactions
     * @return List of reactions that uses reactants and products
     */
    public List<NameStruct> getSuitableReactions(VisTransition reaction) {
        List<NameStruct> reactants = reaction.getSourceNodesStruct();
        List<NameStruct> products = reaction.getTargetNodesStruct();
        List<NameStruct> finalList = new ArrayList<NameStruct>(allReactionList);
        List<NameStruct> notDownloadedSpeciesList;
        List<NameStruct> downloadedSpeciesList;
        if (reactants != null) {
            notDownloadedSpeciesList = srBuffer.getStructsToDownload(reactants, false, false);

            for (NameStruct notDownloadSpecies : notDownloadedSpeciesList) {
                List<NameStruct> downloadedReactionsList = dataProvider.getReactionsForSpecies(modelName, notDownloadSpecies.getSid(), false);
                finalList.retainAll(downloadedReactionsList);
                srBuffer.addNameStructList(notDownloadSpecies, downloadedReactionsList, false, false);
            }
            downloadedSpeciesList = srBuffer.getDownloadedStructs(reactants, false, false);
            for (NameStruct downlodedSpecies : downloadedSpeciesList) {
                finalList.retainAll(srBuffer.getListOfReactions(downlodedSpecies, false));
            }
        }
        if (products != null) {
            notDownloadedSpeciesList = srBuffer.getStructsToDownload(products, false, true);

            for (NameStruct notDownloadSpecies : notDownloadedSpeciesList) {
                List<NameStruct> downloadedReactionsList = dataProvider.getReactionsForSpecies(modelName, notDownloadSpecies.getSid(), true);
                finalList.retainAll(downloadedReactionsList);
                srBuffer.addNameStructList(notDownloadSpecies, downloadedReactionsList, false, true);
            }
            downloadedSpeciesList = srBuffer.getDownloadedStructs(products, false, true);
            for (NameStruct downlodedSpecies : downloadedSpeciesList) {
                finalList.retainAll(srBuffer.getListOfReactions(downlodedSpecies, true));
            }
        }
        return finalList;
    }

    public List<NameStruct> getSuitableSpecies(VisPlace place) {
        List<NameStruct> sourceReactions = place.getSourceNodesStruct();
        List<NameStruct> targetReactions = place.getTargetNodesStruct();
        List<NameStruct> finalSpeciesList = new ArrayList<NameStruct>(allSpeciesList);
        List<NameStruct> notDownloadedReactionList;
        List<NameStruct> downloadedReactionList;

        if (sourceReactions != null) {
            notDownloadedReactionList = srBuffer.getStructsToDownload(sourceReactions, true, false);
            for (NameStruct reaction : notDownloadedReactionList) {
                List<NameStruct> downloadedSpeciesList = dataProvider.getSpeciesForReaction(modelName, reaction.getSid(), false);
                finalSpeciesList.retainAll(downloadedSpeciesList);
                srBuffer.addNameStructList(reaction, downloadedSpeciesList, true, false);
            }
            downloadedReactionList = srBuffer.getDownloadedStructs(sourceReactions, true, false);
            for (NameStruct downloadedReaction : downloadedReactionList) {
                finalSpeciesList.retainAll(srBuffer.getListOfSpecies(downloadedReaction, false));
            }
        }

        if (targetReactions != null) {
            notDownloadedReactionList = srBuffer.getStructsToDownload(targetReactions, true, true);

            for (NameStruct reaction : notDownloadedReactionList) {
                List<NameStruct> downloadedSpeciesList = dataProvider.getSpeciesForReaction(modelName, reaction.getSid(), true);
                finalSpeciesList.retainAll(downloadedSpeciesList);
                srBuffer.addNameStructList(reaction, downloadedSpeciesList, true, true);
            }

            downloadedReactionList = srBuffer.getDownloadedStructs(targetReactions, true, true);
            for (NameStruct downloadedReaction : downloadedReactionList) {
                finalSpeciesList.retainAll(srBuffer.getListOfSpecies(downloadedReaction, true));
            }
        }
        return finalSpeciesList;
    }

    public void setSpeciesForReaction(NameStruct reaction) {
        List<NameStruct> reactions = new ArrayList<NameStruct>(1);
        reactions.add(reaction);
        List<NameStruct> reactionsForDownload;
        List<NameStruct> downloadedSpeciesForReaction;
        reactionsForDownload = srBuffer.getStructsToDownload(reactions, true, true);
        for (NameStruct react : reactionsForDownload) {
            downloadedSpeciesForReaction =
                    dataProvider.getSpeciesForReaction(modelName, react.getSid(), true);
            srBuffer.addNameStructList(react, downloadedSpeciesForReaction, true, true);
        }
        reactionsForDownload = srBuffer.getStructsToDownload(reactions, true, false);
        for (NameStruct react : reactionsForDownload) {
            downloadedSpeciesForReaction =
                    dataProvider.getSpeciesForReaction(modelName, react.getSid(), false);
            srBuffer.addNameStructList(react, downloadedSpeciesForReaction, true, false);
        }
    }

    /**
     *
     * @param reactionKey reaction NameStruct
     * @param sourceSpecies if true returns source list of species NameStructs else target list
     * @return list of reactants or products for reactionKey reaction
     */
    public List<NameStruct> getSpeciesForReaction(NameStruct reactionKey, boolean sourceSpecies){
        List<NameStruct> species = srBuffer.getListOfSpecies(reactionKey, sourceSpecies);
        if (species == null){
            species = dataProvider.getSpeciesForReaction(modelName, reactionKey.getSid(), sourceSpecies);
            srBuffer.addNameStructList(reactionKey, species, true, sourceSpecies);
        }
        return new ArrayList<NameStruct>(species);
    }

    public List<NameStruct> getReactionsForSpecies(NameStruct speciesKey, boolean sourceReactions){
        List<NameStruct> reactions = srBuffer.getListOfReactions(speciesKey, sourceReactions);

        if (reactions == null){
            reactions = dataProvider.getReactionsForSpecies(modelName, speciesKey.getSid(), sourceReactions);
            srBuffer.addNameStructList(speciesKey, reactions, false, sourceReactions);
        }
        return new ArrayList<NameStruct>(reactions);
    }

    public String getModelName() {
        return modelName;
    }

    public boolean setModelName(String modelName) {
        if (modelNameList.contains(modelName)) {
            this.modelName = modelName;
            allReactionList = dataProvider.getAllReactionsByModelName(modelName);
            allSpeciesList = dataProvider.getAllSpeciesByModelName(modelName);
            graphProvider.modelSet();
            this.updateVisualizationNames();
            graphProvider.clearVisualization();
            this.fbaTask = dataProvider.isFbaTask(modelName);
            this.doneTask = dataProvider.isTaskDone(modelName);

//            this.srBuffer.clear();
            return true;
        }
        return false;
    }

    public String[] getModelNameList() {
        String[] array = modelNameList.toArray(new String[0]);
        Arrays.sort(array);
        return array;
    }

    public NameStruct[] getAllReactionList() {
        return getSortedArray(allReactionList);
//        return allReactionList;
    }

    public NameStruct[] getAllSpeciesList() {
        return getSortedArray(allSpeciesList);
//        return allSpeciesList;
    }

    private NameStruct[] getSortedArray(List<NameStruct> list) {
        NameStruct[] array = list.toArray(template);
        Arrays.sort(array);
        return array;
    }

    public List<String> getVisualizationNames() {
        return visualizationNames;
    }

    public void updateVisualizationNames() {
        if (this.modelNameList.contains(this.modelName)) {
            this.visualizationNames = dataProvider.getDescendantVisualizationNames(modelName);
        }
    }

    public void removeVisualization(String name) {
        dataProvider.removeVisualization(name);
        this.updateVisualizationNames();
    }

    /**
     * if transitions are not valid VisValidationException is thrown with description what is not valid 
     * @param transitionList transitions which are validetad
     * @throws acorndatabuffer.structures.BadKeyInBufferStruct if transition is not in buffer struct
     * @throws acorndatabuffer.structures.VisValidationException if transitions aren't valid
     */
    public void validateVisualization() throws BadKeyInBufferStruct, VisValidationException {
        graphProvider.validateVisualizationGraph();
        List<VisTransition> transitionList = graphProvider.getTransitionsFromScene();
        List<NameStruct> sourceStructs = null;
        List<NameStruct> targetStructs = null;
        List<NameStruct> notValidStructs = null;
        for (VisTransition trans : transitionList) {
            sourceStructs = trans.getSourceNodesStruct();
            targetStructs = trans.getTargetNodesStruct();

            NameStruct transStruct = trans.getStruct();
            if (sourceStructs != null) {
                notValidStructs = srBuffer.getNotValidNameStructs(transStruct, sourceStructs, true, true);
                if (notValidStructs.size() != 0) {
                    String message = transStruct.getSid() + " doesn't have reactant(s): ";
                    for (NameStruct notValidStruct : notValidStructs) {
                        message = message.concat(notValidStruct.getSid() + ", ");
                    }
                    message = message.concat(".");
                    message = message.replace(", .", ".");
                    throw new VisValidationException(message);
                }
            }
            if (targetStructs != null) {
                notValidStructs = srBuffer.getNotValidNameStructs(transStruct, targetStructs, true, false);
                if (notValidStructs.size() != 0) {
                    String message = transStruct.getSid() + " doesn't have product(s): ";
                    for (NameStruct notValidStruct : notValidStructs) {
                        message = message.concat(notValidStruct.getSid() + ", ");
                    }
                    message = message.concat(".");
                    message = message.replace(", .", ".");
                    throw new VisValidationException(message);
                }
            }
        }
    }

    public void saveVisualization(String visualizationName)
            throws RepeatedVisualizationNameException_Exception, BadKeyInBufferStruct, VisValidationException, VisValidationException_Exception {

        validateVisualization();
        if (visualizationNames.contains(visualizationName)) {
            new VisValidationException("Visualization: " + visualizationName + "is in database.\n Write another one.");
        }
        List<VisTransition> transitions = graphProvider.getTransitionsFromScene();
        List<VisPlace> places = graphProvider.getPlacesFromScene();
        List<VisEdge> edges = graphProvider.getEdgesFromScene();
        dataUploader.saveVisualization(modelName, visualizationName, transitions, places, edges);
        visualizationNames.add(visualizationName);
        savedVisualizationName = visualizationName;
        graphProvider.nodesLocationAndControlPointsChanged();
        this.updateVisualizationNames();
    }

    public void eraseOldSaveNewVisualization(String visualizationName) throws BadKeyInBufferStruct, VisValidationException, RepeatedVisualizationNameException_Exception, VisValidationException_Exception {
        validateVisualization();
        this.removeVisualization(visualizationName);

        List<VisTransition> transitions = graphProvider.getTransitionsFromScene();
        List<VisPlace> places = graphProvider.getPlacesFromScene();
        List<VisEdge> edges = graphProvider.getEdgesFromScene();
        dataUploader.saveVisualization(modelName, visualizationName, transitions, places, edges);
        visualizationNames.add(visualizationName);
        savedVisualizationName = visualizationName;
        graphProvider.nodesLocationAndControlPointsChanged();
        this.updateVisualizationNames();

    }

    public void getVisualization(String visualizationName) {
        List<VisEdge> edges = dataProvider.getVisualization(visualizationName);
        List<NameStruct> sourceTransitions = new ArrayList<NameStruct>(0);
        List<NameStruct> targetTransitions = new ArrayList<NameStruct>(0);

        List<NameStruct> sourceTransitionsToDown = new ArrayList<NameStruct>(0);
        List<NameStruct> targetTransitionsToDown = new ArrayList<NameStruct>(0);

        //updates SpeciesReactionBuffer
        for (VisEdge edge : edges) {
            if (edge.getSource().isTransition()) {
                sourceTransitions.add(edge.getSource().getStruct());
            } else {
                targetTransitions.add(edge.getTarget().getStruct());
            }
        }
        sourceTransitionsToDown = srBuffer.getStructsToDownload(sourceTransitions, true, false);
        targetTransitionsToDown = srBuffer.getStructsToDownload(sourceTransitions, true, true);

        //downloads from web service
        for (NameStruct trans : sourceTransitionsToDown) {
            List<NameStruct> targetSpecies = dataProvider.getSpeciesForReaction(modelName, trans.getSid(), false);
            srBuffer.addNameStructList(trans, targetSpecies, true, false);
        }

        for (NameStruct trans : targetTransitionsToDown) {
            List<NameStruct> sourceSpecies = dataProvider.getSpeciesForReaction(modelName, trans.getSid(), true);
            srBuffer.addNameStructList(trans, sourceSpecies, true, true);
        }
        Collection<ComputationsVis> c = (Collection<ComputationsVis>) visCompResult.allInstances();
        if (!c.isEmpty()) {
            graphProvider.loadVisualization(edges, true);
        } else {
            graphProvider.loadVisualization(edges, false);
        }
        this.savedVisualizationName = visualizationName;
    }

    public void addcomputationsToTransitionsName(boolean addComp) {
        graphProvider.addComputationsToTransitionsLabel(addComp);
    }

    public void nameVisNode(VisNode node, NameStruct struct){
        node.setName(struct.getName());
        node.setSid(struct.getSid());
        node.setXmlSid("");
        
        if(node.isTransition()){
            ((VisTransition)node).setFlux( dataProvider.getFlux(modelName, struct.getSid()));
        }
    }

    public float getFlux(String sid){
        return dataProvider.getFlux(modelName, sid);
    }
}