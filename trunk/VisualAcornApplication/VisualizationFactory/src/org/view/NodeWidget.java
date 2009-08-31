/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.view;

import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.general.IconNodeWidget;
import org.visualapi.VisNode;

/**
 *
 * @author markos
 */
public abstract class NodeWidget extends IconNodeWidget {

    private GraphScene scene;
    private VisNode node;

    public NodeWidget(GraphScene arg0) {
        super(arg0);
        scene = arg0;
    }

    public NodeWidget(GraphScene scene, VisNode node) {
        super(scene);
        this.scene = scene;
        this.node = node;
    }
    public VisNode getVisNode(){
        return node;
    }

    public boolean isTransitionWidget(){
        return false;
    }

    public boolean isPlaceWidget(){
        return false;
    }


    public void setName(String name) {
        node.setName(name);
    }

    public String getName() {
        return node.getName();
    }

    public void setNameAndSid(String name, String sid) {
        node.setName(name);
        node.setSid(sid);
    }

    public void setSid(String sid) {
        node.setSid(sid);
    }
    public String getSid(){
        return node.getSid();
    }
}
