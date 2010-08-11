<%-- 
    Document   : login
    Created on : Feb 29, 2008, 7:15:02 PM
    Author     : kuba
--%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<div id="content" class="content">
    <h1>Login</h1>

    <h:outputText style="color: green" value="#{UserManager.registerOutcome}"/>

    <h:messages style="color: red"
                showDetail="true"/>
    <h:form id="login">
        <h:panelGrid columns="2" border="0">
            Username: <h:inputText id="login" 
                         value="#{UserManager.login}"/>
            Password: <h:inputSecret id="password"
                           value="#{UserManager.password}"/>
        </h:panelGrid>
        <h:commandButton id="submit" 
                         type="submit"
                         value="Login"
                         action="#{UserManager.loginUser}"/>
    </h:form>
    <p>
        You can use the system as a guest without loging in, but registering and loging in gives access to additional features.
    </p>
</div>
