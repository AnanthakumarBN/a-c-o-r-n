
import com.thoughtworks.selenium.*;

/**
 *
 * @author jsroka
 */
public class NewSeleneseTest extends SeleneseTestCase {

  @Override
  public void setUp() throws Exception {
    //super.setUp("http://localhost:8080/");
    System.out.println("początek");
    selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://localhost:8080/");
    System.out.println("po DefaultSelenium");
    //selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://sysbio3.fhms.surrey.ac.uk:8080/");
    selenium.start();
    System.out.println("po start");
    WebUserManagement.createUser(selenium, this);
    System.out.println("po createUser");
    WebUserManagement.loginUser(selenium, this);
    System.out.println("po loginUser");
    selenium.setSpeed("0");
  }

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
  private final String temporaryModelName = "temporaryModel";

  public void testUploadingAndDeletingModels() throws Exception {
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
  }

  public void testRunFba() throws Exception {
    selenium.open("/acorn/homepage.jsf");
    selenium.click("//div[@id='menu']/ul/li[2]/a/em");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Model List"));
    selenium.click("link=M. tuberculosis GSMN-TB");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Model details"));
    selenium.click("link=Single Flux Balance Analysis");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Single Flux Balance Analysis Parameters"));
    selenium.type("content:j_id_id18pc4:j_id_id12pc5", "biom");
    selenium.click("content:j_id_id18pc4:reactionTable:0:reactionSpeciesRadio");
    selenium.click("content:j_id_id18pc4:j_id_id22pc5");
    selenium.click("content:j_id_id18pc4:reactionTable:0:reactionSpeciesRadio");
    selenium.click("link=Start the simulation");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("[M. tuberculosis GSMN-TB - TASK]"));
    verifyTrue(selenium.isTextPresent("Task List"));
  }

  public void testDeleteFbaTask() throws Exception {
    selenium.open("/acorn/homepage.jsf");
    selenium.click("//div[@id='menu']/ul/li[3]/a/em");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Task List"));
    verifyTrue(selenium.isTextPresent("[M. tuberculosis GSMN-TB - TASK]"));
    String taskName = "[M. tuberculosis GSMN-TB - TASK]";
    String modelName = taskName + " task's model";
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
    selenium.click("//div[@id='menu']/ul/li[2]/a/em");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Model List"));
    verifyFalse(selenium.isTextPresent(modelName));
    selenium.click("//div[@id='menu']/ul/li[1]/a/em");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Welcome to Acorn"));
  }

  public void testRunFva() throws Exception {
    selenium.open("/acorn/homepage.jsf");
    selenium.click("//div[@id='menu']/ul/li[2]/a/em");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Model List"));
    verifyTrue(selenium.isTextPresent("M. tuberculosis GSMN-TB"));
    selenium.click("link=M. tuberculosis GSMN-TB");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Model details"));
    selenium.click("link=Flux Variability Analysis");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Task List"));
    verifyTrue(selenium.isTextPresent("[M. tuberculosis GSMN-TB - TASK]"));
    selenium.click("//div[@id='menu']/ul/li[1]/a/em");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Welcome to Acorn"));
  }

  public void testDeleteFvaTask() throws Exception {
    selenium.open("/acorn/homepage.jsf");
    selenium.click("//div[@id='menu']/ul/li[3]/a/em");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Task List"));
    verifyTrue(selenium.isTextPresent("[M. tuberculosis GSMN-TB - TASK]"));
    selenium.click("//div[@id='menu']/ul/li[2]/a/em");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Model List"));
    verifyTrue(selenium.isTextPresent("[M. tuberculosis GSMN-TB - TASK] task's model"));
    selenium.click("//div[@id='menu']/ul/li[3]/a/em");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Task List"));
    String taskName = "[M. tuberculosis GSMN-TB - TASK]";
    String modelName = taskName + " task's model";
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
    selenium.click("//div[@id='menu']/ul/li[2]/a/em");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Model List"));
    verifyFalse(selenium.isTextPresent(modelName));
    selenium.click("//div[@id='menu']/ul/li[1]/a/em");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Welcome to Acorn"));
  }

  public void testRunRscan() throws Exception {
    selenium.open("/acorn/homepage.jsf");
    selenium.click("//div[@id='menu']/ul/li[2]/a/em");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Model List"));
    verifyTrue(selenium.isTextPresent("M. tuberculosis GSMN-TB"));
    selenium.click("link=M. tuberculosis GSMN-TB");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Model details"));
    verifyTrue(selenium.isTextPresent("Reaction Essentiality Scan"));
    selenium.click("link=Reaction Essentiality Scan");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Reaction Essentiality Scan Parameters"));
    selenium.type("content:j_id_id18pc4:j_id_id12pc5", "biom");
    selenium.click("content:j_id_id18pc4:j_id_id22pc5");
    selenium.click("content:j_id_id18pc4:j_id_id22pc5");
    verifyTrue(selenium.isTextPresent("0.041 P-L-GLX"));
    verifyTrue(selenium.isTextPresent("BIOMASS2"));
    selenium.click("content:j_id_id18pc4:reactionTable:0:reactionSpeciesRadio");
    verifyTrue(selenium.isTextPresent("Start the simulation"));
    selenium.click("link=Start the simulation");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Task List"));
    verifyTrue(selenium.isTextPresent("[M. tuberculosis GSMN-TB - TASK]"));
    selenium.click("//div[@id='menu']/ul/li[1]/a/em");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Welcome to Acorn"));
  }

  public void testDeleteRscanTask() throws Exception {
    selenium.open("/acorn/homepage.jsf");
    selenium.click("//div[@id='menu']/ul/li[2]/a/em");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Model List"));
    verifyTrue(selenium.isTextPresent("[M. tuberculosis GSMN-TB - TASK] task's model"));
    selenium.click("//div[@id='menu']/ul/li[3]/a/em");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Task List"));
    verifyTrue(selenium.isTextPresent("[M. tuberculosis GSMN-TB - TASK]"));
    String taskName = "[M. tuberculosis GSMN-TB - TASK]";
    String modelName = taskName + " task's model";
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
    selenium.waitForPageToLoad("30000");
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
    selenium.open("/acorn/homepage.jsf");
    selenium.click("//div[@id='menu']/ul/li[2]/a/em");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Model List"));
    verifyTrue(selenium.isTextPresent("M. tuberculosis GSMN-TB"));
    selenium.click("link=M. tuberculosis GSMN-TB");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Model details"));
    verifyTrue(selenium.isTextPresent("Single Gene Knockout"));
    selenium.click("link=Single Gene Knockout");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Single Gene Knockout Parameters"));
    selenium.type("content:j_id_id18pc4:j_id_id12pc5", "biom");
    selenium.click("content:j_id_id18pc4:j_id_id22pc5");
    selenium.click("content:j_id_id18pc4:j_id_id22pc5");
    verifyTrue(selenium.isTextPresent("BIOMASS2"));
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
    verifyTrue(selenium.isTextPresent("[M. tuberculosis GSMN-TB - TASK]"));
    selenium.click("//div[@id='menu']/ul/li[1]/a/em");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Welcome to Acorn"));
  }

  public void testDeleteKgeneTask() throws Exception {
    selenium.open("/acorn/homepage.jsf");
    selenium.click("//div[@id='menu']/ul/li[3]/a/em");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Task List"));
    verifyTrue(selenium.isTextPresent("[M. tuberculosis GSMN-TB - TASK]"));
    selenium.click("//div[@id='menu']/ul/li[2]/a/em");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Model List"));
    verifyTrue(selenium.isTextPresent("[M. tuberculosis GSMN-TB - TASK] task's model"));
    selenium.click("//div[@id='menu']/ul/li[3]/a/em");
    selenium.waitForPageToLoad("30000");
    verifyTrue(selenium.isTextPresent("Task List"));
    verifyTrue(selenium.isTextPresent("[M. tuberculosis GSMN-TB - TASK]"));
    String taskName = "[M. tuberculosis GSMN-TB - TASK]";
    String modelName = taskName + " task's model";
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
    selenium.waitForPageToLoad("30000");
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
