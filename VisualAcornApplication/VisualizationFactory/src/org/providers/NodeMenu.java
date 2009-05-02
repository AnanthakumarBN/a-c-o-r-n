/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.providers;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.Widget;
import org.view.NodeWidget;
import org.visualapi.VisNode;

/**
 *
 * @author markos
 */
public class NodeMenu implements PopupMenuProvider, ActionListener {

    private static final String DELETE_NODE_ACTION = "deleteNodeAction"; // NOI18N

    private JPopupMenu menu;
    private NodeWidget node;

    private Point point;
    private GraphScene scene;

    public NodeMenu(GraphScene scene) {
        this.scene=scene;
        menu = new JPopupMenu("Node Menu");
        JMenuItem item;

        item = new JMenuItem("Delete node with all input and output edges");
        item.setActionCommand(DELETE_NODE_ACTION);
        item.addActionListener(this);
        menu.add(item);
    }

    public JPopupMenu getPopupMenu(Widget widget,Point point){
        this.point=point;
        this.node=(NodeWidget)widget;
        return menu;
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(DELETE_NODE_ACTION)){
            VisNode visNode = (VisNode)scene.findObject (node);
            visNode.removeAllConnections();
            scene.removeNodeWithEdges(visNode);
            scene.validate();
        }
    }


}