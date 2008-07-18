package acorn.task;

import acorn.db.ETask;
import java.util.Comparator;

/**
 * TaskModelComparator
 * @author lukasz
 */
public class TaskModelComparator implements Comparator<ETask> {
    public int compare(ETask t1, ETask t2) { 
        return t1.getModel().getName().compareTo(t2.getModel().getName());
    }
}
