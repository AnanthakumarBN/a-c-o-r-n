/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.userManagement;

import javax.faces.event.PhaseListener;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;
import acorn.configuration.AcornConfiguration;

/**
 *
 * @author kuba
 */
public class AuthenticationPhaseListener implements PhaseListener {

    public void afterPhase(PhaseEvent event) {

        FacesContext context = event.getFacesContext();

        String status, requestedLevel, path;

        path = getRequestPath(context);
        status = UserManager.getUserStatus();

        if (!path.endsWith(".jsf")) {
            context.responseComplete();
            context.getApplication().
                    getNavigationHandler().handleNavigation(context,
                    null,
                    "login");
        }

        requestedLevel = AcornConfiguration.getSecurityLevel(path);

        if (requestedLevel.equals("guest")) {
            return;
        }

        /* we are requesting a non-public page */

        if (status.equals("guest")) /* not logged in */ {
            /* goto login page */
            context.responseComplete();
            context.getApplication().
                    getNavigationHandler().handleNavigation(context,
                    null,
                    "login");
        }

        /* logged in */
        if (requestedLevel.equals("admin")) {
            if ("admin".equals(status)) {
                return;
            } else {
                context.responseComplete();
                context.getApplication().
                        getNavigationHandler().handleNavigation(context,
                        null,
                        "login");
            }
        }
    }

    public void beforePhase(PhaseEvent event) {
    }

    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

    private String getRequestPath(FacesContext context) {
        ExternalContext extContext = context.getExternalContext();
        return extContext.getRequestServletPath();
    }
}
