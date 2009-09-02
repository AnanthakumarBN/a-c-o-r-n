/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vislogicengine;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author markos
 */
public class NotifyMessage {

    public static void displayInfromationMessage(String str) {
        NotifyDescriptor d = new NotifyDescriptor.Message(str,
                NotifyDescriptor.INFORMATION_MESSAGE);
        DialogDisplayer.getDefault().notify(d);
    }
}
