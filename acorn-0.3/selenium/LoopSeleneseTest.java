
import com.thoughtworks.selenium.*;
import java.util.Random;

/**
 *
 * @author jsroka
 */
public class LoopSeleneseTest extends SeleneseTestCase {

    private String getRandomString() {
        Random r = new Random();
        return Long.toString(Math.abs(r.nextLong()), 36);
    }
    boolean createDummyModelOnLocalhost = false;

    @Override
    public void setUp() throws Exception {
        selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://localhost:8080/");
        createDummyModelOnLocalhost = true;

        //selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://sysbio3.fhms.surrey.ac.uk:8080/");


        selenium.start();
        //selenium.setSpeed("500");
        WebUserManagement.createUser(selenium, this);
        WebUserManagement.loginUser(selenium, this);
        //selenium.setSpeed("500");
    }

    
   
     
    public void testVisLoop() throws Exception {
        //selenium.setSpeed("5000");
        WebUserManagement.logoutUser(selenium, this);
        int i = 0;
        while (1 == 1) {
            System.out.println("i=" + i++);
            selenium.click("//div[@id='menu']/ul/li[3]/a/em");
            selenium.waitForPageToLoad("30000");
            selenium.click("link=f");
            selenium.waitForPageToLoad("30000");
            selenium.click("content:visualizations:visualizationsTable:1:reactionVisualizationsRadio");
            selenium.click("link=Generate drawing.");
            selenium.waitForPageToLoad("30000");
            verifyTrue(selenium.isTextPresent("Can't create graphic file. "));
//            verifyTrue(selenium.isElementPresent("//div[@id='content']/img"));

//            selenium.click("//div[@id='menu']/ul/li[3]/a/em");
//            selenium.waitForPageToLoad("30000");
//            selenium.click("link=f1");
//            selenium.waitForPageToLoad("30000");
//            selenium.click("content:visualizations:visualizationsTable:0:reactionVisualizationsRadio");
//            selenium.click("link=Generate drawing.");
//            selenium.waitForPageToLoad("30000");
//            verifyTrue(selenium.isTextPresent("Can't create graphic file. "));
//            verifyTrue(selenium.isElementPresent("//div[@id='content']/img"));

            selenium.click("//div[@id='menu']/ul/li[3]/a/em");
            selenium.waitForPageToLoad("30000");
            selenium.click("link=fva");
            selenium.waitForPageToLoad("30000");
//            selenium.click("content:visualizations:visualizationsTable:2:reactionVisualizationsRadio");
//            selenium.click("link=Generate drawing.");
//            selenium.waitForPageToLoad("30000");
//            verifyTrue(selenium.isElementPresent("//div[@id='content']/img"));
        }
    }

    @Override
    public void tearDown() throws Exception {
        WebUserManagement.logoutUser(selenium, this);
        WebUserManagement.deleteUser(selenium, this);
        selenium.stop();
    }
}
