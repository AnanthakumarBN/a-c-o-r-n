/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.providers;

import java.awt.Point;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.action.ReconnectProvider;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.view.EdgeWidget;
import org.view.NodeWidget;
import org.visualapi.VisEdge;
import org.visualapi.VisNode;

/**
 *
 * @author markos
 */
public class SceneReconnectProvider implements ReconnectProvider {

    private GraphScene scene;
    private VisEdge edge;
    private VisNode originalNode;
    private VisNode replacementNode;

    public SceneReconnectProvider(GraphScene scene) {
        this.scene = scene;
    }

    public void reconnectingStarted(ConnectionWidget connectionWidget, boolean reconnectingSource) {
    }

    public void reconnectingFinished(ConnectionWidget connectionWidget, boolean reconnectingSource) {
    }

    public boolean isSourceReconnectable(ConnectionWidget connectionWidget) {
        Object object = scene.findObject(connectionWidget);
        edge = scene.isEdge(object) ? (VisEdge) object : null;
        originalNode = (VisNode) (edge != null ? scene.getEdgeSource(edge) : null);
        return originalNode != null;
    }

    public boolean isTargetReconnectable(ConnectionWidget connectionWidget) {
        Object object = scene.findObject(connectionWidget);
        edge = scene.isEdge(object) ? (VisEdge) object : null;
        originalNode = (VisNode) (edge != null ? scene.getEdgeTarget(edge) : null);
        return originalNode != null;
    }

    public ConnectorState isReplacementWidget(ConnectionWidget connectionWidget, Widget replacementWidget, boolean reconnectingSource) {
        Object object = scene.findObject(replacementWidget);
        replacementNode = scene.isNode(object) ? (VisNode) object : null;
        if (replacementNode != null && replacementNode.isTransition() == originalNode.isTransition()) {
            return ConnectorState.ACCEPT;
        }
        return object != null ? ConnectorState.REJECT_AND_STOP : ConnectorState.REJECT;
    }

    public boolean hasCustomReplacementWidgetResolver(Scene scene) {
        return false;
    }

    public Widget resolveReplacementWidget(Scene scene, Point sceneLocation) {
        return null;
    }

    public void reconnect(ConnectionWidget edgeWidget, Widget replacementWidget, boolean reconnectingSource) {
        VisEdge visEdge = ((EdgeWidget)edgeWidget).getEdge();
        VisNode visNode = null;
        if (replacementWidget == null) {
            visEdge.removeEdgeFromNodes();
            scene.removeEdge(visEdge);
        } else if (replacementWidget instanceof NodeWidget) {
            visNode = ((NodeWidget) replacementWidget).getVisNode();
            if (reconnectingSource) {
                visEdge.setSource(visNode);
                scene.setEdgeSource(visEdge, replacementNode);
            } else {
                visEdge.setTarget(visNode);
                scene.setEdgeTarget(visEdge, replacementNode);
            }
        }
    }
}
