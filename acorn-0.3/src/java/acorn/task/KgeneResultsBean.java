package acorn.task;

import acorn.db.ECommonResults;
import acorn.db.ETask;
import acorn.db.ETaskController;
import acorn.errorHandling.ErrorBean;
import javax.faces.context.FacesContext;

/**
 * KgeneResultBean
 * @author lb235922
 */

public class KgeneResultsBean {
    private static double nonEssentialLimit = 0.00001;
  
    private ETask task;
    
    public KgeneResultsBean() {
        task = null;
    }
    
    /**
     * HACK - this method should be invoked at the very beginning
     * @return nothing
     */
    public String getInit() {
        fetchTask();
        return null;
    }
    
    /**
     * Get task from database
     */
    private void fetchTask() {
        FacesContext fc = FacesContext.getCurrentInstance();        
        String taskId = (String) fc.getExternalContext().getRequestParameterMap().get("taskID");

        if (taskId != null) {
            try {
                ETaskController tc = new ETaskController();
                task = tc.getTask(Integer.parseInt(taskId));
            }        
            catch (Exception e) {
                ErrorBean.printStackTrace(e);
                task = null;
            }
        }
    }
    
    /**
     * Get optimisation staus
     * @return optimisation status
     */
    public String getOptStatus() {
        assert task != null;
        return task.getCommonResults().getStatus();
    }
    
    /**
     * Get growth rate
     * @return growth rate
     */
    public Float getGrowthRate() {
        assert task != null;
        return task.getCommonResults().getGrowthRate();
    }
   
    /**
     * Is gene essential for our target? 
     * @return true or false
     */ 
    public boolean isGeneEssential() {
        assert task != null;
        return  task.getCommonResults().getGrowthRate() < nonEssentialLimit
                || !task.getCommonResults().getStatus().equalsIgnoreCase(ECommonResults.statusOptimal);
    }
    
    /**
     * Is gene not essential for our target? 
     * @return true or false
     */ 
    public boolean isGeneNotEssential() {
        assert task != null;
        return !isGeneEssential();
    }
    
    /**
     * Getters and setters
     */

    public ETask getTask() {
        return task;
    }

    public void setTask(ETask task) {
        this.task = task;
    }

}
