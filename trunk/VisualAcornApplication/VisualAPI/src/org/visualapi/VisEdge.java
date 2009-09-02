/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.visualapi;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *  edge between: source -----> target
 * @author markos
 */
public class VisEdge {

    private VisNode source;
    private VisNode target;
    private List<Point> controlPoints;

    public VisEdge(VisNode source, VisNode target) {
        this.source = source;
        this.target = target;
        source.addTargetNode(target);
//        target.addSourceNode(source);
        this.controlPoints = new ArrayList<Point>(0);
    }

    public VisEdge() {
        super();
    }

    public List<Point> getControlPoints() {
        return controlPoints;
    }

    public void setControlPoints(List<Point> controlPoints) {
        this.controlPoints = controlPoints;
    }

    public VisNode getSource() {
        return source;
    }

    public void setSource(VisNode source) {
        if (this.source != null) {
            removeEdgeFromNodes();
        }
        this.source = source;
        if (this.source != null && this.target != null) {
            addEdgeToNodes(this.source, this.target);
        }
    }

    public VisNode getTarget() {
        return target;
    }

    public void setTarget(VisNode target) {
        if (this.target != null) {
            removeEdgeFromNodes();
        }
        this.target = target;
        if (this.source != null && this.target != null) {
            addEdgeToNodes(this.source, this.target);
        }
    }

    /**
     * removes this edge from sourceNode and targetNode
     */
    public void removeEdgeFromNodes() {
        target.removeSourceNode(source);
        source.removeTargetNode(target);
    }

    public void addEdgeToNodes(VisNode src, VisNode tar) {
        src.addTargetNode(tar);
    }
}
