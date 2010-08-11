<%-- 
    Document   : menu
    Created on : Feb 29, 2008, 12:43:09 PM
    Author     : kuba, lukasz
--%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<div id="menu" class="menu">
    <br>
    <!-- Guest Menu -->
    <h:panelGroup rendered="#{UserManager.isGuest}">
        <ul class="slant">
            <li><h:outputLink value="homepage.jsf">
                <b class="p1"></b><b class="p2"></b><b class="p3"></b><b class="p4"></b><b class="p5"></b>
                <em>Homepage</em> 
                <span></span>
                </h:outputLink>
            </li>
            <li><h:outputLink value="modelList.jsf">
                <b class="p1"></b><b class="p2"></b><b class="p3"></b><b class="p4"></b><b class="p5"></b>
                <em>Models</em>
                <span></span>
                </h:outputLink>
            </li>
            <li><h:outputLink value="taskList.jsf">
                <b class="p1"></b><b class="p2"></b><b class="p3"></b><b class="p4"></b><b class="p5"></b>
                <em>Tasks</em>
                <span></span>
                </h:outputLink>
            </li>
            <li><h:outputLink value="login.jsf">
                <b class="p1"></b><b class="p2"></b><b class="p3"></b><b class="p4"></b><b class="p5"></b>
                <em>Login</em>
                <span></span>
                </h:outputLink>
            </li>
            <li><h:outputLink value="register.jsf">
                <b class="p1"></b><b class="p2"></b><b class="p3"></b><b class="p4"></b><b class="p5"></b>
                <em>Register</em>
                <span></span>
                </h:outputLink>
            </li>
        </ul>
    </h:panelGroup>
    
    <!-- Normal User Menu -->
    
    <h:panelGroup rendered="#{UserManager.isNormal}">
        <ul class="slant">
            <li><h:outputLink value="homepage.jsf">
                <b class="p1"></b><b class="p2"></b><b class="p3"></b><b class="p4"></b><b class="p5"></b>
                <em>Homepage</em> 
                <span></span>
                </h:outputLink>
            </li>
            <li><h:outputLink value="modelList.jsf">
                <b class="p1"></b><b class="p2"></b><b class="p3"></b><b class="p4"></b><b class="p5"></b>
                <em>Models</em>
                <span></span>
                </h:outputLink>
            </li>
            <li><h:outputLink value="taskList.jsf">
                <b class="p1"></b><b class="p2"></b><b class="p3"></b><b class="p4"></b><b class="p5"></b>
                <em>Tasks</em>
                <span></span>
                </h:outputLink>
            </li>
            <li><h:outputLink value="userDetails.jsf">
                <b class="p1"></b><b class="p2"></b><b class="p3"></b><b class="p4"></b><b class="p5"></b>
                <em>My Account</em>
                <span></span>
                </h:outputLink>
            </li>
            <li><h:form style = "margin-left=200px">
                    <h:commandLink action="#{UserManager.logoutUser}">
                        <b class="p1"></b><b class="p2"></b><b class="p3"></b><b class="p4"></b><b class="p5"></b>
                        <em>Log Out</em>
                        <span></span>
                    </h:commandLink>
                </h:form>
            </li>
        </ul>
    </h:panelGroup>
    
    <!-- Admin Menu -->
    
    <h:panelGroup rendered="#{UserManager.isAdmin}">
        <ul class="slant">
            <li><h:outputLink value="homepage.jsf">
                <b class="p1"></b><b class="p2"></b><b class="p3"></b><b class="p4"></b><b class="p5"></b>
                <em>Homepage</em> 
                <span></span>
                </h:outputLink>
            </li>
            <li><h:outputLink value="modelList.jsf">
                <b class="p1"></b><b class="p2"></b><b class="p3"></b><b class="p4"></b><b class="p5"></b>
                <em>Models</em>
                <span></span>
                </h:outputLink>
            </li>
            <li><h:outputLink value="taskList.jsf">
                <b class="p1"></b><b class="p2"></b><b class="p3"></b><b class="p4"></b><b class="p5"></b>
                <em>Tasks</em>
                <span></span>
                </h:outputLink>
            </li>
            <li><h:outputLink value="addModel.jsf">
                <b class="p1"></b><b class="p2"></b><b class="p3"></b><b class="p4"></b><b class="p5"></b>
                <em>Add Model</em>
                <span></span>
                </h:outputLink>
            </li>
            <!--<li><h:outputLink value="addVisualization.jsf">
                <b class="p1"></b><b class="p2"></b><b class="p3"></b><b class="p4"></b><b class="p5"></b>
                <em>Add pathway map</em>
                <span></span>
                </h:outputLink>
            </li>-->
            <li><h:outputLink value="userList.jsf">
                <b class="p1"></b><b class="p2"></b><b class="p3"></b><b class="p4"></b><b class="p5"></b>
                <em>User List</em>
                <span></span>
                </h:outputLink>
            </li>
            <li><h:outputLink value="userDetails.jsf">
                <b class="p1"></b><b class="p2"></b><b class="p3"></b><b class="p4"></b><b class="p5"></b>
                <em>My Account</em>
                <span></span>
                </h:outputLink>
            </li>
            <li><h:form style="margin-left=200px">
                    <h:commandLink action="#{UserManager.logoutUser}">
                        <b class="p1"></b><b class="p2"></b><b class="p3"></b><b class="p4"></b><b class="p5"></b>
                        <em>Log Out</em>
                        <span></span>
                    </h:commandLink>
                </h:form>
            </li>
        </ul>        
    </h:panelGroup>
</div>