<%-- 
    Document   : registerContent
    Created on : Feb 29, 2008, 5:49:41 PM
    Author     : kuba
--%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<div id="content" class="content">
        <h1><h:outputText value="#{UserDetailsBean.title}"/></h1>
        <h:form id="updateForm"> 
            <%--validator="#{UserManager.validateName}" --%>
            <h:panelGrid columns="3" border="0">
                Login: <h:inputText disabled="true"
                                    id="login" 
                                    requiredMessage="*"
                                    value="#{UserDetailsBean.login}"
                                    required="true">
                    <f:validateLength minimum="3" maximum="255"/>
                </h:inputText>
                <h:message for="updateForm:login" style="color: red"/>
                
                Creation date: <h:inputText disabled="true"
                                    id="date" 
                                    requiredMessage="*"
                                    value="#{UserDetailsBean.date}"
                                    required="true">
                </h:inputText>
                <h:message for="updateForm:date" style="color: red"/>
                
                First Name: <h:inputText id="name"       
                                         requiredMessage="*"
                                         value="#{UserDetailsBean.name}"
                                         required="true">
                    <f:validateLength minimum="3" maximum="255"/>
                </h:inputText>                
                <h:message for="updateForm:name" style="color: red"/>
                
                Last Name: <h:inputText id="surname"  
                                        requiredMessage="*"
                                        value="#{UserDetailsBean.surname}"
                                        required="true">
                    <f:validateLength minimum="3" maximum="255"/>
                </h:inputText>
                <h:message for="updateForm:surname" style="color: red"/>
                
                Institution: <h:inputText id="institution"  
                                        requiredMessage="*"
                                        value="#{UserDetailsBean.institution}"
                                        required="false">
                    <f:validateLength minimum="0" maximum="255"/>
                </h:inputText>
                <h:message for="updateForm:institution" style="color: red"/>
                
                E-mail: <h:inputText id="email" 
                                     requiredMessage="*"
                                     value="#{UserDetailsBean.email}"
                                     validator="#{UserDetailsBean.validateEmail}"
                                     required="true">
                    <f:validateLength minimum="3" maximum="255"/>
                </h:inputText>
                <h:message for="updateForm:email" style="color: red"/>
                </h:panelGrid>
                <br/>
                <h:commandButton value="Save"
                            action="#{UserDetailsBean.saveUser}"/>
                <h:commandButton value="Cancel"
                            action="#{UserDetailsBean.cancelUser}"/>                
                </h:form>
                <br/><br/>
            <h:form id="passwordForm">
                <h:panelGrid columns="3" border="0">
                Password: <h:inputSecret id="password"    
                                         requiredMessage="*"
                                         value="#{UserDetailsBean.password}"
                                         required="true">
                    <f:validateLength minimum="3" maximum="255"/>
                    
                </h:inputSecret>
                <h:message for="passwordForm:password" style="color: red"/>
                Password (verify): <h:inputSecret id="passwordConfirmation"   
                                                  requiredMessage="*"
                                                  value="#{UserDetailsBean.passwordConfirmation}"
                                                  required="true"
                                                  validator="#{UserDetailsBean.passwordConfirmationValidator}">
                    <f:attribute name="passwordId" value="content:passwordForm:password"/>
                </h:inputSecret>
                <h:message for="passwordForm:passwordConfirmation" style="color: red"/>
                
            </h:panelGrid>
            <br/>
            <h:commandButton type="submit" 
                             value="Save"
                             action="#{UserDetailsBean.savePassword}"/>
                             
            <h:commandButton id="cancel" 
                             value="Cancel"
                             action="#{UserDetailsBean.cancelPassword}"/>
            <h:messages style="color: red" globalOnly="true"/>
        </h:form>
</div>
