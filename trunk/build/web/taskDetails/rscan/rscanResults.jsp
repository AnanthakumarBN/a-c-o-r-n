<%-- 
    Document   : rscanResults
    Created on : 2008-04-11, 16:32:26
    Author     : lb235922
--%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="a4j" uri="https://ajax4jsf.dev.java.net/ajax" %>

<h:panelGroup id="rscanResults" rendered="#{TaskDetailsBean.rscan}">
    <!-- HACK -->
    <h:form id="rscanInitForm">
        <h:outputText value="#{RscanResultsBean.init}" />
    </h:form>
    
    <!-- Common Results -->
    
    <div class="commonResults">
    
    <h:panelGrid columns="2">
        Optimisation status:
        <h:outputText value="#{RscanResultsBean.optStatus}" />
        
        Maximal theoretical flux:
        <h:outputText value="#{RscanResultsBean.growthRate}">
            <f:convertNumber minFractionDigits="1" maxFractionDigits="8"/>
        </h:outputText>
    </h:panelGrid>

    </div>
    
    <!-- FBA Results -->
    
    <div class="filterBox">
    
    <h:form id="rscanFilterForm">
        <a4j:region>
            <h:panelGrid columns="2">
                Name filter:
                <h:inputText value="#{RscanResultsBean.reactionNameFilter}" >
                    <a4j:support event="onkeyup" reRender="rscanTableForm" action="#{RscanResultsBean.filterList}" status="filterStatus">
                        <a4j:actionparam name="taskID" value="#{param['taskID']}" />
                    </a4j:support>
                </h:inputText>
            
                Reactant name filter:
                <h:inputText value="#{RscanResultsBean.reactantNameFilter}" >
                    <a4j:support event="onkeyup" reRender="rscanTableForm" action="#{RscanResultsBean.filterList}" status="filterStatus">
                        <a4j:actionparam name="taskID" value="#{param['taskID']}" /> 
                    </a4j:support>
                </h:inputText>
                
                Product name filter:
                <h:inputText value="#{RscanResultsBean.productNameFilter}" >
                    <a4j:support event="onkeyup" reRender="rscanTableForm" action="#{RscanResultsBean.filterList}" status="filterStatus">
                        <a4j:actionparam name="taskID" value="#{param['taskID']}" /> 
                    </a4j:support>
                </h:inputText>
            </h:panelGrid>
        
            <br/>
        
            <a4j:commandButton value="Update filter" reRender="rscanTableForm" action="#{RscanResultsBean.filterList}" type="Submit"> 
                <a4j:actionparam name="taskID" value="#{param['taskID']}" />
            </a4j:commandButton> &nbsp;
            
            <a4j:status id="filterStatus" startText=" Searching..." stopText=""/>
        </a4j:region>
    </h:form>
        
    </div>
    
    Each row in the table represents FBA simulation in which reaction has been removed from the system. Thus reactions for which the maximal growth rate is 0 or the optimisation status is different from OPTIMAL are predicted to be essential for growth.
    
    <div class="tableBox">
    
    <h:form id="rscanTableForm">
        <div class="navigationBox">
        
        <div class="navigationElement">
            
        <a4j:commandLink styleClass="navArrow" reRender="rscanTableForm" action="#{RscanResultsBean.firstPage}">
            <h:graphicImage value="/chrome/nav_fst.gif" />
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
        </a4j:commandLink>
        <a4j:commandLink styleClass="navArrow" reRender="rscanTableForm" action="#{RscanResultsBean.prevPage}">
            <h:graphicImage value="/chrome/nav_bwd.gif" />
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
        </a4j:commandLink>
       
        <span style="vertical-align: 42%">
        <h:outputText  id="results" value="#{RscanResultsBean.resultsString}" />
        </span>
        
        <a4j:commandLink styleClass="navArrow" reRender="rscanTableForm" action="#{RscanResultsBean.nextPage}">
            <h:graphicImage value="/chrome/nav_fwd.gif" />
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
        </a4j:commandLink>
        <a4j:commandLink styleClass="navArrow" reRender="rscanTableForm" action="#{RscanResultsBean.lastPage}">
            <h:graphicImage value="/chrome/nav_lst.gif" />
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
        </a4j:commandLink>
        
        </div>
        
        <div class="navigationElement">
              
        Number of elements on page: &nbsp;
        <a4j:commandLink id="rows10" reRender="rscanTableForm" action="#{RscanResultsBean.Rows}" value="10">
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
            <a4j:actionparam name="rows" value="10" />
        </a4j:commandLink>
        ..
        <a4j:commandLink id="rows30" reRender="rscanTableForm" action="#{RscanResultsBean.Rows}" value="30">
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
            <a4j:actionparam name="rows" value="30" />
        </a4j:commandLink>
        ..
        <a4j:commandLink id="rows50" reRender="rscanTableForm" action="#{RscanResultsBean.Rows}" value="50">
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
            <a4j:actionparam name="rows" value="50" />
        </a4j:commandLink>
        ..
        <a4j:commandLink id="rows100" reRender="rscanTableForm" action="#{RscanResultsBean.Rows}" value="100">
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
            <a4j:actionparam name="rows" value="100" />
        </a4j:commandLink>
        ..
        <a4j:commandLink id="rows300" reRender="rscanTableForm" action="#{RscanResultsBean.Rows}" value="300">
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
            <a4j:actionparam name="rows" value="300" />
        </a4j:commandLink>
        
        </div>
        
        </div>
        
        <h:dataTable 
            id="rscanFilteredRowList"
            var="row"
            value="#{TaskDetailsBean.rscanResults}"
            width="100%"
            headerClass="tableHeader"
            columnClasses="rscanReactionName, rscanStatus, rscanGrowthRate, rscanFormula"
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
                    <h:outputText value="Optimisation status" />
                </f:facet>
                <h:outputText value="#{row.optStatus}" />
            </h:column>
                          
            <h:column>
                <f:facet name="header">
                    <h:outputText value="Growth Rate" />
                </f:facet>
                <h:outputText value="#{row.growthRate}" />
            </h:column>
                         
            <h:column>
                <f:facet name="header">
                    <h:outputText value="Reaction formula" />
                </f:facet>
                <div class="mayScroll">
                    <h:outputText value="#{row.formula}" escape="false" />
                </div>
            </h:column>
        </h:dataTable> 
    </h:form>
    
    </div>
</h:panelGroup>
