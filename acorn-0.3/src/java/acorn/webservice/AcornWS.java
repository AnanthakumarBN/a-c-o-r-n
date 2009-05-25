/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.webservice;

import acorn.db.EMethodData;
import acorn.db.EModel;
import acorn.db.EModelController;
import acorn.db.EProductController;
import acorn.db.EReactantController;
import acorn.db.EReaction;
import acorn.db.EReactionController;
import acorn.db.ESpecies;
import acorn.db.ESpeciesController;
import acorn.db.ETask;
import acorn.db.ETaskController;
import acorn.db.EVisArcProduct;
import acorn.db.EVisArcReactant;
import acorn.db.EVisPlace;
import acorn.db.EVisTransition;
import acorn.db.EVisualization;
import acorn.db.EVisualizationController;
import acorn.exception.RepeatedVisualizationNameException;
import java.awt.Point;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.String;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.persistence.NoResultException;
import org.dbStructs.NameStruct;
import org.exceptions.VisValidationException;
import org.visualapi.VisEdge;
import org.visualapi.VisPlace;
import org.visualapi.VisTransition;

/**
 *
 * @author markos
 */
@WebService()
public class AcornWS {

    private EReactantController eReactantContr = new EReactantController();
    private EProductController eProductContr = new EProductController();
    private ESpeciesController eSpeciesContr = new ESpeciesController();
    private EReactionController eReactionContr = new EReactionController();
    private EModelController eModelController = new EModelController();
    private ETaskController eTaskController = new ETaskController();
    private EVisualizationController eVisualizationController = new EVisualizationController();

    /**
     * Web service operation
     */
    /**
     *
     * @return sorted array of models names
     */
    @WebMethod(operationName = "getModels")
    public String[] getModels() {
        List<EModel> lem = eModelController.getModels();
//        ArrayList<String> modelElements = new ArrayList<String>(lem.size());
        String[] modelElements = new String[lem.size()];
        int i = 0;
        for (EModel em : lem) {
//            modelElements.set(i++, em.getName());
            modelElements[i++] = em.getName();
        }
        Arrays.sort(modelElements);
        return modelElements;
    }

    /**
     * @param modelName name of model
     * @return sorted NameStruct of all ractions for model
     */
    @WebMethod(operationName = "getAllReactionsByModelName")
    public String getAllReactionsByModelName(@WebParam(name = "modelName") String modelName) {
        List<EReaction> reactList = eReactionContr.getByModelName(modelName);
        NameStruct[] allReactions = new NameStruct[reactList.size()];
        int i = 0;
        for (EReaction react : reactList) {
            allReactions[i++] = new NameStruct(react.getName(), react.getSid());
        }
        Arrays.sort(allReactions);
        return getXmlSerialization(allReactions);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getAllSpeciesByModelName")
    public String getAllSpeciesByModelName(@WebParam(name = "modelName") String modelName) {
        List<ESpecies> specList = eSpeciesContr.getSpecies(modelName);
        NameStruct[] allSpecies = new NameStruct[specList.size()];
        int i = 0;
        for (ESpecies spec : specList) {
            allSpecies[i++] = new NameStruct(spec.getName(), spec.getSid());
        }
        Arrays.sort(allSpecies);

        return getXmlSerialization(allSpecies);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getReactionsForSpecies")
    public String getReactionsForSpecies(@WebParam(name = "modelName") String modelName, @WebParam(name = "speciesSid") String speciesSid, @WebParam(name = "isSource") boolean isSource) {
        ESpecies species = eSpeciesContr.getBySidName(modelName, speciesSid);
        NameStruct[] structArray = null;
        if (species == null) {
            return new String("");
        }

        // list of reaction is source, so species is target/product so use eProductContr
        if (isSource) {
            structArray = StructArrayGen.getSortedReactionsArray(eProductContr.getReactions(species), modelName);
        } else {
            structArray = StructArrayGen.getSortedReactionsArray(eReactantContr.getReactions(species), modelName);
        }
        return getXmlSerialization(structArray);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getSpeciesForReaction")
    public String getSpeciesForReaction(@WebParam(name = "modelName") String modelName, @WebParam(name = "reactionSid") String reactionSid, @WebParam(name = "isSource") boolean isSource) {
        EReaction reaction = eReactionContr.getByModelNameAndReactionSid(reactionSid, modelName);
        NameStruct[] structArray = null;
        if (reaction == null) {
            return new String("");
        }
        //List of Species is source/reactant so use eReactantController
        if (isSource) {
            structArray = StructArrayGen.getSortedSpeciesArray(eReactantContr.getSpecies(reaction));
        } else {
            structArray = StructArrayGen.getSortedSpeciesArray(eProductContr.getSpecies(reaction));
        }

        return getXmlSerialization(structArray);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getEncoding")
    public String getEncoding() {
        OutputStreamWriter out = new OutputStreamWriter(new ByteArrayOutputStream());
        return out.getEncoding();
    }

    private String getXmlSerialization(NameStruct[] structArray) {
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        try {
            XMLEncoder encoder = new XMLEncoder(fos);
            for (NameStruct str : structArray) {
                encoder.writeObject(str);
            }
            encoder.close();
            return fos.toString();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "saveVisualization")
    public boolean saveVisualization(
            @WebParam(name = "modelName") String modelName, @WebParam(name = "visualizationName") String visualizationName,
            @WebParam(name = "reactions") String reactions, @WebParam(name = "species") String species,
            @WebParam(name = "arcs") String arcs,
            @WebParam(name = "clientEncoding") String clientEncoding) throws RepeatedVisualizationNameException, VisValidationException {
        List<VisTransition> visTransitions = null;
        List<VisPlace> visPlaces = null;
        List<VisEdge> visEdges = null;
        if (eVisualizationController.isVisualizationNameUsed(visualizationName)) {
            throw new RepeatedVisualizationNameException("Visualization: " + visualizationName + " is in database. Write another one.");
        }
        try {
            visTransitions = getDeserializedVisTransitions(reactions, clientEncoding);
            visPlaces = getDeserializedVisPlaces(species, clientEncoding);
            visEdges = getDeserializedVisEdges(arcs, clientEncoding);

        } catch (UnsupportedEncodingException ex) {
            return false;
        }
        EVisualization vis = new EVisualization(visualizationName);

        //maps will be used for finding connection/creating EVisArcReactant and EVisArcProduct
        HashMap<String, EVisTransition> transitionMap = new HashMap<String, EVisTransition>(0);
        HashMap<String, EVisPlace> placeMap = new HashMap<String, EVisPlace>(0);

        HashMap<String, ESpecies> speciesMap = new HashMap<String, ESpecies>(0);
        HashMap<String, EReaction> reactionMap = new HashMap<String, EReaction>(0);

        List<EVisTransition> eVisTransitions = new ArrayList<EVisTransition>(0);
        List<EVisPlace> eVisPlaces = new ArrayList<EVisPlace>(0);
        List<EVisArcReactant> eVisArcReactants = new ArrayList<EVisArcReactant>(0);
        List<EVisArcProduct> eVisArcProducts = new ArrayList<EVisArcProduct>(0);

        EModel model = eModelController.getModelByName(modelName);

        Point point;
        String xmlSid;
        //transitions creation
        for (VisTransition trans : visTransitions) {
            point = trans.getLocation();
            xmlSid = trans.getXmlSid();

            if (!reactionMap.containsKey(trans.getSid())) {
                EReaction reaction = eReactionContr.getByModelAndReactionSid(trans.getSid(), model);
                if (reaction == null) {
                    throw new VisValidationException("Model: " + modelName + " does not have " + trans.getSid() + " reaction.");
                }
                reactionMap.put(trans.getSid(), reaction);
            }

            EVisTransition eVisTransition = new EVisTransition(xmlSid, point, reactionMap.get(trans.getSid()), vis);
            eVisTransitions.add(eVisTransition);
            transitionMap.put(xmlSid, eVisTransition);
        }

//        places creation
        for (VisPlace place : visPlaces) {
            point = place.getLocation();
            xmlSid = place.getXmlSid();
            if (!speciesMap.containsKey(place.getSid())) {
                ESpecies spec = eSpeciesContr.getBySidName(modelName, place.getSid());
                if (spec == null) {
                    throw new VisValidationException("Model: " + modelName + " does not have " + place.getSid() + " species.");
                }
                speciesMap.put(place.getSid(), spec);
            }
            EVisPlace eVisPlace = new EVisPlace(xmlSid, point, vis, speciesMap.get(place.getSid()));
            eVisPlaces.add(eVisPlace);
            placeMap.put(xmlSid, eVisPlace);
        }
        //EVisArcReactants and EVisArcProducts creation

        for (VisEdge edge : visEdges) {
            if (edge.getSource() instanceof VisPlace) {
                VisPlace visPlace = (VisPlace) edge.getSource();
                VisTransition visTransition = (VisTransition) edge.getTarget();
                EVisArcReactant arcReactant = new EVisArcReactant(placeMap.get(visPlace.getXmlSid()), transitionMap.get(visTransition.getXmlSid()), vis);
                eVisArcReactants.add(arcReactant);
            } else {
                VisPlace visPlace = (VisPlace) edge.getTarget();
                VisTransition visTransition = (VisTransition) edge.getSource();
                EVisArcProduct arcProduct = new EVisArcProduct(transitionMap.get(visTransition.getXmlSid()), placeMap.get(visPlace.getXmlSid()), vis);
                eVisArcProducts.add(arcProduct);
            }
        }


//        for (VisTransition trans : visTransitions) {
//            HashSet<VisNode> sourcePlaces = trans.getSourceNodes();
//            HashSet<VisNode> targetPlaces = trans.getTargetNodes();
//            point = trans.getLocation();
//            for (VisNode place : sourcePlaces) {
//
//                EVisArcReactant arcReactant = new EVisArcReactant(placeMap.get(place.getXmlSid())
//                        , transitionMap.get(trans.getXmlSid()), vis);
//                eVisArcReactants.add(arcReactant);
//            }
//            for (VisNode place : targetPlaces) {
//
//                EVisArcProduct arcProduct = new EVisArcProduct(transitionMap.get(trans.getXmlSid())
//                        , placeMap.get(place.getXmlSid()), vis);
//                eVisArcProducts.add(arcProduct);
//            }
//        }

        vis.setModel(model);
        vis.setArcResource(eVisArcReactants);
        vis.setArcProduct(eVisArcProducts);
        vis.setPlaces(eVisPlaces);
        vis.setTransitions(eVisTransitions);

        model.getEVisualizations().add(vis);
        eVisualizationController.addVisualization(vis);
        return true;
    }

    private List<VisPlace> getDeserializedVisPlaces(String inputString, String clientEncoding)
            throws UnsupportedEncodingException {

        List<VisPlace> placeList = new ArrayList<VisPlace>(0);
        XMLDecoder d = getXMLDecoder(inputString, clientEncoding);

        try {
            while (true) {
                placeList.add((VisPlace) d.readObject());
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            return placeList;
        }
    }

    private List<VisEdge> getDeserializedVisEdges(String inputString, String clientEncoding)
            throws UnsupportedEncodingException {

        List<VisEdge> edgeList = new ArrayList<VisEdge>(0);
        XMLDecoder d = getXMLDecoder(inputString, clientEncoding);

        try {
            while (true) {
                edgeList.add((VisEdge) d.readObject());
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            return edgeList;
        }
    }

    private List<VisTransition> getDeserializedVisTransitions(String inputString, String clientEncoding)
            throws UnsupportedEncodingException {
        List<VisTransition> transitions = new ArrayList<VisTransition>(0);
        XMLDecoder d = getXMLDecoder(inputString, clientEncoding);

        try {
            while (true) {
                transitions.add((VisTransition) d.readObject());
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            return transitions;
        }
    }

    private XMLDecoder getXMLDecoder(String inputString, String clientEncoding) throws UnsupportedEncodingException {
        byte[] byteArray = null;
        byteArray = inputString.getBytes(clientEncoding);
        ByteArrayInputStream stream = new ByteArrayInputStream(byteArray);

        return new XMLDecoder(new BufferedInputStream(stream));
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getAllVisualizationNames")
    public List<String> getAllVisualizationNames() {
        List<EVisualization> visualizations = eVisualizationController.getAllVisualizations();
        List<String> visualizationNames = new ArrayList<String>(0);
        for (EVisualization vis : visualizations) {
            visualizationNames.add(vis.getName());
        }
        return visualizationNames;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "removeVisualization")
    public boolean removeVisualization(@WebParam(name = "visualizationName") String visualizationName) {
        eVisualizationController.removeVisualization(visualizationName);
        return true;
    }

    /**
     *
     * @param modelName
     * @return visualization names - modelName and descendant models
     */
    @WebMethod(operationName = "getDescendantVisualizationNames")
    public List<String> getDescendantVisualizationNames(@WebParam(name = "modelName") String modelName) {
        List<EVisualization> visuals = eVisualizationController.getDescVisualizations(modelName);
        List<String> names = new ArrayList<String>(0);
        for (EVisualization vis : visuals) {
            names.add(vis.getName());
        }
        return names;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getMethodType")
    public String getMethodType(@WebParam(name = "modelName") String modelName) {
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        EMethodData method = eModelController.getMethodType(modelName);
        if (method == null) {
            System.out.println("NULL DLA METHODtYPE");
            return "method przyjmuje null";
        }
        System.out.print("NAME OF TASK: " + method.getTask().getName() + "\n");
        return method.getTask().getName();
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "isFba")
    public boolean isFba(@WebParam(name = "modelName") String modelName) {
        return eModelController.isFbaTask(modelName);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getVisualization")
    public String getVisualization(@WebParam(name = "visName") String visName) {
        EVisualization vis = null;
        try{
            vis = eVisualizationController.getVisualizationByName(visName);
        }catch (NoResultException ex){
            return "";
        }
        Collection<EVisTransition> transitions = vis.getTransitions();
        Collection<EVisPlace> places = vis.getPlaces();
        Collection<EVisArcProduct> arcProducts = vis.getArcProduct();
        Collection<EVisArcReactant> arcResources = vis.getArcResource();
        HashMap<String, VisPlace> placesMap = new HashMap<String, VisPlace>(0);
        HashMap<String, VisTransition> transitionMap = new HashMap<String, VisTransition>(0);

        String modelName = vis.getModel().getName();
        boolean isDoneAndFba = (eModelController.isDoneTask(modelName) && eModelController.isFbaTask(modelName));
        ETask task = vis.getModel().getTask();

        List<VisEdge> edges = new ArrayList<VisEdge>(0);

        // VisPlaces creation
        for (EVisPlace epl : places) {
            VisPlace pl = new VisPlace(epl.getSpeciesName(), epl.getSpeciesSid(), epl.getPosition(), epl.getXmlSid());
            placesMap.put(pl.getXmlSid(), pl);
        }

        // VisTransition creation
        for (EVisTransition etrans : transitions) {
            float flux = 0;
            if (isDoneAndFba) {
                flux = eTaskController.getFlux(task, etrans.getReactionSid());
            }
            VisTransition trans = new VisTransition(etrans.getReactionName(), etrans.getReactionSid(), etrans.getPosition(), etrans.getXmlSid(), flux);
            transitionMap.put(trans.getXmlSid(), trans);
        }

        // VisEdge creation
        for (EVisArcReactant arcResource : arcResources) {
            VisPlace source = placesMap.get(arcResource.getSource().getXmlSid());
            VisTransition target = transitionMap.get(arcResource.getTarget().getXmlSid());
            edges.add(new VisEdge(source, target));
        }
        for (EVisArcProduct arcProduct : arcProducts) {
            VisPlace target = placesMap.get(arcProduct.getTarget().getXmlSid());
            VisTransition source = transitionMap.get(arcProduct.getSource().getXmlSid());
            edges.add(new VisEdge(source, target));
        }

        return getXmlSerialization(edges);
    }

    private String getXmlSerialization(List<VisEdge> nodes) {
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        try {
            XMLEncoder encoder = new XMLEncoder(fos);
            for (VisEdge str : nodes) {
                encoder.writeObject(str);
            }
            encoder.close();
            return fos.toString();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "isTaskDone")
    public boolean isTaskDone(@WebParam(name = "modelName")
    String modelName) {
        return eModelController.isDoneTask(modelName);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getFlux")
    public Float getFlux(@WebParam(name = "modelName")
    String modelName, @WebParam(name = "reactionSid")
    String reactionSid) {
        float flux = 0;
        EModel model = eModelController.getModelByName(modelName);
        ETask task = model.getTask();
        if(task == null){
            return flux;
        }
        flux = eTaskController.getFlux(task, reactionSid);
        return flux;
    }
}
