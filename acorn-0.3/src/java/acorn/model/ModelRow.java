/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.model;

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
public class ModelRow {

    private EModel model;
    private Boolean canDelete;

    ModelRow(EModel model, Boolean canDelete) {
        super();
        this.model = model;
        this.canDelete = canDelete;
    }
    
    @Override
    public int hashCode()
    {
        return model.getId();
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof ModelRow) return ((ModelRow)o).model.getId().equals(model.getId());
        else return false;
    }

    public String getId() {
        return model.getId().toString();
    }

    public String getName() {
        return model.getName();
    }

    public Date getDate() {
        return model.getDate();
    }

    public Date getLastChange() {
        return model.getLastChange();
    }

    public String getReadOnly() {
        return model.getReadOnly() ? "True" : "False";
    }
    
    public Boolean getCanDelete() {
        return canDelete;
    }
    
    public static Comparator<ModelRow> getComparator(String comparator, boolean up) {
        final Method m;
        try {
            m = ModelRow.class.getMethod("get" + comparator, new Class[]{});
        
            if (up) return new Comparator<ModelRow>() {
                public int compare(ModelRow t1, ModelRow t2) {
                    try {
                        Comparable d1 = (Comparable) m.invoke(t1, new Object[] {});
                        Comparable d2 = (Comparable) m.invoke(t2, new Object[] {});
                        return d1.compareTo(d2);
                    } catch (Exception e) {ErrorBean.printStackTrace(e); return 0; }
                }};
            else return new Comparator<ModelRow>() {   
                public int compare(ModelRow t1, ModelRow t2) {
                    try {
                        Comparable d1 = (Comparable) m.invoke(t1, new Object[] {});
                        Comparable d2 = (Comparable) m.invoke(t2, new Object[] {});
                        return - d1.compareTo(d2);
                    } catch (Exception e) {ErrorBean.printStackTrace(e); return 0; }
            }};
        } catch (Exception e) { 
            ErrorBean.printStackTrace(e); 
            return null; 
        }
    }
}
