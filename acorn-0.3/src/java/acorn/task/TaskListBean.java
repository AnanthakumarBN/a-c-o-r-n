package acorn.task;

import acorn.db.ETask;
import acorn.db.ETaskController;
import acorn.db.EUser;
import acorn.errorHandling.ErrorBean;
import acorn.userManagement.UserManager;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.faces.context.FacesContext;

/**
 * TaskListBean
 * @author lukasz
 */
public class TaskListBean {    
    /* Sorting */
    private static int byName = 0;
    private static int byModel = 1;
    private static int byDate = 2;
    private static int byStatus = 3;
    private static int byMethod = 4;
    
    /* Fields */
    private boolean mine;
    private boolean shared;
    private boolean others;
    
    private String  taskNameFilter;
    private String  modelNameFilter;
    
    private int     sort;
    private boolean sortDir; // true - ascending, false - descending
    
    private int     rows;
    private int     page;
    
    private List<ETask> taskList;
    private List<ETask> filteredTaskList;

    public TaskListBean() {
        taskNameFilter = new String();
        modelNameFilter = new String();
        
        mine = false;
        shared = false;
        others = false;
        
        setSortDefault();
        setSortDirDefault();
        setRowsDefault();
        setPageDefault();

        taskList = new LinkedList<ETask>();
        filteredTaskList = new LinkedList<ETask>();
    }
    
    /**
     * HACK - this method should be invoked at the very beginning 
     * @return - nothig
     */
    public String getInit() {
        setShow();
        fetchList();
        filterList();
        return null;
    }
    
    /**
     * Set @mine, @shared and @others fields.
     * @return - nothing
     */
    private void setShow() {
        if (UserManager.getUserStatus().equals(EUser.statusAdmin)) {
            mine = true;
            shared = true;
            others = true;
        } else if (UserManager.getUserStatus().equals(EUser.statusNormal)) {
            mine = true;
            shared = false;
            others = false;
        } else {
            mine = false;
            shared = true;
            others = false;
        }
    }

    /**
     * Get @taskList from database 
     */
    public void fetchList() {
        taskList = new LinkedList<ETask>();
        
        try {
            ETaskController et = new ETaskController();
            
            if (mine) {
                List<ETask> myTasks = et.getTasks(UserManager.getCurrentUser());
                myTasks.removeAll(taskList); taskList.addAll(myTasks);
            }
            
            if (shared) {
                List<ETask> sharedTasks = et.getSharedTasks();
                sharedTasks.removeAll(taskList); taskList.addAll(sharedTasks);
            }
            
            if (others) {
                List<ETask> allTasks = et.getTasks();
                allTasks.removeAll(taskList); taskList.addAll(allTasks);
            }
        }
        catch (Exception e) {        
            ErrorBean.printStackTrace(e);
        }
        
        sort(sort, sortDir);
    }
    
    /**
     * Filter @taskList and create @filteredTaskList
     */
    public void filterList() {
        setFilteredTaskList(new LinkedList<ETask>());
        
        if (getTaskList() != null)
            for (ETask task : getTaskList()) {
                if (task.getModel() == null) {
                    ErrorBean.printStackTrace(new Exception());
                    return;
                }

                if ((getTaskNameFilter() == null || task.getName().toLowerCase().contains(getTaskNameFilter().toLowerCase())) 
                        &&
                    (getModelNameFilter() == null || task.getModel().getName().toLowerCase().contains(getModelNameFilter()))) {
                    filteredTaskList.add(task);
                }
            }
            
        setPageDefault();
    }
    
    /**
     * Get part of filteredTaskList
     * @return - list of tasks to be displayed
     */
    public List<ETask> getList() {
        return getFilteredTaskList().subList(getPage()*getRows(), Math.min((getPage() + 1)*getRows() - 1, getFilteredTaskList().size()));
    }
    
    /**
     * Is current user allowed to delete tasks from database?
     * @return - true or false
     */
    public boolean isDeleteAllowed() {
        return !isShared() || !UserManager.getUserStatus().equals(UserManager.GUEST_STATUS);
    }
    
    /**
     * Removes task from database
     * @return - navigation string
     */
    public String deleteTask() {
        FacesContext fc = FacesContext.getCurrentInstance();
        String taskId = fc.getExternalContext().getRequestParameterMap().get("taskID");
        
        try {
            ETaskController tc = new ETaskController();
            tc.removeTask(Integer.parseInt(taskId));
        } catch (Exception e) {
            ErrorBean.printStackTrace(e);
            return "null";
        }
        
        return "taskList";
    }
    
    /**
     * Table navigation:
     */
    
    public void Rows() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc.getExternalContext().getRequestParameterMap().containsKey("rows")) {
            rows = (int) Integer.parseInt((String) fc.getExternalContext().getRequestParameterMap().get("rows"));
        }
    }
    
    public void firstPage() {
        setPage(0);
    }
    
    public void prevPage() {
        if (getPage() > 0) {
            setPage(getPage() - 1);
        }
    }
    
    public String getResultsString() {
        if (getFilteredTaskList().size() > 0)
            return  Integer.toString(getPage()*getRows() + ((getFilteredTaskList().size() > 0) ? 1 : 0)) +
                    " .. " +
                    Integer.toString(Math.min((getPage() + 1)*getRows(), getFilteredTaskList().size())) +
                    " of " +
                    Integer.toString(getFilteredTaskList().size());
        else
            return "No results found.";
    }
    
    public void nextPage() {
        if ((getPage() + 1)*getRows() < getFilteredTaskList().size()) {
            setPage(getPage() + 1);
        }
    }
    
    public void lastPage() {
        int n = getFilteredTaskList().size();
        if (n > 0 && n % getRows() == 0) {
            setPage(n / getRows() - 1);
        } else {
            setPage(n / getRows());
        }
    }
    
    /**
     * Table sorting:
     */
    
    public void sortAsc() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc.getExternalContext().getRequestParameterMap().containsKey("sort")) {
            sort((int) Integer.parseInt((String) fc.getExternalContext().getRequestParameterMap().get("sort")), true);
        }
    }
    
    public void sortDesc() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc.getExternalContext().getRequestParameterMap().containsKey("sort")) {
            sort((int) Integer.parseInt((String) fc.getExternalContext().getRequestParameterMap().get("sort")), false);
        }
    }
    
    private void sort(int sort, boolean asc) {
        if (sort == byName) {
            setSort(sort); setSortDir(asc);
            if (getTaskList() != null) {
                Collections.sort(taskList, new TaskNameComparator());
                if (!asc) Collections.reverse(taskList);
            }
        } else if (sort == byModel) {
            setSort(sort); setSortDir(asc);
            if (getTaskList() != null) {
                Collections.sort(taskList, new TaskModelComparator());
                if (!asc) Collections.reverse(taskList);
            } 
        } else if (sort == byDate) {
            setSort(sort); setSortDir(asc);
            if (getTaskList() != null) {
                Collections.sort(taskList, new TaskSubmissionDateComparator());
                if (!asc) Collections.reverse(taskList);
            }
        } else if (sort == byStatus) {
            setSort(sort); setSortDir(asc);
            if (getTaskList() != null) {
                Collections.sort(taskList, new TaskStatusComparator());
                if (!asc) Collections.reverse(taskList);
            }
        }
        else if (sort == byMethod) {
            setSort(sort); setSortDir(asc);
            if (getTaskList() != null) {
                Collections.sort(taskList, new TaskMethodComparator());
                if (!asc) Collections.reverse(taskList);
            }
        }
        
        filterList();
    }
    
    /**
     * Setters and getters:
     */
    
    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }
    
    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public boolean isOthers() {
        return others;
    }

    public void setOthers(boolean others) {
        this.others = others;
    }
    
    public String getTaskNameFilter() {
        return taskNameFilter;
    }

    public void setTaskNameFilter(String taskNameFilter) {
        this.taskNameFilter = taskNameFilter;
    }
    
    public String getModelNameFilter() {
        return modelNameFilter;
    }

    public void setModelNameFilter(String modelNameFilter) {
        this.modelNameFilter = modelNameFilter;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
    
    private void setSortDefault() {
        setSort(0);
    }
    
    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }
    
    private void setRowsDefault() {
        setRows(30);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
    
    private void setPageDefault() {
        setPage(0);
    }
    
    public List<ETask> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<ETask> taskList) {
        this.taskList = taskList;
    }

    public List<ETask> getFilteredTaskList() {
        return filteredTaskList;
    }

    public void setFilteredTaskList(List<ETask> filteredTaskList) {
        this.filteredTaskList = filteredTaskList;
    }

    public boolean getSortDir() {
        return sortDir;
    }

    public void setSortDir(boolean sortDir) {
        this.sortDir = sortDir;
    }
    
    private void setSortDirDefault() {
        setSortDir(true);
    }    
}
