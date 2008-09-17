/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.visualizationParser;

import acorn.db.EModelController;
import acorn.db.EReactionController;
import acorn.db.EVisualizationController;
import acorn.exception.XmlParseException;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.NoResultException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Mateusza
 */
public class VisualizationParser {

    Document dom;
    Element docEle;
    String visualizationName;
    String modelName;
    InsertVisToBaseBean vbb;

    public VisualizationParser(String visName, String mName, UploadedFile file) throws XmlParseException {
        visualizationName = visName;
        modelName = mName;
        parseXmlFile(file);
        docEle = dom.getDocumentElement();
        vbb = new InsertVisToBaseBean(docEle, visualizationName, modelName);
    }

    public void runParser() throws XmlParseException {
        isModelNameValid();
        if (!isVisualizationNameFree()){
            throw new XmlParseException("Name of visualization is in use. Write another one.");
        };
        parseDocument();
        vbb.insertToDB();
    }
    
    public void isModelNameValid() throws XmlParseException{
        EModelController emc = new EModelController();
        try{
            emc.getModelByName(modelName);
        } catch (NoResultException e){
            throw(new XmlParseException("Invalid model's name"));
        }
    }

    public boolean isVisualizationNameFree(){
        EVisualizationController evc = new EVisualizationController();
        try{
            evc.getVisualizationByName(visualizationName);
        } catch(NoResultException e){
            return true;
        }
        return false;
    }
    private void parseXmlFile(UploadedFile file) throws XmlParseException {
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        XmlParseException xpe = new XmlParseException("Insert XML file. Your file is not xml file.");
        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            dom = db.parse(file.getInputStream());
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
            throw xpe;
        } catch (SAXException se) {
            se.printStackTrace();
            throw xpe;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw xpe;
        }

    }

    /*
     *parse DOM tree
     * 
     */
    public void parseDocument() throws XmlParseException {
        //biorę listę wszystkich reakcji dla xmla
        NodeList nl = docEle.getElementsByTagName("transition");
        Element ele;
        for (int i = 0; i < nl.getLength(); i++) {
            ele = (Element) nl.item(i);
            String id = ele.getAttribute("id");
            System.out.println(id);
            Element nameEle = (Element) ele.getElementsByTagName("name").item(0);
            String name = vbb.getValueOfNode(nameEle, "value");

            if (!validateReactants(id, name) || !validateProducts(id, name)) {
                XmlParseException xpe = new XmlParseException("Your reactions, reactants or products are not valid for the model you chose.");
                throw xpe;
            }
        }
    }

    /* reactants validation with database for reaction
     * @params id - atrribute of transition node in xml file
     * @params name - name of reaction in db, name node in xml file
     */
    public boolean validateReactants(String id, String name) {
        ArrayList<String> al = new ArrayList();
        ArrayList<String> dbAl = new ArrayList();

        al = getSpeciesList(id, true);
        Iterator iter = al.iterator();
        while (iter.hasNext()) {
            String s = (String) iter.next();
        }
        EReactionController rc = new EReactionController();
        dbAl = rc.getReactantsSpeciesList(name);
//        System.out.println("REACTANTS");
//        wypiszListe((List<String>) al);
//        System.out.println("REACTANTS DB");
//        wypiszListe((List<String>) dbAl);
        if (dbAl.size() != al.size()) {
            return false;
        }
        for (String sid : dbAl) {
            if (!al.contains(sid)) {
                return false;
            }
        }
        return true;
    }

    public void wypiszListe(List<String> l) {
        for (String s : l) {
            System.out.println(s);
        }
        System.out.println("KONIEC WYPISYWANIA");
    }
    /* products validation with database for reaction
     * @params id - atrribute of transition node in xml file
     * @params name - name of reaction in db, name node in xml file
     */

    public boolean validateProducts(String id, String name) {
        ArrayList<String> al = new ArrayList();
        ArrayList<String> dbAl = new ArrayList();

        al = getSpeciesList(id, false);
        Iterator iter = al.iterator();
        while (iter.hasNext()) {
            String s = (String) iter.next();
        }
        EReactionController rc = new EReactionController();
        dbAl = rc.getProductsSpeciesList(name);
//        System.out.println("REACTANTS");
//        wypiszListe((List<String>) al);
//        System.out.println("REACTANTS DB");
//        wypiszListe((List<String>) dbAl);

        if (dbAl.size() != al.size()) {
            return false;
        }
        for (String sid : dbAl) {
            if (!al.contains(sid)) {
                return false;
            }
        }
        return true;

    }


    /*returns reactants or products of reaction - list of first part place's name in XML
     *
     * @params reactionId - in xml id of reaction / transition
     * @params ifReactants - says if looks for reactants of products
     */
    ArrayList<String> getSpeciesList(String reactionId, boolean ifReactants) {
        ArrayList<String> al = new ArrayList();
        NodeList nl = dom.getElementsByTagName("arc");
        Element reactant;
        Element reactantName;
        String reactantStrId;
        String firstPartId;
        String speciesId;

        for (int i = 0; i < nl.getLength(); i++) {
            Element ele = (Element) nl.item(i);
            if (!ifReactants && reactionId.equals(ele.getAttribute("source"))) {
                speciesId = ele.getAttribute("target");
            } else if (ifReactants && reactionId.equals(ele.getAttribute("target"))) {
                speciesId = ele.getAttribute("source");
            } else {
                continue;
            }
            reactant = (Element) vbb.getElementById("place", speciesId);
            reactantName = (Element) reactant.getElementsByTagName("name").item(0);
            reactantStrId = vbb.getValueOfNode(reactantName, "value");
            firstPartId = reactantStrId.split(";")[0];
            al.add(firstPartId);
        }
        return al;
    }
}
