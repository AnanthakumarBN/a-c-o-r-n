
import com.thoughtworks.selenium.*;
import java.util.Random;

/**
 *
 * @author jsroka
 */
public class NewSeleneseTest extends SeleneseTestCase {

    private String getRandomString() {
        Random r = new Random();
        return Long.toString(Math.abs(r.nextLong()), 36);
    }

    @Override
    public void setUp() throws Exception {
        //super.setUp("http://localhost:8080/");
        System.out.println("początek");
        //selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://localhost:8080/");
        System.out.println("po DefaultSelenium");
        selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://sysbio3.fhms.surrey.ac.uk:8080/");
        selenium.start();
        System.out.println("po start");
        WebUserManagement.createUser(selenium, this);
        System.out.println("po createUser");
        WebUserManagement.loginUser(selenium, this);
        System.out.println("po loginUser");
        selenium.setSpeed("0");
    }

/*
    public void testLoop() throws Exception {
        while (1 == 1) {
            testIfPublishedModelsAreThere();
            testUploadingAndDeletingModels();
            testRunFba();
            testRunFva();
            testRunKgene();
            testRunRscan();
        }
    }
*/

    public void testIfPublishedModelsAreThere() throws Exception {
        System.out.println("test3");
        selenium.click("//div[@id='menu']/ul/li[2]/a/em");
        selenium.waitForPageToLoad("30000");
        //verifyTrue(selenium.isTextPresent("S. cerevisiae iND750"));
        //verifyTrue(selenium.isTextPresent("M. tuberculosis GSMN-TB"));
        //verifyTrue(selenium.isTextPresent("E. coli iAF1260"));

//        selenium.click("link=S. cerevisiae iND750");
//        selenium.waitForPageToLoad("30000");
//        selenium.click("//form[@id='menu:j_id_id189pc3']/a/em");
//        selenium.waitForPageToLoad("30000");
//        verifyTrue(selenium.isTextPresent("Login"));
    }

    public void testUploadingAndDeletingModels() throws Exception {
        String temporaryModelName = getRandomString();
        String cwd = null;
        try {
            cwd = new java.io.File(".").getCanonicalPath();
        } catch (java.io.IOException e) {
        }

        WebUserManagement.logoutUser(selenium, this);
        WebUserManagement.loginAdmin(selenium, this);
        selenium.click("//div[@id='menu']/ul/li[4]/a/em");
        selenium.waitForPageToLoad("30000");
        selenium.type("content:add:file", cwd + "/slow1.sbml");
        selenium.type("content:add:name", temporaryModelName);
        selenium.type("content:add:organism", "Tuberculosis");
        selenium.click("content:add:submit");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Model List"));
        selenium.click("//tr[.//a/text()='" + temporaryModelName + "']//a[./text()='delete']");
        labelFor:
        for (int i = 0; i < 30; i++) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("przerwany wait");
            }
            if (!selenium.isTextPresent(temporaryModelName)) {
                break labelFor;
            }
        }
        verifyFalse(selenium.isTextPresent(temporaryModelName));
        verifyTrue(selenium.isTextPresent("Model List"));
    }

    public void testRunFba() throws Exception {
        String taskName = "FBA - " + getRandomString();
        String modelName = taskName + " task's model";

        selenium.open("/acorn/homepage.jsf");
        selenium.click("//div[@id='menu']/ul/li[2]/a/em");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Model List"));
        selenium.click("link=M. tuberculosis GSMN-TB");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Model details"));
        selenium.type("content:modelDetails:j_id_id85pc4", taskName);
        selenium.click("link=Single Flux Balance Analysis");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Single Flux Balance Analysis Parameters"));
        selenium.type("content:j_id_id18pc4:j_id_id12pc5", "biom");
        selenium.click("content:j_id_id18pc4:reactionTable:0:reactionSpeciesRadio");
        labelFor2:
        for (int i = 0; i < 30; i++) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("przerwany wait");
            }
            if (selenium.isTextPresent("R_biomass_SC4_bal")) {
                break labelFor2;
            }
        }
        verifyTrue(selenium.isTextPresent("R_biomass_SC4_bal"));
        selenium.click("content:j_id_id18pc4:j_id_id22pc5");
        selenium.click("link=Start the simulation");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent(taskName));
        verifyTrue(selenium.isTextPresent("Task List"));

        selenium.click("//div[@id='menu']/ul/li[3]/a/em");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Task List"));
        verifyTrue(selenium.isTextPresent(taskName));
        selenium.click("//tr[.//a/text()='" + taskName + "']//a[./text()='delete']");
        labelFor:
        for (int i = 0; i < 30; i++) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("przerwany wait");
            }
            if (!selenium.isTextPresent(taskName)) {
                break labelFor;
            }
        }
        verifyTrue(selenium.isTextPresent("Task List"));
        verifyFalse(selenium.isTextPresent(taskName));
        selenium.click("//div[@id='menu']/ul/li[2]/a/em");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Model List"));
        verifyFalse(selenium.isTextPresent(modelName));
        selenium.click("//div[@id='menu']/ul/li[1]/a/em");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Welcome to Acorn"));
    }

    public void testRunFva() throws Exception {
        String taskName = "FVA - " + getRandomString();
        String modelName = taskName + " task's model";

        selenium.open("/acorn/homepage.jsf");
        selenium.click("//div[@id='menu']/ul/li[2]/a/em");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Model List"));
        verifyTrue(selenium.isTextPresent("M. tuberculosis GSMN-TB"));
        selenium.click("link=M. tuberculosis GSMN-TB");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Model details"));
        selenium.type("content:modelDetails:j_id_id85pc4", taskName);
        selenium.click("link=Flux Variability Analysis");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Task List"));
        verifyTrue(selenium.isTextPresent(taskName));
        selenium.click("//div[@id='menu']/ul/li[1]/a/em");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Welcome to Acorn"));

        selenium.click("//div[@id='menu']/ul/li[3]/a/em");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Task List"));
        verifyTrue(selenium.isTextPresent(taskName));
        selenium.click("//div[@id='menu']/ul/li[2]/a/em");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Model List"));
        verifyTrue(selenium.isTextPresent(modelName));
        selenium.click("//div[@id='menu']/ul/li[3]/a/em");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Task List"));
        selenium.click("//tr[.//a/text()='" + taskName + "']//a[./text()='delete']");
        labelFor:
        for (int i = 0; i < 30; i++) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("przerwany wait");
            }
            if (!selenium.isTextPresent(taskName)) {
                break labelFor;
            }
        }
        verifyTrue(selenium.isTextPresent("Task List"));
        verifyFalse(selenium.isTextPresent(taskName));
        selenium.click("//div[@id='menu']/ul/li[2]/a/em");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Model List"));
        verifyFalse(selenium.isTextPresent(modelName));
        selenium.click("//div[@id='menu']/ul/li[1]/a/em");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Welcome to Acorn"));
    }

    public void testRunRscan() throws Exception {
        String taskName = "RSCAN - " + getRandomString();
        String modelName = taskName + " task's model";

        selenium.open("/acorn/homepage.jsf");
        selenium.click("//div[@id='menu']/ul/li[2]/a/em");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Model List"));
        verifyTrue(selenium.isTextPresent("M. tuberculosis GSMN-TB"));
        selenium.click("link=M. tuberculosis GSMN-TB");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Model details"));
        verifyTrue(selenium.isTextPresent("Reaction Essentiality Scan"));
        selenium.type("content:modelDetails:j_id_id85pc4", taskName);
        selenium.click("link=Reaction Essentiality Scan");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Reaction Essentiality Scan Parameters"));
        selenium.type("content:j_id_id18pc4:j_id_id12pc5", "biom");
        selenium.click("content:j_id_id18pc4:j_id_id22pc5");
        labelFor2:
        for (int i = 0; i < 30; i++) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("przerwany wait");
            }
            if (selenium.isTextPresent("0.041 P-L-GLX")) {
                break labelFor2;
            }
        }
        verifyTrue(selenium.isTextPresent("0.041 P-L-GLX"));
        selenium.click("content:j_id_id18pc4:reactionTable:0:reactionSpeciesRadio");
        verifyTrue(selenium.isTextPresent("Start the simulation"));
        selenium.click("link=Start the simulation");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Task List"));
        verifyTrue(selenium.isTextPresent(taskName));
        selenium.click("//div[@id='menu']/ul/li[1]/a/em");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Welcome to Acorn"));

        selenium.click("//div[@id='menu']/ul/li[2]/a/em");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Model List"));
        verifyTrue(selenium.isTextPresent(modelName));
        selenium.click("//div[@id='menu']/ul/li[3]/a/em");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Task List"));
        verifyTrue(selenium.isTextPresent(taskName));
        selenium.click("//tr[.//a/text()='" + taskName + "']//a[./text()='delete']");
        labelFor:
        for (int i = 0; i < 30; i++) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("przerwany wait");
            }
            if (!selenium.isTextPresent(taskName)) {
                break labelFor;
            }
        }
        verifyTrue(selenium.isTextPresent("Task List"));
        verifyFalse(selenium.isTextPresent(taskName));
        selenium.click("//div[@id='menu']/ul/li[2]/a/em");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Model List"));
        verifyFalse(selenium.isTextPresent(modelName));
        selenium.click("//div[@id='menu']/ul/li[1]/a/em");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Welcome to Acorn"));
    }

    public void testRunKgene() throws Exception {
        String taskName = "KGENE - " + getRandomString();
        String modelName = taskName + " task's model";

        selenium.open("/acorn/homepage.jsf");
        selenium.click("//div[@id='menu']/ul/li[2]/a/em");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Model List"));
        verifyTrue(selenium.isTextPresent("M. tuberculosis GSMN-TB"));
        selenium.click("link=M. tuberculosis GSMN-TB");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Model details"));
        selenium.type("content:modelDetails:j_id_id85pc4", taskName);
        verifyTrue(selenium.isTextPresent("Single Gene Knockout"));
        selenium.click("link=Single Gene Knockout");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Single Gene Knockout Parameters"));
        selenium.type("content:j_id_id18pc4:j_id_id12pc5", "biom");
        selenium.click("content:j_id_id18pc4:j_id_id22pc5");
        labelFor2:
        for (int i = 0; i < 30; i++) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("przerwany wait");
            }
            if (selenium.isTextPresent("0.041 P-L-GLX")) {
                break labelFor2;
            }
        }
        verifyTrue(selenium.isTextPresent("0.041 P-L-GLX"));
        selenium.click("content:j_id_id18pc4:reactionTable:0:reactionSpeciesRadio");
        verifyTrue(selenium.isTextPresent("Start the simulation"));
        selenium.click("link=Start the simulation");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Some of the parameters have not been set"));
        selenium.type("content:j_id_id18pc4:j_id_id14pc7", "A");
        verifyTrue(selenium.isTextPresent("RV2922A"));
        selenium.click("content:j_id_id18pc4:genesTable:0:genesRadio");
        selenium.click("link=Start the simulation");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Task List"));
        verifyTrue(selenium.isTextPresent(taskName));
        selenium.click("//div[@id='menu']/ul/li[1]/a/em");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Welcome to Acorn"));

        selenium.open("/acorn/homepage.jsf");
        selenium.click("//div[@id='menu']/ul/li[3]/a/em");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Task List"));
        verifyTrue(selenium.isTextPresent(taskName));
        selenium.click("//div[@id='menu']/ul/li[2]/a/em");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Model List"));
        verifyTrue(selenium.isTextPresent(modelName));
        selenium.click("//div[@id='menu']/ul/li[3]/a/em");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Task List"));
        verifyTrue(selenium.isTextPresent(taskName));
        selenium.click("//tr[.//a/text()='" + taskName + "']//a[./text()='delete']");
        labelFor:
        for (int i = 0; i < 30; i++) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("przerwany wait");
            }
            if (!selenium.isTextPresent(taskName)) {
                break labelFor;
            }
        }
        verifyTrue(selenium.isTextPresent("Task List"));
        verifyFalse(selenium.isTextPresent(taskName));
        selenium.click("//div[@id='menu']/ul/li[2]/a/em");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Model List"));
        verifyFalse(selenium.isTextPresent(modelName));
        selenium.click("//div[@id='menu']/ul/li[1]/a/em");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Welcome to Acorn"));
    }

    @Override
    public void tearDown() throws Exception {
        WebUserManagement.logoutUser(selenium, this);
        WebUserManagement.deleteUser(selenium, this);
        selenium.stop();
    }
}
