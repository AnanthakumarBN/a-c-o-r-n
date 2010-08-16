<%-- 
    Document   : taskListContent
    Created on : Mar 14, 2008, 2:53:41 PM
    Author     : kuba, lukasz
--%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="a4j" uri="https://ajax4jsf.dev.java.net/ajax"%>

<div id="content" class="content">
    <h1>Task List</h1>
    
    <!-- HACK -->
    <h:form id="initForm">
        <h:outputText value="#{TaskListBean.init}" />
    </h:form>
    
    <div class="filterBox">
    
    <h:form id="filterForm">
        <a4j:region>
            <h:panelGrid columns="2" border="0">
                Task name filter:
                <h:inputText value="#{TaskListBean.taskNameFilter}" >
                    <a4j:support event="onkeyup" reRender="tableForm" action="#{TaskListBean.filterList}" status="filterStatus" />
                </h:inputText>
                
                Model name filter:
                <h:inputText value="#{TaskListBean.modelNameFilter}" >
                    <a4j:support event="onkeyup" reRender="tableForm" action="#{TaskListBean.filterList}" status="filterStatus" />
                </h:inputText>
                
                <h:panelGroup rendered="#{!UserManager.isGuest}">
                    Show my tasks:
                </h:panelGroup>
                <h:panelGroup rendered="#{!UserManager.isGuest}">
                    <h:selectBooleanCheckbox value="#{TaskListBean.mine}">
                        <a4j:support 
                            event="onclick"
                            reRender="tableForm"
                            action="#{TaskListBean.fetchList}" >
                        </a4j:support>
                    </h:selectBooleanCheckbox>
                </h:panelGroup>
                
                Show shared tasks:
                <h:selectBooleanCheckbox value="#{TaskListBean.shared}">
                    <a4j:support event="onclick"
                        reRender="tableForm"
                        action="#{TaskListBean.fetchList}" >
                    </a4j:support>
                </h:selectBooleanCheckbox>
                
                <h:panelGroup rendered="#{UserManager.isAdmin}">
                    Show all other tasks:
                </h:panelGroup>
                <h:panelGroup rendered="#{UserManager.isAdmin}">
                    <h:selectBooleanCheckbox value="#{TaskListBean.others}">
                        <a4j:support event="onclick"
                            reRender="tableForm"
                            action="#{TaskListBean.fetchList}" >
                        </a4j:support>
                    </h:selectBooleanCheckbox>
                </h:panelGroup>
            </h:panelGrid>
            
            <br/>
            
            <a4j:commandButton value="Update filter" reRender="tableForm" action="#{TaskListBean.filterList}" type="Submit"> 
                <a4j:actionparam name="shared" value="#{param['shared']}" />
            </a4j:commandButton> &nbsp;
            
            <a4j:status id="filterStatus" startText="Searching..." stopText=""/>
        </a4j:region>
    </h:form>
        
    </div>
    
    <div class="tableBox">
    
    <h:form id="tableForm">
        
        <div class="navigationBox">
        
        <a4j:commandLink styleClass="navArrow" reRender="tableForm" action="#{TaskListBean.firstPage}">
            <h:graphicImage value="/chrome/nav_fst.gif" />
        </a4j:commandLink>
        <a4j:commandLink styleClass="navArrow" reRender="tableForm" action="#{TaskListBean.prevPage}">
            <h:graphicImage value="/chrome/nav_bwd.gif" />
        </a4j:commandLink>
       
        <span style="vertical-align: 42%">
        <h:outputText  id="results" value="#{TaskListBean.resultsString}" />
        </span>
        
        <a4j:commandLink styleClass="navArrow" reRender="tableForm" action="#{TaskListBean.nextPage}">
            <h:graphicImage value="/chrome/nav_fwd.gif" />
        </a4j:commandLink>
        <a4j:commandLink styleClass="navArrow" reRender="tableForm" action="#{TaskListBean.lastPage}">
            <h:graphicImage value="/chrome/nav_lst.gif" />
        </a4j:commandLink>
        
        <div class="navigationElement">
        
        Number of elements on page:
        <a4j:commandLink id="rows10" reRender="tableForm" action="#{TaskListBean.Rows}" value="10">
            <a4j:actionparam name="rows" value="10" />
        </a4j:commandLink>
        ..
        <a4j:commandLink id="rows30" reRender="tableForm" action="#{TaskListBean.Rows}" value="30">
            <a4j:actionparam name="rows" value="30" />
        </a4j:commandLink>
        ..
        <a4j:commandLink id="rows50" reRender="tableForm" action="#{TaskListBean.Rows}" value="50">
            <a4j:actionparam name="rows" value="50" />
        </a4j:commandLink>
        ..
        <a4j:commandLink id="rows100" reRender="tableForm" action="#{TaskListBean.Rows}" value="100">
            <a4j:actionparam name="rows" value="100" />
        </a4j:commandLink>
        ..
        <a4j:commandLink id="rows300" reRender="tableForm" action="#{TaskListBean.Rows}" value="300">
            <a4j:actionparam name="rows" value="300" />
        </a4j:commandLink>
        
        </div>
        
        </div>
        
        <h:dataTable 
            id="filteredTaskList" 
            var="task" 
            value="#{TaskListBean.list}" 
            width="100%"
            headerClass="tableHeader"
            columnClasses="taskName, taskModel, taskMethod, taskSubmissionDate, taskStatus, taskShared, taskDelete"
            rowClasses="tableRow1, tableRow2">
            <h:column>
                <f:facet name="header">
                    <h:panelGroup>
                        Name
                        <a4j:commandLink 
                            styleClass="ordArrow" 
                            reRender="filteredTaskList" 
                            action="#{TaskListBean.sortAsc}">
                            <h:graphicImage value="/chrome/nav_ordasc.gif" />
                            <a4j:actionparam name="sort" value="0" />
                        </a4j:commandLink>
                        <a4j:commandLink 
                            styleClass="ordArrow" 
                            reRender="filteredTaskList" 
                            action="#{TaskListBean.sortDesc}">
                            <h:graphicImage value="/chrome/nav_orddesc.gif" />
                            <a4j:actionparam name="sort" value="0" />
                        </a4j:commandLink>
                    </h:panelGroup>
                </f:facet>
                
                <h:outputLink value="#{facesContext.externalContext.requestContextPath}/taskDetails.jsf?taskID=#{task.id}">
                    <h:outputText value="#{task.name}"/>
                </h:outputLink>
            </h:column>
            
            <h:column>
                <f:facet name="header">
                    <h:panelGroup>
                        Model
                        <a4j:commandLink 
                            styleClass="ordArrow" 
                            reRender="filteredTaskList" 
                            action="#{TaskListBean.sortAsc}">
                            <h:graphicImage value="/chrome/nav_ordasc.gif" />
                            <a4j:actionparam name="sort" value="1" />
                        </a4j:commandLink>
                        <a4j:commandLink 
                            styleClass="ordArrow" 
                            reRender="filteredTaskList" 
                            action="#{TaskListBean.sortDesc}">
                            <h:graphicImage value="/chrome/nav_orddesc.gif" />
                            <a4j:actionparam name="sort" value="1" />
                        </a4j:commandLink>
                    </h:panelGroup>
                </f:facet>
                
                <h:outputLink value="#{facesContext.externalContext.requestContextPath}/modelDetails.jsf?modelID=#{task.model.id}">
                    <h:outputText value="#{task.model.name}" />
                </h:outputLink>
            </h:column>
          
            <h:column>
                <f:facet name="header">
                    <h:panelGroup>
                        Method
                        <a4j:commandLink 
                            styleClass="ordArrow" 
                            reRender="filteredTaskList" 
                            action="#{TaskListBean.sortAsc}">
                            <h:graphicImage value="/chrome/nav_ordasc.gif" />
                            <a4j:actionparam name="sort" value="4" />
                        </a4j:commandLink>
                        <a4j:commandLink 
                            styleClass="ordArrow" 
                            reRender="filteredTaskList" 
                            action="#{TaskListBean.sortDesc}">
                            <h:graphicImage value="/chrome/nav_orddesc.gif" />
                            <a4j:actionparam name="sort" value="4" />
                        </a4j:commandLink>
                    </h:panelGroup>
                </f:facet>
                <h:outputText value="#{task.method.ident}" />
            </h:column>
          
            <h:column>
                <f:facet name="header">
                    <h:panelGroup>
                        Submission
                        <a4j:commandLink 
                            styleClass="ordArrow" 
                            reRender="filteredTaskList" 
                            action="#{TaskListBean.sortAsc}">
                            <h:graphicImage value="/chrome/nav_ordasc.gif" />
                            <a4j:actionparam name="sort" value="2" />
                        </a4j:commandLink>
                        <a4j:commandLink 
                            styleClass="ordArrow" 
                            reRender="filteredTaskList" 
                            action="#{TaskListBean.sortDesc}">
                            <h:graphicImage value="/chrome/nav_orddesc.gif" />
                            <a4j:actionparam name="sort" value="2" />
                        </a4j:commandLink>
                    </h:panelGroup>
                </f:facet>
                    
                <h:outputText value="#{task.date}">
                    <f:convertDateTime pattern="kk:mm, dd/MM/yyyy" />
                </h:outputText>
            </h:column>
            
            <h:column>
                <f:facet name="header">
                    <h:panelGroup>
                        Status
                        <a4j:commandLink 
                            styleClass="ordArrow" 
                            reRender="filteredTaskList" 
                            action="#{TaskListBean.sortAsc}">
                            <h:graphicImage value="/chrome/nav_ordasc.gif" />
                            <a4j:actionparam name="sort" value="3" />
                        </a4j:commandLink>
                        <a4j:commandLink 
                            styleClass="ordArrow" 
                            reRender="filteredTaskList" 
                            action="#{TaskListBean.sortDesc}">
                            <h:graphicImage value="/chrome/nav_orddesc.gif" />
                            <a4j:actionparam name="sort" value="3" />
                        </a4j:commandLink>
                    </h:panelGroup>
                </f:facet>
               
                <h:outputText rendered="#{!task.inProgress}" value="#{task.status}" escape="false"/>
                <h:panelGroup rendered="#{task.inProgress}">
                    <h:outputText value="#{100*task.progress}">
                        <f:convertNumber type="number" maxFractionDigits="0"/>
                    </h:outputText>
                    <h:outputText value="% completed"/>
                </h:panelGroup>
            </h:column>
            
            <h:column>
                <f:facet name="header">
                    <h:panelGroup>
                        Shared
                        <a4j:commandLink
                            styleClass="ordArrow"
                            reRender="filteredTaskList"
                            action="#{TaskListBean.sortAsc}">
                            <h:graphicImage value="/chrome/nav_ordasc.gif" />
                            <a4j:actionparam name="sort" value="5" />
                        </a4j:commandLink>
                        <a4j:commandLink
                            styleClass="ordArrow"
                            reRender="filteredTaskList"
                            action="#{TaskListBean.sortDesc}">
                            <h:graphicImage value="/chrome/nav_orddesc.gif" />
                            <a4j:actionparam name="sort" value="5" />
                        </a4j:commandLink>
                    </h:panelGroup>
                </f:facet>
                
                <h:outputText value="#{task.shared}" />    
            </h:column>
            
            <h:column>
                <f:facet name="header">
                    <h:panelGroup>
                        Delete
                        <a4j:commandLink
                            styleClass="ordArrow"
                            reRender="filteredTaskList"
                            action="#{TaskListBean.sortAsc}">
                            <h:graphicImage value="/chrome/nav_ordasc.gif" />
                            <a4j:actionparam name="sort" value="6" />
                        </a4j:commandLink>
                        <a4j:commandLink
                            styleClass="ordArrow"
                            reRender="filteredTaskList"
                            action="#{TaskListBean.sortDesc}">
                            <h:graphicImage value="/chrome/nav_orddesc.gif" />
                            <a4j:actionparam name="sort" value="6" />
                        </a4j:commandLink>
                    </h:panelGroup>
                </f:facet>
            
                <a4j:commandLink rendered="#{(task.ownerId == TaskListBean.currentUserId) || TaskListBean.deleteAllUser}" id="delete" action="#{TaskListBean.deleteTask}" reRender="tableForm"  value="delete">
                    <a4j:actionparam name="taskID" value="#{task.id}" />
                </a4j:commandLink>
            </h:column>
        </h:dataTable>
    </h:form>
    
    </div>
</div>