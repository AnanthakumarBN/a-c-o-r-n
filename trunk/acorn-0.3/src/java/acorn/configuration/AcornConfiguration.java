package acorn.configuration;

import java.util.Iterator;
import java.util.List;
import java.util.Hashtable;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

/**
 *
 * @author kuba
 */
public class AcornConfiguration {

    static private XMLConfiguration instance = null;
    static private Hashtable securityLevel = null;

    private static void getConfiguration() {
        try {
            /* I had to place a config file under:
             * domains/domain1/config/
             * in Glassfish installation directory
             */

            instance = new XMLConfiguration("acornConfig.xml");

            securityLevel = new Hashtable();
            List pages = instance.configurationsAt("pages.page");
            for (Iterator it = pages.iterator(); it.hasNext();) {
                HierarchicalConfiguration sub = (HierarchicalConfiguration) it.next();
                String secLevel = sub.getString("securityLevel");
                String page = sub.getString("name");
                securityLevel.put(page, secLevel);
            }
            
            String driver = null;
            try {
                driver = instance.getString("database.driver");
                if (driver != null) {
                    Class.forName(driver);
                    //Class.forName(driver, true, ClassLoader.getSystemClassLoader());
                    //Class.forName(driver, false, ClassLoader.getSystemClassLoader());
                } 
                else System.err.println("property database.driver is not set!");
            } catch (Exception e1) {
                if (driver == null) {
                    System.err.println("can't find database.driver property!");
                    e1.printStackTrace(System.err);
                }
                else {
                    System.err.println("Class.forName(\"" + driver + "\") failed!");
                    e1.printStackTrace(System.err);
                    try {
                        Class.forName(driver, true, ClassLoader.getSystemClassLoader());
                    } catch (Exception e2) {
                        System.err.println("Class.forName(\"" + driver + "\", true) failed!");
                        e2.printStackTrace(System.err);
                        try {
                            Class.forName(driver, false, ClassLoader.getSystemClassLoader());                
                        } catch (Exception e3) {
                            System.err.println("Class.forName(\"" + driver + "\", false) failed!");e3.printStackTrace(System.err);
                        }
                    }
                }
            }

            String st = instance.getString("database.url");
            if (st != null) {
                System.setProperty("toplink.jdbc.url", st);
            }

            st = instance.getString("database.user");
            if (st != null) {
                System.setProperty("toplink.jdbc.user", st);
            }

            st = instance.getString("database.password");
            if (st != null) {
                System.setProperty("toplink.jdbc.password", st);
            }

        } catch (ConfigurationException e) {
            e.printStackTrace(System.out);
        /* we leave instance = null. what else can we do? */
        }
    }

    /**
     * 
     * @return security level required to browse 'page'
     */
    public static String getSecurityLevel(String page) {
        if (instance == null) {
            getConfiguration();
        }
        if (securityLevel != null && securityLevel.containsKey(page)) {
            return (String) (securityLevel.get(page));
        } else {
            return "admin";
        } /* a page not listed in a config file is accessible by only by admin */
    }
}
