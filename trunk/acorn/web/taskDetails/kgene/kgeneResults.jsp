<%-- 
    Document   : kgeneResults
    Created on : 2008-04-11, 16:21:23
    Author     : lb235922
--%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:panelGroup id="kgeneResults" rendered="#{TaskDetailsBean.kgene}">
    <!-- HACK -->
    <h:form id="kgeneInitForm">
        <h:outputText value="#{KgeneResultsBean.init}" />
    </h:form>
    
    <!-- Common Results -->
    
    <div class="commonResults">
    
    <h:panelGrid columns="2">
        Optimisation status:
        <h:outputText value="#{KgeneResultsBean.optStatus}" />
        
        Maximal theoretical flux:
        <h:outputText value="#{KgeneResultsBean.growthRate}">
            <f:convertNumber minFractionDigits="1" maxFractionDigits="8"/>
        </h:outputText>
    </h:panelGrid>
    
    </div>
    
    <!-- Kgene Results -->

    <h:panelGroup rendered="#{KgeneResultsBean.geneEssential}">
        The gene is predicted to be essential!
    </h:panelGroup>
                   
    <h:panelGroup rendered="#{KgeneResultsBean.geneNotEssential}">
        The gene is predicted to be non-essential.
    </h:panelGroup>               
</h:panelGroup>
