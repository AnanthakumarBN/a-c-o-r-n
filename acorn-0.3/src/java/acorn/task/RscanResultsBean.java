package acorn.task;

import acorn.db.EProductController;
import acorn.db.EReactantController;
import acorn.db.ETask;
import acorn.db.ETaskController;
import acorn.db.ErscanResultElement;
import acorn.errorHandling.ErrorBean;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import javax.faces.context.FacesContext;

/**
 * RscanResultsBean
 * @author lb235922
 */

public class RscanResultsBean {
    private ETask task;
    
    private String reactionNameFilter;
    private String reactantNameFilter;
    private String productNameFilter;
    
    private int rows;
    private int page;
    
    private List<RscanResultRow> rowList;
    private List<RscanResultRow> filteredRowList;
    
    private Semaphore mutex;
    
    public RscanResultsBean() {
        task = null;
        
        reactionNameFilter = new String();
        reactantNameFilter = new String();
        productNameFilter = new String();
      
        setRowsDefault();
        setPageDefault();

        rowList = new LinkedList<RscanResultRow>();
        filteredRowList = new LinkedList<RscanResultRow>();
        
        mutex = new Semaphore(1);
    }
    
    /**
     * HACK - this method should be invoked at the very beginning
     * @return nothing
     */
    public String getInit() {
        fetchTask();
        fetchList();
        filterList();
        setPageDefault();
        setRowsDefault();
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
     * Get results from database
     */
    private void fetchList() {
        assert task != null;
        
        try {
            mutex.acquire();
            
            rowList = new LinkedList<RscanResultRow>();
            
            for (ErscanResultElement elem : getTask().getErscanResultElementCollection()) {
                if (elem.getReaction().getEReactantCollection().isEmpty()) {
                    /* Look for reactants */
                    EReactantController rc = new EReactantController();
                    elem.getReaction().setEReactantCollection(rc.getReactants(elem.getReaction()));
                }
                
                if (elem.getReaction().getEProductCollection().isEmpty()) {
                    /* Look for products */
                    EProductController pc = new EProductController();
                    elem.getReaction().setEProductCollection(pc.getProducts(elem.getReaction()));
                }
                
                getRowList().add(new RscanResultRow(elem.getReaction().getName(), 
                                                    elem.getStatus(),
                                                    elem.getGrowthRate(),
                                                    elem.getReaction().getDivedReactionFormula()));
            }
            
            mutex.release();
        }
        catch (InterruptedException e) {
            ErrorBean.printStackTrace(e);
        }
        catch (Exception e) {
            mutex.release();
            ErrorBean.printStackTrace(e);
        }
    }
    
    /**
     * Filter rowList
     */
    public void filterList() {
        try {
            mutex.acquire();
            
            setFilteredRowList(new LinkedList<RscanResultRow>());
        
            if (getRowList() != null)
                for (RscanResultRow row : getRowList()) {
                    if ((getReactionNameFilter() == null || row.getReactionName().toLowerCase().contains(getReactionNameFilter().toLowerCase())) 
                            &&
                        (getReactantNameFilter() == null || row.getFormula().split("=")[0].toLowerCase().contains(getReactantNameFilter()))
                            &&
                        (getProductNameFilter() == null || row.getFormula().split("=")[1].toLowerCase().contains(getProductNameFilter()))) {
                        getFilteredRowList().add(row);
                    }
                }
            
            mutex.release();
                
            setPageDefault();
        }
        catch (InterruptedException e) {
            ErrorBean.printStackTrace(e);
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
     * Get part of a filteredRowList
     * @return list of tasks to be displayed
     */
    public List<RscanResultRow> getList() {
        try {
            mutex.acquire();
            List<RscanResultRow> res = getFilteredRowList().subList(getPage()*getRows(), Math.min((getPage() + 1)*getRows() - 1, getFilteredRowList().size()));
            mutex.release();
            return res;
        }
        catch (InterruptedException e) {
            ErrorBean.printStackTrace(e);
            return null;
        }
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
        if (getFilteredRowList().size() > 0)
            return  Integer.toString(getPage()*getRows() + ((getFilteredRowList().size() > 0) ? 1 : 0)) +
                    " .. " +
                    Integer.toString(Math.min((getPage() + 1)*getRows(), getFilteredRowList().size())) +
                    " of " +
                    Integer.toString(getFilteredRowList().size());
        else
            return "No results found.";
    }
    
    public void nextPage() {
        if ((getPage() + 1)*getRows() < getFilteredRowList().size()) {
            setPage(getPage() + 1);
        }
    }
    
    public void lastPage() {
        int n = getFilteredRowList().size();
        if (n > 0 && n % getRows() == 0) {
            setPage(n / getRows() - 1);
        } else {
            setPage(n / getRows());
        }
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

    public String getReactionNameFilter() {
        return reactionNameFilter;
    }

    public void setReactionNameFilter(String reactionNameFilter) {
        this.reactionNameFilter = reactionNameFilter;
    }

    public String getReactantNameFilter() {
        return reactantNameFilter;
    }

    public void setReactantNameFilter(String reactantNameFilter) {
        this.reactantNameFilter = reactantNameFilter;
    }

    public String getProductNameFilter() {
        return productNameFilter;
    }

    public void setProductNameFilter(String productNameFilter) {
        this.productNameFilter = productNameFilter;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }
    
    public void setRowsDefault() {
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

    public List<RscanResultRow> getRowList() {
        return rowList;
    }

    public void setRowList(List<RscanResultRow> rowList) {
        this.rowList = rowList;
    }

    public List<RscanResultRow> getFilteredRowList() {
        return filteredRowList;
    }

    public void setFilteredRowList(List<RscanResultRow> filteredRowList) {
        this.filteredRowList = filteredRowList;
    }

}