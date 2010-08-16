/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package factories;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import org.openide.LifecycleManager;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author markos
 */
public class NDFactory {

    public static NotifyDescriptor getCannotModifyNotifyDescriptor() {
        String msg = "You have opened a shared visualization of another user. You cannot override the original, but you can save it as your own. WARNING: Pay attention to not to override your own visualization that has the same name if such exists.";
        NotifyDescriptor nd = new NotifyDescriptor.Message(msg, NotifyDescriptor.INFORMATION_MESSAGE);
        nd.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (NotifyDescriptor.CLOSED_OPTION.equals(evt.getNewValue())) {
                    LifecycleManager.getDefault().exit();
                }
            }
        });
        return nd;
    }

    private static NotifyDescriptor getErrorNotifyDescriptor(String msg) {
        NotifyDescriptor nd = new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE);
        nd.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (NotifyDescriptor.CLOSED_OPTION.equals(evt.getNewValue())) {
                    LifecycleManager.getDefault().exit();
                }
            }
        });
        return nd;
    }

    public static NotifyDescriptor getNoConnectionError() {
        NotifyDescriptor nd = getErrorNotifyDescriptor(NbBundle.getMessage(NDFactory.class, "NDF.noInternetConnection"));

        JButton exit = new JButton();
        exit.setText("EXIT");
        exit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LifecycleManager.getDefault().exit();
            }
        });
        nd.setOptions(new Object[]{exit});
        return nd;
    }

    public static NotifyDescriptor getServerRestartError() {
        NotifyDescriptor nd = getErrorNotifyDescriptor(NbBundle.getMessage(NDFactory.class, "NDF.serverRestart"));

        JButton loginIn = new JButton();
        loginIn.setText("Log in to Acorn web.");
        loginIn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String wwwAdress = NbBundle.getMessage(NDFactory.class, "NDF.acornWebSite");
//                BareBonesBrowserLaunch.openURL(NbBundle.getMessage(wwwAdress));

//                SimpleBrowserLaunch.openURL(wwwAdress);

                if (!java.awt.Desktop.isDesktopSupported()) {
                    System.err.println("Desktop is not supported (fatal)");
                    System.exit(1);
                }

                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

                if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                    System.err.println("Desktop doesn't support the browse action (fatal)");
                    System.exit(1);
                }

                try {

                    java.net.URI uri = new java.net.URI(wwwAdress);
                    desktop.browse(uri);
                } catch (Exception ex) {

                    System.err.println(ex.getMessage());
                }
                LifecycleManager.getDefault().exit();
            }
        });
        nd.setOptions(new Object[]{loginIn});
        return nd;
    }
}
