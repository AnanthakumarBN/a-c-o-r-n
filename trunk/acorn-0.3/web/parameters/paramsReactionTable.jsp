<%-- 
    Document   : paramsReactionTable
    Created on : 2008-04-18, 19:26:27
    Author     : sg236027
--%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib uri="/WEB-INF/customTags.tld" prefix="customTags" %>
<%@taglib prefix="a4j" uri="https://ajax4jsf.dev.java.net/ajax"%>

<div class="filterBox">

    <a4j:region id="reactionFilterRegion">
        <h:panelGrid columns="2">
            Reaction name filter:
            <h:inputText value="#{TaskBean.reactionNameFilter}" >
                <a4j:support event="onkeyup"
                             reRender="reactions"
                             requestDelay="500"
                             action="#{TaskBean.filterConditions}" >
                    <a4j:actionparam name="modelID" value="#{param['modelID']}" />
                </a4j:support>
            </h:inputText>
        </h:panelGrid>

        <br/>

        <a4j:commandButton value="Update reaction filter"
                           reRender="reactions"
                           action="#{TaskBean.filterConditions}"
                           type="Submit" >
            <a4j:actionparam name="modelID" value="#{param['modelID']}" />
        </a4j:commandButton> &nbsp;

        <a4j:status id="reactoinStatus" startText=" Searching..." stopText=""/>
    </a4j:region>

</div>

<div class="tableBox">

    <h:panelGroup id="reactions">

        <div class="navigationBox">

            <a4j:commandLink styleClass="navArrow" reRender="reactions" action="#{TaskBean.reactionFirstPage}">
                <h:graphicImage value="/chrome/nav_fst.gif" />
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
            </a4j:commandLink>
            <a4j:commandLink styleClass="navArrow" reRender="reactions" action="#{TaskBean.reactionPrevPage}">
                <h:graphicImage value="/chrome/nav_bwd.gif" />
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
            </a4j:commandLink>

            <span style="vertical-align: 42%">
                <h:outputText  id="reactionsResults" value="#{TaskBean.reactionResultsString}" />
            </span>

            <a4j:commandLink styleClass="navArrow" reRender="reactions" action="#{TaskBean.reactionNextPage}">
                <h:graphicImage value="/chrome/nav_fwd.gif" />
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
            </a4j:commandLink>
            <a4j:commandLink styleClass="navArrow" reRender="reactions" action="#{TaskBean.reactionLastPage}">
                <h:graphicImage value="/chrome/nav_lst.gif" />
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
            </a4j:commandLink>

        </div>

        <h:dataTable
            id="reactionTable"
            var="reaction"
            value="#{TaskBean.conditions}"
            width="100%"
            headerClass="tableHeader"
            columnClasses="select, nameShort, formulaOneLine"
            rowClasses="tableRow1, tableRow2">
            <h:column>
                <f:facet name="header">
                    <h:outputText value="Select reaction" />
                </f:facet>
                <customTags:radioButton id="reactionSpeciesRadio" name="reactionSpeciesRadio" overrideName="true" value="#{TaskBean.selectedReaction}" itemValue="#{reaction.index}" />
            </h:column>
            <h:column>
                <f:facet name="header">
                    <h:outputText value="Reaction name" />
                </f:facet>
                <h:outputText escape="false" value="
                              <div title=\"header=[Sbml Id] body=[#{reaction.reaction.sid}]\">
                              #{reaction.reactionName}
                              </div>"
                              />
            </h:column>
            <h:column>
                <f:facet name="header">
                    <h:outputText value="Reaction formula" />
                </f:facet>
                <div class="mayScroll">
                    <h:outputText value="#{reaction.reactionFormulaOneLine}" escape="false"/>
                </div>
            </h:column>
        </h:dataTable>
    </h:panelGroup>

</div>
