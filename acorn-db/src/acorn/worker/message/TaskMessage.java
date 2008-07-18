/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package acorn.worker.message;

import java.io.Serializable;

/**
 *
 * @author kuba
 */
public class TaskMessage implements Serializable {
    private int taskId;

    public TaskMessage(int id)
    {
        taskId = id;
    }
   
    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
}
