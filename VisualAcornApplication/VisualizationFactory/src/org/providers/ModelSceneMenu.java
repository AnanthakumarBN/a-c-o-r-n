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
import org.visualapi.VisNode;
import org.visualapi.VisPlace;
import org.visualapi.VisTransition;

/**
 *
 * @author markos
 */
public class ModelSceneMenu implements PopupMenuProvider, ActionListener {

    private static final String ADD_NEW_PLACE_ACTION = "addNewPlaceAction";
    private static final String ADD_NEW_TRANSITION_ACTION = "addNewTransitionAction";
    private GraphScene scene;
    private JPopupMenu menu;
    private Point point;

    public ModelSceneMenu(GraphScene scene) {
        this.scene = scene;
        menu = new JPopupMenu("Scene Menu");
        JMenuItem itemPlace;
        JMenuItem itemTransition;

        itemPlace = new JMenuItem("Add New place");
        itemPlace.setActionCommand(ADD_NEW_PLACE_ACTION);
        itemTransition = new JMenuItem("Add New transition");
        itemTransition.setActionCommand(ADD_NEW_TRANSITION_ACTION);

        itemPlace.addActionListener(this);
        menu.add(itemPlace);
        itemTransition.addActionListener(this);
        menu.add(itemTransition);
    }

    public JPopupMenu getPopupMenu(Widget widget, Point point) {
        this.point = point;
        return menu;
    }

    public void actionPerformed(ActionEvent e) {
        if (ADD_NEW_PLACE_ACTION.equals(e.getActionCommand()) || ADD_NEW_TRANSITION_ACTION.equals(e.getActionCommand())) {
            VisNode place = null;
            if (ADD_NEW_PLACE_ACTION.equals(e.getActionCommand())) {
                place = new VisPlace();
            } else {
                place = new VisTransition();
            }

                Widget newNode = scene.addNode(place);
                //newNode.setPreferredLocation(point);
                scene.getSceneAnimator().animatePreferredLocation(newNode, point);
                scene.validate();
        }
    }
}
