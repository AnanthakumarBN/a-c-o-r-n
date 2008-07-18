package acorn.userManagement;

import acorn.db.EUser;
import acorn.db.EUserController;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import acorn.errorHandling.ErrorBean;

/**
 * 
 * @author dl236088
 */

public class UserDetailsBean {
    private EUser user;
    private String password;
    private String passwordConfirmation;
    private String name;
    private String surname;
    private String institution;
    private String email;
    
    /** Creates a new instance of UserDetailsBean */
    public UserDetailsBean() {
    }
    
    private void getUser() {
        user = UserManager.getCurrentUser();
        name = user.getName();
        surname = user.getSurname();
        institution = user.getInstitution();
        email = user.getEmail();
    }
    
    public String getTitle() {
        getUser();
        return "Account details";
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
    
    public String getInstitution() {
        return institution;
    }
    public void setInstitution(String institution) {
        this.institution = institution;
    }
    
    public String getLogin() {
        return user.getLogin();
    }
    
    public Date getDate() {
        return user.getDate();
    }
     
    public String getEmail() {
        return email;
    }    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void validateEmail(FacesContext context,
            UIComponent toValidate,
            Object value) 
    {
        String emailCandidate = (String) value;

        if (emailCandidate.indexOf("@") == -1) {
            ((UIInput) toValidate).setValid(false);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Bad e-mail",
                    "E-mail must contain a '@'");
            context.addMessage("content:updateForm:email", message);
        }
    }
    
    public String saveUser()
    {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            EUserController uc = new EUserController();
            user.setName(name);
            user.setSurname(surname);
            user.setInstitution(institution);
            user.setEmail(email);
            uc.mergeUser(user);
        } catch (Exception ex) {
            
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error updating user information!",
                    "Unexpected error when creating your account.  Please contact the system Administrator");
            context.addMessage(null, message);
            ErrorBean.printStackTrace(ex);
            return null;
        }
        return "userUpdated";
    }
    
    public String cancelUser() {
        getUser();
        return "cancel";
    }
    
    public String getPassword() {
        return user.getPasswd();
    }    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }    
    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
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
    
    private Boolean validatePasswordForm(FacesContext context) {

        Boolean isCorrect;

        isCorrect = true;
        
        if (!password.equals(passwordConfirmation)) {
            FacesMessage message = new FacesMessage("The specified passwords do not match.  Please try again");
            context.addMessage("content:create:password", message);
            isCorrect = false;
        }
        return isCorrect;
    }
    
    public String savePassword() {
        if (!validatePasswordForm(FacesContext.getCurrentInstance())) {
            return null;
        } else try {
            EUserController ec = new EUserController();
            user.setPasswd(password);
            ec.mergeUser(user);
            return "userUpdated";
        } catch (Exception e) {
            ErrorBean.printStackTrace(e);
            return null;
        }
    }
    
    public String cancelPassword() {
        this.password = null;
        this.passwordConfirmation = null;
        return "cancel";
    }
}
