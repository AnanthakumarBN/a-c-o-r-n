<%-- 
    Document   : paramsSpeciesTable
    Created on : 2008-04-18, 19:26:27
    Author     : sg236027
--%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib uri="/WEB-INF/customTags.tld" prefix="customTags" %>
<%@taglib prefix="a4j" uri="https://ajax4jsf.dev.java.net/ajax"%>

        <div class="filterBox">

        <a4j:region id="speciesFilterRegion">
            <h:panelGrid columns="2">
                Species name filter:
                <h:inputText value="#{TaskBean.speciesNameFilter}" >
                    <a4j:support event="onkeyup"
                        requestDelay="500"
                        reRender="species" 
                        action="#{TaskBean.filterSpecies}" >
                        <a4j:actionparam name="modelID" value="#{param['modelID']}" />
                    </a4j:support>
                </h:inputText>
            </h:panelGrid>

            <br/>
            
            <a4j:commandButton value="Update species filter" 
                reRender="species" 
                action="#{TaskBean.filterSpecies}" 
                type="Submit" >
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
            </a4j:commandButton> &nbsp;

            <a4j:status id="speciesStatus" startText=" Searching..." stopText=""/>
        </a4j:region>
        
        </div>
    
        <div class="tableBox">
            
        <h:panelGroup id="species">

            <div class="navigationBox">
                
            <a4j:commandLink styleClass="navArrow" reRender="species" action="#{TaskBean.speciesFirstPage}">
                <h:graphicImage value="/chrome/nav_fst.gif" />
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
            </a4j:commandLink>
            <a4j:commandLink styleClass="navArrow" reRender="species" action="#{TaskBean.speciesPrevPage}">
                <h:graphicImage value="/chrome/nav_bwd.gif" />
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
            </a4j:commandLink>
       
            <span style="vertical-align: 42%">
                <h:outputText id="speciesResults" value="#{TaskBean.speciesResultsString}" />
            </span>
        
            <a4j:commandLink styleClass="navArrow" reRender="species" action="#{TaskBean.speciesNextPage}">
                <h:graphicImage value="/chrome/nav_fwd.gif" />
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
            </a4j:commandLink>
            <a4j:commandLink styleClass="navArrow" reRender="species" action="#{TaskBean.speciesLastPage}">
                <h:graphicImage value="/chrome/nav_lst.gif" />
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
            </a4j:commandLink>

            </div>
            
            <h:dataTable
                id="speciesTable"
                var="species"
                value="#{TaskBean.species}"
                width="100%"
                headerClass="tableHeader"
                columnClasses="select, name"
                rowClasses="tableRow1, tableRow2">
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Select species" />
                    </f:facet>
                    <customTags:radioButton id="reactionSpeciesRadio" name="reactionSpeciesRadio" overrideName="true" value="#{TaskBean.selectedSpecies}" itemValue="#{species.index}" />
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Species name" />
                    </f:facet>
                    <h:outputText escape="false" value="
                        <div title=\"header=[Sbml Id] body=[#{species.sid}]\">
                            #{species.name}
                        </div>"
                    />
                </h:column>
            </h:dataTable>
        </h:panelGroup>
        
        </div>