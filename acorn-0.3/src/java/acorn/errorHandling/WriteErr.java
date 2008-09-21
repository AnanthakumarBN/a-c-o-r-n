/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.errorHandling;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Mateusza
 */
public class WriteErr {

    private String filePath;

    public WriteErr() {
        String dir = "acorn-err-dir";
        String path = System.getProperty("user.home");
        String sFile = "web-err";
        String p = path + "/" + dir;
        if (!new File(p).exists()) {
            (new File(p)).mkdir();
        }
        filePath = p + "/" + sFile;
        if (!new File(filePath).exists()) {
            new File(filePath);
        }
    }

    public void write(String s) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
            out.write(s + "\n");
            out.close();
        } catch (IOException e) {
        }
    }
}