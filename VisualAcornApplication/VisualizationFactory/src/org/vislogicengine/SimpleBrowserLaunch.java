/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vislogicengine;

/**
 *
 * @author markos
 */
public class SimpleBrowserLaunch {

    public static void openURL(String wwwAdress) {

        if (!java.awt.Desktop.isDesktopSupported()) {
            System.err.println("Desktop is not supported (fatal)");
            System.exit(1);
        }

        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

        if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
            System.err.println("Desktop doesn't support the browse action (fatal)");
            System.exit(1);
        }

//        String wwwAdress = NbBundle.getMessage(NDFactory.class, "NDF.acornWebSite");
        try {

            java.net.URI uri = new java.net.URI(wwwAdress);
            desktop.browse(uri);
        } catch (Exception ex) {

            System.err.println(ex.getMessage());
        }
    }
}

