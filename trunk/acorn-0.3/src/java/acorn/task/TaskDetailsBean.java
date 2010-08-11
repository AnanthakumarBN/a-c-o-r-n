package acorn.task;

import acorn.db.EFbaData;
import acorn.db.EKgeneData;
import acorn.db.EMethod;
import acorn.db.EModel;
import acorn.db.EModelController;
import acorn.db.ERscanData;
import acorn.db.ETask;
import acorn.db.ETaskController;
import acorn.db.EUser;
import acorn.db.EVisualization;
import acorn.db.EVisualizationController;
import acorn.drawing.DrawingBean;
import acorn.errorHandling.ErrorBean;
import acorn.exception.DotFileException;
import acorn.model.TaskBeanData;
import acorn.model.Visualization;
import acorn.model.VisualizationComparator;
import acorn.userManagement.UserManager;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 * TaskDetailsBean
 * @author lukasz
 */
public class TaskDetailsBean {

    public static Boolean modelSecurityCheck(EModel m) {
        if (m == null) {
            return false;
        }
        if (m.isShared()) {
            return true;
        }
        EUser user = UserManager.getCurrentUser();
        if (user == null) {
            return false;
        } else if (user.getStatus().equals(EUser.statusAdmin)) {
            return true;
        } else {
            return m.getOwner().getId().equals(user.getId());
        }
    }
    /* Current task */
    private ETask task;
    private Map<Integer, TaskDetailsBeanData> data;
    private int displayVisualizationsRowMax;
    private int dataCountMax;
    private int displayRowMax;
    private String pathForServlet;
    private final static String PREFIX = "/picture";

    public TaskDetailsBean() {
        task = null;
        displayVisualizationsRowMax = displayRowMax = 50;
        dataCountMax = 20;
        data = new TreeMap();
    }

    /**
     * HACK - this method should be invoked at the very beginning
     * @return - nothing
     */
    public String getInit() {
        fetchTask();
        Integer id = getModelID();
        initData(id);

        return null;
    }

    public boolean initData(int id) {

        if ((data.containsKey(id)) && (data.get(id).visualizations == null)) {
            data.get(id).visualizations =
                    justFetchVisualizations(data.get(id).getModel());
            data.get(id).touch();
            return true;
        }

        while (data.size() > dataCountMax - 1) {
            Date a = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, -60);
            TaskDetailsBeanData oldest = Collections.min(data.values(),
                    new Comparator<Object>() {

                        public int compare(Object a, Object b) {
                            return ((TaskBeanData) a).lastUse.compareTo(
                                    ((TaskBeanData) b).lastUse);
                        }
                    });
            if (oldest.lastUse.compareTo(calendar.getTime()) < 0) {
                data.remove(oldest.getModel().getId());
            }
        }


        EModel m = null;

        try {
            EModelController mc = new EModelController();
            m = mc.getModel(id);
        } catch (Exception e) {
            /* there is no ErrorBean call here because Exception means
             * that there's no such model so we should return false */
            return false;
        }
        TaskDetailsBeanData d = new TaskDetailsBeanData(m);
        d.filteredVisualizations = d.visualizations = justFetchVisualizations(m);
        d.visualizationsStart = 0;
        data.put(id, d);
        return true;
    }

    /**
     * 
     * @return path for servlet to the jpg drawing
     */
    public String getPathForServlet() {
        return pathForServlet;
    }



    /** 
     * Fetches visualizations from database
     * @return
     */
    private List<Visualization> justFetchVisualizations(EModel m) {
        try {
            EVisualizationController vc = new EVisualizationController();
            List<EVisualization> res;
            if (UserManager.getIsGuestS()) {
                res=vc.getAncestorVisualizationsShared(m.getId());
            } else if (UserManager.getIsAdminS()) {
                res=vc.getAncestorVisualizationsAll(m.getId());
            } else {//normal user
                res=vc.getAncestorVisualizationsForUser(m.getId(), UserManager.getCurrentUser().getLogin());
            }

            List<Visualization> list = new LinkedList<Visualization>();
            int ii = 0;
            for (EVisualization vis : res) {
                list.add(new Visualization(vis, ii));
                ii++;
            }
            return list;
        } catch (Exception e) {
            ErrorBean.printStackTrace(e);
            return null;
        }
    }

    /** Get current model ID form the context (address or AJAX request)
     * see getIsModelValid and deleteModel after changing
     * @return
     */
    public int getModelID() {
        if (task != null) {
            Integer id = (task.getModel()).getId();
            //initData(id);
            return (int) id;
        } else { // in case of incorrect call
            return 0;
        }
    }

    private List<Visualization> fetchVisualizations() {
        Integer id = getModelID();

        data.get(id).visualizations = justFetchVisualizations(data.get(id).getModel());
        data.get(id).visualizationsStart = 0;
        data.get(id).filteredVisualizations = data.get(id).visualizations;
        return data.get(id).visualizations;
    }

    public String filterVisualizations() {
        Integer id = getModelID();

        data.get(id).filteredVisualizations = new LinkedList();
        for (Visualization row : data.get(id).visualizations) {
            if (data.get(id).visualizationsNameFilter == null ||
                    row.getQualifiedName().toLowerCase().contains(data.get(id).visualizationsNameFilter.toLowerCase())) {
                data.get(id).filteredVisualizations.add(row);
            }
        }
        return "";
    }

    public List<Visualization> getVisualizations() {
        Integer id = getModelID();

        Collections.sort(data.get(id).filteredVisualizations, new VisualizationComparator());

        return data.get(id).filteredVisualizations.subList(
                java.lang.Math.max(0, java.lang.Math.min(data.get(id).visualizationsStart, data.get(id).filteredVisualizations.size())),
                java.lang.Math.max(0, java.lang.Math.min(data.get(id).visualizationsStart + displayVisualizationsRowMax, data.get(id).filteredVisualizations.size())));
    }

    public String fetchAndFilterVisualizations() {
        fetchVisualizations();
        return filterVisualizations();
    }

    public String visualizationsFirstPage() {
        Integer id = getModelID();

        data.get(id).visualizationsStart = 0;
        return null;
    }

    public String visualizationsNextPage() {
        Integer id = getModelID();

        data.get(id).visualizationsStart += displayVisualizationsRowMax;
        if (data.get(id).visualizationsStart >= data.get(id).filteredVisualizations.size()) {
            data.get(id).visualizationsStart -= displayVisualizationsRowMax;
        }
        return null;
    }

    public String visualizationsPrevPage() {
        Integer id = getModelID();

        data.get(id).visualizationsStart -= displayVisualizationsRowMax;
        if (data.get(id).visualizationsStart < 0) {
            data.get(id).visualizationsStart = 0;
        }
        return null;
    }

    public String visualizationsLastPage() {
        Integer id = getModelID();

        int n = data.get(id).filteredVisualizations.size();
        /*nie wystarczy:
         * data.get(id).visualizationsStart = n - displayVisualizationsRowMax;
         * if (data.get(id).visualizationsStart < 0){
         *  data.get(id).visualizationsStart = 0
         * } ????????
         */
        if (n > 0 && n % displayVisualizationsRowMax == 0) {
            data.get(id).visualizationsStart = (n / displayVisualizationsRowMax - 1) * displayVisualizationsRowMax;
        } else {
            data.get(id).visualizationsStart = (n / displayVisualizationsRowMax) * displayVisualizationsRowMax;
        }

        return null;
    }

    public String visualizationsRows() {
        FacesContext fc = FacesContext.getCurrentInstance();
        displayVisualizationsRowMax = Integer.parseInt((String) fc.getExternalContext().getInitParameter("displayRowMax"));
        if (fc.getExternalContext().getRequestParameterMap().containsKey("rows")) {
            displayVisualizationsRowMax = (int) Integer.parseInt((String) fc.getExternalContext().
                    getRequestParameterMap().get("rows"));
        }
        return null;
    }

    public String generateDrawing() throws DotFileException, InterruptedException {
//        int modelId = getModelID();
        Long visId = getSelectedVisualizations();
        FacesContext context = FacesContext.getCurrentInstance();
        if (visId == null) {
            FacesMessage message = new FacesMessage(
                    FacesMessage.SEVERITY_FATAL,
                    "Choose visualization.", null);
            context.addMessage(null, message);
            return null;
        }
        EVisualizationController vc = new EVisualizationController();
        EVisualization v = vc.getVisualizationById(visId);
        fetchTask();

        if (!ETask.statusDone.equals(task.getStatus())) {
            FacesMessage message = new FacesMessage(
                    FacesMessage.SEVERITY_FATAL,
                    "Task is not done. You can't visualize it.", null);
            context.addMessage(null, message);
            return null;
        } else if (!EMethod.fba.equals(task.getMethod().getIdent())) {
            FacesMessage message = new FacesMessage(
                    FacesMessage.SEVERITY_FATAL,
                    "Task should be FBA task. You can't visualize it.", null);
            context.addMessage(null, message);
            return null;
        }
        DrawingBean db = new DrawingBean(v, task);
        try {
            String path = db.draw();
            ((HttpServletRequest) context.getExternalContext().getRequest()).getSession().setAttribute("PICTURE_PATH", path);
            this.pathForServlet =PREFIX+path;
        } catch (DotFileException dfe) {
            FacesMessage message = new FacesMessage(
                    FacesMessage.SEVERITY_FATAL,
                    dfe.getMessage(), null);
            context.addMessage(null, message);
            return null;
        }
        return null;
    }

    public String getVisualizationsResultsString() {
        Integer id = getModelID();

        Integer from = new Integer(java.lang.Math.max(0, java.lang.Math.min(
                data.get(id).visualizationsStart,
                data.get(id).filteredVisualizations.size())) + 1);
        Integer to = new Integer(java.lang.Math.max(0, java.lang.Math.min(
                data.get(id).visualizationsStart + displayVisualizationsRowMax,
                data.get(id).filteredVisualizations.size())));
        Integer total = new Integer(data.get(id).filteredVisualizations.size());
        if (total > 0) {
            return from.toString() +
                    " .. " + to.toString() +
                    " of " + total.toString();
        } else {
            return "No results found.";
        }
    }

    public String getVisualizationNameFilter() {
        Integer id = getModelID();
        if (!data.containsKey(id)) {
            initData(id);
        }
        return data.get(id).visualizationsNameFilter;
    }

    public void setVisualizationNameFilter(String in) {
        Integer id = getModelID();

        if (!in.equals(data.get(id).visualizationsNameFilter)) {
            data.get(id).visualizationsStart = 0;
        }
        data.get(id).visualizationsNameFilter = in;
    }

    public Long getSelectedVisualizations() {
        Integer id = getModelID();

        return data.get(id).getSelectedVisualization();
    }

    public void setSelectedVisualizations(Long in) {
        Integer id = getModelID();

        try {
            data.get(id).setSelectedVisualization(in);
        } catch (Exception e) {
            ErrorBean.printStackTrace(e);
            return;
        }

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
            } catch (Exception e) {
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
        } catch (Exception e) {
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
        } catch (Exception e) {
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
        } catch (Exception e) {
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
        //return UserManager.getCurrentUser() != null && (task.getModel().getOwner().getId().equals(UserManager.getCurrentUser().getId()) || UserManager.getUserStatus().equalsIgnoreCase(EUser.statusAdmin));
        return UserManager.getIsAdminS() ||
               ((UserManager.getCurrentUser() != null) &&
                (task.getModel().getOwner() != null) &&
                (task.getModel().getOwner().getId().equals(UserManager.getCurrentUser().getId()))
               );
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
        return ((EFbaData) (task.getMethodData())).getObjFunctionName();
    }

    /**
     * Get RSCAN's target function
     * @return string with object function name
     */
    public String getRscanCriterion() {
        assert task != null;
        return ((ERscanData) (task.getMethodData())).getObjectFunctionName();
    }

    /**
     * Get KGENE's target function
     * @return string with object function name
     */
    public String getKgeneCriterion() {
        assert task != null;
        return ((EKgeneData) (task.getMethodData())).getObjFunctionName();
    }

    /**
     * Get KGENE's knockout gene's name
     * @return string with gene name
     */
    public String getKgeneGene() {
        assert task != null;
        return (((EKgeneData) (task.getMethodData()))).getGene();
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

    public String getStatusInformation() {
        fetchTask();
        if(task.getStatus().equals(ETask.statusDone)) {
            return "Done";
        }
        else if(task.getStatus().equals(ETask.statusQueued)) {
            return "Queued";
        }
        else if(task.getStatus().equals(ETask.statusInProgress)) {
            return String.valueOf((int)(task.getProgress()*100)) + "% completed";
        } else {
            return "Error";
        }
    }
}
