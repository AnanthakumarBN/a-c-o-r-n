
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

    @Override
    public void tearDown() throws Exception {
        WebUserManagement.logoutUser(selenium, this);
        WebUserManagement.deleteUser(selenium, this);
        selenium.stop();
    }
}
