/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acornwsclient.upload;

import acorn.webservice.AcornWS;
import acorn.webservice.AcornWSService;
import acorn.webservice.RepeatedVisualizationNameException_Exception;
import acorn.webservice.VisValidationException_Exception;
import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import org.visualapi.VisEdge;
import org.visualapi.VisPlace;
import org.visualapi.VisTransition;

/**
 *
 * @author markos
 */
public class DBDataUploader {

    private AcornWSService acornService;
    private AcornWS port;
    private static String webServiceEncoding;
    private static String clientEncoding;

    public DBDataUploader() {
        acornService = new AcornWSService();
        port = acornService.getAcornWSPort();
        webServiceEncoding = port.getEncoding();

        OutputStreamWriter out = new OutputStreamWriter(new ByteArrayOutputStream());
        clientEncoding = out.getEncoding();
    }

    public boolean saveVisualization(String model, String visualizationName,
            List<VisTransition> transitions, List<VisPlace> places, List<VisEdge> edges) throws RepeatedVisualizationNameException_Exception, VisValidationException_Exception {
        String transitionsString = getXmlTransitionsSerialization(transitions);
        String placesString = getXmlPlacesSerialization(places);
        String edgesString = getXmlEdgesSerialization(edges);
//        try {
        return port.saveVisualization(model, visualizationName, transitionsString, placesString, edgesString, clientEncoding);
//        } catch (RepeatedVisualizationNameException_Exception ex) {
//            throw new RepeatedVisualizationNameException(ex.getMessage());
//        }
    }

    private String getXmlTransitionsSerialization(List<VisTransition> objectList) {
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        try {
            XMLEncoder encoder = new XMLEncoder(fos);
            for (VisTransition str : objectList) {
                encoder.writeObject(str);
            }
            encoder.close();
            return fos.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    private String getXmlPlacesSerialization(List<VisPlace> objectList) {
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        try {
            XMLEncoder encoder = new XMLEncoder(fos);
            for (VisPlace str : objectList) {
                encoder.writeObject(str);
            }
            encoder.close();
            return fos.toString();
        } catch (Exception ex) {
            return "";
        }
    }


    private String getXmlEdgesSerialization(List<VisEdge> objectList) {
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        try {
            XMLEncoder encoder = new XMLEncoder(fos);
            for (VisEdge str : objectList) {
                encoder.writeObject(str);
            }
            encoder.close();
            return fos.toString();
        } catch (Exception ex) {
            return "";
        }
    }
}
