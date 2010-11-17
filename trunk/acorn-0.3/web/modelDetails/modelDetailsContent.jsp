<%-- 
    Document   : model_details_content
    Created on : 2008-03-07, 12:16:07
    Author     : dl236088
--%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="a4j" uri="https://ajax4jsf.dev.java.net/ajax"%>

<div id="content" class="content">
    <h1> <h:outputText value="#{TaskBean.titleModelDetails}" /> </h1>
   
   <h:panelGroup id="invalidModel" rendered="#{!TaskBean.isModelValid}" >
        Invalid model!
    </h:panelGroup>
    
    <h:panelGroup id="validModel" rendered="#{TaskBean.isModelValid}">
        <h:form id="modelDetails">

            <div class="modelInfo">

            <h2>Information</h2>
            
            <h:panelGrid id="infopanel" columns="2" border="0" width="40%">
                Model name:
                <h:inputText disabled="#{!TaskBean.canEditModels}" id="modelname"
                    value="#{TaskBean.model.name}">
                        <f:validateLength minimum="3" maximum="255"/>
                </h:inputText>
                Organism name:
                <h:outputText id="organismname" 
                    value="#{TaskBean.model.metabolism.organism}"/>
                External gene description:
                <h:inputText disabled="#{!TaskBean.canChangeGeneLink}" id="geneLink" 
                    value="#{TaskBean.model.metabolism.geneLink}"/>
                Model creation time:
                <h:outputText id="creationtime" value="#{TaskBean.model.date}">
                    <f:convertDateTime pattern="kk:mm, dd/MM/yyyy" />
                </h:outputText>
                Model modificatin time:
                <h:outputText id="modificationtime" value="#{TaskBean.model.lastChange}">
                    <f:convertDateTime pattern="kk:mm, dd/MM/yyyy" />
                </h:outputText>
            </h:panelGrid>
            
            <h:panelGroup rendered="#{TaskBean.canEditModels}">
                <br/>
                <h:panelGroup rendered="#{TaskBean.updateAllowed}">
                    <a4j:commandLink
                        action="#{TaskBean.updateModel}" 
                        value="Save Changes"
                        reRender="modelDetails">
                        <a4j:actionparam name="modelID" value="#{param['modelID']}" />
                    </a4j:commandLink> &nbsp;
                </h:panelGroup>
                
                <h:panelGroup rendered="#{TaskBean.saveAllowed}">
                    <a4j:commandLink
                        action="#{TaskBean.saveModel}" 
                        value="Save as New Model">
                        <a4j:actionparam name="modelID" value="#{param['modelID']}" />
                    </a4j:commandLink> &nbsp;
                </h:panelGroup>
                
                <a4j:commandLink
                    action="#{TaskBean.discardChanges}" 
                    value="Discard changes" 
                    reRender="infopanel, reactions">
                        <a4j:actionparam name="modelID" value="#{param['modelID']}" />
                </a4j:commandLink> &nbsp;
            </h:panelGroup>
            
            <h:panelGroup rendered="#{TaskBean.canDelete}">
                <h:commandLink 
                    action="#{TaskBean.deleteModel}"
                    value="Delete model">
                    <f:param name="modelID" value="#{param['modelID']}" />
                </h:commandLink> &nbsp;
            </h:panelGroup>
            
            </div>
       
        
        <%-- TODO? div class="expandable"> 
            <@ include file="task_list.jsp" >
        </div--%>
        
        <div class="createTask">   
            <h2>Start new simulation for this model</h2>
            
            <p>Before running the simulation you can use the table below to modify
            reaction bounds. The bounds of transport reactions can be used to
            account for nutrients available in the medium. Set upper and lower
            bound to 0 if the nutrient is not available. Set the upper bound to
            positive value for transport reactions of nutrients available in the
            medium. If the substance is secreted set the lower bound to negative
            value. You can also use the reaction bounds to set the flux on any
            reaction to experimentally measured value (upper and lower bound
            equal to the value) or to exclude reactions from the model (upper
            and lower bounds set to 0). The set of reaction names can be searched
            to quickly identify reactions of interest
            </p>

            <p>
            Once reaction bounds are defined, set the name of your simulation
            and choose one of the simulation methods. The name will let you
            identify your simulation on task lists.
            </p>

            
            <h:inputText disabled="#{!TaskBean.editable}" onfocus="this.value=''" value="#{TaskBean.taskName}" /><br/>

            <h:selectBooleanCheckbox disabled="#{!TaskBean.editable}" value="#{TaskBean.performLocally}" />
            Download data necessary to perform simulation locally.<br/><br/>

            <h:commandLink action="#{TaskBean.taskFBA}"
                value="Single Flux Balance Analysis" >
                <f:param name="modelID" value="#{param['modelID']}" />
            </h:commandLink><br>Use this method to compute maximal flux towards one of the metabolites
                    or maximal flux through selected reaction (objective function). Remember that there are many
                    possible flux distributions that sustain maximal value of the objective function and
                    you will see only one of them as the solution.<br/><br/>

            <h:commandLink action="#{TaskBean.taskFVA}"
                value="Flux Variability Analysis" >
                <f:param name="modelID" value="#{param['modelID']}" />
            </h:commandLink><br>Use this method to investigate intracellular
            flux distribution. The program will constraint the objective
            function to its maximal value under given conditions. Subsequently,
            the maximal and minimal flux will be calculated for every reaction
            by running two FBA simulations with the reaction being set as an
            objective.<br/><br/>

            <h:commandLink action="#{TaskBean.taskRSCAN}"
                value="Reaction Essentiality Scan" >
                <f:param name="modelID" value="#{param['modelID']}" />
            </h:commandLink><br>Use this function to identify all reactions essential
            for the flux towards objective function. After setting objective function the
            program will inactivate every single reaction and run FBA.<br/><br/>

            <h:commandLink action="#{TaskBean.taskKGENE}" 
                value="Single Gene Knockout" >
                <f:param name="modelID" value="#{param['modelID']}" />
            </h:commandLink><br>Set objective function and a gene to be inactivated.
            The flux through each of the reactions requiring this gene will be
            constrined to 0 and single FBA simulation will be run.<br/><br/>
                    
            <!-- IF YOU WANT TO ADD NEW METHOD -> put here commandLink similar to the above ones -->                    
        </div>

            <div class="filterBox">
                
            <h:panelGrid columns="2" border="0" style="margin-bottom:15px">
                Reaction name filter:
                <h:inputText value="#{TaskBean.reactionNameFilter}" >
                    <a4j:support event="onkeyup" requestDelay="500"
                        reRender="reactions" 
                        action="#{TaskBean.filterConditions}" >
                        <a4j:actionparam name="modelID" value="#{param['modelID']}" />
                    </a4j:support>
                </h:inputText>
            </h:panelGrid>
            
            <a4j:commandButton value="Discard changes in table"
                reRender="reactions" 
                action="#{TaskBean.fetchAndFilterConditions}" >
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
            </a4j:commandButton>
            
            <%-- <a4j:status id="commonstatus" startText=" Searching..." stopText=""/> --%>
            
            </div>
            
            <h:messages style="color:red" layout="list"/>
            
            <div class="tableBox">
            
            <h:panelGroup id="reactions">
            
            <div class="navigationBox">
                
            <div class="navigationElement">
                
            <a4j:commandLink styleClass="navArrow" reRender="reactions" action="#{TaskBean.reactionFirstPage}">
                <h:graphicImage value="/chrome/nav_fst.gif" />
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
            </a4j:commandLink>
            <a4j:commandLink styleClass="navArrow" reRender="reactions" action="#{TaskBean.reactionPrevPage}">
                <h:graphicImage value="/chrome/nav_bwd.gif" />
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
            </a4j:commandLink>
            
            <span style="vertical-align: 42%">
            <h:outputText  id="results" value="#{TaskBean.reactionResultsString}" />
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
            
            <div class="navigationElement">
                
            Number of elements on page: &nbsp;
            <a4j:commandLink id="rows10" reRender="reactions" action="#{TaskBean.Rows}" value="10">
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
                <a4j:actionparam name="rows" value="10" />
            </a4j:commandLink>
            ..
            <a4j:commandLink id="rows30" reRender="reactions" action="#{TaskBean.Rows}" value="30">
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
                <a4j:actionparam name="rows" value="30" />
            </a4j:commandLink>
            ..
            <a4j:commandLink id="rows50" reRender="reactions" action="#{TaskBean.Rows}" value="50">
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
                <a4j:actionparam name="rows" value="50" />
            </a4j:commandLink>
            ..
            <a4j:commandLink id="rows100" reRender="reactions" action="#{TaskBean.Rows}" value="100">
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
                <a4j:actionparam name="rows" value="100" />
            </a4j:commandLink>
            ..
            <a4j:commandLink id="rows300" reRender="reactions" action="#{TaskBean.Rows}" value="300">
                <a4j:actionparam name="modelID" value="#{param['modelID']}" />
                <a4j:actionparam name="rows" value="300" />
            </a4j:commandLink>
            
            </div>

            </div>
            
            <h:dataTable
                id="reactionsTable" 
                var="condition" 
                value="#{TaskBean.conditions}" 
                width="auto"
                style="overflow : inherit"
                headerClass="tableHeader"
                columnClasses="modelReaction, modelLower, modelUpper, modelFormula, modelGenes"
                rowClasses="tableRow1, tableRow2">
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Reaction name" />
                    </f:facet>
                    <h:outputText escape="false" value="
                        <div title=\"header=[Sbml Id] body=[#{condition.reaction.sid}]\" class=\"mayScroll\">
                            #{condition.reactionName}
                        </div>"
                    />

                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Lower bound" />
                    </f:facet>
                    <%--
                      There is a problem with the coherence of on-site data
                      with the data contained in backing bean. The fact is
                      JSF assumes that "conditions" lists are equal, however,
                      it's possible for the user (even without use of 
                      AJAX, still this makes errors happen more often)
                      to send the request form deprecated site. To fix this,
                      it is needed that EBounds ids are kept on the site 
                      explicitely so we can match them afterwards.
                      The Hidden fields are the places where the ids are kept.
                      There are two them, preHidden and postHidden,
                      to ensure some security by acquiring and 
                      to releasing semaphores.
                    --%>
                    <%--
                      acquire accesor semaphore
                      acquire validator semaphore
                    --%>
                    <h:inputHidden id="lowerPreHidden" 
                                   value="#{condition.preHidden}" 
                                   validator="#{condition.lowerHiddenPreValidator}"/>
                    <h:panelGroup styleClass="#{condition.styleClass}">
                        <h:inputText id="lower" 
                            style="width : 3cm"
                            value="#{condition.lowerBound}" 
                            validator="#{condition.lowerBoundValidator}">
                        </h:inputText>                        
                    </h:panelGroup>
                    <%-- release accesor semaphore --%>
                    <h:inputHidden id="lowerPostHidden" 
                                   value="#{condition.postHidden}"/>
                </h:column>
                <h:column>
                    <f:facet name="header">
                         <h:outputText value="Upper bound" />
                    </f:facet>
                    <%-- acquire accessor semaphore --%>
                    <h:inputHidden id="upperPreHidden" 
                                   value="#{condition.preHidden}"/>
                    <h:panelGroup styleClass="#{condition.styleClass}">
                        <h:inputText id="upper"
                            style="width : 3cm"
                            value="#{condition.upperBound}" 
                            validator="#{condition.upperBoundValidator}">
                        </h:inputText>
                    </h:panelGroup>
                    <%--
                      release accesor semaphore
                      release validator semaphore
                    --%>
                    <h:inputHidden id="upperPostHidden" 
                                   value="#{condition.postHidden}"
                                   validator="#{condition.upperHiddenPostValidator}"/>
                </h:column>
                <h:column>
                    <f:facet name="header">
                         <h:outputText value="Reaction formula" />
                    </f:facet>
                    <div class="mayScroll">
                        <h:outputText value="#{condition.reactionFormula}" escape="false"/>
                    </div>
                </h:column>
                <h:column>
                     <f:facet name="header">
                         <h:outputText value="Gene formula" />
                     </f:facet>
                     <h:outputText value="#{condition.geneFormula}" escape="false"/>
                </h:column>
            </h:dataTable>
                 
            </h:panelGroup>
            
            </div>
        </h:form> 
    </h:panelGroup>
</div>
