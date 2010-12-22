/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package acorn.userManagement;

import acorn.db.EUser;
import acorn.db.EUserController;
import acorn.errorHandling.ErrorBean;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.faces.context.FacesContext;

/**
 *
 * @author lukasz
 */

public class UserListBean {    
    private int displayRowMax;
    private List<UserListRow> users;
    private List<UserListRow> filteredUsers;
    private String userNameFilter;
    private String userSurnameFilter;
    private String userLoginFilter;
    private int start;
    
    private boolean sortUp;
    private String sortComparator;
    
    /** Creates a new instance of UserListBean */
    public UserListBean() {
        sortUp = false;
        sortComparator = "Name";
    }
    
    public String getTitle()
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        displayRowMax = Integer.parseInt((String) 
                fc.getExternalContext().getInitParameter("displayRowMax"));
        start = 0;
        fetchAndFilterList();
        return "Users list";
    }
    
    public void checkActivateUser(EUser user) {
        if (!user.getStatus().equals(EUser.statusInactive)) 
            throw new IllegalArgumentException(
                    "operation not permited for user id = " 
                    + user.getId().toString());
    }
    public void checkUnbanUser(EUser user) {
        if (!user.getStatus().equals(EUser.statusBanned)) 
            throw new IllegalArgumentException(
                    "operation not permited for user id = " 
                    + user.getId().toString());
    }
    public void checkBanUser(EUser user) {
        if (user.getStatus().equals(EUser.statusBanned) ||
               user.getStatus().equals(EUser.statusAdmin)) 
            throw new IllegalArgumentException(
                    "operation not permited for user id = " 
                    + user.getId().toString());
    }
    public void checkMakeAdmin(EUser user) {
        if (!user.getStatus().equals(EUser.statusNormal)) 
            throw new IllegalArgumentException(
                    "operation not permited for user id = " 
                    + user.getId().toString());
    }
    public void checkMakeUser(EUser user) {
        if (!user.getStatus().equals(EUser.statusAdmin) ||
              (user.getId() == UserManager.getCurrentUser().getId())) 
            throw new IllegalArgumentException(
                    "operation not permited for user id = " 
                    + user.getId().toString());
    }
    public void checkDelete(EUser user) {
        if (user.getStatus().equals(EUser.statusAdmin) ||
               (user.getId() == UserManager.getCurrentUser().getId())) 
            throw new IllegalArgumentException(
                    "operation not permited for user id = " 
                    + user.getId().toString());
    }
    
    public String activateUser() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc.getExternalContext().getRequestParameterMap().containsKey("userID")) {
            try {
                Integer id = Integer.parseInt((String) fc.getExternalContext().
                    getRequestParameterMap().get("userID"));
                
                EUserController uc = new EUserController();
                EUser u = uc.getUser(id);
                checkActivateUser(u);
                u.setStatus(EUser.statusNormal);
                uc.mergeUser(u);
                        
                fetchAndFilterList();
                return "done";
            } catch (Exception e) { ErrorBean.printStackTrace(e); return "error"; }
        } 
        else return "error";
    }
    public String unbanUser() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc.getExternalContext().getRequestParameterMap().containsKey("userID")) {
            try {
                Integer id = Integer.parseInt((String) fc.getExternalContext().
                    getRequestParameterMap().get("userID"));
                
                EUserController uc = new EUserController();
                EUser u = uc.getUser(id);
                checkUnbanUser(u);
                u.setStatus(EUser.statusNormal);
                uc.mergeUser(u);
                
                fetchAndFilterList();
                return "done";
            } catch (Exception e) { ErrorBean.printStackTrace(e); return "error"; }
        } 
        else return "error";
    }
    public String banUser() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc.getExternalContext().getRequestParameterMap().containsKey("userID")) {
            try {
                Integer id = Integer.parseInt((String) fc.getExternalContext().
                    getRequestParameterMap().get("userID"));
                EUserController uc = new EUserController();
                EUser u = uc.getUser(id);
                checkBanUser(u);
                u.setStatus(EUser.statusBanned);
                uc.mergeUser(u);                

                fetchAndFilterList();
                return "done";
            } catch (Exception e) { ErrorBean.printStackTrace(e); return "error"; }
        } 
        else return "error";
    }
    public String makeAdmin() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc.getExternalContext().getRequestParameterMap().containsKey("userID")) {
            try {
                Integer id = Integer.parseInt((String) fc.getExternalContext().
                    getRequestParameterMap().get("userID"));
                
                EUserController uc = new EUserController();
                EUser u = uc.getUser(id);
                checkMakeAdmin(u);
                u.setStatus(EUser.statusAdmin);
                uc.mergeUser(u);

                fetchAndFilterList();
                return "done";
            } catch (Exception e) { ErrorBean.printStackTrace(e); return "error"; }
        } 
        else return "error";
    }
    public String makeUser() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc.getExternalContext().getRequestParameterMap().containsKey("userID")) {
            try {
                Integer id = Integer.parseInt((String) fc.getExternalContext().
                    getRequestParameterMap().get("userID"));
                
                EUserController uc = new EUserController();
                EUser u = uc.getUser(id);
                checkMakeUser(u);
                u.setStatus(EUser.statusNormal);
                uc.mergeUser(u);

                fetchAndFilterList();
                return "done";
            } catch (Exception e) { ErrorBean.printStackTrace(e); return "error"; }
        } 
        else return "error";
    }
    
    public String deleteUser() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc.getExternalContext().getRequestParameterMap().containsKey("userID")) {
            try {
                Integer id = Integer.parseInt((String) fc.getExternalContext().
                    getRequestParameterMap().get("userID"));
                
                EUserController uc = new EUserController();
                uc.removeUser(id);

                fetchAndFilterList();
                return "userDeleted";
            } catch (Exception e) { ErrorBean.printStackTrace(e); return "error"; }
        } 
        else return "error";
    }
    
    private List<UserListRow> fetchList()
    {
        try {
            EUserController uc = new EUserController();
            List<EUser> eusers = uc.getUsers();
            List<UserListRow> ur = new LinkedList<UserListRow>();
            EUser current = UserManager.getCurrentUser();
            for(EUser u : eusers) ur.add(new UserListRow(u, current));            
            users = ur;
            return users;
        } catch (Exception e) { ErrorBean.printStackTrace(e); return null; }
    }
    
    public String filterList()
    {
        filteredUsers = new LinkedList();
        for(UserListRow row : users)
            if (userNameFilter == null || row.getName().toLowerCase().contains(userNameFilter.toLowerCase()))
            if (userSurnameFilter == null || row.getSurname().toLowerCase().contains(userSurnameFilter.toLowerCase()))
            if (userLoginFilter == null || row.getLogin().toLowerCase().contains(userLoginFilter.toLowerCase()))
                filteredUsers.add(row);
        
        return null;
    }
    
    public String fetchAndFilterList()
    {
        fetchList();
        return filterList();
    }
    
    public List<UserListRow> getList()
    {
        return filteredUsers.subList(
                java.lang.Math.max(0, java.lang.Math.min(start, filteredUsers.size())),
                java.lang.Math.max(0, java.lang.Math.min(start + displayRowMax, filteredUsers.size())));
    }
    
    public String firstPage() {
        start = 0;
        return null;
    }
    
    public String nextPage() {
        start += displayRowMax;
        if (start >= filteredUsers.size()) 
            start -= displayRowMax;
        return null;
    }

    public String prevPage() {
        start -= displayRowMax;
        if (start < 0) start = 0;
        return null;
    }

    public String lastPage() {
        int n = filteredUsers.size();
        if (n > 0 && n % displayRowMax == 0) {
            start = (n / displayRowMax - 1) * displayRowMax;
        } else {
            start = (n / displayRowMax) * displayRowMax;
        }

        return null;
    }
    
    public String rows() {
        FacesContext fc = FacesContext.getCurrentInstance();
        displayRowMax = Integer.parseInt((String) fc.getExternalContext().getInitParameter("displayRowMax"));
        if (fc.getExternalContext().getRequestParameterMap().containsKey("rows")) {
            displayRowMax = (int) Integer.parseInt((String) fc.getExternalContext().
                    getRequestParameterMap().get("rows"));
        }
        return null;
    }
    
    public String getResultsString() {
        Integer from = new Integer(java.lang.Math.max(0, java.lang.Math.min(
                start, filteredUsers.size())) + 1);
        Integer to = new Integer(java.lang.Math.max(0, java.lang.Math.min(
                start + displayRowMax, filteredUsers.size())));
        Integer total = new Integer(filteredUsers.size());
        if (total > 0) 
            return from.toString() + " .. " + to.toString() + " of " + total.toString();
        else 
            return "No results found.";
    }
    
    public String getNameFilter() {
        return userNameFilter;
    }

    public void setNameFilter(String in) {
        if (!in.equals(userNameFilter)) start = 0;
        userNameFilter = in;
    }
    
    public String getSurnameFilter() {
        return userSurnameFilter;
    }

    public void setSurnameFilter(String in) {
        if (!in.equals(userSurnameFilter)) start = 0;
        userSurnameFilter = in;
    }
    
    public String getLoginFilter() {
        return userLoginFilter;
    }

    public void setLoginFilter(String in) {
        if (!in.equals(userLoginFilter)) start = 0;
        userLoginFilter = in;
    }
    
    public String sort()
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc.getExternalContext().getRequestParameterMap().
                containsKey("comparator")) {
            sortComparator = fc.getExternalContext().
                    getRequestParameterMap().get("comparator");
        }
        else sortComparator = "Name";
        if (fc.getExternalContext().getRequestParameterMap().containsKey("up")) {
            sortUp = fc.getExternalContext().getRequestParameterMap().
                    get("up").equalsIgnoreCase("True");
        }
        else sortUp = false;
        Collections.sort(filteredUsers, UserListRow.getComparator(sortComparator, sortUp));
        return null;
    }
    
    public Boolean getSortUp()
    {
        return sortUp;
    }
    
    public String getSortComparator()
    {
        return sortComparator;
    }
}
