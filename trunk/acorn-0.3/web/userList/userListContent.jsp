<%-- 
    Document   : user_list_content
    Created on : 2008-03-07, 13:17:34
    Author     : dl236088
--%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="a4j" uri="https://ajax4jsf.dev.java.net/ajax"%>

<div id="content" class="content">
    <h1><h:outputText value="#{UserListBean.title}"/></h1>
    
    <h:form id="list">
        <div class="filterBox">
        
        <a4j:region id="filterRegion">
            <h:panelGrid columns="2">
                Login filter:
                <h:inputText value="#{UserListBean.loginFilter}" >
                    <a4j:support event="onkeyup"
                        reRender="users" 
                        action="#{UserListBean.filterList}" >
                    </a4j:support>
                </h:inputText>
                Name filter:
                <h:inputText value="#{UserListBean.nameFilter}" >
                    <a4j:support event="onkeyup"
                        reRender="users" 
                        action="#{UserListBean.filterList}" >
                    </a4j:support>
                </h:inputText>
                Surname filter:
                <h:inputText value="#{UserListBean.surnameFilter}" >
                    <a4j:support event="onkeyup"
                        reRender="users" 
                        action="#{UserListBean.filterList}" >
                    </a4j:support>
                </h:inputText>
            </h:panelGrid>
            
            <br/>            
            
            <a4j:commandButton value="Update filter" 
                reRender="users" 
                action="#{UserListBean.filterList}" 
                type="Submit" /> &nbsp;
                
            <%--
            <a4j:commandButton value="Refresh table" 
                reRender="users" 
                action="#{UserListBean.fetchAndFilterList}" /> &nbsp;
            --%>
            
            <a4j:status id="commonstatus" startText=" Searching..." stopText=""/>
        </a4j:region>
            
        </div>
        
        <div class="tableBox">
            
        <h:panelGroup id="users">
            <div class="navigationBox">
            
            <div class="navigationElement">
            
            <a4j:commandLink styleClass="navArrow" reRender="users" action="#{UserListBean.firstPage}">
                <h:graphicImage value="/chrome/nav_fst.gif" />
            </a4j:commandLink>
            <a4j:commandLink styleClass="navArrow" reRender="users" action="#{UserListBean.prevPage}">
                <h:graphicImage value="/chrome/nav_bwd.gif" />
            </a4j:commandLink>
            
            <span style="vertical-align: 42%">
                <h:outputText  id="results" value="#{UserListBean.resultsString}" />
            </span>
        
            <a4j:commandLink styleClass="navArrow" reRender="users" action="#{UserListBean.nextPage}">
                <h:graphicImage value="/chrome/nav_fwd.gif" />
            </a4j:commandLink>
            <a4j:commandLink styleClass="navArrow" reRender="users" action="#{UserListBean.lastPage}">
                <h:graphicImage value="/chrome/nav_lst.gif" />
            </a4j:commandLink>
            
            </div>
            
            <div class="navigationElement">
    
            Number of elements on page: &nbsp;
            <a4j:commandLink id="rows10" reRender="users" action="#{UserListBean.rows}" value="10">
                <a4j:actionparam name="rows" value="10" />
            </a4j:commandLink>
            ..
            <a4j:commandLink id="rows30" reRender="users" action="#{UserListBean.rows}" value="30">
                <a4j:actionparam name="rows" value="30" />
            </a4j:commandLink>
            ..
            <a4j:commandLink id="rows50" reRender="users" action="#{UserListBean.rows}" value="50">
                <a4j:actionparam name="rows" value="50" />
            </a4j:commandLink>
            ..
            <a4j:commandLink id="rows100" reRender="users" action="#{UserListBean.rows}" value="100">
                <a4j:actionparam name="rows" value="100" />
            </a4j:commandLink>
            ..
            <a4j:commandLink id="rows300" reRender="users" action="#{UserListBean.rows}" value="300">
                <a4j:actionparam name="rows" value="300" />
            </a4j:commandLink>
            
            </div>

            </div>

            <h:dataTable 
                id="usersTable"
                var="user" 
                value="#{UserListBean.list}"
                width="100%"
                headerClass="tableHeader"
                columnClasses="userLogin, userName, userSurname, userEmail, userInstitution, userDate, userStatus, userDelete"
                rowClasses="tableRow1, tableRow2">
                <h:column>
                    <f:facet name="header">
                        <h:panelGroup>
                            Login
                            <a4j:commandLink 
                                styleClass="ordArrow" 
                                reRender="users" 
                                action="#{UserListBean.sort}">
                                <a4j:actionparam name="up" value="True" />
                                <a4j:actionparam name="comparator" value="Login" />
                                <h:graphicImage value="/chrome/nav_ordasc.gif" />
                            </a4j:commandLink>
                            <a4j:commandLink 
                                styleClass="ordArrow" 
                                reRender="users" 
                                action="#{UserListBean.sort}">
                                <a4j:actionparam name="up" value="False" />
                                <a4j:actionparam name="comparator" value="Login" />
                                <h:graphicImage value="/chrome/nav_orddesc.gif" />
                            </a4j:commandLink>
                        </h:panelGroup>
                    </f:facet>                    
                    <h:outputLink 
                        value="userDetails.jsp">
                        <f:param name="userID" value="#{user.id}" />
                        <h:outputText value="#{user.login}"/>
                    </h:outputLink>
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:panelGroup>
                            Name
                            <a4j:commandLink 
                                styleClass="ordArrow" 
                                reRender="users" 
                                action="#{UserListBean.sort}">
                                <a4j:actionparam name="up" value="True" />
                                <a4j:actionparam name="comparator" value="Name" />
                                <h:graphicImage value="/chrome/nav_ordasc.gif" />
                            </a4j:commandLink>
                            <a4j:commandLink 
                                styleClass="ordArrow" 
                                reRender="users" 
                                action="#{UserListBean.sort}">
                                <a4j:actionparam name="up" value="False" />
                                <a4j:actionparam name="comparator" value="Name" />
                                <h:graphicImage value="/chrome/nav_orddesc.gif" />
                            </a4j:commandLink>
                        </h:panelGroup>
                    </f:facet>
                    <h:outputText value="#{user.name}" />
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:panelGroup>
                            Surname
                            <a4j:commandLink 
                                styleClass="ordArrow" 
                                reRender="users" 
                                action="#{UserListBean.sort}">
                                <a4j:actionparam name="up" value="True" />
                                <a4j:actionparam name="comparator" value="Surname" />
                                <h:graphicImage value="/chrome/nav_ordasc.gif" />
                            </a4j:commandLink>
                            <a4j:commandLink 
                                styleClass="ordArrow" 
                                reRender="users" 
                                action="#{UserListBean.sort}">
                                <a4j:actionparam name="up" value="False" />
                                <a4j:actionparam name="comparator" value="Surname" />
                                <h:graphicImage value="/chrome/nav_orddesc.gif" />
                            </a4j:commandLink>
                        </h:panelGroup>
                     </f:facet>
                     <h:outputText value="#{user.surname}" />
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:panelGroup>
                            Email
                            <a4j:commandLink 
                                styleClass="ordArrow" 
                                reRender="users" 
                                action="#{UserListBean.sort}">
                                <a4j:actionparam name="up" value="True" />
                                <a4j:actionparam name="comparator" value="EMail" />
                                <h:graphicImage value="/chrome/nav_ordasc.gif" />
                            </a4j:commandLink>
                            <a4j:commandLink 
                                styleClass="ordArrow" 
                                reRender="users" 
                                action="#{UserListBean.sort}">
                                <a4j:actionparam name="up" value="False" />
                                <a4j:actionparam name="comparator" value="Email" />
                                <h:graphicImage value="/chrome/nav_orddesc.gif" />
                            </a4j:commandLink>
                        </h:panelGroup>
                     </f:facet>
                     <h:outputText value="#{user.email}" />
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:panelGroup>
                            Institution
                            <a4j:commandLink 
                                styleClass="ordArrow" 
                                reRender="users" 
                                action="#{UserListBean.sort}">
                                <a4j:actionparam name="up" value="True" />
                                <a4j:actionparam name="comparator" value="Institution" />
                                <h:graphicImage value="/chrome/nav_ordasc.gif" />
                            </a4j:commandLink>
                            <a4j:commandLink 
                                styleClass="ordArrow" 
                                reRender="users" 
                                action="#{UserListBean.sort}">
                                <a4j:actionparam name="up" value="False" />
                                <a4j:actionparam name="comparator" value="Institution" />
                                <h:graphicImage value="/chrome/nav_orddesc.gif" />
                            </a4j:commandLink>
                        </h:panelGroup>
                     </f:facet>
                     <h:outputText value="#{user.institution}" />
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:panelGroup>
                            Creation Date
                            <a4j:commandLink 
                                styleClass="ordArrow" 
                                reRender="users" 
                                action="#{UserListBean.sort}">
                                <a4j:actionparam name="up" value="True" />
                                <a4j:actionparam name="comparator" value="Date" />
                                <h:graphicImage value="/chrome/nav_ordasc.gif" />
                            </a4j:commandLink>
                            <a4j:commandLink 
                                styleClass="ordArrow" 
                                reRender="users" 
                                action="#{UserListBean.sort}">
                                <a4j:actionparam name="up" value="False" />
                                <a4j:actionparam name="comparator" value="Date" />
                                <h:graphicImage value="/chrome/nav_orddesc.gif" />
                            </a4j:commandLink>
                        </h:panelGroup>
                     </f:facet>
                     <h:outputText value="#{user.date}" />
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:panelGroup>
                            Status
                            <a4j:commandLink 
                                styleClass="ordArrow" 
                                reRender="users" 
                                action="#{UserListBean.sort}">
                                <a4j:actionparam name="up" value="True" />
                                <a4j:actionparam name="comparator" value="Status" />
                                <h:graphicImage value="/chrome/nav_ordasc.gif" />
                            </a4j:commandLink>
                            <a4j:commandLink 
                                styleClass="ordArrow" 
                                reRender="users" 
                                action="#{UserListBean.sort}">
                                <a4j:actionparam name="up" value="False" />
                                <a4j:actionparam name="comparator" value="Status" />
                                <h:graphicImage value="/chrome/nav_orddesc.gif" />
                            </a4j:commandLink>
                        </h:panelGroup>
                    </f:facet>
                    <h:outputText value="#{user.status}" />
                    <h:panelGroup rendered="#{user.enableActivateUser}">
                        <br/> <a4j:commandLink action="#{UserListBean.activateUser}" value="activate" reRender="users" >
                            <a4j:actionparam name="userID" value="#{user.id}"/>
                        </a4j:commandLink>
                    </h:panelGroup>
                    <h:panelGroup rendered="#{user.enableUnbanUser}">
                        <br/> <a4j:commandLink action="#{UserListBean.unbanUser}" value="unban" reRender="users" >
                            <a4j:actionparam name="userID" value="#{user.id}"/>
                        </a4j:commandLink>
                    </h:panelGroup>
                    <h:panelGroup rendered="#{user.enableBanUser}">
                        <br/> <a4j:commandLink action="#{UserListBean.banUser}" value="ban" reRender="users" >
                            <a4j:actionparam name="userID" value="#{user.id}"/>
                        </a4j:commandLink>
                    </h:panelGroup>
                    <h:panelGroup rendered="#{user.enableMakeAdmin}">
                        <br/> <a4j:commandLink action="#{UserListBean.makeAdmin}" value="make admin" reRender="users" >
                            <a4j:actionparam name="userID" value="#{user.id}"/>
                        </a4j:commandLink>
                    </h:panelGroup>
                    <h:panelGroup rendered="#{user.enableMakeUser}">
                        <br/> <a4j:commandLink action="#{UserListBean.makeUser}" value="make user" reRender="users" >
                            <a4j:actionparam name="userID" value="#{user.id}"/>
                        </a4j:commandLink>
                    </h:panelGroup>
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="" />
                     </f:facet>
                     <h:panelGroup rendered="#{user.enableDeleteUser}">
                         <a4j:commandLink action="#{UserListBean.deleteUser}" value="delete" reRender="users" >
                             <a4j:actionparam name="userID" value="#{user.id}"/>
                         </a4j:commandLink>
                     </h:panelGroup>
                </h:column>
                </h:dataTable>
            </h:panelGroup>     
        </div>    
    </h:form> 
</div>

