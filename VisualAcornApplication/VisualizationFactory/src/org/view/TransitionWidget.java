/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.view;

import org.netbeans.api.visual.graph.GraphScene;
import org.visualapi.VisTransition;

/**
 *
 * @author markos
 */
public class TransitionWidget extends NodeWidget{

    public TransitionWidget(GraphScene arg0) {
        super(arg0);
    }

    public TransitionWidget(GraphScene arg0, VisTransition trans) {
        super(arg0, trans);
    }

    @Override
    public boolean isTransitionWidget(){
        return true;
    }

    @Override
    public boolean isPlaceWidget(){
        return false;
    }
}
