package acorn.task;

import acorn.db.ETask;
import java.util.Comparator;

public class TaskSharedComparator implements Comparator<ETask> {
    public int compare(ETask t1, ETask t2) {
        return t1.getShared().compareTo(t2.getShared());
    }
}
