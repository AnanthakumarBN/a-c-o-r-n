/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graphscene;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;
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
import org.providers.NodeMenu;
import org.providers.SceneConnectProvider;
import org.providers.SceneReconnectProvider;
import org.view.EdgeWidget;
import org.view.NodeWidget;
import org.view.PlaceWidget;
import org.view.TransitionWidget;
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
//    private WidgetAction moveControlPointAction = ActionFactory.createFreeMoveControlPointAction();
//    private WidgetAction selectAction = ActionFactory.createSelectAction(new ObjectSelectProvider());
    private ModelSceneMenu modelMenu = new ModelSceneMenu(this);
    private NodeMenu nodeMenu = new NodeMenu(this);
    private SelectionNodeListener selectionNodeListener = null;
    private WidgetAction mouseClick;
//    private SetModelListener setModelList;
    private LoadSaveInterface loadSaveListener;

    public GraphModelScene(SelectionNodeListener selNodeList) {
        mainLayer = new LayerWidget(this);
        addChild(mainLayer);

        connectionLayer = new LayerWidget(this);
        addChild(connectionLayer);
        addChild(interractionLayer);
        getActions().addAction(ActionFactory.createRectangularSelectAction(this, backgroundLayer));
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

//    private class SetModelImpl implements SetModelListener {
//
//        GraphModelScene scene;
//
//        public SetModelImpl(GraphModelScene scene) {
//            this.scene = scene;
//        }
//
//        public void modelIsSet() {
//            scene.isModelSet = true;
//            scene.setToolTipText("Left mouse click for creating a new place or transition");
//            scene.getActions().addAction(ActionFactory.createPopupMenuAction(modelMenu));
//            scene.loadSaveListener = new LoadSaveListener();
//        }
//
//        public LoadSaveInterface getLoadSaveImpl() {
//            return scene.loadSaveListener;
//        }
//    }

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

        public List<VisEdge> getEdgesFromScene(){
            ArrayList<VisEdge> edges = new ArrayList<VisEdge>(0);
            setControlPoints();

            for(VisEdge edge: getEdges()){
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
                        if(node.isPlace()){
                            node.setXmlSid("P"+i);
                        }else{
                            node.setXmlSid("T"+i);
                        }
                        i++;
                    }
                }
            }
            isLocationForNodesSet = true;
        }

        private void setControlPoints(){
            if(!isSetControlPoints){
                List<Widget> edgeWidgets = connectionLayer.getChildren();
                for(Widget wid : edgeWidgets){
                    if(wid instanceof EdgeWidget){
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
        public void nodesLocationAndControlPointsChanged(){
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
            setToolTipText("Left mouse click for creating a new place or transition");
            getActions().addAction(ActionFactory.createPopupMenuAction(modelMenu));
        }
    }
}