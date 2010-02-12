package acorn.worker.main;

import acorn.worker.amkfba.AdapterAmkfba;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author kuba
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        String amkfbaPath;
        String logfile;
        FileHandler fh;
        Properties p = System.getProperties();
        
        logfile = p.getProperty("acorn.worker.logfile");
        
        if (logfile != null && !logfile.equals("")) {
            fh = new FileHandler(logfile, true);
            fh.setFormatter(new SimpleFormatter());
            AcornLogger.getInstance().addHandler(fh);
        }
        AcornLogger.getInstance().addHandler(new ConsoleHandler());
        String dummy = p.getProperty("acorn.worker.modelDump");
        if (dummy != null && !dummy.equals(""))
            AcornLogger.getInstance().setModelDumpFile(dummy);
        
        amkfbaPath = p.getProperty("acorn.worker.amkfbaPath");
        if (amkfbaPath != null) {
            AdapterAmkfba.setAmkfbaPath(amkfbaPath);
        }
        else
        {
            AcornLogger.logError("acorn.worker.amkfbaPath not set, exiting");
            return; 
        }
        
        new WorkerDaemon().run();
    }
}
