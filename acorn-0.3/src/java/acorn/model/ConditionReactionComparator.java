package acorn.model;

import java.util.Comparator;

/**
 * ConditionReactionComparator
 * @author lukasz
 */
public class ConditionReactionComparator implements Comparator<Condition> {
    public int compare(Condition c1, Condition c2) { 
        return c1.getReactionName().compareTo(c2.getReactionName());
    }
}
