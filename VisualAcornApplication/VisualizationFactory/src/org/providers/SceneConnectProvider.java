/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.providers;

import java.awt.Point;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.visualapi.VisEdge;
import org.visualapi.VisNode;

/**
 *
 * @author markos
 */
public class SceneConnectProvider implements ConnectProvider {

    private VisNode source = null;
    private VisNode target = null;
    private GraphScene scene;

    public SceneConnectProvider(GraphScene scene) {
        this.scene = scene;
    }

    public boolean isSourceWidget(Widget sourceWidget) {
        Object object = scene.findObject(sourceWidget);
        source = scene.isNode(object) ? (VisNode) object : null;
        return source != null;
    }

    public ConnectorState isTargetWidget(Widget sourceWidget, Widget targetWidget) {
        Object object = scene.findObject(targetWidget);
        target = scene.isNode(object) ? (VisNode) object : null;
        if (target != null && source != null && (source.isPlace() != target.isPlace())) {
            return ConnectorState.ACCEPT;
        }
        return object != null ? ConnectorState.REJECT_AND_STOP : ConnectorState.REJECT;
    }

    public boolean hasCustomTargetWidgetResolver(Scene scene) {
        return false;
    }

    public Widget resolveTargetWidget(Scene scene, Point sceneLocation) {
        return null;
    }

    /*Connection is created only between place and transition
     *
     */
    public void createConnection(Widget sourceWidget, Widget targetWidget) {
        if (source != null && target != null && (source.isPlace() != target.isPlace())) {
            VisEdge edge = new VisEdge(source, target);
            scene.addEdge(edge);
            scene.setEdgeSource(edge, source);
            scene.setEdgeTarget(edge, target);
        }
    }
}
