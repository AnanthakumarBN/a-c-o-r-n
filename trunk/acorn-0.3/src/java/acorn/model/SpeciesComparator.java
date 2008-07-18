package acorn.model;

import java.util.Comparator;

/**
 * SpeciesComparator
 * @author lukasz
 */
public class SpeciesComparator implements Comparator<Species> {
    public int compare(Species s1, Species s2) { 
        return s1.getName().compareTo(s2.getName());
    }
}
