<%-- 
    Document   : model_list_content
    Created on : 2008-03-07, 13:17:34
    Author     : dl236088
--%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="a4j" uri="https://ajax4jsf.dev.java.net/ajax"%>

<div id="content" class="content">
    <h1><h:outputText value="#{ModelListBean.title}"/></h1>
    
    <h:form id="modelList">        
        <div class="filterBox">
        
        <a4j:region id="filterRegion">
            <h:panelGrid columns="2" border="0">
                Name filter:
                <h:inputText value="#{ModelListBean.modelNameFilter}" >
                    <a4j:support event="onkeyup"
                        reRender="models" 
                        action="#{ModelListBean.filterList}" >
                    </a4j:support>
                </h:inputText>
                
                <h:panelGroup rendered="#{!UserManager.isGuest}">
                    Show my models:
                </h:panelGroup>
                <h:panelGroup rendered="#{!UserManager.isGuest}">
                    <h:selectBooleanCheckbox value="#{ModelListBean.showPrivateFilter}">
                        <a4j:support event="onclick"
                            reRender="models"
                            action="#{ModelListBean.filterList}" >
                        </a4j:support>
                    </h:selectBooleanCheckbox>
                </h:panelGroup>
                
                Show shared models:
                <h:selectBooleanCheckbox value="#{ModelListBean.showSharedFilter}">
                    <a4j:support event="onclick"
                        reRender="models"
                        action="#{ModelListBean.filterList}" >
                    </a4j:support>
                </h:selectBooleanCheckbox>
                
                <h:panelGroup rendered="#{UserManager.isAdmin}">
                    Show all other models:
                </h:panelGroup>
                <h:panelGroup rendered="#{UserManager.isAdmin}">
                    <h:selectBooleanCheckbox value="#{ModelListBean.showOtherFilter}">
                        <a4j:support event="onclick"
                            reRender="models"
                            action="#{ModelListBean.filterList}" >
                        </a4j:support>
                    </h:selectBooleanCheckbox>
                </h:panelGroup>
            </h:panelGrid>
            
            <br/>
            
            <a4j:commandButton value="Update filter"
                reRender="models" 
                action="#{ModelListBean.filterList}" 
                type="Submit" /> &nbsp;
            
            <%--
            <a4j:commandButton value="Refresh table" 
                reRender="models" 
                action="#{ModelListBean.fetchAndFilterList}" /> &nbsp;
            --%>
            
            <a4j:status id="commonstatus" startText="Searching..." stopText=""/>
        </a4j:region>
        
        </div>
        
        <div class="tableBox">
            
        <h:panelGroup id="models">
            <div class="navigationBox">
            
            <div class="navigationElement">
            
            <a4j:commandLink styleClass="navArrow" reRender="models" action="#{ModelListBean.firstPage}">
                <h:graphicImage value="/chrome/nav_fst.gif" />
            </a4j:commandLink>
            <a4j:commandLink styleClass="navArrow" reRender="models" action="#{ModelListBean.prevPage}">
                <h:graphicImage value="/chrome/nav_bwd.gif" />
            </a4j:commandLink>
           
            <span style="vertical-align: 42%">
            <h:outputText  id="results" value="#{ModelListBean.resultsString}" />
            </span>
            
            <a4j:commandLink styleClass="navArrow" reRender="models" action="#{ModelListBean.nextPage}">
                <h:graphicImage value="/chrome/nav_fwd.gif" />
            </a4j:commandLink>
            <a4j:commandLink styleClass="navArrow" reRender="models" action="#{ModelListBean.lastPage}">
                <h:graphicImage value="/chrome/nav_lst.gif" />
            </a4j:commandLink>
            
            </div>
            
            <div class="navigationElement">
            
            Number of elements on page:
            <a4j:commandLink id="rows10" reRender="models" action="#{ModelListBean.Rows}" value="10">
                <a4j:actionparam name="rows" value="10" />
            </a4j:commandLink>
            ..
            <a4j:commandLink id="rows30" reRender="models" action="#{ModelListBean.Rows}" value="30">
                <a4j:actionparam name="rows" value="30" />
            </a4j:commandLink>
            ..
            <a4j:commandLink id="rows50" reRender="models" action="#{ModelListBean.Rows}" value="50">
                <a4j:actionparam name="rows" value="50" />
            </a4j:commandLink>
            ..
            <a4j:commandLink id="rows100" reRender="models" action="#{ModelListBean.Rows}" value="100">
                <a4j:actionparam name="rows" value="100" />
            </a4j:commandLink>
            ..
            <a4j:commandLink id="rows300" reRender="models" action="#{ModelListBean.Rows}" value="300">
                <a4j:actionparam name="rows" value="300" />
            </a4j:commandLink>
            
            </div>

            </div>

            <h:dataTable 
                id="modelsTable"
                width="100%"
                var="model" 
                value="#{ModelListBean.list}"
                headerClass="tableHeader"
                columnClasses="modelName, modelCreationDate, modelLastChange, modelReadOnly, modelDelete"
                rowClasses="tableRow1, tableRow2">
                <h:column>
                    <f:facet name="header">
                        <h:panelGroup>
                            Name
                            <a4j:commandLink 
                                styleClass="ordArrow" 
                                reRender="models" 
                                action="#{ModelListBean.sort}">
                                <a4j:actionparam name="up" value="True" />
                                <a4j:actionparam name="comparator" value="Name" />
                                <h:graphicImage value="/chrome/nav_ordasc.gif" />
                            </a4j:commandLink>
                            <a4j:commandLink 
                                styleClass="ordArrow" 
                                reRender="models" 
                                action="#{ModelListBean.sort}">
                                <a4j:actionparam name="up" value="False" />
                                <a4j:actionparam name="comparator" value="Name" />
                                <h:graphicImage value="/chrome/nav_orddesc.gif" />
                            </a4j:commandLink>
                        </h:panelGroup>
                    </f:facet> 
                    <h:outputLink 
                        value="modelDetails.jsf">
                        <f:param name="modelID" value="#{model.id}" />
                        <h:outputText value="#{model.name}"/>
                    </h:outputLink>
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:panelGroup>
                            Creation Date
                            <a4j:commandLink 
                                styleClass="ordArrow" 
                                reRender="models" 
                                action="#{ModelListBean.sort}">
                                <a4j:actionparam name="up" value="True" />
                                <a4j:actionparam name="comparator" value="Date" />
                                <h:graphicImage value="/chrome/nav_ordasc.gif" />
                            </a4j:commandLink>
                            <a4j:commandLink 
                                styleClass="ordArrow" 
                                reRender="models" 
                                action="#{ModelListBean.sort}">
                                <a4j:actionparam name="up" value="False" />
                                <a4j:actionparam name="comparator" value="Date" />
                                <h:graphicImage value="/chrome/nav_orddesc.gif" />
                            </a4j:commandLink>
                        </h:panelGroup>
                    </f:facet>
                    <h:outputText value="#{model.date}">
                        <f:convertDateTime pattern="kk:mm, dd/MM/yyyy" />
                    </h:outputText>
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:panelGroup>
                            Last Change
                            <a4j:commandLink 
                                styleClass="ordArrow" 
                                reRender="models" 
                                action="#{ModelListBean.sort}">
                                <a4j:actionparam name="up" value="True" />
                                <a4j:actionparam name="comparator" value="LastChange" />
                                <h:graphicImage value="/chrome/nav_ordasc.gif" />
                            </a4j:commandLink>
                            <a4j:commandLink 
                                styleClass="ordArrow" 
                                reRender="models" 
                                action="#{ModelListBean.sort}">
                                <a4j:actionparam name="up" value="False" />
                                <a4j:actionparam name="comparator" value="LastChange" />
                                <h:graphicImage value="/chrome/nav_orddesc.gif" />
                            </a4j:commandLink>
                        </h:panelGroup>
                     </f:facet>
                     <h:outputText value="#{model.lastChange}">
                        <f:convertDateTime pattern="kk:mm, dd/MM/yyyy" />
                     </h:outputText>
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:panelGroup>
                            Read Only
                            <a4j:commandLink 
                                styleClass="ordArrow" 
                                reRender="models" 
                                action="#{ModelListBean.sort}">
                                <a4j:actionparam name="up" value="True" />
                                <a4j:actionparam name="comparator" value="ReadOnly" />
                                <h:graphicImage value="/chrome/nav_ordasc.gif" />
                            </a4j:commandLink>
                            <a4j:commandLink 
                                styleClass="ordArrow" 
                                reRender="models" 
                                action="#{ModelListBean.sort}">
                                <a4j:actionparam name="up" value="False" />
                                <a4j:actionparam name="comparator" value="ReadOnly" />
                                <h:graphicImage value="/chrome/nav_orddesc.gif" />
                            </a4j:commandLink>
                        </h:panelGroup>
                     </f:facet>
                     <h:outputText value="#{model.readOnly}" />
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="" />
                     </f:facet>
                     <h:panelGroup rendered="#{model.canDelete}">
                         <a4j:commandLink action="#{ModelListBean.deleteModel}" value="delete" reRender="models" >
                             <a4j:actionparam name="modelID" value="#{model.id}"/>
                         </a4j:commandLink>
                     </h:panelGroup>
                </h:column>
            </h:dataTable>
        </h:panelGroup>
        </div>
    </h:form>
</div>
