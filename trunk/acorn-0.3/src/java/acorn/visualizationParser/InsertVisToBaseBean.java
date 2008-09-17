/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.visualizationParser;

import acorn.db.EModel;
import acorn.db.EModelController;
import acorn.db.EVisArc;
import acorn.db.EVisArcProduct;
import acorn.db.EVisArcReactant;
import acorn.db.EVisArcpath;
import acorn.db.EVisPlace;
import acorn.db.EVisTransition;
import acorn.db.EVisualization;
import acorn.db.EVisualizationController;
import java.awt.Point;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Mateusza
 */
public class InsertVisToBaseBean {

    private Element docEle;
    private String visualizationName;
    private String modelName;
    private NodeList nl;
    ArrayList<EVisPlace> placeList;
    NodeList transNl;
    ArrayList<EVisTransition> transitionList;
    EVisualization vis;

    public InsertVisToBaseBean(Element docEle, String visualizationName, String mName) {
        this.docEle = docEle;
        this.visualizationName = visualizationName;
        this.modelName = mName;
        this.nl = docEle.getElementsByTagName("place");
        this.placeList = new ArrayList<EVisPlace>(nl.getLength());
        this.transNl = docEle.getElementsByTagName("transition");
        this.transitionList = new ArrayList<EVisTransition>(transNl.getLength());
        this.vis = new EVisualization(visualizationName);

    }

    public void insertToDB() {
        EModelController emc = new EModelController();
        EModel model = emc.getModelByName(modelName);
        vis.setModel(model);
        for
         (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            EVisPlace place = createPlace(e, vis);
            placeList.add(place);
        }

        for (int i = 0; i < transNl.getLength(); i++) {
            Element e = (Element) transNl.item(i);
            EVisTransition transition = createTransition(e, vis);
            transitionList.add(transition);
        }

        vis.setArcResource(createArcReactants());
        vis.setArcProduct(createArcProducts());
        vis.setPlaces(placeList);
        vis.setTransitions(transitionList);

        model.getEVisualizations().add(vis);
        EVisualizationController vc = new EVisualizationController();
        vc.addVisualization(vis);
    }
    /*todo: dla określonego place stworzyć EVisArcReactant - będzie procedura która stworzy listę ArcReactant
     * podłączyć ją do Place
     * 
     * stworzyć procedurę która daje listę Element's które dla określonego atrybutu mają określoną wartość
     */

    public EVisPlace createPlace(Element elem, EVisualization evis) {
        String sid = elem.getAttribute("id");
        Element nameElem = (Element) elem.getElementsByTagName("name").item(0);
        String name = getValueOfNode(nameElem, "value");
        Element graphPosEle = (Element) elem.getElementsByTagName("graphics").item(0);
        Element positionEle = (Element) graphPosEle.getElementsByTagName("position").item(0);
        Point p = getPoint(positionEle);
        int x = (int) p.getX();
        int y = (int) p.getY();
        ArrayList<EVisArcReactant> rl = new ArrayList<EVisArcReactant>();
        EVisPlace pl = new EVisPlace(name, sid, evis, x, y);
        return pl;
    }

    public EVisTransition createTransition(Element elem, EVisualization evis) {
        String sid = elem.getAttribute("id");
        Element nameElem = (Element) elem.getElementsByTagName("name").item(0);
        String name = getValueOfNode(nameElem, "value");
        Element graphPosEle = (Element) elem.getElementsByTagName("graphics").item(0);
        Element positionEle = (Element) graphPosEle.getElementsByTagName("position").item(0);
        Point p = getPoint(positionEle);
        int x = (int) p.getX();
        int y = (int) p.getY();
        ArrayList<EVisArcReactant> rl = new ArrayList<EVisArcReactant>();
        EVisTransition trans = new EVisTransition(name, sid, x, y, evis);
        return trans;
    }
    /*
     * 
     * create EVisArcREactant from place to transition(from reactant to reaction)
     */

    public ArrayList<EVisArcReactant> createArcReactants() {

        ArrayList<EVisArcReactant> rl = new ArrayList<EVisArcReactant>();
        for (EVisPlace place : placeList) {
            ArrayList<Element> eleList = getElementsByAttribute("arc", "source", place.getSid());
            EVisArcReactant arc;
            for (Element ele : eleList) {
                String sid = ele.getAttribute("id");
                String transitionSid = ele.getAttribute("target");
                EVisTransition transition = getTransitionBySid(transitionSid);

                arc = new EVisArcReactant(sid, vis, place, transition);
                ArrayList<EVisArcpath> apl= new ArrayList<EVisArcpath>();
                arc.setArcpathList(createArcpaths(ele, arc));
//                arc.setArcpathList(apl);
                rl.add(arc);
            }
        }
        return rl;
    }

        /*
     * 
     * create EVisArcRProduct from transition to place(from reaction to product)
     */

    public ArrayList<EVisArcProduct> createArcProducts() {

        ArrayList<EVisArcProduct> rl = new ArrayList<EVisArcProduct>();
        for (EVisPlace place : placeList) {
            ArrayList<Element> eleList = getElementsByAttribute("arc", "target", place.getSid());
            EVisArcProduct arc;
            for (Element ele : eleList) {
                String sid = ele.getAttribute("id");
                String transitionSid = ele.getAttribute("source");
                EVisTransition transition = getTransitionBySid(transitionSid);

                arc = new EVisArcProduct(sid, vis, place, transition);
                arc.setArcpathList(createArcpaths(ele, arc));
                rl.add(arc);
            }
        }
        return rl;
    }
    
    /* @args - ele is xmldom Element with tag arc
     * @returns -  list of EVisArcpath objects associated with arc element
     */
    public ArrayList<EVisArcpath> createArcpaths(Element ele, EVisArc arc) {
        ArrayList<EVisArcpath> apl = new ArrayList<EVisArcpath>();
        String tagName = ele.getTagName();
        if (!(tagName).equals("arc")) {
            return apl;
        }
        NodeList arcpathNL = ele.getElementsByTagName("arcpath");
        for (int i = 0; i < arcpathNL.getLength(); i++) {
            Element arcpath = (Element) arcpathNL.item(i);
            String sid = arcpath.getAttribute("id");
            String x = arcpath.getAttribute("x");
            String y = arcpath.getAttribute("y");
            String curvePoint = arcpath.getAttribute("curvePoint");
            int intX = new Integer(x);
            int intY = new Integer(y);
            boolean cp = false;
            if (curvePoint.equals("true")) {
                cp = true;
            }
            EVisArcpath ap = new EVisArcpath(intX, intY, sid, cp, arc);
            apl.add(ap);
        }
        return apl;
    }
    /* @returns - from transistionList EVisTransition with sid like argument of function
     * 
     */

    public EVisTransition getTransitionBySid(String sid) {
        for (EVisTransition t : transitionList) {
            if (sid.equals(t.getSid())) {
                return t;
            }
        }
        return null;
    }
    /* @returns - from transistionList EVisTransition with sid like argument of function
     * 
     */

    public EVisPlace getPlaceBySid(String sid) {
        for (EVisPlace p : placeList) {
            if (sid.equals(p.getSid())) {
                return p;
            }
        }
        return null;
    }

    /*finds element with tag and id - if not, returns ele
     * 
     */
    public Element getElementById(String tag, String id) {
        NodeList nl = docEle.getElementsByTagName(tag);

        for (int i = 0; i < nl.getLength(); i++) {
            Element tEle = (Element) nl.item(i);
            String tEleId = tEle.getAttribute("id");
            if (id.equals(tEleId)) {
                return (tEle);
            }
        }
        /*
         *TODO raise error no Place of Id id 
         */
        return (docEle);
    }
    /*
     * @returns - array of Element - elements of tag names ele with
     * attribute attr and value of this attribute val
     */

    public ArrayList<Element> getElementsByAttribute(String ele, String attr, String val) {
        ArrayList<Element> el = new ArrayList<Element>();
        NodeList nl = docEle.getElementsByTagName(ele);
        Element element;
        try {
            for (int i = 0; i < nl.getLength(); i++) {
                element = (Element) nl.item(i);
                if (val.equals(element.getAttribute(attr))) {
                    el.add(element);
                }
            }

        } catch (Exception e) {
            return el;
        }
        return el;
    }

    /*returns value of tag node in ele node
     * 
     */
    public String getValueOfNode(Element ele, String tag) {
        NodeList nl = ele.getElementsByTagName(tag);
        Element e = (Element) nl.item(0);
        return e.getFirstChild().getNodeValue();
    }

    public Point getPoint(Element ele) {
        //ele.getTagName();
        String strx = ele.getAttribute("x");

        String stry = ele.getAttribute("y");
        Double dx = new Double(strx);
        Double dy = new Double(stry);
        int x = dx.intValue();
        int y = dy.intValue();
        return (new Point(x, y));
    }
}
