package acorn.task;

import acorn.db.ETask;
import java.util.Comparator;

/**
 * TaskNameComparator
 * @author lukasz
 */
public class TaskNameComparator implements Comparator<ETask> {
    public int compare(ETask t1, ETask t2) {
        return t1.getName().compareTo(t2.getName());
    }
}
