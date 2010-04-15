/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.worker.main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

/**
 *
 * @author kuba
 */
public class AcornLogger extends Logger {

    private static AcornLogger singleton = new AcornLogger();
    private static String modelDumpFile = "";

    AcornLogger() {
        super(null, null);
    }

    public static AcornLogger getInstance() {
        return singleton;
    }

    public static void logError(String s) {
        singleton.severe(s);
    }

    public static void logWarning(String s) {
        singleton.warning(s);
    }

    public static void logInfo(String s) {
        singleton.info(s);
    }

    public static void setModelDumpFile(String path) {
        modelDumpFile = path;
    }

    public static void logInput(String s) {
        if (!modelDumpFile.equals("")) {
            try {
                FileWriter dumpFile = new FileWriter(modelDumpFile);
                PrintWriter printer = new PrintWriter(dumpFile);
                printer.print(s);
                printer.close();
                dumpFile.close();
            } catch (IOException e) {
                logError("Error dumping model to " + modelDumpFile);
            }
        }
    }
}
