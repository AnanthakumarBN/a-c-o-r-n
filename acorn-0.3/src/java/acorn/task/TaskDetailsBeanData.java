/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.task;

import acorn.db.EModel;
import acorn.model.Params;
import acorn.model.Visualization;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Mateusza
 */
public class TaskDetailsBeanData {

    public Params parameters;
    public String visualizationsNameFilter;
    public int visualizationsStart;
    public List<Visualization> visualizations;
    public List<Visualization> filteredVisualizations;
    private Long selectedVisualizationId;
    public String taskName;
    public String method;
    public Date lastUse;
    private Semaphore mutex;
    public String errorMessage;
    public EModel model;

    public TaskDetailsBeanData(EModel e) {
            super();
        visualizationsStart = 0;
        visualizationsNameFilter = "";
        errorMessage = "";
        lastUse = new Date();
        this.model = e;
        mutex = new Semaphore(1);
    }
    
    
    public void lock() {
        try {
            mutex.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unlock() {
        mutex.release();
    }

    public Date touch() {
        Date tmp = lastUse;
        lastUse = new Date();
        return tmp;
    }

    public EModel getModel() {
        return model;
    }

    public Long getSelectedVisualization() {
        return selectedVisualizationId;
    }

    public void setSelectedVisualization(Long selectedVisualizationId) {
        this.selectedVisualizationId = selectedVisualizationId;
    }

    
}
