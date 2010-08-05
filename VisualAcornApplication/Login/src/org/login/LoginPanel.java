/*
 * To change this template, choose Tool | Templates
 * and open the template in the editor.
 */
package org.login;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.auth.LoginService;
import org.jdesktop.swingx.auth.UserNameStore;
import org.openide.DialogDescriptor;
import org.openide.LifecycleManager;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author markos
 */
public class LoginPanel extends JXLoginPane {
    private JButton okButton;
    private JButton cancelButton;
    private JButton guestButton;

    public LoginPanel(LoginService loginService) {
        super(loginService);
//no idea why this (and override of getLocalet()) does not work
//hack that works is to delete org\jdesktop\swingx\plaf\basic\resources\LoginPane_xx.properties
//in VisualAcornApplication/swing-worker-x/release/modules/ext/swingx.jar
//        setLocale(Locale.US);
        init();
    }

    public LoginPanel(LoginService loginService, UserNameStore userNameStore) {
        super(loginService, null, userNameStore);
//        setLocale(Locale.US);
        init();
    }

//    @Override
//    public Locale getLocale() {
//        System.err.println("getLocale()="+super.getLocale());
//        return Locale.US;
//    }

    protected void init() {
        this.okButton = createOkButton();
        this.cancelButton = createCancelButton();
        this.guestButton = createGuestButton();

//        setLocale(Locale.UK);
        addPropertyChangeListener("status", new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                JXLoginPane.Status status = (JXLoginPane.Status) evt.getNewValue();
                switch (status) {
                    case NOT_STARTED:
                        break;
                    case IN_PROGRESS:
                        cancelButton.setEnabled(false);
                        break;
                    case CANCELLED:
                        cancelButton.setEnabled(true);
                        pack();
                        break;
                    case FAILED:
                        cancelButton.setEnabled(true);
                        pack();
                        break;
                    case SUCCEEDED:
                        // dispose dialog to allow main window to be opened...
                        SwingUtilities.getWindowAncestor(LoginPanel.this).dispose();
                        final JFrame frame = new JFrame("Select model for visualizations.");
                        JButton button = new JButton("My Button");
                        button.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                frame.dispose();
                            }
                        });
                }
            }
        });
    }

    protected JButton createOkButton() {
        //getActionMap().get(LOGIN_ACTION_COMMAND).putValue(Action.NAME, NbBundle.getMessage(LoginPanel.class, "BTN_OK"));
        return new JButton(getActionMap().get(LOGIN_ACTION_COMMAND));
    }

    protected JButton createGuestButton() {
        //return new JButton(getActionMap().get(LOGIN_ACTION_COMMAND));
        JButton button =  new JButton(NbBundle.getMessage(LoginPanel.class, "BTN_Guest"));
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setPassword("".toCharArray());
                setUserName("");
                getActionMap().get(LOGIN_ACTION_COMMAND).actionPerformed(e);
            }
        });

        return button;
    }

    protected JButton createCancelButton() {
        JButton button = new JButton(NbBundle.getMessage(LoginPanel.class, "BTN_Cancel"));
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                SwingUtilities.getWindowAncestor(LoginPanel.this).dispose();
                exit();
            }
        });

        return button;
    }

    protected DialogDescriptor createDialogDescriptor() {
        DialogDescriptor dd = new DialogDescriptor(this,
                NbBundle.getMessage(LoginPanel.class, "TITLE_Login"),
                true,
                new Object[]{guestButton, okButton, cancelButton},
                okButton,
                DialogDescriptor.DEFAULT_ALIGN,
                null,
                null);

        // no option should close dialog by default...
        dd.setClosingOptions(new Object[]{});

        dd.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ("value".equalsIgnoreCase(evt.getPropertyName())) {
                    // Escape pressed or dialog closed...
                    if (NotifyDescriptor.CLOSED_OPTION.equals(evt.getNewValue())) {
                        exit();
                    }
                }
            }
        });

        return dd;
    }

    protected void exit() {
        LifecycleManager.getDefault().exit();
    }

    private void pack() {
        revalidate();
        SwingUtilities.getWindowAncestor(this).pack();
    }
}
