/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.providers;

import java.awt.MenuItem;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.Widget;
import org.view.NodeWidget;
import org.vislogicengine.VisLogic;
import org.visualapi.VisNode;

/**
 *
 * @author markos
 */
public class NodeMenu implements PopupMenuProvider, ActionListener {

    private static final String DELETE_NODE_ACTION = "deleteNodeAction"; // NOI18N
    private static final String ADD_SOURCE_NODES = "addSourceNodes";
    private static final String ADD_TARGET_NODES = "addTargetNodes";
    private JPopupMenu menu;
    private NodeWidget node;
    private Point point;
    private GraphScene scene;
    private VisLogic logic;

    public NodeMenu(GraphScene scene) {
        this.scene = scene;
        menu = new JPopupMenu("Node Menu");
        JMenuItem item;

        item = new JMenuItem("Delete node with all input and output edges");
        item.setActionCommand(DELETE_NODE_ACTION);
        item.addActionListener(this);
        menu.add(item);
        JMenuItem addSourceItem = new JMenuItem("Add source nodes");
        JMenuItem addTargetItem = new JMenuItem("Add target nodes");

        addSourceItem.setActionCommand(ADD_SOURCE_NODES);
        addTargetItem.setActionCommand(ADD_TARGET_NODES);
        addSourceItem.addActionListener(this);
        addTargetItem.addActionListener(this);
        menu.add(addSourceItem);
        menu.add(addTargetItem);
    }

    public void setLogic(VisLogic logic) {
        this.logic = logic;
    }

    public JPopupMenu getPopupMenu(Widget widget, Point point) {
        this.point = point;
        this.node = (NodeWidget) widget;

        MenuElement[] items = menu.getSubElements();
        for (MenuElement elt : items) {
            if (elt instanceof JMenuItem) {
                JMenuItem item = (JMenuItem) elt;
                if (item.getActionCommand().equals(ADD_SOURCE_NODES) || item.getActionCommand().equals(ADD_TARGET_NODES)) {
                    if (this.node.getSid() == null) {
                        item.setEnabled(false);
                    } else {
                        item.setEnabled(true);
                    }
                }
            }
        }
        return menu;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(DELETE_NODE_ACTION)) {
            VisNode visNode = (VisNode) scene.findObject(node);
            visNode.removeAllConnections();
            scene.removeNodeWithEdges(visNode);
            scene.validate();
        } else {
            VisNode visNode = (VisNode) scene.findObject(node);
            visNode.setLocation(node.getLocation());

            if (e.getActionCommand().equals(ADD_SOURCE_NODES)) {
                logic.addNodes(visNode, true);
            } else if (e.getActionCommand().equals(ADD_TARGET_NODES)) {
                logic.addNodes(visNode, false);
            }
//            scene.validate();
        }
    }
}