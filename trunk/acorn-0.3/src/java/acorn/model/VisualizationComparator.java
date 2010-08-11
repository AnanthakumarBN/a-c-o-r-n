/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package acorn.model;

import java.util.Comparator;

/**
 *
 * @author Mateusz
 */
    public class VisualizationComparator implements Comparator<Visualization> {
    public int compare(Visualization s1, Visualization s2) { 
        return s1.getQualifiedName().compareTo(s2.getQualifiedName());
    }
}
