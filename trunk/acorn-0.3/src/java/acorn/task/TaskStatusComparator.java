package acorn.task;

import acorn.db.ETask;
import java.util.Comparator;

/**
 * TaskStatusComparator
 * @author lukasz
 */
public class TaskStatusComparator implements Comparator<ETask> {
    public int compare(ETask t1, ETask t2) { 
        return t1.getStatus().compareTo(t2.getStatus());
    }
}