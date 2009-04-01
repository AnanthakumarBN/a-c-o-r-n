/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.webservice;

import acorn.db.EModel;
import acorn.db.EModelController;
import acorn.db.EProductController;
import acorn.db.EReactantController;
import acorn.db.EReaction;
import acorn.db.EReactionController;
import acorn.db.ESpecies;
import acorn.db.ESpeciesController;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.dbStructs.NameStruct;

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
    private EModelController emc = new EModelController();

    /**
     * Web service operation
     */
    /**
     *
     * @return sorted array of models names
     */
    @WebMethod(operationName = "getModels")
    public String[] getModels() {
        List<EModel> lem = emc.getModels();
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
            structArray = StructArrayGen.getSortedReactionsArray(eProductContr.getReactions(species));
        } else {
            structArray = StructArrayGen.getSortedReactionsArray(eReactantContr.getReactions(species));
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
}
