package acorn.task;

import acorn.db.ETask;
import java.util.Comparator;

/**
 * TaskSubmissionDateComparator
 * @author lukasz
 */
public class TaskSubmissionDateComparator implements Comparator<ETask> {
    public int compare(ETask t1, ETask t2) { 
        return t1.getDate().compareTo(t2.getDate());
    }
}
