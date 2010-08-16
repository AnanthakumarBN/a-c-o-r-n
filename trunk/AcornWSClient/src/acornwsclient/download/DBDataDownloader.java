/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acornwsclient.download;

import acorn.webservice.AcornWS;
import acorn.webservice.AcornWSService;
import acorn.webservice.AuthenticationException_Exception;
import acornwsclient.utils.ClientSerialization;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dbStructs.ModelStruct;
import org.dbStructs.NameStruct;
import org.dbStructs.VisStruct;
import org.visualapi.VisEdge;

/**
 *
 * @author markos
 */
public class DBDataDownloader {

    private AcornWSService acornService;
    private AcornWS port;
    private static String webServiceEncoding;
    private static String clientEncoding;
    private ClientSerialization cSerialization;
    private String user;
    private String MD5pass;

    public DBDataDownloader(String usr, String ps) {
        acornService = new AcornWSService();
        port = acornService.getAcornWSPort();
        this.user = usr;
        this.MD5pass = ps;
        webServiceEncoding = port.getEncoding(this.user, this.MD5pass);

        OutputStreamWriter out = new OutputStreamWriter(new ByteArrayOutputStream());
        clientEncoding = out.getEncoding();
        cSerialization = new ClientSerialization(clientEncoding);
    }

    public List<NameStruct> getSpeciesForReaction(int modelId, String reactionSid, boolean isSource) {
        return getDeserializedNameStruct(port.getSpeciesForReaction(modelId, reactionSid, isSource, this.user, this.MD5pass));
    }

    public List<NameStruct> getReactionsForSpecies(int modelId, String speciesSid, boolean isSource) {
        return getDeserializedNameStruct(port.getReactionsForSpecies(modelId, speciesSid, isSource, this.user, this.MD5pass));
    }

    public List<ModelStruct> getModels() {
        String serializedModelStructs = port.getModels(this.user, this.MD5pass);
        return getDeserializedModelStruct(serializedModelStructs);
    }

//    public List<ModelStruct> getAllModels() {
//        String serializedModelStructs = port.getModels(this.user, this.MD5pass);
//        List<ModelStruct> modelStructs = getDeserializedModelStruct(serializedModelStructs);
//        return modelStructs;
//    }
    public List<NameStruct> getAllSpeciesByModelId(int modelId) {
        String inputString = port.getAllSpeciesByModelId(modelId, this.user, this.MD5pass);
        return getDeserializedNameStruct(inputString);
    }

    public List<NameStruct> getAllReactionsByModelId(int modelId) {
        String inputString = port.getAllReactionsByModelId(modelId, this.user, this.MD5pass);
        return getDeserializedNameStruct(inputString);
    }

    public boolean removeVisualization(String name) throws AuthenticationException_Exception {
        return port.removeVisualization(name, this.user, this.MD5pass);
    }

    public List<String> getAncestorVisualizationNames(int modelId) {
        return port.getAncestorVisualizationNames(modelId, this.user, this.MD5pass);
    }

    /**
     *
     * @param visualizationName visualization name which is downloaded from DataBase
     * @return visualization as list of edges containing visPlace and VisTransition as source/target of edge
     */
    public List<VisEdge> getVisualization(String visualizationName, String ownerLogin) {
        String serializedString = port.getVisualization(visualizationName, ownerLogin, this.user, this.MD5pass);
        List<VisEdge> edges = getDeserializedVisEdge(serializedString);
        for (VisEdge edge : edges) {
            edge.getSource().removeNullsFromSets();
            edge.getTarget().removeNullsFromSets();
        }
        return edges;
    }

    public VisStruct getVisualizationObject(String visualizationName, String ownerLogin) {
        String serializedString = port.getVisualizationObject(visualizationName, ownerLogin, this.user, this.MD5pass);
        return getDeserializedVisStruct(serializedString);
    }

    public boolean isFbaTask(int modelId) throws AuthenticationException_Exception {
        return port.isFba(modelId, this.user, this.MD5pass);
    }

    public boolean isTaskDone(int modelId) throws AuthenticationException_Exception {
        return port.isTaskDone(modelId, this.user, this.MD5pass);
    }

    public float getFlux(int modelId, String reactionSid) {
        return port.getFlux(modelId, reactionSid, this.user, this.MD5pass);
    }

    public void authenticate() throws AuthenticationException_Exception {
        port.authenticate(this.user, this.MD5pass);
    }

    public Date getModelModificationDate(int modelId) {
        String serializedDate = port.getModelModificationDate(modelId, this.user, this.MD5pass);
        XMLDecoder decoder = cSerialization.getXMLDecoder(serializedDate);
        Date date = (Date) decoder.readObject();
        return date;
    }

    public Collection<NameStruct> getDetachedReactions(int modelId) {

        String serializedNameStructs = port.getDetachedReactions(modelId, this.user, this.MD5pass);
        Collection<NameStruct> detachedNameStructs = getDeserializedNameStruct(serializedNameStructs);
        return detachedNameStructs;
    }

    private List<VisEdge> getDeserializedVisEdge(String inputString) {
        byte[] byteArray = null;
        try {
            byteArray = inputString.getBytes(webServiceEncoding);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DBDataDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }
        ByteArrayInputStream stream = new ByteArrayInputStream(byteArray);

        List<VisEdge> edges = new ArrayList<VisEdge>(0);
        XMLDecoder d = new XMLDecoder(new BufferedInputStream(stream));

        try {
            while (true) {
                edges.add((VisEdge) d.readObject());
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            return edges;
        }
    }

    private VisStruct getDeserializedVisStruct(String inputString) {
        byte[] byteArray = null;
        try {
            byteArray = inputString.getBytes(webServiceEncoding);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DBDataDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }
        ByteArrayInputStream stream = new ByteArrayInputStream(byteArray);

        XMLDecoder d = new XMLDecoder(new BufferedInputStream(stream));

        return (VisStruct) d.readObject();
    }

    private List<NameStruct> getDeserializedNameStruct(String inputString) {
        byte[] byteArray = null;
        try {
            byteArray = inputString.getBytes(webServiceEncoding);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DBDataDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }
        ByteArrayInputStream stream = new ByteArrayInputStream(byteArray);

        List<NameStruct> structList = new ArrayList<NameStruct>(0);
        XMLDecoder d = new XMLDecoder(new BufferedInputStream(stream));

        try {
            while (true) {
                structList.add((NameStruct) d.readObject());
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            return structList;
        }
    }

    private List<ModelStruct> getDeserializedModelStruct(String inputString) {
        byte[] byteArray = null;
        try {
            byteArray = inputString.getBytes(webServiceEncoding);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DBDataDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }
        ByteArrayInputStream stream = new ByteArrayInputStream(byteArray);

        List<ModelStruct> structList = new ArrayList<ModelStruct>(0);
        XMLDecoder d = new XMLDecoder(new BufferedInputStream(stream));

        try {
            while (true) {
                structList.add((ModelStruct) d.readObject());
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            return structList;
        }
    }
}
