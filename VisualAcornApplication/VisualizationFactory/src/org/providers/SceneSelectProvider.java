/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.providers;

import java.awt.Point;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.general.IconNodeWidget;
import org.openide.windows.TopComponent;

/**
 *
 * @author markos
 */
public class SceneSelectProvider implements SelectProvider{

    private GraphScene scene;

    public SceneSelectProvider(GraphScene scene) {
        this.scene = scene;
    }


    public boolean isAimingAllowed(Widget arg0, Point arg1, boolean arg2) {
        return true;
    }

    /*
     * @return: true if selected widget is IconNodeWidget
     */
    public boolean isSelectionAllowed(Widget arg0, Point arg1, boolean arg2) {
        if(arg0.getClass().equals(IconNodeWidget.class)){
            return true;
        }
        return false;
    }

    public void select(Widget arg0, Point arg1, boolean arg2) {
        IconNodeWidget wid = (IconNodeWidget) arg0;
       TopComponent topComp = (TopComponent)scene.getView().getParent();

    }

}
