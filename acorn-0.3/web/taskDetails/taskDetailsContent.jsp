<%-- 
    Document   : TaskDetailsContent
    Created on : 2008-03-14, 13:10:49
    Author     : lukasz
--%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="a4j" uri="https://ajax4jsf.dev.java.net/ajax"%>

<div id="content" class="content">
    <!-- HACK -->
    <h:form id="taskDetailsInitForm">
        <h:outputText value="#{TaskDetailsBean.init}" />
    </h:form>
    
    <h1>Task Details</h1>
        
    <h:panelGroup rendered="#{TaskDetailsBean.addressValid}">
        <!-- Information -->
        
        <div class="taskInfo">
        
        <h2>Information</h2>
        
        <h:form id="infoForm">
            <a4j:poll id = "refreshId" reRender = "infoForm" interval = "5000">
                <a4j:actionparam name="taskID" value="#{param['taskID']}" />
            </a4j:poll>
            
            <h:panelGrid columns="2" border="0" width="40%">
                Name: 
                <h:outputText value="#{TaskDetailsBean.task.name}" />
                
                Submission date:
                <h:outputText value="#{TaskDetailsBean.task.date}">
                    <f:convertDateTime pattern="kk:mm, dd/MM/yyyy" />
                </h:outputText>
                
                <%--
                Last change:
                <h:outputText value="#{TaskDetailsBean.task.lastChange}" />
                --%>

                Status:
                <h:outputText value="#{TaskDetailsBean.task.status}" />
                    
                Information:
                <h:outputText id="statusInfo" value="#{TaskDetailsBean.info}" />
                
                Shared:
                <h:selectBooleanCheckbox disabled="#{TaskDetailsBean.isTaskNotMine}" value="#{TaskDetailsBean.task.shared}" />
                    
                Model:
                <h:outputLink
                    value="#{facesContext.externalContext.requestContextPath}/modelDetails.jsf?modelID=#{TaskDetailsBean.task.model.id}">
                    <h:outputText value="#{TaskDetailsBean.task.model.name}"/>
                </h:outputLink>
                
                Method:
                <h:outputText value="#{TaskDetailsBean.task.method.name}" />
            </h:panelGrid>
            <br/>
            
            <h:panelGroup id="taskModifiers" rendered="#{TaskDetailsBean.isTaskMine}">
                <a4j:commandLink
                    action="#{TaskDetailsBean.updateTask}" 
                    value="Save Changes"
                    reRender="infoForm">
                    <a4j:actionparam name="taskID" value="#{param['taskID']}" />
                </a4j:commandLink> &nbsp;
            
                <a4j:commandLink 
                    action="#{TaskDetailsBean.discardChanges}" 
                    value="Discard Changes" 
                    reRender="infoForm">
                    <a4j:actionparam name="taskID" value="#{param['taskID']}" />
                </a4j:commandLink> &nbsp;
                
                <a4j:commandLink 
                    action="#{TaskDetailsBean.deleteTask}"
                    value="Delete Task" /> &nbsp;
            </h:panelGroup>
            <h:message for="infoForm:taskModifiers" style="color: red"/>
        </h:form>
        
        </div>
        
        <!-- Target -->
        
        <%@ include file="/taskDetails/fba/fbaTarget.jsp" %>
        <%@ include file="/taskDetails/rscan/rscanTarget.jsp" %>
        <%@ include file="/taskDetails/kgene/kgeneTarget.jsp" %>
        
        <!-- IF YOU WANT TO ADD NEW METHOD (with parameters) -> put here "include" with MyMethodTarget.jsp where MyMethod is a name of your method -->
        
       <!-- Results -->
        
        <h:panelGroup id="results" rendered="#{TaskDetailsBean.results}">
            <h2><h:outputText value="#{TaskDetailsBean.task.method.name}" /> Results</h2>
            
            <%@ include file="/taskDetails/fba/fbaResults.jsp" %>
            <%@ include file="/taskDetails/fva/fvaResults.jsp" %>
            <%@ include file="/taskDetails/rscan/rscanResults.jsp" %>
            <%@ include file="/taskDetails/kgene/kgeneResults.jsp" %>
            
            <!-- IF YOU WANT TO ADD NEW METHOD -> put here "include" with MyMethodResult.jsp where MyMethod is a name of your method -->
    
        </h:panelGroup>
    </h:panelGroup>
       
    <h:panelGroup rendered="#{TaskDetailsBean.addressInvalid}">
        <h:outputText value="Task doesn't exist." />
    </h:panelGroup>
</div>