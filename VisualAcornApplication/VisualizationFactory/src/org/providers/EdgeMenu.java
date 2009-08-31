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
import org.view.EdgeWidget;
import org.visualapi.VisEdge;

/**
 *
 * @author markos
 */
public class EdgeMenu implements PopupMenuProvider, ActionListener {

    private static final String DELETE_EDGE_ACTION = "deleteEdgeAction";
    private JPopupMenu menu;
    private EdgeWidget edgeWidget;
    private Point point;
    private GraphScene scene;

    public EdgeMenu(GraphScene scene) {
        this.scene = scene;
        this.menu = new JPopupMenu("Edge Menu");

        JMenuItem item;
        item = new JMenuItem("Delete arrow");
        item.setActionCommand(DELETE_EDGE_ACTION);
        item.addActionListener(this);
        menu.add(item);
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(DELETE_EDGE_ACTION)){
            VisEdge edge = (VisEdge) scene.findObject(edgeWidget);

//            edgeWidget.getEdge().removeEdgeFromNodes();
            edge.removeEdgeFromNodes();
            scene.removeEdge(edge);
            scene.validate();
        }
    }

    public JPopupMenu getPopupMenu(Widget wid, Point p) {
        this.point = p;
        this.edgeWidget = (EdgeWidget) wid;
        return menu;
    }
}
