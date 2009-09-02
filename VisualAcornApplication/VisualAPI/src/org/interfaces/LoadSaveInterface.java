/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.interfaces;

import java.util.Collection;
import java.util.List;
import org.dbStructs.NameStruct;
import org.exceptions.VisValidationException;
import org.visualapi.VisEdge;
import org.visualapi.VisNode;
import org.visualapi.VisPlace;
import org.visualapi.VisTransition;

/**
 *
 * @author markos
 */
public interface LoadSaveInterface {

    public List<VisTransition> getTransitionsFromScene();

    public List<VisPlace> getPlacesFromScene();

    public List<VisEdge> getEdgesFromScene();

    public void validateNewGraph();

    public void validateVisualizationGraph() throws VisValidationException;

    public void nodesLocationAndControlPointsChanged();

    public void modelSet();

    public void loadVisualization(List<VisEdge> edges, boolean addComp);

    public void clearVisualization();

    public void addComputationsToTransitionsLabel(boolean addComp);

    public void addNodeToScene(VisNode node, boolean addComp);

    public void addNewEdge(VisNode source, VisNode target);

    public void removeDetachedTransitions(Collection<NameStruct> detachedReactions);
}
