package acorn.userManagement;

import acorn.db.EUser;
import acorn.db.EUserController;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.component.UIInput;
import javax.faces.component.UIComponent;
import javax.servlet.http.*;
import java.security.*;
import com.octo.captcha.service.CaptchaServiceException;
import java.util.Random;
import javax.persistence.NoResultException;
import java.util.Date;
import java.util.concurrent.Semaphore;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author kuba
 */
public class UserManager {

    public static String statusGuest = "guest";

    private String login;
    private String password;
    private String passwordConfirmation;
    private String name;
    private String surname;
    private String captchaText;
    private String email;
    private String institution;
    private String registerOutcome;
    public static final String USER_SESSION_KEY = "user";
    private static Semaphore mutex = new Semaphore(1);
            
    public UserManager() {
        super();
    }

    private Boolean isVerificationStringCorrect() {
        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        /* remember that we need an id to validate */

        String captchaId = request.getSession().getId();

        /* Call the Service method */
        try {
            return CaptchaServiceSingleton.getInstance().validateResponseForID(captchaId, captchaText);
        } catch (CaptchaServiceException e) {
            /* should not happen, may be thrown if the id is not valid */
            return false;
        }
    }
    
    public void passwordConfirmationValidator(FacesContext context, UIComponent component, Object value)
        throws ValidatorException
    {
        // Obtain the client ID of the first password field from f:attribute.
        String passwordId = (String) component.getAttributes().get("passwordId");

        // Find the actual JSF component for the client ID.
        UIInput passwordInput = (UIInput) context.getViewRoot().findComponent(passwordId);

        // Get its value, the entered password of the first field.
        String passwordString = (String) passwordInput.getValue();

        // Cast the value of the entered password of the second field back to String.
        String confirmString = (String) value;

        // Check if the first password is actually entered and compare it with second password.
        if (confirmString != null && confirmString.length() < 3)
            throw new ValidatorException(new FacesMessage("Must be at least 3 characters long"));
        if (confirmString != null && confirmString.length() > 255)
            throw new ValidatorException(new FacesMessage("Must be at most 255 characters long"));
        if (confirmString != null && !confirmString.equals(passwordString))
            throw new ValidatorException(new FacesMessage("Passwords are not equal."));
    }

    private Boolean validateRegistrationForm(FacesContext context) {

        Boolean isCorrect;

        isCorrect = true;

        if (!password.equals(passwordConfirmation)) {
            FacesMessage message = new FacesMessage("The specified passwords do not match.  Please try again");
            context.addMessage("content:passwordForm:password", message);
            isCorrect = false;
        }
        return isCorrect;
    }

    private EUser getUser() {
        try {
            EUserController uc = new EUserController();
            return uc.findUserByLogin(login);
        } catch (NoResultException nre) {
            return null;
        }
    }

    private EUser createNewUser() {
        EUser user;

        user = new EUser();
        assert user.getId() != null;

        user.setActivationCode(generateActivationCode());
        user.setEmail(getEmail());
        user.setInstitution(getInstitution());
        user.setLogin(getLogin());
        user.setName(getName());
        user.setPasswd(getPassword());
        user.setStatus(EUser.statusNormal);
        user.setSurname(getSurname());
        user.setDate(new Date());

        return user;
    }

    public String createUser() {

        FacesContext context = FacesContext.getCurrentInstance();
        if (!validateRegistrationForm(context)) {
            return null;
        }
        EUser existingUser;
        EUser user = createNewUser();
        
        try {
            /* We need to have a mutex, as there is no SELECT FOR UPDATE in JPA to make proper locking */
            mutex.acquire();

            existingUser = getUser();
            
            EUserController uc = new EUserController();

            if (uc.getUsers().size() == 0) {
                //creating the first user - he gets admin priviledges
                user.setStatus(EUser.statusAdmin);
            } 

            if (existingUser == null) { /* we add an user only if he does not exist */
                uc.addUser(user);
            }
            
            mutex.release();
        } 
        catch (InterruptedException e)
        {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error creating user!",
                    "Unexpected error when creating your account.  Please contact the system Administrator");
            context.addMessage(null, message);
            return null;
        }
        catch (Exception e) {
            mutex.release();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error creating user!",
                    "Unexpected error when creating your account.  Please contact the system Administrator");
            context.addMessage(null, message);
            return null;
        }
        if (existingUser != null) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error creating user!",
                    "The requested login is not available.");
            context.addMessage("content:create:login", message);
            return null;
        }

        /* It is possible to send email needed for user activation
         * In this version user is active after making the acount, no email is needed
         * The code below may be helpful to make sending emails
         */
        
        /*try {
        String emailContent = "http://localhost:17095/acorn-0.1/ActivateUser?code=" + wuser.getActivationCode() + "\nHave a nice day";
        EmailSender.sendEmail(wuser.getEmail(), "kuba@radunica.net", "Confirm your e-mail", emailContent);
        return "login";
        } catch (AddressException e) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
        "Error sending verification e-mail!",
        "Bad e-mail address");
        context.addMessage("create:email", message);
        return null;
        } catch (MessagingException e) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
        "Error sending verification e-mail!",
        "Unexpected error when sending verification e-mail");
        context.addMessage(null, message);
        return null;
        }*/
        
        
        setRegisterOutcome("Your account has been successfully created");
        return "login";
    }

    public String loginUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        EUser user = getUser();

        if (user != null) {
            if (!user.getPasswd().equals(password)) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Login Failed!",
                        "The password specified is not correct.");
                context.addMessage("password", message);
                return null;
            }
            if (user.getStatus().equals("inactive")) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Login Failed!",
                        "This account has not been activated yet.");
                context.addMessage(null, message);
                return null;
            }
            if (user.getStatus().equals("banned")) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Login Failed!",
                        "This account has been banned.");
                context.addMessage(null, message);
                return null;
            }

            context.getExternalContext().getSessionMap().put(USER_SESSION_KEY, user);
            return "authenticated";
        } else {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Login Failed!",
                    "User '" + login +
                    "' does not exist.");
            context.addMessage(null, message);
            return null;
        }
    }

    public String logoutUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        EUser user = getUser();

        context.getExternalContext().getSessionMap().remove(USER_SESSION_KEY);
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return "login";
    }

    /**
     * Get current user (or null if the nobody is logged in)
     * @return the user, who is logged in or null
     */
    public static EUser getCurrentUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        return (EUser) context.getExternalContext().getSessionMap().get(USER_SESSION_KEY);

    }

    /**
     * Get status of the logged user (or statusGuest if the nobody is logged in)
     * @return status of the user, who is logged in or null
     */
    public static String getUserStatus() {
        EUser user;
        FacesContext context = FacesContext.getCurrentInstance();
        user = (EUser) context.getExternalContext().getSessionMap().get(USER_SESSION_KEY);
        if (user == null) {
            return statusGuest;
        } else {
            return user.getStatus();
        }
    }

    public boolean getIsAdmin() {
        return getUserStatus().equals(EUser.statusAdmin);
    }

    public boolean getIsNormal() {
        return getUserStatus().equals(EUser.statusNormal);
    }

    public boolean getIsGuest() {
        return getUserStatus().equals(statusGuest);
    }

    public boolean getIsNotGuest() {
        return !getUserStatus().equals(statusGuest);
    }

    public void validateLogin(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String loginCandidate = (String) value;

        if (!loginCandidate.matches("[a-z0-9_\\-\\.]*")) {
            ((UIInput) toValidate).setValid(false);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Bad login",
                    "Login can contain only the following characters: abcdefghijklmnopqrstuvwxyz0123456789_-.");
            context.addMessage("content:create:login", message);
        }

    }

    public void validateEmail(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String emailCandidate = (String) value;

        if (emailCandidate.indexOf("@") == -1) {
            ((UIInput) toValidate).setValid(false);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Bad e-mail",
                    "E-mail must contain a '@'");
            context.addMessage("content:create:email", message);
        }
    }

    public void validateCaptchaText(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String captchaResponse = (String) value;
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        Boolean isCorrect;

        /* remember that we need an id to validate */

        String captchaId = request.getSession().getId();
        isCorrect = true;
        /* Call the Service method */
        try {
            isCorrect = CaptchaServiceSingleton.getInstance().validateResponseForID(captchaId, captchaResponse);

        } catch (CaptchaServiceException e) {
            isCorrect = false;
        /* should not happen, may be thrown if the id is not valid */
        }
        if (!isCorrect) {
            ((UIInput) toValidate).setValid(false);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Incorrect Captcha Text",
                    "Please read the text carefully");
            context.addMessage("content:create:captchaText", message);
        }
    }

    /**
     * Generate a long, random string
     */
    private String generateActivationCode() {
        Random r = new Random();

        return Long.toString(Math.abs(r.nextLong()), 36) + Long.toString(Math.abs(r.nextLong()), 36);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public String getCaptchaText() {
        return captchaText;
    }

    public void setCaptchaText(String captchaText) {
        this.captchaText = captchaText;
    }

    public String getRegisterOutcome() {
        String ret = registerOutcome;
        registerOutcome = "";
        return ret;
    }

    public void setRegisterOutcome(String registerOutcome) {
        this.registerOutcome = registerOutcome;
    }
}
