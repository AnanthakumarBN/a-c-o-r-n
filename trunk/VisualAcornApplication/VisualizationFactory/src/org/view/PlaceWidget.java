/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.view;

import org.netbeans.api.visual.graph.GraphScene;
import org.visualapi.VisPlace;

/**
 *
 * @author markos
 */
public class PlaceWidget extends NodeWidget {

    public PlaceWidget(GraphScene arg0) {
        super(arg0);
    }

    public PlaceWidget(GraphScene arg0, VisPlace place) {
        super(arg0, place);
    }

    @Override
    public boolean isTransitionWidget() {
        return false;
    }

    @Override
    public boolean isPlaceWidget() {
        return true;
    }
}
