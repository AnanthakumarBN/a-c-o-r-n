<%-- 
    Document   : fbaResults
    Created on : 2008-04-11, 14:48:12
    Author     : lb235922
--%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="a4j" uri="https://ajax4jsf.dev.java.net/ajax"%>

<h:panelGroup id="fbaResults" rendered="#{TaskDetailsBean.fba}">    
    <!-- HACK -->
    <h:form id="fbaInitForm">
        <h:outputText value="#{FbaResultsBean.init}" />
    </h:form>
    
    <!-- Common Results -->
    
    <div class="commonResults">
    
    <h:panelGrid columns="2" border="0" width="40%">
        Optimisation status:
        <h:outputText value="#{FbaResultsBean.optStatus}" />
        
        Maximal theoretical flux:
        <h:outputText value="#{FbaResultsBean.growthRate}">
            <f:convertNumber minFractionDigits="1" maxFractionDigits="8"/>
        </h:outputText>
    </h:panelGrid>
    
    </div>
    
    <!-- FBA Results -->
    
    <div class="filterBox">
    
    <h:form id="fbaFilterForm">        
        <a4j:region>
            <h:panelGrid columns="2">
                Name filter:
                <h:inputText value="#{FbaResultsBean.reactionNameFilter}" >
                    <a4j:support event="onkeyup" reRender="fbaTableForm" action="#{FbaResultsBean.filterList}" status="filterStatus">
                        <a4j:actionparam name="taskID" value="#{param['taskID']}" />
                    </a4j:support>
                </h:inputText>
            
                Reactant name filter:
                <h:inputText value="#{FbaResultsBean.reactantNameFilter}" >
                    <a4j:support event="onkeyup" reRender="fbaTableForm" action="#{FbaResultsBean.filterList}" status="filterStatus">
                        <a4j:actionparam name="taskID" value="#{param['taskID']}" /> 
                    </a4j:support>
                </h:inputText>
                
                Product name filter:
                <h:inputText value="#{FbaResultsBean.productNameFilter}" >
                    <a4j:support event="onkeyup" reRender="fbaTableForm" action="#{FbaResultsBean.filterList}" status="filterStatus">
                        <a4j:actionparam name="taskID" value="#{param['taskID']}" /> 
                    </a4j:support>
                </h:inputText>
            </h:panelGrid>
            
            <br/>
        
            <a4j:commandButton value="Update filter" reRender="fbaTableForm" action="#{FbaResultsBean.filterList}" type="Submit"> 
                <a4j:actionparam name="taskID" value="#{param['taskID']}" />
            </a4j:commandButton> &nbsp;
            
            <a4j:status id="filterStatus" startText=" Searching..." stopText=""/>
        </a4j:region>
    </h:form>
    
    </div>
    
    One of possibly many flux distributions sustaining maximal theoretical growth rate:
    
    <div class="tableBox">
    
    <h:form id="fbaTableForm">
        <div class="navigationBox">
        
        <div class="navigationElement">
            
        <a4j:commandLink styleClass="navArrow" reRender="fbaTableForm" action="#{FbaResultsBean.firstPage}">
            <h:graphicImage value="/chrome/nav_fst.gif" />
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
        </a4j:commandLink>
        <a4j:commandLink styleClass="navArrow" reRender="fbaTableForm" action="#{FbaResultsBean.prevPage}">
            <h:graphicImage value="/chrome/nav_bwd.gif" />
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
        </a4j:commandLink>
       
        <span style="vertical-align: 42%">
        <h:outputText  id="results" value="#{FbaResultsBean.resultsString}" />
        </span>
        
        <a4j:commandLink styleClass="navArrow" reRender="fbaTableForm" action="#{FbaResultsBean.nextPage}">
            <h:graphicImage value="/chrome/nav_fwd.gif" />
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
        </a4j:commandLink>
        <a4j:commandLink styleClass="navArrow" reRender="fbaTableForm" action="#{FbaResultsBean.lastPage}">
            <h:graphicImage value="/chrome/nav_lst.gif" />
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
        </a4j:commandLink>
        
        </div>
        
        <div class="navigationElement">
              
        Number of elements on page: &nbsp;
        <a4j:commandLink id="rows10" reRender="fbaTableForm" action="#{FbaResultsBean.Rows}" value="10">
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
            <a4j:actionparam name="rows" value="10" />
        </a4j:commandLink>
        ..
        <a4j:commandLink id="rows30" reRender="fbaTableForm" action="#{FbaResultsBean.Rows}" value="30">
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
            <a4j:actionparam name="rows" value="30" />
        </a4j:commandLink>
        ..
        <a4j:commandLink id="rows50" reRender="fbaTableForm" action="#{FbaResultsBean.Rows}" value="50">
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
            <a4j:actionparam name="rows" value="50" />
        </a4j:commandLink>
        ..
        <a4j:commandLink id="rows100" reRender="fbaTableForm" action="#{FbaResultsBean.Rows}" value="100">
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
            <a4j:actionparam name="rows" value="100" />
        </a4j:commandLink>
        ..
        <a4j:commandLink id="rows300" reRender="fbaTableForm" action="#{FbaResultsBean.Rows}" value="300">
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
            <a4j:actionparam name="rows" value="300" />
        </a4j:commandLink>
        
        </div>
        
        </div>
        
        <h:dataTable 
            id="fbaFilteredRowList" 
            var="row" 
            value="#{FbaResultsBean.list}"
            width="100%"
            headerClass="tableHeader"
            columnClasses="fbaReactionName, fbaFlux, fbaFormula, fbaLower, fbaUpper"
            rowClasses="tableRow1, tableRow2">
            <h:column>
                <f:facet name="header">
                    <h:outputText value="Reaction name" />
                </f:facet>
                <div class="mayScroll">
                    <h:outputText value="#{row.reactionName}" />
                </div>
            </h:column>
                            
            <h:column>
                <f:facet name="header">
                    <h:outputText value="Flux" />
                </f:facet>
                <h:outputText value="#{row.flux}" />
            </h:column>
                           
            <h:column>
                <f:facet name="header">
                    <h:outputText value="Reaction formula" />
                </f:facet>
                <div class="mayScroll">
                    <h:outputText value="#{row.formula}" escape="false" />
                </div>
            </h:column>
                            
            <h:column>
                <f:facet name="header">
                    <h:outputText value="Lower bound" />
                </f:facet>
                <h:outputText value="#{row.lowerBound}" />
            </h:column>
                           
            <h:column>
                <f:facet name="header">
                    <h:outputText value="Upper bound" />
                </f:facet>
                <h:outputText value="#{row.upperBound}" />
            </h:column>
        </h:dataTable>
    </h:form>
    
    </div>
</h:panelGroup>
