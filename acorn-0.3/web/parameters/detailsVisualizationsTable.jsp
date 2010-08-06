<%-- 
    Document   : detailsVisualizationsTable
    Created on : 2008-09-04, 22:45:10
    Author     : Rosomak
--%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib uri="/WEB-INF/customTags.tld" prefix="customTags" %>
<%@taglib prefix="a4j" uri="https://ajax4jsf.dev.java.net/ajax"%>

<div class="filterBox">

    <h:form id="visualizationsFilterRegion">
        <a4j:region>
            <h:panelGrid columns="2">
                Visualization's name filter:
                <h:inputText value="#{TaskDetailsBean.visualizationNameFilter}" >
                    <a4j:support event="onkeyup"
                                 requestDelay="500"
                                 reRender="visualizations"
                                 action="#{TaskDetailsBean.filterVisualizations}" >
                        <a4j:actionparam name="taskID" value="#{param['taskID']}" />
                    </a4j:support>
                </h:inputText>
            </h:panelGrid>

            <br/>

            <a4j:commandButton value="Update visualizations filter"
                               reRender="visualizations"
                               action="#{TaskDetailsBean.filterVisualizations}"
                               type="Submit" >
                <a4j:actionparam name="taskID" value="#{param['taskID']}" />
            </a4j:commandButton> &nbsp;

            <a4j:status id="visualizationsStatus" startText=" Searching..." stopText=""/>
        </a4j:region>
    </h:form>
</div>

<div class="tableBox">

    <h:form id="visualizations">
        <h:messages style="color: red" globalOnly="true"/>

        <div class="RunButton">

            <h:commandLink action="#{TaskDetailsBean.generateDrawing}" value="Generate drawing.">
                <f:param name="taskID" value="#{param['taskID']}"/>
            </h:commandLink>
        </div>

        <br/>
        <div class="navigationBox">

            <a4j:commandLink styleClass="navArrow" reRender="visualizations" action="#{TaskDetailsBean.visualizationsFirstPage}">
                <h:graphicImage value="/chrome/nav_fst.gif" />
                <a4j:actionparam name="taskID" value="#{param['taskID']}" />
            </a4j:commandLink>
            <a4j:commandLink styleClass="navArrow" reRender="visualizations" action="#{TaskDetailsBean.visualizationsPrevPage}">
                <h:graphicImage value="/chrome/nav_bwd.gif" />
                <a4j:actionparam name="taskID" value="#{param['taskID']}" />
            </a4j:commandLink>

            <span style="vertical-align: 42%">
                <h:outputText id="visualizationsResults" value="#{TaskDetailsBean.visualizationsResultsString}" />
            </span>

            <a4j:commandLink styleClass="navArrow" reRender="visualizations" action="#{TaskDetailsBean.visualizationsNextPage}">
                <h:graphicImage value="/chrome/nav_fwd.gif" />
                <a4j:actionparam name="taskID" value="#{param['taskID']}" />
            </a4j:commandLink>
            <a4j:commandLink styleClass="navArrow" reRender="visualizations" action="#{TaskDetailsBean.visualizationsLastPage}">
                <h:graphicImage value="/chrome/nav_lst.gif" />
                <a4j:actionparam name="taskID" value="#{param['taskID']}" />
            </a4j:commandLink>

        </div>

        <h:dataTable
            id="visualizationsTable"
            var="visualizations"
            value="#{TaskDetailsBean.visualizations}"
            width="100%"
            headerClass="tableHeader"
            columnClasses="select, name"
            rowClasses="tableRow1, tableRow2">
        <h:column>
            <f:facet name="header">
                <h:outputText value="Select visualization" />
            </f:facet>
            <customTags:radioButton id="reactionVisualizationsRadio" name="reactionVisualizationsRadio" overrideName="true" value="#{TaskDetailsBean.selectedVisualizations}" itemValue="#{visualizations.sid}" />
        </h:column>
        <h:column>
            <f:facet name="header">
                <h:outputText value="Name of visualization" />
            </f:facet>
            <h:outputText escape="false" value="#{visualizations.stripedName}"/>
        </h:column>
        </h:dataTable>
    </h:form>
</div>

<% if (request.getSession().getAttribute("PICTURE_PATH") != null) {
            request.getSession().removeAttribute("PICTURE_PATH");
%>
<h:graphicImage value="#{TaskDetailsBean.pathForServlet}"/>
<%      }
%>