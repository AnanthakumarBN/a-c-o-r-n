<%-- 
    Document   : paramsGenesTable
    Created on : 2008-04-18, 19:26:27
    Author     : sg236027
--%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib uri="/WEB-INF/customTags.tld" prefix="customTags" %>
<%@taglib prefix="a4j" uri="https://ajax4jsf.dev.java.net/ajax"%>

        <b>Select Gene:</b>

        <div class="filterBox">

        <a4j:region id="genesFilterRegion">
            <h:panelGrid columns="2">
                Gene name filter:
                <h:inputText value="#{TaskBean.genesNameFilter}" >
                    <a4j:support event="onkeyup"
                        reRender="genes"
                        requestDelay="500"
                        action="#{TaskBean.filterGenes}" >
                        <a4j:actionparam name="modelID" value="#{param['modelID']}" />
                    </a4j:support>
                </h:inputText>
            </h:panelGrid>

            <br/>
            
            <a4j:commandButton value="Update gene filter" 
                reRender="genes" 
                action="#{TaskBean.filterGenes}" 
                type="Submit" >
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
            </a4j:commandButton> &nbsp;
            
            <%--
            <a4j:commandButton value="Refresh genes table" 
                reRender="genes" 
                action="#{TaskBean.fetchAndFilterGenes}" >
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
            </a4j:commandButton> &nbsp;
            --%>
            
            <a4j:status id="genesStatus" startText=" Searching..." stopText=""/>
        </a4j:region>
        
        </div>
    
        <div class="tableBox" style="width:60%">
            
        <h:panelGroup id="genes">
            <div class="navigationBox">
            
            <div class="navigationElement">
                
            <a4j:commandLink styleClass="navArrow" reRender="genes" action="#{TaskBean.genesFirstPage}">
                <h:graphicImage value="/chrome/nav_fst.gif" />
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
            </a4j:commandLink>
            <a4j:commandLink styleClass="navArrow" reRender="genes" action="#{TaskBean.genesPrevPage}">
                <h:graphicImage value="/chrome/nav_bwd.gif" />
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
            </a4j:commandLink>
       
            <span style="vertical-align: 42%">
                <h:outputText  id="genesResults" value="#{TaskBean.genesResultsString}" />
            </span>
        
            <a4j:commandLink styleClass="navArrow" reRender="genes" action="#{TaskBean.genesNextPage}">
                <h:graphicImage value="/chrome/nav_fwd.gif" />
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
            </a4j:commandLink>
            <a4j:commandLink styleClass="navArrow" reRender="genes" action="#{TaskBean.genesLastPage}">
                <h:graphicImage value="/chrome/nav_lst.gif" />
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
            </a4j:commandLink>

            </div>

            Number of displayed genes: &nbsp;
            <a4j:commandLink id="grows10" reRender="genes" action="#{TaskBean.genesRows}" value="10">
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
                <a4j:actionparam name="rows" value="10" />
            </a4j:commandLink>
            ..
            <a4j:commandLink id="grows30" reRender="genes" action="#{TaskBean.genesRows}" value="30">
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
                <a4j:actionparam name="rows" value="30" />
            </a4j:commandLink>
            ..
            <a4j:commandLink id="grows50" reRender="genes" action="#{TaskBean.genesRows}" value="50">
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
                <a4j:actionparam name="rows" value="50" />
            </a4j:commandLink>
            ..
            <a4j:commandLink id="grows100" reRender="genes" action="#{TaskBean.genesRows}" value="100">
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
                <a4j:actionparam name="rows" value="100" />
            </a4j:commandLink>
            ..
            <a4j:commandLink id="grows300" reRender="genes" action="#{TaskBean.genesRows}" value="300">
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
                <a4j:actionparam name="rows" value="300" />
            </a4j:commandLink>

            </div>
            
            <h:dataTable
                id="genesTable"
                var="gene"
                value="#{TaskBean.genes}"
                width="100%"
                headerClass="tableHeader"
                columnClasses="select, name"
                rowClasses="tableRow1, tableRow2">
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Select gene" />
                    </f:facet>
                    <customTags:radioButton id="genesRadio" name="genesRadio" overrideName="true" value="#{TaskBean.selectedGene}" itemValue="#{gene}" />
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Gene name" />
                        </f:facet>
                    <h:outputText value="#{gene}" />
                </h:column>
            </h:dataTable>
        </h:panelGroup>
        </div>