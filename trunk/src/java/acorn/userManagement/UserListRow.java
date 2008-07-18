package acorn.userManagement;

import acorn.db.*;
import java.util.Date;
import java.lang.Boolean;
import java.util.Comparator;
import java.lang.Comparable;
import java.lang.reflect.Method;
import acorn.errorHandling.ErrorBean;

/**
 *
 * @author dl236088
 */
public class UserListRow {

    private EUser user;
    private EUser me;

    UserListRow(EUser user, EUser me) {
        super();
        this.user = user;
        this.me = me;
    }
    
    public String getName() {
        return user.getName();
    }

    public String getSurname() {
        return user.getSurname();
    }

    public String getLogin() {
        return user.getLogin();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getStatus() {
        return user.getStatus();
    }

    public String getInstitution() {
        return user.getInstitution();
    }
    
    public Date getDate() {
        return user.getDate();
    }

    public static Comparator<UserListRow> getComparator(String comparator, boolean up) {
        final Method m;
        try {
            m = UserListRow.class.getMethod("get" + comparator, new Class[]{});
        
            if (up) return new Comparator<UserListRow>() {
                public int compare(UserListRow t1, UserListRow t2) {
                    try {
                        Comparable d1 = (Comparable) m.invoke(t1, new Object[] {});
                        Comparable d2 = (Comparable) m.invoke(t2, new Object[] {});
                        return d1.compareTo(d2);
                    } catch (Exception e) { ErrorBean.printStackTrace(e); return 0; }
                }};
            else return new Comparator<UserListRow>() {           
                public int compare(UserListRow t1, UserListRow t2) {
                    try {
                        Comparable d1 = (Comparable) m.invoke(t1, new Object[] {});
                        Comparable d2 = (Comparable) m.invoke(t2, new Object[] {});
                        return - d1.compareTo(d2);
                    } catch (Exception e) { ErrorBean.printStackTrace(e); return 0; }
            }};
        } catch (Exception e) { 
            ErrorBean.printStackTrace(e); 
            return null; 
        }
    }

    public EUser getUser() {
        return user;
    }
    
    public Integer getId() {
        return user.getId();
    }

    public void setUser(EUser user) {
        this.user = user;
    }
    
    public boolean getMe()
    {
        if (me == null) {
            return true;
        } else {
            return me.getId().equals(user.getId());
        }
    }
    
    public Boolean getEnableActivateUser()
    {
        return user.getStatus().equals(EUser.statusInactive);
    }
    public Boolean getEnableUnbanUser()
    {
        return user.getStatus().equals(EUser.statusBanned);
    }
    public Boolean getEnableBanUser()
    {
        return (!user.getStatus().equals(EUser.statusBanned)) && 
               (!user.getStatus().equals(EUser.statusAdmin));
    }
    public Boolean getEnableMakeAdmin()
    {
        return (user.getStatus().equals(EUser.statusNormal));
    }
    public Boolean getEnableMakeUser()
    {
        return user.getStatus().equals(EUser.statusAdmin) && !getMe();
    }
    public Boolean getEnableDeleteUser()
    {
        return !user.getStatus().equals(EUser.statusAdmin) && !getMe();
    }
}

