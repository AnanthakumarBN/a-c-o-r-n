/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.data.buffer;

import acorn.webservice.AuthenticationException_Exception;
import acorn.webservice.RepeatedVisualizationNameException_Exception;
import acorn.webservice.VisValidationException_Exception;
import org.exceptions.BadKeyInBufferStruct;
import acorn.data.buffer.structures.SpeciesReactionBuffer;
import acornwsclient.download.DBDataDownloader;
import acornwsclient.upload.DBDataUploader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.dbStructs.ModelStruct;
import org.dbStructs.NameStruct;
import org.dbStructs.VisStruct;
import org.exceptions.VisValidationException;
import org.openide.util.Exceptions;
import org.visualapi.VisPlace;
import org.visualapi.VisTransition;
import org.visualapi.VisEdge;

/**
 *
 * @author markos
 */
public class DBSupporter {

    private ModelStruct selectedModel;
    private Date modelModificationDate;
    private List<ModelStruct> modelList;
    private DBDataDownloader dataProvider;
    private DBDataUploader dataUploader;
    private SpeciesReactionBuffer srBuffer;
    private List<NameStruct> allReactionList;
    private List<NameStruct> allSpeciesList;
    private NameStruct[] template = new NameStruct[0];
    private List<String> visualizationNames;
    private boolean fbaTask;
    private boolean doneTask;
    private HashMap<String, Float> fluxMap;
    private VisStruct vis;

    public DBSupporter(String user, String MD5pass) {
        dataProvider = new DBDataDownloader(user, MD5pass);
        dataUploader = new DBDataUploader(user, MD5pass);

//        String user = NbPreferences.forModule(UserManagementPanel.class).get("user", "");
        modelList = dataProvider.getModels();
        srBuffer = new SpeciesReactionBuffer();
        selectedModel = null;
        updateVisualizationNames();
        fbaTask = false;
        doneTask = false;

        fluxMap = new HashMap<String, Float>();
    }

    public void removeDetachedTransitions(Collection<NameStruct> detachedReactions) {
        List<NameStruct> newListReaction = dataProvider.getAllReactionsByModelId(selectedModel.getId());
        List<NameStruct> duplicateNewList = new ArrayList<NameStruct>(newListReaction);
        duplicateNewList.removeAll(this.allReactionList);
        this.allReactionList = newListReaction;

        /**
         * TODO update srbuffer -> reactions in duplicateNewList
         */
        for (NameStruct updateReaction : duplicateNewList) {
            List<NameStruct> sourceSpec = getSpeciesForReaction(updateReaction, true);
            List<NameStruct> targetSpec = getSpeciesForReaction(updateReaction, false);

            srBuffer.addReactionAndUpdateSpecies(updateReaction, sourceSpec, targetSpec);
        }

        for (NameStruct detReact : detachedReactions) {
            this.srBuffer.removeReaction(detReact);
        }
    }

    public void updateModelsList() {
        modelList = dataProvider.getModels();
    }

    public boolean isDoneTask() {
        return doneTask;
    }

    public int getSelectedModelId() {
        if (selectedModel == null) {
            return -1;
        }
        return selectedModel.getId();
    }

    public boolean isFbaTask() {
        return fbaTask;
    }

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
                List<NameStruct> downloadedReactionsList = dataProvider.getReactionsForSpecies(selectedModel.getId(), notDownloadSpecies.getSid(), false);
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
                List<NameStruct> downloadedReactionsList = dataProvider.getReactionsForSpecies(selectedModel.getId(), notDownloadSpecies.getSid(), true);
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
                List<NameStruct> downloadedSpeciesList = dataProvider.getSpeciesForReaction(selectedModel.getId(), reaction.getSid(), false);
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
                List<NameStruct> downloadedSpeciesList = dataProvider.getSpeciesForReaction(selectedModel.getId(), reaction.getSid(), true);
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
                    dataProvider.getSpeciesForReaction(selectedModel.getId(), react.getSid(), true);
            srBuffer.addNameStructList(react, downloadedSpeciesForReaction, true, true);
        }
        reactionsForDownload = srBuffer.getStructsToDownload(reactions, true, false);
        for (NameStruct react : reactionsForDownload) {
            downloadedSpeciesForReaction =
                    dataProvider.getSpeciesForReaction(selectedModel.getId(), react.getSid(), false);
            srBuffer.addNameStructList(react, downloadedSpeciesForReaction, true, false);
        }
    }

    /**
     *
     * @param reactionKey reaction NameStruct
     * @param sourceSpecies if true returns source list of species NameStructs else target list
     * @return list of reactants or products for reactionKey reaction
     */
    public List<NameStruct> getSpeciesForReaction(NameStruct reactionKey, boolean sourceSpecies) {
        List<NameStruct> species = srBuffer.getListOfSpecies(reactionKey, sourceSpecies);
        if (species == null) {
            species = dataProvider.getSpeciesForReaction(selectedModel.getId(), reactionKey.getSid(), sourceSpecies);
            srBuffer.addNameStructList(reactionKey, species, true, sourceSpecies);
        }
        return new ArrayList<NameStruct>(species);
    }

    public List<NameStruct> getReactionsForSpecies(NameStruct speciesKey, boolean sourceReactions) {
        List<NameStruct> reactions = srBuffer.getListOfReactions(speciesKey, sourceReactions);

        if (reactions == null) {
            reactions = dataProvider.getReactionsForSpecies(selectedModel.getId(), speciesKey.getSid(), sourceReactions);
            srBuffer.addNameStructList(speciesKey, reactions, false, sourceReactions);
        }
        return new ArrayList<NameStruct>(reactions);
    }

    public String getModelName() {
        return selectedModel.getName();
    }

    public ModelStruct getSelectedModel() {
        return selectedModel;
    }

    public void setSelectedModel(ModelStruct selectedModel) {
        this.selectedModel = selectedModel;
    }

    public void setModel(ModelStruct model) {
        this.selectedModel = model;
        this.modelModificationDate = dataProvider.getModelModificationDate(model.getId());
        allReactionList = dataProvider.getAllReactionsByModelId(selectedModel.getId());
        allSpeciesList = dataProvider.getAllSpeciesByModelId(selectedModel.getId());
        this.updateVisualizationNames();
        try {
            this.fbaTask = dataProvider.isFbaTask(selectedModel.getId());
            this.doneTask = dataProvider.isTaskDone(selectedModel.getId());
        } catch (AuthenticationException_Exception ex) {
            Exceptions.printStackTrace(ex);
        }


        //deletes all data buffered earlier
        fluxMap.clear();
        this.srBuffer.clear();
    }

    public Date getModelModificationDate() {
        return modelModificationDate;
    }

    public void setModelModificationDate(Date modelModificationDate) {
        this.modelModificationDate = modelModificationDate;
    }

    public ModelStruct[] getModelsArray() {
        ModelStruct[] array = modelList.toArray(new ModelStruct[0]);
        Arrays.sort(array);
        return array;
    }

    public List<ModelStruct> getModelList() {
        return modelList;
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
        if (this.modelList.contains(this.selectedModel)) {
            this.visualizationNames = dataProvider.getAncestorVisualizationNames(selectedModel.getId());
        }
    }

    public void removeVisualization(String name) {
        try {
            dataProvider.removeVisualization(name);
        } catch (AuthenticationException_Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        this.updateVisualizationNames();
    }

    /**
     * if transitions are not valid VisValidationException is thrown with description what is not valid 
     * @param transitionList transitions which are validetad
     * @throws acorndatabuffer.structures.BadKeyInBufferStruct if transition is not in buffer struct
     * @throws acorndatabuffer.structures.VisValidationException if transitions aren't valid
     */
    public void validateVisualization(List<VisTransition> transitionList) throws BadKeyInBufferStruct, VisValidationException {

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

    public void saveNewVisualization(String visualizationName, List<VisTransition> transitions, List<VisPlace> places, List<VisEdge> edges, boolean shared)
            throws RepeatedVisualizationNameException_Exception, BadKeyInBufferStruct, VisValidationException, VisValidationException_Exception {

        if (visualizationNames.contains(visualizationName)) {
            new VisValidationException("Visualization: " + visualizationName + "is in database.\n Write another one.");
        }
        saveVisualization(visualizationName, transitions, places, edges, shared);
    }

    public void eraseOldSaveNewVisualization(String visualizationName, List<VisTransition> transitions, List<VisPlace> places, List<VisEdge> edges, boolean shared)
            throws BadKeyInBufferStruct, VisValidationException, RepeatedVisualizationNameException_Exception, VisValidationException_Exception {

        this.removeVisualization(visualizationName);
        saveVisualization(visualizationName, transitions, places, edges, shared);
    }

    private void saveVisualization(String visualizationName, List<VisTransition> transitions, List<VisPlace> places, List<VisEdge> edges, boolean shared)
            throws RepeatedVisualizationNameException_Exception, VisValidationException_Exception, VisValidationException_Exception {
        try {
            dataUploader.saveVisualization(selectedModel.getId(), visualizationName, transitions, places, edges, shared);
        } catch (AuthenticationException_Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        visualizationNames.add(visualizationName);
        this.updateVisualizationNames();
    }

    //temporary solution until this VAAP is revritten
    public static String getOwnerLogin(String visName) {
        int l = visName.indexOf('.');
        return visName.substring(0, l);
    }

    //temporary solution until this VAAP is revritten
    public static String getShortName(String visName) {
        int l = visName.indexOf('.');
        return visName.substring(l + 1);
    }

    public List<VisEdge> getVisualization(String vN) {
        String ownerLogin = getOwnerLogin(vN);
        String visualizationName = getShortName(vN);
        List<VisEdge> edges = dataProvider.getVisualization(visualizationName, ownerLogin);
        this.vis = dataProvider.getVisualizationObject(visualizationName, ownerLogin);
        List<NameStruct> sourceTransitions = new ArrayList<NameStruct>(0);
        List<NameStruct> targetTransitions = new ArrayList<NameStruct>(0);

        List<NameStruct> sourceTransitionsToDown = new ArrayList<NameStruct>(0);
        List<NameStruct> targetTransitionsToDown = new ArrayList<NameStruct>(0);

        //updates SpeciesReactionBuffer - validation expects updated structure for transitions
        for (VisEdge edge : edges) {
            if (edge.getSource().isTransition()) {
                sourceTransitions.add(edge.getSource().getStruct());
            } else {
                targetTransitions.add(edge.getTarget().getStruct());
            }
        }
        //for sourceTransitions -> searched for target species
        sourceTransitionsToDown = srBuffer.getStructsToDownload(sourceTransitions, true, false);
        //for targetTransitions -> searched for source species
        targetTransitionsToDown = srBuffer.getStructsToDownload(targetTransitions, true, true);

        //downloads from web service
        for (NameStruct trans : sourceTransitionsToDown) {
            List<NameStruct> targetSpecies = dataProvider.getSpeciesForReaction(selectedModel.getId(), trans.getSid(), false);
            srBuffer.addNameStructList(trans, targetSpecies, true, false);
        }

        for (NameStruct trans : targetTransitionsToDown) {
            List<NameStruct> sourceSpecies = dataProvider.getSpeciesForReaction(selectedModel.getId(), trans.getSid(), true);
            srBuffer.addNameStructList(trans, sourceSpecies, true, true);
        }
        return edges;
    }

    public float getFlux(String sid) {
        if (!fluxMap.containsKey(sid)) {
            fluxMap.put(sid, dataProvider.getFlux(selectedModel.getId(), sid));
        }
        return fluxMap.get(sid);
    }

    public boolean getIsCurrentVisShared() {
        return vis.isShared();
    }

    public boolean getCanCurrentVisBeModified() {
        return vis.isCanModify();
    }
}
