/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graphscene;

import java.awt.Image;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.exceptions.VisValidationException;
import org.interfaces.LoadSaveInterface;
import org.providers.ModelSceneMenu;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.router.Router;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.ImageUtilities;
import org.providers.EdgeMenu;
import org.providers.NodeMenu;
import org.providers.SceneConnectProvider;
import org.providers.SceneReconnectProvider;
import org.view.EdgeWidget;
import org.view.NodeWidget;
import org.view.PlaceWidget;
import org.view.TransitionWidget;
import org.vislogicengine.VisLogic;
import org.visualapi.VisEdge;
import org.visualapi.VisNode;
import org.visualapi.VisPlace;
import org.visualapi.VisTransition;

/**
 *
 * @author markos
 */
public class GraphModelScene extends GraphScene<VisNode, VisEdge> {

    public interface SelectionNodeListener {

        public void nodeSelected(NodeWidget w);

        public void unselect();
    }
    private static final Image PLACE_IMG = ImageUtilities.loadImage("org/graphics/place.png");
    private static final Image TRANSITION_IMG = ImageUtilities.loadImage("org/graphics/transition_small2.gif");
    private LayerWidget mainLayer;
    private LayerWidget connectionLayer;
    private LayerWidget interractionLayer = new LayerWidget(this);
    private LayerWidget backgroundLayer = new LayerWidget(this);
    private WidgetAction moveAction = ActionFactory.createMoveAction();
    private Router router = RouterFactory.createFreeRouter();
//
    private WidgetAction connectAction = ActionFactory.createExtendedConnectAction(interractionLayer, new SceneConnectProvider(this));
    private WidgetAction reconnectAction = ActionFactory.createReconnectAction(new SceneReconnectProvider(this));
    private WidgetAction zoomAction = ActionFactory.createZoomAction();
    private WidgetAction panAction = ActionFactory.createPanAction();
//    private WidgetAction moveControlPointAction = ActionFactory.createFreeMoveControlPointAction();
//    private WidgetAction selectAction = ActionFactory.createSelectAction(new ObjectSelectProvider());
    private ModelSceneMenu modelMenu = new ModelSceneMenu(this);
    private NodeMenu nodeMenu = new NodeMenu(this);
    private EdgeMenu edgeMenu = new EdgeMenu(this);
    private SelectionNodeListener selectionNodeListener = null;
    private WidgetAction mouseClick;
//    private SetModelListener setModelList;
    private LoadSaveInterface loadSaveListener;
    private VisLogic logic;

    public GraphModelScene(SelectionNodeListener selNodeList) {
        mainLayer = new LayerWidget(this);
        addChild(mainLayer);

        connectionLayer = new LayerWidget(this);
        addChild(connectionLayer);
        addChild(interractionLayer);
        getActions().addAction(ActionFactory.createRectangularSelectAction(this, backgroundLayer));
        getActions().addAction(panAction);
        getActions().addAction(zoomAction);

        selectionNodeListener = selNodeList;
        setToolTipText("Set model, then generate visualization.");
        mouseClick = new WidgetAction.Adapter() {

            @Override
            public State mouseClicked(Widget wid, WidgetMouseEvent me) {

                if (wid.equals(GraphModelScene.this)) {
                    selectionNodeListener.unselect();
                    return State.CONSUMED;
                } else if (wid instanceof NodeWidget) {
                    selectionNodeListener.nodeSelected((NodeWidget) wid);
                    return State.CONSUMED;
                }
                return State.REJECTED;
            }
            ;
        };
        this.getActions().addAction(mouseClick);
        loadSaveListener = new LoadSaveListener();

    }

    @Override
    protected Widget attachNodeWidget(VisNode node) {
        NodeWidget wid = null;
        if (node.isPlace()) {
            wid = new PlaceWidget(this, (VisPlace) node);
            wid.setImage(PLACE_IMG);
        } else {
            wid = new TransitionWidget(this, (VisTransition) node);
            wid.setImage(TRANSITION_IMG);
        }
        wid.setToolTipText("Hold 'Ctrl'+'Mouse Right Button' to create arrow between place and transition");

        wid.getActions().addAction(connectAction);
        wid.getActions().addAction(moveAction);
        mainLayer.addChild(wid);
        wid.getActions().addAction(ActionFactory.createPopupMenuAction(nodeMenu));
        wid.getActions().addAction(mouseClick);
        wid.getActions().addAction(createSelectAction());
        return wid;
    }

    @Override
    protected Widget attachEdgeWidget(VisEdge edge) {
        EdgeWidget connection = new EdgeWidget(this, edge);
        connection.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        connection.setEndPointShape(PointShape.SQUARE_FILLED_BIG);
        connection.getActions().addAction(createObjectHoverAction());
        connection.getActions().addAction(createSelectAction());
        connection.getActions().addAction(reconnectAction);
        connection.setToolTipText("Hold 'Ctrl'+'Mouse Right Button' to move arrow from one place(or transition) to another");
        connectionLayer.addChild(connection);
        connection.getActions().addAction(ActionFactory.createPopupMenuAction(edgeMenu));
        return connection;
    }

    @Override
    protected void attachEdgeSourceAnchor(VisEdge edge, VisNode oldNode, VisNode sourceNode) {
        Widget w = sourceNode != null ? findWidget(sourceNode) : null;
        ((EdgeWidget) findWidget(edge)).setSourceAnchor(AnchorFactory.createRectangularAnchor(w));
    }

    @Override
    protected void attachEdgeTargetAnchor(VisEdge edge, VisNode oldNode, VisNode targetNode) {
        Widget w = targetNode != null ? findWidget(targetNode) : null;
        ((EdgeWidget) findWidget(edge)).setTargetAnchor(AnchorFactory.createRectangularAnchor(w));
    }

    public LoadSaveInterface getLoadSaveListener() {
        return loadSaveListener;
    }

    public VisLogic getLogic() {
        return logic;
    }

    public void setLogic(VisLogic logic) {
        this.nodeMenu.setLogic(logic);
        this.logic = logic;
    }

    private class LoadSaveListener implements LoadSaveInterface {

        private boolean isLocationForNodesSet;
        private boolean isSetControlPoints;

        public LoadSaveListener() {
            isLocationForNodesSet = false;
            isSetControlPoints = false;
        }

        public boolean isIsLocationForNodesSet() {
            return isLocationForNodesSet;
        }

        public void setIsLocationForNodesSet(boolean isLocationForNodesSet) {
            this.isLocationForNodesSet = isLocationForNodesSet;
        }

        public List<VisTransition> getTransitionsFromScene() {

            Collection<VisNode> listVisNode = getNodes();
            List<VisTransition> listVisTransition = new ArrayList<VisTransition>(0);

            setVisNodeLocation();
            for (VisNode node : listVisNode) {
                if (node instanceof VisTransition) {
                    listVisTransition.add((VisTransition) node);
                }
            }
            return listVisTransition;
        }

        public List<VisPlace> getPlacesFromScene() {
            Collection<VisNode> listVisNode = getNodes();
            List<VisPlace> placeses = new ArrayList<VisPlace>(0);

            setVisNodeLocation();
            for (VisNode node : listVisNode) {
                if (node instanceof VisPlace) {
                    VisPlace visPlace = (VisPlace) node;
                    placeses.add(visPlace);
                }
            }
            return placeses;
        }

        public List<VisEdge> getEdgesFromScene() {
            ArrayList<VisEdge> edges = new ArrayList<VisEdge>(0);
            setControlPoints();

            for (VisEdge edge : getEdges()) {
                edges.add(edge);
            }
            return edges;
        }

        /**
         * sets location and xmlSid (from PIPE xml documents) for all visNodes on scene
         */
        private void setVisNodeLocation() {
            if (!isLocationForNodesSet) {
                List<Widget> widgets = mainLayer.getChildren();
                int i = 1;
                for (Widget wid : widgets) {
                    if (wid instanceof NodeWidget) {
                        NodeWidget nodeWidget = (NodeWidget) wid;
                        VisNode node = nodeWidget.getVisNode();
                        node.setLocation(wid.getLocation());
                        if (node.isPlace()) {
                            node.setXmlSid("P" + i);
                        } else {
                            node.setXmlSid("T" + i);
                        }
                        i++;
                    }
                }
            }
            isLocationForNodesSet = true;
        }

        private void setControlPoints() {
            if (!isSetControlPoints) {
                List<Widget> edgeWidgets = connectionLayer.getChildren();
                for (Widget wid : edgeWidgets) {
                    if (wid instanceof EdgeWidget) {
                        VisEdge edge = ((EdgeWidget) wid).getEdge();
                        edge.setControlPoints(((EdgeWidget) wid).getControlPoints());
                    }
                }
            }
            isSetControlPoints = true;
        }

        /**
         * when position of nodes on scene changed this method should be run
         */
        public void nodesLocationAndControlPointsChanged() {
            isLocationForNodesSet = false;
            isSetControlPoints = false;
        }

        /**
         *
         */
        public void validateVisualizationGraph() throws VisValidationException {
            Collection<VisNode> listVisNode = getNodes();

            for (VisNode node : listVisNode) {
                if (node.getSid() == null) {
                    throw new VisValidationException("Name all reactions and species");
                }
            }
            for (VisNode node : listVisNode) {
                if (node.getSourceNodes().size() == 0 && node.getTargetNodes().size() == 0) {
                    if (node.isPlace()) {
                        throw new VisValidationException("Connect " + node.getSid() + " with reaction, or delete it.");
                    } else {
                        throw new VisValidationException("Add reactants or products for reaction: " + node.getSid());
                    }
                }
            }
        }

        public void modelSet() {
            setToolTipText("Right mouse click for creating a new place or transition");
            getActions().addAction(ActionFactory.createPopupMenuAction(modelMenu));
        }

        public void clearVisualization() {
//            mainLayer.removeChildren();
//            connectionLayer.removeChildren();
            for (VisEdge edge : new ArrayList<VisEdge>(getEdges())) {
                removeEdge(edge);
            }
            for (VisNode node : new ArrayList<VisNode>(getNodes())) {
                removeNode(node);
            }
            validate();
        }

        public void loadVisualization(List<VisEdge> edges, boolean addComp) {
            HashMap<String, VisPlace> placeMap = new HashMap<String, VisPlace>(0);
            HashMap<String, VisTransition> transitionMap = new HashMap<String, VisTransition>(0);
            HashSet<String> usedNodes = new HashSet<String>(0);

            clearVisualization();

            //adds nodes and edges
            for (VisEdge edge : edges) {
//                if (edge.getSource().isPlace()) {
//                    VisPlace source = (VisPlace) edge.getSource();
//                    VisTransition target = (VisTransition) edge.getTarget();
//                    if (!usedNodes.contains(source.getXmlSid())) {
//                        source.removeNullsFromSets();
//                        usedNodes.add(source.getXmlSid());
//                        NodeWidget w = (NodeWidget) addNode(source);
//                        w.setPreferredLocation(source.getLocation());
//                        w.setNameAndSid(source.getName(), source.getSid());
//                        w.setLabel(source.getSid());
//                    }
//                    if (!usedNodes.contains(target.getXmlSid())) {
//                        target.removeNullsFromSets();
//                        usedNodes.add(target.getXmlSid());
//                        NodeWidget w = (NodeWidget) addNode(target);
//                        w.setPreferredLocation(target.getLocation());
//                        w.setNameAndSid(target.getName(), target.getSid());
//                        String name = target.getSid();
//                        if (addComp) {
//                            name = name.concat(" " + Float.toString(target.getFlux()));
//                        }
//                        w.setLabel(name);
//                    }
//                } else {
                VisNode source = edge.getSource();
                VisNode target = edge.getTarget();
                if (!usedNodes.contains(source.getXmlSid())) {
                    source.removeNullsFromSets();
                    usedNodes.add(source.getXmlSid());
                    addNodeToScene(source, addComp);
//                        NodeWidget w = (NodeWidget) addNode(source);
//                        w.setPreferredLocation(source.getLocation());
//                        w.setNameAndSid(source.getName(), source.getSid());
//                        String name = source.getSid();
//                        if (addComp) {
//                            name = name.concat(" " + Float.toString(source.getFlux()));
//                        }
//                        w.setLabel(name);
                }
                if (!usedNodes.contains(target.getXmlSid())) {
                    target.removeNullsFromSets();
                    usedNodes.add(target.getXmlSid());
                    addNodeToScene(target, addComp);
//                        NodeWidget w = (NodeWidget) addNode(target);
//                        w.setPreferredLocation(target.getLocation());
//                        w.setNameAndSid(target.getName(), target.getSid());
//                        w.setLabel(target.getSid());
//                    }
                }
                addEdge(edge);
                setEdgeSource(edge, edge.getSource());
                setEdgeTarget(edge, edge.getTarget());
            }
            validate();
        }

        public void addNewEdge(VisNode source, VisNode target) {
            if (!source.isTheSame(target)) {
                VisEdge edge = new VisEdge(source,target);
                addEdge(edge);
                setEdgeSource(edge, edge.getSource());
                setEdgeTarget(edge, edge.getTarget());
            }
        }

        /**
         * adds node to graphScene
         */
        public void addNodeToScene(VisNode node, boolean addComp) {
            NodeWidget w = (NodeWidget) addNode(node);
            w.setPreferredLocation(node.getLocation());
            w.setNameAndSid(node.getName(), node.getSid());

            String widgetLabel = node.getSid();
            if (addComp && node.isTransition() && node.getSid() != null) {
                VisTransition trans = (VisTransition) node;
                widgetLabel = widgetLabel.concat(" " + Float.toString(trans.getFlux()));
            }
            w.setLabel(widgetLabel);
        }

        /**
         * 
         * @param addComp if true label of transition widget will be with computations, if false only its sid
         */
        public void addComputationsToTransitionsLabel(boolean addComp) {
            List<Widget> widgets = mainLayer.getChildren();

            String name;
            for (Widget widget : widgets) {
                if (widget instanceof TransitionWidget) {
                    TransitionWidget transWidget = (TransitionWidget) widget;
                    VisTransition trans = (VisTransition) transWidget.getVisNode();
                    name = trans.getSid();
                    if (addComp && name != null) {
                        name = name.concat(" " + Float.toString(trans.getFlux()));
                    }
                    transWidget.setLabel(name);
                }
            }
            validate();
        }

        public void validateNewGraph() {
            validate();
        }
        
    }
}
