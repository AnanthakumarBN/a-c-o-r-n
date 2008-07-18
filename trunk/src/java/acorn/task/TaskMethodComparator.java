/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package acorn.task;

import acorn.db.ETask;
import java.util.Comparator;

/**
 *
 * @author kuba
 */
public class TaskMethodComparator implements Comparator<ETask> {
    public int compare(ETask t1, ETask t2) { 
        return t1.getMethod().getIdent().compareTo(t2.getMethod().getIdent());
    }
}
