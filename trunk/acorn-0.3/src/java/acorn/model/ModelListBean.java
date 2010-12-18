package acorn.model;

import javax.faces.context.FacesContext;
import acorn.db.*;
import acorn.errorHandling.ErrorBean;
import acorn.userManagement.*;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import acorn.db.EModelController;

/**
 *
 * @author dl236088
 */
public class ModelListBean {

    private List<ModelRow> privateList;
    private List<ModelRow> sharedList;
    private List<ModelRow> otherList;
    private List<ModelRow> filteredList;
    private int start;
    private int displayRowMax;
    private String modelNameFilter;
    private Boolean sortUp;
    private String sortComparator;
    private Boolean showPrivateFilter;
    private Boolean showSharedFilter;
    private Boolean showOtherFilter;

    public ModelListBean() {
        super();
        sortUp = true;
        sortComparator = "Name";
        showPrivateFilter = true;
        showSharedFilter = true;
        showOtherFilter = true;
        modelNameFilter = new String("");
    }

    public String getTitle() {
        FacesContext fc = FacesContext.getCurrentInstance();
        displayRowMax = Integer.parseInt((String) fc.getExternalContext().getInitParameter("displayRowMax"));
        start = 0;
        privateList = new LinkedList<ModelRow>();
        sharedList = new LinkedList<ModelRow>();
        otherList = new LinkedList<ModelRow>();
        fetchAndFilterList();
        return "Model List";
    }

    private void fetchList() {
        EModelController emc = new EModelController();

        start = 0;
        try {
            if (UserManager.getIsGuestS()) {
                List<ModelRow> newSharedList = new LinkedList<ModelRow>();
                List<EModel> resShared = emc.getModelsShared();

                for (EModel m : resShared) {
                    newSharedList.add(new ModelRow(m, m.getOwner() == null));
                }
                privateList = new LinkedList<ModelRow>();
                sharedList = newSharedList;
                otherList = new LinkedList<ModelRow>();
                showOtherFilter = false;
                showPrivateFilter = false;
                showSharedFilter = true;
                return;
            } else if (UserManager.getIsAdminS()) {
                EUser user = UserManager.getCurrentUser();
                List<ModelRow> newPrivateList = new LinkedList<ModelRow>();
                List<ModelRow> newSharedList = new LinkedList<ModelRow>();
                List<ModelRow> newOtherList = new LinkedList<ModelRow>();

                List<EModel> resOther = emc.getModels();

                for (EModel m : resOther) {
                    if (m.getOwner() == null) {//to avoid NullPointerException later
                        newSharedList.add(new ModelRow(m, true));
                    } else if (m.getOwner().equals(user)) {
                        newPrivateList.add(new ModelRow(m, true));
                    } else if (m.isShared()) {//shared but not owned by the user (we want to avoid having models in two lists)
                        newSharedList.add(new ModelRow(m, true));
                    } else {
                        newOtherList.add(new ModelRow(m, true));
                    }
                }
                privateList = newPrivateList;
                sharedList = newSharedList;
                otherList = newOtherList;
                showOtherFilter = true;
                showPrivateFilter = true;
                showSharedFilter = true;
            } else {//normal user
                EUser user = UserManager.getCurrentUser();
                List<ModelRow> newPrivateList = new LinkedList<ModelRow>();
                List<ModelRow> newSharedList = new LinkedList<ModelRow>();

                List<EModel> resPrivate = emc.getModels(user);
                for (EModel m : resPrivate) {
                    newPrivateList.add(new ModelRow(m, true));
                }

                List<EModel> resShared = emc.getModelsShared();

                for (EModel m : resShared) {
                    if (m.getOwner() == null) {//to avoid NullPointerException later
                        newSharedList.add(new ModelRow(m, false));
                    } else if (m.getOwner().equals(user)) {
                        newPrivateList.add(new ModelRow(m, true));
                    } else { //if (m.isShared()) {//shared but not owned by the user (we want to avoid having models in two lists)
                        newSharedList.add(new ModelRow(m, false));
                    }
                }
                privateList = newPrivateList;
                sharedList = newSharedList;
                otherList = new LinkedList<ModelRow>();
                showOtherFilter = false;
                showPrivateFilter = true;
                showSharedFilter = true;
            }
        } catch (Exception e) {
            privateList = new LinkedList<ModelRow>();
            sharedList = new LinkedList<ModelRow>();
            otherList = new LinkedList<ModelRow>();
            ErrorBean.printStackTrace(e);
            return;
        }
        return;
    }

    private List<ModelRow> getFilteredList() {
        if (filteredList == null) {
            fetchAndFilterList();
        }
        return filteredList;
    }

    public List<ModelRow> getList() {
        return this.getFilteredList().subList(
                java.lang.Math.max(0, java.lang.Math.min(start, getFilteredList().size())),
                java.lang.Math.max(0, java.lang.Math.min(start + displayRowMax, getFilteredList().size())));
    }

    public void filterList() {
        filteredList = new LinkedList();
        if (showOtherFilter) {
            for (ModelRow row : otherList) {
                if (getModelNameFilter() == null ||
                        row.getName().toLowerCase().
                        contains(getModelNameFilter().toLowerCase())) {
                    filteredList.add(row);
                }
            }
        }
        if (showPrivateFilter) {
            for (ModelRow row : privateList) {
                if (getModelNameFilter() == null ||
                        row.getName().toLowerCase().
                        contains(getModelNameFilter().toLowerCase())) {
                    filteredList.add(row);
                }
            }
        }
        if (showSharedFilter) {
            for (ModelRow row : sharedList) {
                if (getModelNameFilter() == null ||
                        row.getName().toLowerCase().
                        contains(getModelNameFilter().toLowerCase())) {
                    if (!filteredList.contains(row)) {
                        filteredList.add(row);
                    }
                }
            }
        }

        Collections.sort(filteredList, ModelRow.getComparator(sortComparator, sortUp));
    }

    public void fetchAndFilterList() {
        fetchList();
        filterList();
    }

    public String firstPage() {
        start = 0;
        return null;
    }

    public String nextPage() {
        start += displayRowMax;
        if (start >= getFilteredList().size()) {
            start -= displayRowMax;
        }

        return null;
    }

    public String prevPage() {
        start -= displayRowMax;
        if (start < 0) {
            start = 0;
        }

        return null;
    }

    public String lastPage() {
        int n = getFilteredList().size();
        if (n > 0 && n % displayRowMax == 0) {
            start = (n / displayRowMax - 1) * displayRowMax;
        } else {
            start = (n / displayRowMax) * displayRowMax;
        }

        return null;
    }

    public String Rows() {
        FacesContext fc = FacesContext.getCurrentInstance();
        displayRowMax =
                Integer.parseInt((String) fc.getExternalContext().getInitParameter("displayRowMax"));
        if (fc.getExternalContext().getRequestParameterMap().containsKey("rows")) {
            displayRowMax = (int) Integer.parseInt((String) fc.getExternalContext().
                    getRequestParameterMap().get("rows"));
        }

        return null;
    }

    public String getResultsString() {

        Integer from = new Integer(java.lang.Math.max(0, java.lang.Math.min(
                start,
                getFilteredList().size())) + 1);
        Integer to = new Integer(java.lang.Math.max(0, java.lang.Math.min(
                start + displayRowMax,
                getFilteredList().size())));
        Integer total = new Integer(getFilteredList().size());

        if (total > 0) {
            return from.toString() +
                    " .. " + to.toString() +
                    " of " + total.toString();
        } else {
            return "No results found.";
        }

    }

    public String getModelNameFilter() {
        return modelNameFilter;
    }

    public void setModelNameFilter(String modelNameFilter) {
        this.modelNameFilter = modelNameFilter;
    }

    public String deleteModel() {
        FacesContext fc = FacesContext.getCurrentInstance();

        if (fc.getExternalContext().getRequestParameterMap().containsKey("modelID")) {
            Integer modelId = Integer.parseInt((String) fc.getExternalContext().
                    getRequestParameterMap().
                    get("modelID"));

            try {
                EModelController mc = new EModelController();
                mc.removeModel(modelId);
            } catch (Exception e) {
                ErrorBean.printStackTrace(e);
            }

        }

        fetchAndFilterList();
        return null;
    }

    public String sort() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc.getExternalContext().getRequestParameterMap().
                containsKey("comparator")) {
            sortComparator = fc.getExternalContext().
                    getRequestParameterMap().get("comparator");
        } else {
            sortComparator = "Name";
        }

        if (fc.getExternalContext().getRequestParameterMap().containsKey("up")) {
            sortUp = fc.getExternalContext().getRequestParameterMap().
                    get("up").equalsIgnoreCase("True");
        } else {
            sortUp = false;
        }

        Collections.sort(filteredList, ModelRow.getComparator(sortComparator, sortUp));
        return null;
    }

    public Boolean getSortUp() {
        return sortUp;
    }

    public String getSortComparator() {
        return sortComparator;
    }

    public Boolean getShowSharedFilter() {
        return showSharedFilter;
    }

    public void setShowSharedFilter(Boolean sharedFilter) {
        this.showSharedFilter = sharedFilter;
    }

    public Boolean getShowPrivateFilter() {
        return showPrivateFilter;
    }

    public void setShowPrivateFilter(Boolean showPrivateFilter) {
        this.showPrivateFilter = showPrivateFilter;
    }

    public Boolean getShowOtherFilter() {
        return showOtherFilter;
    }

    public void setShowOtherFilter(Boolean showOthersFilter) {
        this.showOtherFilter = showOthersFilter;
    }
}
