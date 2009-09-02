/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.login;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.auth.LoginService;
import org.jdesktop.swingx.auth.UserNameStore;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
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

    public LoginPanel(LoginService loginService) {
        super(loginService);

        init();
    }

    public LoginPanel(LoginService loginService, UserNameStore userNameStore) {
        super(loginService, null, userNameStore);
        init();
    }

    protected void init() {
        this.okButton = createOkButton();
        this.cancelButton = createCancelButton();

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
//                        JPanel panel = new JPanel();
//                        panel.setLayout(new GridLayout(2, 2));
//                        JLabel label = new JLabel("Model name:");
//                        JComboBox modelBox = new JComboBox(new Object[]{"odin", "dwa", "tri"});
//                        panel.add(label);
//                        panel.add(modelBox);
//                        panel.add(button);
//                        frame.add(panel, BorderLayout.NORTH);
//                        frame.setSize(380, 380);
//                        frame.setVisible(true);
//                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//                        String msg = "I'm plugged in!";
//                        NotifyDescriptor d = new NotifyDescriptor.Message(msg,
//                                NotifyDescriptor.INFORMATION_MESSAGE);
//                        DialogDisplayer.getDefault().notify(d);
                }
            }
        });
    }

    protected JButton createOkButton() {
        return new JButton(getActionMap().get(LOGIN_ACTION_COMMAND));
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
                new Object[]{okButton, cancelButton},
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
