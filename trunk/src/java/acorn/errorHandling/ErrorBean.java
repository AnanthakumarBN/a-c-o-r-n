package acorn.errorHandling;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 * ErrorBean
 * @author lukasz
 */

public class ErrorBean {

    /** Creates a new instance of ErrorBean */
    public ErrorBean() {
    }
    
    /**
     * Print error message and stack trace of exception ecx
     * @param ecx   - exception,
     * @param whr   - site on page where error message should be printed,
     * @param msg   - error message.
     */
    public static void printMessage(Exception exc, String whr, String msg) {
        FacesContext        context = FacesContext.getCurrentInstance();
        HttpServletRequest  request = (HttpServletRequest)context.getExternalContext().getRequest();
        
        FacesMessage message = new FacesMessage(
            FacesMessage.SEVERITY_FATAL,
            exc.getClass().getName(), 
            msg);
        
        context.addMessage(whr, message);
        
        exc.printStackTrace(System.err);
    }
    
    /**
     * Print stack trace of exception e
     * @param exc - exception
     */
    public static void printStackTrace(Exception exc) {
        exc.printStackTrace(System.err);
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.getApplication().
                    getNavigationHandler().handleNavigation(fc,
                    null,
                    "error");
    }

}
