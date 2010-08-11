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
import acorn.db.EUser;
import acorn.db.EUserController;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.persistence.NoResultException;
import org.dbStructs.ModelStruct;
import org.dbStructs.NameStruct;
import org.exceptions.AuthenticationException;
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
    private EUserController eUserController = new EUserController();

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getModels")
    public String getModels(@WebParam(name = "login") String login, @WebParam(name = "pass") String pass) {
        List<EModel> lem;
        try {
            if (isGuest(login, pass)) {
                lem = eModelController.getModelsShared();
            } else if (isUser(login, pass)) {
                if (isUserAnAdmin(login)) {
                    lem = eModelController.getModels();
                } else {
                    HashSet<EModel> set = new HashSet<EModel>();
                    set.addAll(eModelController.getModels(eUserController.findUserByLogin(login)));
                    set.addAll(eModelController.getModelsShared());
                    lem = new ArrayList<EModel>(set);
                }
            } else {
                return null;
            }
        } catch (NoResultException ex) {
            return null;
        }
        ModelStruct[] modelElements = new ModelStruct[lem.size()];
        int i = 0;
        EUser user = eUserController.getUser(login);
        for (EModel em : lem) {
            modelElements[i++] = new ModelStruct(em.getId(), em.getQualifiedName(user));
        }
        Arrays.sort(modelElements);
        return getModelStructXmlSerialization(modelElements);
    }

//    /**
//     * @param user model names for this user are given
//     * @return sorted array of models names
//     */
//    @WebMethod(operationName = "getModelsForUser")
//    public String getModelsForUser(
//            @WebParam(name = "login") String login, @WebParam(name = "pass") String pass) {
//        if (!isUser(login, pass)) {
//            return null;
//        }
//        EUser eUser = null;
//        try {
//            eUser = eUserController.findUserByLogin(login);
//        } catch (NoResultException ex) {
//            return null;
//        }
//        List<EModel> lem = eModelController.getModels(eUser);
////        ArrayList<String> modelElements = new ArrayList<String>(lem.size());
//        ModelStruct[] modelElements = new ModelStruct[lem.size()];
//        int i = 0;
//        for (EModel em : lem) {
//            modelElements[i++] = new ModelStruct(em.getId(), em.getName());
//        }
//        Arrays.sort(modelElements);
//
//        return getModelStructXmlSerialization(modelElements);
//    }
    /**
     * @param modelId ID of model
     * @return sorted NameStruct of all ractions for model
     */
    @WebMethod(operationName = "getAllReactionsByModelId")
    public String getAllReactionsByModelId(@WebParam(name = "modelId") int modelId,
            @WebParam(name = "login") String login, @WebParam(name = "pass") String pass) {
        if (!isUser(login, pass) && !isGuest(login, pass)) {
            return null;
        }
        List<EReaction> reactList = eReactionContr.getByModelId(modelId);
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
    @WebMethod(operationName = "getAllSpeciesByModelId")
    public String getAllSpeciesByModelId(@WebParam(name = "modelId") int modelId,
            @WebParam(name = "login") String login, @WebParam(name = "pass") String pass) {
        if (!isUser(login, pass) && !isGuest(login, pass)) {
            return null;
        }
        List<ESpecies> specList = eSpeciesContr.getSpecies(modelId);
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
    public String getReactionsForSpecies(@WebParam(name = "modelId") int modelId,
            @WebParam(name = "speciesSid") String speciesSid, @WebParam(name = "isSource") boolean isSource,
            @WebParam(name = "login") String login, @WebParam(name = "pass") String pass) {
        if (!isUser(login, pass) && !isGuest(login, pass)) {
            return null;
        }
        ESpecies species = eSpeciesContr.getByModelIdAndSidName(modelId, speciesSid);
        EModel model = eModelController.getModel(modelId);
        NameStruct[] structArray = null;
        if (species == null) {
            return new String("");
        }

        // list of reaction is source, so species is target/product so use eProductContr
        if (isSource) {
            structArray = StructArrayGen.getSortedReactionsArray(eProductContr.getReactions(species, model.getId()));
        } else {
            structArray = StructArrayGen.getSortedReactionsArray(eReactantContr.getReactions(species, model.getId()));
        }
        return getXmlSerialization(structArray);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getSpeciesForReaction")
    public String getSpeciesForReaction(@WebParam(name = "modelId") int modelId,
            @WebParam(name = "reactionSid") String reactionSid, @WebParam(name = "isSource") boolean isSource,
            @WebParam(name = "login") String login, @WebParam(name = "pass") String pass) {
        if (!isUser(login, pass) && !isGuest(login, pass)) {
            return null;
        }
        EReaction reaction = eReactionContr.getByModelIdAndReactionSid(modelId, reactionSid);
        NameStruct[] structArray = null;
        if (reaction == null) {
            return new String("");
        }
        EReactantController rc = new EReactantController();
        EProductController pc = new EProductController();
        //List of Species is source/reactant so use eReactantController
        if (isSource) {
            structArray = StructArrayGen.getSortedSpeciesArray(rc.getSpecies(reaction));
        } else {
            structArray = StructArrayGen.getSortedSpeciesArray(pc.getSpecies(reaction));
        }

        return getXmlSerialization(structArray);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getEncoding")
    public String getEncoding(@WebParam(name = "login") String login, @WebParam(name = "pass") String pass) {
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

    private String getModelStructXmlSerialization(ModelStruct[] structArray) {
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        try {
            XMLEncoder encoder = new XMLEncoder(fos);
            for (ModelStruct str : structArray) {
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
            @WebParam(name = "modelId") int modelId, @WebParam(name = "visualizationName") String visualizationName,
            @WebParam(name = "reactions") String reactions, @WebParam(name = "species") String species,
            @WebParam(name = "arcs") String arcs,
            @WebParam(name = "clientEncoding") String clientEncoding,
            @WebParam(name = "login") String login, @WebParam(name = "pass") String pass)
            throws RepeatedVisualizationNameException, VisValidationException, AuthenticationException {
        if (!isUser(login, pass) && !isGuest(login, pass)) {
            throw new AuthenticationException("You are not authenticated.");
        }
        List<VisTransition> visTransitions = null;
        List<VisPlace> visPlaces = null;
        List<VisEdge> visEdges = null;
        if (eVisualizationController.isVisualizationNameUsed(visualizationName, login)) {
            throw new RepeatedVisualizationNameException("Visualization: " + visualizationName + " is in database. Use another name.");
        }
        try {
            visTransitions = getDeserializedVisTransitions(reactions, clientEncoding);
            visPlaces = getDeserializedVisPlaces(species, clientEncoding);
            visEdges = getDeserializedVisEdges(arcs, clientEncoding);

        } catch (UnsupportedEncodingException ex) {
            return false;
        }

        EUser owner = null;
        if (!isGuest(login, pass)) {
            owner = eUserController.getUser(login);
        }

        EVisualization vis = new EVisualization(visualizationName, owner);

        //maps will be used for finding connection/creating EVisArcReactant and EVisArcProduct
        HashMap<String, EVisTransition> transitionMap = new HashMap<String, EVisTransition>(0);
        HashMap<String, EVisPlace> placeMap = new HashMap<String, EVisPlace>(0);

        HashMap<String, ESpecies> speciesMap = new HashMap<String, ESpecies>(0);
        HashMap<String, EReaction> reactionMap = new HashMap<String, EReaction>(0);

        List<EVisTransition> eVisTransitions = new ArrayList<EVisTransition>(0);
        List<EVisPlace> eVisPlaces = new ArrayList<EVisPlace>(0);
        List<EVisArcReactant> eVisArcReactants = new ArrayList<EVisArcReactant>(0);
        List<EVisArcProduct> eVisArcProducts = new ArrayList<EVisArcProduct>(0);

        EModel model = eModelController.getModel(modelId);

        Point point;
        String xmlSid;
        //transitions creation
        for (VisTransition trans : visTransitions) {
            point = trans.getLocation();
            xmlSid = trans.getXmlSid();

            if (!reactionMap.containsKey(trans.getSid())) {
                EReaction reaction = eReactionContr.getByModelAndReactionSid(trans.getSid(), model);
                if (reaction == null) {
                    throw new VisValidationException("Model: " + model.getName() + " does not have " + trans.getSid() + " reaction.");
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
                ESpecies spec = eSpeciesContr.getByModelIdAndSidName(model.getId(), place.getSid());
                if (spec == null) {
                    throw new VisValidationException("Model: " + model.getName() + " does not have " + place.getSid() + " species.");
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
    public List<String> getAllVisualizationNames(@WebParam(name = "login") String login, @WebParam(name = "pass") String pass) {
        List<EVisualization> visualizations = eVisualizationController.getAllVisualizations();
        if (!isUser(login, pass) && !isGuest(login, pass)) {
            return null;
        }
        List<String> visualizationNames = new ArrayList<String>(0);
        EUser owner = eUserController.getUser(login);
        for (EVisualization vis : visualizations) {
            visualizationNames.add(vis.getQualifiedName(owner));
        }
        return visualizationNames;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "removeVisualization")
    public boolean removeVisualization(@WebParam(name = "visualizationName") String visualizationName,
            @WebParam(name = "login") String login, @WebParam(name = "pass") String pass) throws AuthenticationException {
        if (!isUser(login, pass) && !isGuest(login, pass)) {
            throw new AuthenticationException("You are not authenticated.");
        }
        eVisualizationController.removeVisualization(visualizationName, login);
        return true;
    }

    /**
     *
     * @param modelId
     * @return visualization names - model name and ancestor models
     */
    @WebMethod(operationName = "getAncestorVisualizationNames")
    public List<String> getAncestorVisualizationNames(@WebParam(name = "modelId") int modelId,
            @WebParam(name = "login") String login, @WebParam(name = "pass") String pass) {
        List<EVisualization> visuals;
        Set<String> names = new HashSet<String>();
        if (isGuest(login, pass)) {
            visuals = eVisualizationController.getAncestorVisualizationsShared(modelId);
            for (EVisualization vis : visuals) {
                names.add(vis.getQualifiedName(eUserController.findUserByLogin(login)));
            }
        } else if (isUser(login, pass)) {
            if (isUserAnAdmin(login)) {
                visuals = eVisualizationController.getAncestorVisualizationsAll(modelId);
                //don't streep names for admin
                for (EVisualization vis : visuals) {
                    names.add(vis.getQualifiedName(eUserController.findUserByLogin(login)));
                }
            } else {
                visuals = eVisualizationController.getAncestorVisualizationsForUser(modelId, login);
                for (EVisualization vis : visuals) {
                    names.add(vis.getQualifiedName(eUserController.findUserByLogin(login)));
                }
            }
        } else {
            return null;
        }

        return new ArrayList<String>(names);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getMethodType")
    public String getMethodType(@WebParam(name = "modelId") int modelId,
            @WebParam(name = "login") String login, @WebParam(name = "pass") String pass) {
        if (!isUser(login, pass) && !isGuest(login, pass)) {
            return null;
        }
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        EMethodData method = eModelController.getMethodType(modelId);
        if (method == null) {
            return "method przyjmuje null";
        }
        return method.getTask().getName();
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "isFba")
    public boolean isFba(@WebParam(name = "modelId") int modelId,
            @WebParam(name = "login") String login, @WebParam(name = "pass") String pass) throws javax.naming.AuthenticationException {
        if (!isUser(login, pass) && !isGuest(login, pass)) {
            throw new javax.naming.AuthenticationException("You are not authenticated.");
        }
        return eModelController.isFbaTask(modelId);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getVisualization")
    public String getVisualization(@WebParam(name = "visName") String visName, @WebParam(name = "ownerId") String ownerLogin,
            @WebParam(name = "login") String login, @WebParam(name = "pass") String pass) {
        if (!isUser(login, pass) && !isGuest(login, pass)) {
            return null;
        }
        EVisualizationController visController = new EVisualizationController();

        List<VisEdge> edges;
        if (isUserAnAdmin(login)) {
            edges = visController.getEdgesOfVisualization(visName, ownerLogin);
        } else {
            edges = visController.getEdgesOfVisualization(visName, ownerLogin);
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
    public boolean isTaskDone(@WebParam(name = "modelId") int modelId, @WebParam(name = "login") String login, @WebParam(name = "pass") String pass) throws AuthenticationException {
        if (!isUser(login, pass) && !isGuest(login, pass)) {
            throw new AuthenticationException("You are not authenticated.");
        }
        return eModelController.isDoneTask(modelId);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getFlux")
    public Float getFlux(@WebParam(name = "modelId") int modelId, @WebParam(name = "reactionSid") String reactionSid,
            @WebParam(name = "login") String login, @WebParam(name = "pass") String pass) {
        if (!isUser(login, pass) && !isGuest(login, pass)) {
            return null;
        }
        float flux = 0;
        EModel model = eModelController.getModel(modelId);
        ETask task = model.getTask();
        if (task == null) {
            return flux;
        }
        flux = eTaskController.getFlux(task, reactionSid);
        return flux;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "authenticate")
    public void authenticate(@WebParam(name = "login") String login, @WebParam(name = "pass") String pass) throws AuthenticationException {
        if (isGuest(login, pass) || isUser(login, pass)) {
            return;
        } else {
            throw new AuthenticationException("Incorrect login.");
        }
    }

    private boolean isUser(String login, String pass) {
        if ((login == null) || (pass == null)) {
            return false;
        }
        try {
            char[] passwordFromDB = null;
            char[] passwordFromWS = pass.toCharArray();

            EUser user = null;
            try {
                user = eUserController.findUserByLogin(login);
            } catch (NoResultException ex) {
                return false;
                //throw new AuthenticationException("Incorrect login.");
            }

            String noMD5PasswordDB = new String(user.getPasswd());

//            if(!noMD5PasswordDB.equals(pass)){
//                throw new AuthenticationException("Incorrect password.");
//            }

            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] tmp = noMD5PasswordDB.getBytes();
            md5.update(tmp);
            passwordFromDB = byteArrToString(md5.digest()).toCharArray();
            int correctCount = 0;
            if (passwordFromDB.length != passwordFromWS.length) {
                return false;
                //throw new AuthenticationException("Incorrect password.");
            }
            for (int i = 0; i < passwordFromWS.length; i++) {
                if (passwordFromWS[i] == passwordFromDB[i]) {
                    correctCount++;
                }
            }
            if (passwordFromWS.length == correctCount) {
                //do nothing here
            } else {
                return false;
                //throw new AuthenticationException("Incorrect password.");
            }
        } catch (NoSuchAlgorithmException ex) {
            return false;
            //throw new AuthenticationException("No such algorithm exception.");
        }
        return true;
    }

    private boolean isGuest(String login, String pass) {
        if (login == null) {
            return false;
        } else {
            return (login.equals(""));
        }
    }

    //assumes that this is really an user
    private boolean isUserAnAdmin(String login) {
        EUser user = null;
        try {
            user = eUserController.findUserByLogin(login);
            if (user.getStatus().equals(EUser.statusAdmin)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
            //throw new AuthenticationException("Incorrect login.");
        }
    }

    private static String byteArrToString(byte[] b) {
        String res = null;
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int j = b[i] & 0xff;
            if (j < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(j));
        }
        res = sb.toString();
        return res.toUpperCase();
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getModelModificationDate")
    public String getModelModificationDate(@WebParam(name = "modelId") int modelId,
            @WebParam(name = "login") String login, @WebParam(name = "pass") String pass) {
        if (!isUser(login, pass) && !isGuest(login, pass)) {
            return null;
        }
        EModel model = eModelController.getModel(modelId);
        if (model == null) {
            return null;
        }

        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(fos);
        encoder.writeObject(model.getLastChange());
        encoder.close();
        return fos.toString();
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getDetachedReactions")
    public String getDetachedReactions(
            @WebParam(name = "modelId") int modelId,
            @WebParam(name = "login") String login, @WebParam(name = "pass") String pass) {
        if (!isUser(login, pass) && !isGuest(login, pass)) {
            return null;
        }
        Collection<EReaction> reactions = eModelController.getDetachedReactions(modelId);
        NameStruct[] reactionStructs = StructArrayGen.getSortedReactionsArray(reactions);

        return getXmlSerialization(reactionStructs);
    }
}
