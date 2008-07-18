<%-- 
    Document   : kgeneTarget
    Created on : 2008-04-11, 13:06:07
    Author     : lb235922
--%>

<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:panelGroup id="kgeneTarget" rendered="#{TaskDetailsBean.kgene}">
    <div class="taskTarget">
        <h2><h:outputText value="#{TaskDetailsBean.task.method.name}" /> Target</h2>
        
        <h:panelGrid columns="2" border="0" width="60%">
            Optimisation criterion:
            <h:outputText value="#{TaskDetailsBean.kgeneCriterion}" />
            
            Gene:
            <h:outputText value="#{TaskDetailsBean.kgeneGene}" />
        </h:panelGrid>
    </div>
</h:panelGroup>
