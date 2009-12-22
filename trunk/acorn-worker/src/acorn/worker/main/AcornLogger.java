/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package acorn.worker.main;

import java.util.logging.Logger;

/**
 *
 * @author kuba
 */
public class AcornLogger extends Logger {
    private static AcornLogger singleton = new AcornLogger();
    private static boolean printInputToFile = false;
    AcornLogger()
    {
        super(null, null);
    }
    public static AcornLogger getInstance()
    {
        return singleton;
    }
    public static void logError(String s)
    {
        singleton.severe(s);
    }
    public static void logWarning(String s)
    {
        singleton.warning(s);
    }
    public static void logInfo(String s)
    {
        singleton.info(s);
    }
    public static void setPrintInputToFile(boolean value) {
        printInputToFile = value;
    }
    public static void logInput(String s) {
        singleton.logError(s);
    }
}
