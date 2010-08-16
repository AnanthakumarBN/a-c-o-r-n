package acorn.task;

import acorn.db.ETask;
import java.util.Comparator;

public class TaskDeleteComparator implements Comparator<ETask> {
    TaskListBean tlb;

    public TaskDeleteComparator(TaskListBean tlb) {
        this.tlb = tlb;
    }

    public int compare(ETask t1, ETask t2) {
        Boolean b1 = (t1.getOwnerId() == tlb.getCurrentUserId()) || tlb.isDeleteAllUser();
        Boolean b2 = (t2.getOwnerId() == tlb.getCurrentUserId()) || tlb.isDeleteAllUser();
        return b1.compareTo(b2);
    }
}
