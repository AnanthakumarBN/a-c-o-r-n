/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acornwsclient.download;

import acorn.webservice.AcornWS;
import acorn.webservice.AcornWSService;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dbStructs.NameStruct;
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

    public DBDataDownloader() {
        acornService = new AcornWSService();
        port = acornService.getAcornWSPort();
        webServiceEncoding = port.getEncoding();

        OutputStreamWriter out = new OutputStreamWriter(new ByteArrayOutputStream());
        clientEncoding = out.getEncoding();
    }

    public List<NameStruct> getSpeciesForReaction(String modelName, String reactionSid, boolean isSource) {
        return getDeserializedNameStruct(port.getSpeciesForReaction(modelName, reactionSid, isSource));
    }

    public List<NameStruct> getReactionsForSpecies(String modelName, String speciesSid, boolean isSource) {
        return getDeserializedNameStruct(port.getReactionsForSpecies(modelName, speciesSid, isSource));
    }

    public List<String> getModels() {
        return port.getModels();
    }

    public List<NameStruct> getAllSpeciesByModelName(String modelName) {
        String inputString = port.getAllSpeciesByModelName(modelName);
        return getDeserializedNameStruct(inputString);
    }

    public List<NameStruct> getAllReactionsByModelName(String modelName) {
        String inputString = port.getAllReactionsByModelName(modelName);
        return getDeserializedNameStruct(inputString);
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

    public List<String> getAllVisualizationNames() {
        return port.getAllVisualizationNames();
    }

    public boolean removeVisualization(String name) {
        return port.removeVisualization(name);
    }

    public List<String> getDescendantVisualizationNames(String modelName) {
        return port.getDescendantVisualizationNames(modelName);
    }

    private List<String> getDeserializedString(String inputString) {
        byte[] byteArray = null;
        try {
            byteArray = inputString.getBytes(webServiceEncoding);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DBDataDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }
        ByteArrayInputStream stream = new ByteArrayInputStream(byteArray);

        List<String> strings = new ArrayList<String>(0);
        XMLDecoder d = new XMLDecoder(new BufferedInputStream(stream));

        try {
            while (true) {
                strings.add((String) d.readObject());
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            return strings;
        }
    }

    /**
     *
     * @param visualizationName visualization name which is downloaded from DataBase
     * @return visualization as list of edges containing visPlace and VisTransition as source/target of edge
     */
    public List<VisEdge> getVisualization(String visualizationName) {
        String serializedString = port.getVisualization(visualizationName);
        List<VisEdge> edges = getDeserializedVisEdge(serializedString);
        for (VisEdge edge : edges) {
            edge.getSource().removeNullsFromSets();
            edge.getTarget().removeNullsFromSets();
        }
        return edges;
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
}
