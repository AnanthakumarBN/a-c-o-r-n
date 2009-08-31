/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.view;

import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Scene;
import org.visualapi.VisEdge;
import org.visualapi.VisNode;

/**
 *
 * @author markos
 */
public class EdgeWidget extends ConnectionWidget {

    VisEdge edge;

    public EdgeWidget(Scene arg0, VisEdge edge) {
        super(arg0);
        this.edge = edge;
    }

    public VisEdge getEdge() {
        return edge;
    }

    public VisNode getSource() {
        return edge.getSource();
    }

    public VisNode getTarget() {
        return edge.getTarget();
    }

//    @Override
//    protected void paintWidget() {
//        this.setForeground(Color.BLACK);
//        this.setLineColor(Color.BLACK);
//        super.paintWidget();
//    }
}
