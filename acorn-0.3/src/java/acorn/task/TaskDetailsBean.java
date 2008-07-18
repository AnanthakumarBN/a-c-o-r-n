package acorn.task;

import acorn.db.EFbaData;
import acorn.db.EKgeneData;
import acorn.db.EMethod;
import acorn.db.ERscanData;
import acorn.db.ETask;
import acorn.db.ETaskController;
import acorn.db.EUser;
import acorn.errorHandling.ErrorBean;
import acorn.userManagement.UserManager;
import javax.faces.context.FacesContext;

/**
 * TaskDetailsBean
 * @author lukasz
 */

public class TaskDetailsBean {
    /* Current task */
    private ETask task;
    
    public TaskDetailsBean() {
        task = null;
    }
    
    /**
     * HACK - this method should be invoked at the very beginning
     * @return - nothing
     */
    public String getInit() {
        fetchTask();
        return null;
    }
    
    /**
     * Get current task from database
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
     * Merge state of @task with persistent context
     */
    public void updateTask() {
        try {            
            ETaskController et = new ETaskController();
            et.mergeTask(task);
        } 
        catch (Exception e) { 
            ErrorBean.printMessage(e, 
                    "content:infoForm:taskModifiers", 
                    "Unexpected error while connecting database. Please contact the system Administrator.");
        }
    }
    
    /**
     * Refresh state of @task
     */
    public void discardChanges() {
        try {
            ETaskController tc = new ETaskController();
            task = tc.getTask(task);
        }
        catch (Exception e) { 
            ErrorBean.printMessage(e, 
                    "content:infoForm:taskModifiers", 
                    "Unexpected error while connecting database. Please contact the system Administrator.");
        }    
    }
    
    /**
     * Remove @task from database
     * @return navigation string
     */
    public String deleteTask() {
        try {
            ETaskController tc = new ETaskController();
            tc.removeTask(task);
        }
        catch (Exception e) {
            ErrorBean.printMessage(e,
                    "content:infoForm:taskModifiers", 
                    "Unexpected error while connecting database. Please contact the system Administrator.");
            return null;
        }
        
        task = null;
        
        return "taskList";
    }
    
    /**
     * Does @task belong to current user?
     * @return true or false
     */
    public boolean getIsTaskMine() {
        return UserManager.getCurrentUser() != null && (task.getModel().getOwner().getId().equals(UserManager.getCurrentUser().getId()) || UserManager.getUserStatus().equalsIgnoreCase(EUser.statusAdmin));
    }
    
    /**
     * Does @task belong to current user?
     * @return true or false
     */
    public boolean getIsTaskNotMine() {
        return !getIsTaskMine();
    }
    
    /**
     * Is current user entitled to see @task's details?
     * @return true or false
     */
    public boolean isAddressValid() {
        return getTask() != null && (getTask().getShared() || getIsTaskMine());
    }
    
    /**
     * Does current user have permission to see @task's details?
     * @return true or false
     */
    public boolean isAddressInvalid() {
        return !isAddressValid();
    }
    
    /**
     * Is @task binded to FBA method?
     * @return true or false
     */
    public boolean isFba() {
        assert task != null;
        return getTask().getMethod().getIdent().equals(EMethod.fba);
    }
    
    /**
     * Is @task binded to FVA method?
     * @return true or false
     */
    public boolean isFva() {
        assert task != null;
        return getTask().getMethod().getIdent().equals(EMethod.fva);
    }
    
    /**
     * Is @task binded to RSCAN method?
     * @return true or false
     */
    public boolean isRscan() {
        assert task != null;
        return getTask().getMethod().getIdent().equals(EMethod.rscan);
    }
     
    /**
     * Is @task binded to KGENE method?
     * @return true or false
     */
    public boolean isKgene() {
        assert task != null;
        return getTask().getMethod().getIdent().equals(EMethod.kgene);
    }
    
    /* IF YOU WANT TO ADD NEW METHOD -> put here function similar to the above */
    
    /**
     * Get FBA's target function
     * @return string with object function name
     */
    public String getFbaCriterion() {
        assert task != null;
        return ((EFbaData)(task.getMethodData())).getObjFunctionName();
    }
    
    /**
     * Get RSCAN's target function
     * @return string with object function name
     */
    public String getRscanCriterion() {
        assert task != null;
        return ((ERscanData)(task.getMethodData())).getObjectFunctionName();
    }
    
    /**
     * Get KGENE's target function
     * @return string with object function name
     */
    public String getKgeneCriterion() {
        assert task != null;
        return ((EKgeneData)(task.getMethodData())).getObjFunctionName();
    }
    
    /**
     * Get KGENE's knockout gene's name
     * @return string with gene name
     */
    public String getKgeneGene() {
        assert task != null;
        return (((EKgeneData)(task.getMethodData()))).getGene();
    }
    
    /* IF YOU WANT TO ADD NEW METHOD (with parameters) -> put here getters to target
     * functions and/or choosen gene and/or other parameters of new method */
    
    /**
     * Are results available?
     * @return true or false
     */
    public boolean isResults() {
        assert task != null;
        return getTask().resultsAvailable();
    }
    
    /**
     * Setters ang getters:
     */
    
    public ETask getTask() {
        return task;
    }

    public void setTask(ETask task) {
        this.task = task;
    }
    
    public String getInfo() {
        boolean shared = task.getShared(); 
        fetchTask(); 
        task.setShared(shared);
        return task.getInfo();
    }
    
}
