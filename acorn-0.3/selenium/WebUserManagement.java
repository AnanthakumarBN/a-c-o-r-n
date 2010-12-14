
import com.thoughtworks.selenium.SeleneseTestCase;
import com.thoughtworks.selenium.Selenium;

/**
 *
 * @author jsroka
 */
public class WebUserManagement {

    private static final String LOGIN = "seltest1";
    private static final String PASSWD = "seltest1";
    private static final String EMAIL = LOGIN + "@" + LOGIN + ".org";
    private static final String LOGIN_ROOT = "root";

    public static void createUser(Selenium selenium, SeleneseTestCase testCase) {
        selenium.open("/acorn/register.jsf");
        selenium.click("//div[@id='menu']/ul/li[5]/a/em");
        selenium.waitForPageToLoad("30000");
        selenium.type("content:create:name", LOGIN);
        selenium.type("content:create:surname", LOGIN);
        selenium.type("content:create:institution", LOGIN);
        selenium.type("content:create:email", EMAIL);
        selenium.type("content:create:login", LOGIN);
        selenium.type("content:create:password", PASSWD);
        selenium.type("content:create:passwordConfirmation", PASSWD);
        selenium.click("content:create:submit");
        selenium.waitForPageToLoad("30000");
        testCase.verifyTrue(selenium.isTextPresent("Your account has been successfully created"));
    }

    public static void loginUser(Selenium selenium, SeleneseTestCase testCase) {
        selenium.open("/acorn/login.jsf");
        selenium.type("content:login:login", LOGIN);
        selenium.type("content:login:password", LOGIN);
        selenium.click("content:login:submit");
        selenium.waitForPageToLoad("30000");
        testCase.verifyFalse(selenium.isTextPresent("Login Failed!"));
        testCase.verifyTrue(selenium.isTextPresent("Task List"));
    }

    public static void logoutUser(Selenium selenium, SeleneseTestCase testCase) {
        testCase.verifyTrue(selenium.isTextPresent("Log Out"));
        selenium.click("//form[@id='menu:j_id_id189pc3']/a/em");
        selenium.waitForPageToLoad("30000");
        selenium.open("/acorn/register.jsf");
    }

    private static void loginAdmin(Selenium selenium, SeleneseTestCase testCase) {
        selenium.open("/acorn/login.jsf");
        selenium.type("content:login:login", LOGIN_ROOT);
        String passwdRoot = "rosomak";
        selenium.type("content:login:password", passwdRoot);
        selenium.click("content:login:submit");
        selenium.waitForPageToLoad("30000");
        testCase.verifyFalse(selenium.isTextPresent("Login Failed!"));
        testCase.verifyTrue(selenium.isTextPresent("Task List"));
    }

    public static void deleteUser(Selenium selenium, SeleneseTestCase testCase) {
        loginAdmin(selenium, testCase);
        selenium.click("//div[@id='menu']/ul/li[5]/a/em");
        selenium.waitForPageToLoad("30000");
        selenium.click("//tr[.//a/text()='" + LOGIN + "']//a[./text()='delete']");
        labelFor:
        for (int i = 0; i < 300; i++) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("przerwany wait");
            }
            if (!selenium.isTextPresent(EMAIL)) {
                break labelFor;
            }
        }
        testCase.verifyFalse(selenium.isTextPresent(EMAIL));
    }
}
