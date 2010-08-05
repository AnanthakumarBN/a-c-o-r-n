/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.login;

import acorn.webservice.AuthenticationException_Exception;
import acornwsclient.download.DBDataDownloader;
import factories.NDFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.prefs.Preferences;
import javax.xml.ws.soap.SOAPFaultException;
import org.jdesktop.swingx.auth.DefaultUserNameStore;
import org.jdesktop.swingx.auth.LoginService;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.usermanagement.UserManagementPanel;
import org.vislogicengine.VisLogic;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        authenticate();
    }

    public boolean closing() {
        return true;
    }

    private void authenticate() {
        LoginService loginService = new MyLoginService();

        DefaultUserNameStore userNames = new DefaultUserNameStore();
        Preferences appPrefs = NbPreferences.forModule(LoginPanel.class);
        userNames.setPreferences(appPrefs.node("login"));

        LoginPanel panel = new LoginPanel(loginService, userNames);

        DialogDisplayer.getDefault().notifyLater(
                panel.createDialogDescriptor());
    }

    private static class MyLoginService extends LoginService {

        public boolean authenticate(String name, char[] password, String server) throws Exception {
            if ((name == null) || (password == null)) {
                return false;
            }

            try {
                String passwordMD5 = null;
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                byte[] tmp = String.copyValueOf(password).getBytes();
                md5.update(tmp);
                passwordMD5 = byteArrToString(md5.digest());
                DBDataDownloader dataProvider = new DBDataDownloader(name, passwordMD5);
                dataProvider.authenticate();

                NbPreferences.forModule(UserManagementPanel.class).put("user", name);
                NbPreferences.forModule(UserManagementPanel.class).put("pass", passwordMD5);
                VisLogic.updateModelsList();
            } catch (NoSuchAlgorithmException ex) {
                Exceptions.printStackTrace(ex);
            } catch (AuthenticationException_Exception ex) {
                return false;
            } //When server was restarted and nobody logs in to Acorn web
            catch (SOAPFaultException ex) {
            String msg = NbBundle.getMessage(NDFactory.class, "NDF.serverRestart");
                NotifyDescriptor nd = NDFactory.getServerRestartError();
                DialogDisplayer.getDefault().notify(nd);
            } catch (Exception ex) {
                System.out.println(ex);
            }

            return true;
        }

        private static String byteArrToString(byte[] b) {
            String res = null;
            StringBuffer sb = new StringBuffer(b.length * 2);
            for (int i = 0; i < b.length; i++) {
                int j = b[i] & 0xff;
                if (j < 16) {
                    sb.append('0');
                }
                sb.append(Integer.toHexString(j));
            }
            res = sb.toString();
            return res.toUpperCase();
        }
    }
}