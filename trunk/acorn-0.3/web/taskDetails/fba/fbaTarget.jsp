<%-- 
    Document   : fbaTarget
    Created on : 2008-04-11, 11:49:27
    Author     : lb235922
--%>

<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:panelGroup id="fbaTarget" rendered="#{TaskDetailsBean.fba}">
    <div class="taskTarget">
        <h2><h:outputText value="#{TaskDetailsBean.task.method.name}" /> Target</h2>
    
        <h:panelGrid columns="2" border="0" width="60%">
            Optimisation criterion: 
            <h:outputText value="#{TaskDetailsBean.fbaCriterion}" />
        </h:panelGrid>
    </div>
</h:panelGroup>
