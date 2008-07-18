<%-- 
    Document   : fvaResults
    Created on : 2008-04-11, 16:01:31
    Author     : lb235922
--%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="a4j" uri="https://ajax4jsf.dev.java.net/ajax"%>

<h:panelGroup id="fvaResults" rendered="#{TaskDetailsBean.fva}">
    <!-- HACK -->
    <h:form id="fvaInitForm">
        <h:outputText value="#{FvaResultsBean.init}" />
    </h:form>
    
    <!-- FVA Results -->
    
    <div class="filterBox">
    
    <h:form id="fvaFilterForm">
        <a4j:region>
            <h:panelGrid columns="2">
                Name filter:
                <h:inputText value="#{FvaResultsBean.reactionNameFilter}" >
                    <a4j:support event="onkeyup" reRender="fvaTableForm" action="#{FvaResultsBean.filterList}" status="filterStatus">
                        <a4j:actionparam name="taskID" value="#{param['taskID']}" />
                    </a4j:support>
                </h:inputText>
            
                Reactant name filter:
                <h:inputText value="#{FvaResultsBean.reactantNameFilter}" >
                    <a4j:support event="onkeyup" reRender="fvaTableForm" action="#{FvaResultsBean.filterList}" status="filterStatus">
                        <a4j:actionparam name="taskID" value="#{param['taskID']}" /> 
                    </a4j:support>
                </h:inputText>
                
                Product name filter:
                <h:inputText value="#{FvaResultsBean.productNameFilter}" >
                    <a4j:support event="onkeyup" reRender="fvaTableForm" action="#{FvaResultsBean.filterList}" status="filterStatus">
                        <a4j:actionparam name="taskID" value="#{param['taskID']}" /> 
                    </a4j:support>
                </h:inputText>
            </h:panelGrid>
        
            <br/>
        
            <a4j:commandButton value="Update filter" reRender="fvaTableForm" action="#{FvaResultsBean.filterList}" type="Submit"> 
                <a4j:actionparam name="taskID" value="#{param['taskID']}" />
            </a4j:commandButton> &nbsp;
            
            <a4j:status id="filterStatus" startText=" Searching..." stopText=""/>
        </a4j:region>
    </h:form>
        
    </div>
    
    Flux ranges:
    
    <div class="tableBox">
    
    <h:form id="fvaTableForm">
        <div class="navigationBox">
        
        <div class="navigationElement">
            
        <a4j:commandLink styleClass="navArrow" reRender="fvaTableForm" action="#{FvaResultsBean.firstPage}">
            <h:graphicImage value="/chrome/nav_fst.gif" />
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
        </a4j:commandLink>
        <a4j:commandLink styleClass="navArrow" reRender="fvaTableForm" action="#{FvaResultsBean.prevPage}">
            <h:graphicImage value="/chrome/nav_bwd.gif" />
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
        </a4j:commandLink>
       
        <span style="vertical-align: 42%">
        <h:outputText  id="results" value="#{FvaResultsBean.resultsString}" />
        </span>
        
        <a4j:commandLink styleClass="navArrow" reRender="fvaTableForm" action="#{FvaResultsBean.nextPage}">
            <h:graphicImage value="/chrome/nav_fwd.gif" />
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
        </a4j:commandLink>
        <a4j:commandLink styleClass="navArrow" reRender="fvaTableForm" action="#{FvaResultsBean.lastPage}">
            <h:graphicImage value="/chrome/nav_lst.gif" />
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
        </a4j:commandLink>
        
        </div>
        
        <div class="navigationElement">
              
        Number of elements on page: &nbsp;
        <a4j:commandLink id="rows10" reRender="fvaTableForm" action="#{FvaResultsBean.Rows}" value="10">
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
            <a4j:actionparam name="rows" value="10" />
        </a4j:commandLink>
        ..
        <a4j:commandLink id="rows30" reRender="fvaTableForm" action="#{FvaResultsBean.Rows}" value="30">
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
            <a4j:actionparam name="rows" value="30" />
        </a4j:commandLink>
        ..
        <a4j:commandLink id="rows50" reRender="fvaTableForm" action="#{FvaResultsBean.Rows}" value="50">
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
            <a4j:actionparam name="rows" value="50" />
        </a4j:commandLink>
        ..
        <a4j:commandLink id="rows100" reRender="fvaTableForm" action="#{FvaResultsBean.Rows}" value="100">
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
            <a4j:actionparam name="rows" value="100" />
        </a4j:commandLink>
        ..
        <a4j:commandLink id="rows300" reRender="fvaTableForm" action="#{FvaResultsBean.Rows}" value="300">
            <a4j:actionparam name="taskID" value="#{param['taskID']}" />
            <a4j:actionparam name="rows" value="300" />
        </a4j:commandLink>
        
        </div>
        
        </div>
        
        <h:dataTable 
            id="fvaFilteredRowList"
            var="row" value="#{FvaResultsBean.list}"
            width="100%"
            headerClass="tableHeader"
            columnClasses="fvaReactionName, fvaMinFlux, fvaMaxFlux, fvaFormula"
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
                    <h:outputText value="Min Flux" />
                </f:facet>
                <h:outputText value="#{row.minFlux}" />
            </h:column>
                        
            <h:column>
                <f:facet name="header">
                    <h:outputText value="Max Flux" />
                </f:facet>
                <h:outputText value="#{row.maxFlux}" />
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
