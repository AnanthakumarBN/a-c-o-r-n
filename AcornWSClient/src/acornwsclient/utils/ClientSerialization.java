/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package acornwsclient.utils;

import acornwsclient.download.DBDataDownloader;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author markos
 */
public class ClientSerialization {

    private static String webServiceEncoding;

    public ClientSerialization(String webSE) {
        webServiceEncoding = webSE;
    }




    public XMLDecoder getXMLDecoder(String inputString){
    byte[] byteArray = null;
        try {
            byteArray = inputString.getBytes(webServiceEncoding);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DBDataDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }
        ByteArrayInputStream stream = new ByteArrayInputStream(byteArray);


        XMLDecoder d = new XMLDecoder(new BufferedInputStream(stream));
        return d;
    }

}
