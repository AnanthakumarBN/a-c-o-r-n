<%-- 
    Document   : registerContent
    Created on : Feb 29, 2008, 5:49:41 PM
    Author     : kuba
--%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>


<div id="content" class="content">
    <h1>Create a new account</h1>
    <h:form id="create"> 
        <%--validator="#{UserManager.validateName}" --%>
        <h:panelGrid columns="3" border="0">
            First Name: <h:inputText id="name"       
                                     requiredMessage="*"
                                     value="#{UserManager.name}"
                                     required="true">
                <f:validateLength minimum="3" maximum="255"/>
            </h:inputText>                
            <h:message for="create:name" style="color: red"/>
            
            Last Name: <h:inputText id="surname"  
                                    requiredMessage="*"
                                    value="#{UserManager.surname}"
                                    required="true">
                <f:validateLength minimum="3" maximum="255"/>
            </h:inputText>                
            <h:message for="create:institution" style="color: red"/>
            
            Institution: <h:inputText id="institution"  
                                      requiredMessage="*"
                                      value="#{UserManager.institution}"
                                      required="false">
                <f:validateLength minimum="0" maximum="255"/>
            </h:inputText>               
            <h:message for="create:institution" style="color: red"/>
            
            Login: <h:inputText id="login" 
                                requiredMessage="*"
                                value="#{UserManager.login}"
                                validator="#{UserManager.validateLogin}"
                                required="true">
                <f:validateLength minimum="3" maximum="255"/>
            </h:inputText>
            <h:message for="create:login" style="color: red"/>
            
            E-mail: <h:inputText id="email" 
                                 requiredMessage="*"
                                 value="#{UserManager.email}"
                                 validator="#{UserManager.validateEmail}"
                                 required="true">
                 <f:validateLength minimum="3" maximum="255"/>
            </h:inputText>
            <h:message for="create:email" style="color: red"/>
            
            Password: <h:inputSecret id="password"    
                                     requiredMessage="*"
                                     value="#{UserManager.password}"
                                     required="true">
                <f:validateLength minimum="3" maximum="255"/>                
            </h:inputSecret>
            
            <h:message for="create:password" style="color: red"/>
            Password (verify): <h:inputSecret id="passwordConfirmation"   
                                              requiredMessage="*"
                                              value="#{UserManager.passwordConfirmation}"
                                              required="true"
                                              validator="#{UserManager.passwordConfirmationValidator}">
                <f:attribute name="passwordId" value="content:create:password"/>
                <%--f:validateLength minimum="3" maximum="255"/--%>
            </h:inputSecret>
            <h:message for="create:passwordConfirmation" style="color: red"/>
        </h:panelGrid>
        
        <h:graphicImage url="/jcaptcha" />
        
        <h:panelGrid columns="3" border="0">
         
            Verification string: <h:inputText id="captchaText" 
                                              requiredMessage="*" 
                                              validator="#{UserManager.validateCaptchaText}"
                                              required="true"
                                              value="#{UserManager.captchaText}">
                <f:validateLength minimum="2" maximum="20"/>
            </h:inputText>
            <h:message for="create:captchaText" style="color: red"/>
        </h:panelGrid>
            
        <h:commandButton id="submit" 
                         value="Create"
                         action="#{UserManager.createUser}"/>
        <h:messages style="color: red" globalOnly="true"/>
    </h:form>
</div>
