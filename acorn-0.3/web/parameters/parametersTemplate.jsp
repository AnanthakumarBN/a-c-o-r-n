<%-- 
    Document   : parametersTemplate
    Created on : 2008-04-18, 19:20:45
    Author     : sg236027
--%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="tiles" uri="WEB-INF/tiles-jsp.tld"%>

<div id="content" class="content">
    <h1> <tiles:insertAttribute name="methodTitle" /> </h1>
    
    <h:outputText value="#{TaskBean.parametersInit}" />
    
    <h:panelGroup id="invalidModel" rendered="#{!TaskBean.isModelValid}" >
        Invalid model!
    </h:panelGroup>
    
    <h:panelGroup id="validModel" rendered="#{TaskBean.isModelValid}" >
        <h:form>
            <div class="RunButton">

            <h:panelGroup rendered="#{!TaskBean.performLocally}" >
            <h:commandLink action="#{TaskBean.calcTask}" value="Start the simulation" >
              <f:param name="modelID" value="#{param['modelID']}"/>
            </h:commandLink>
            </h:panelGroup>

            <h:panelGroup rendered="#{TaskBean.performLocally}" >
            <h:commandLink action="#{TaskBean.prepareParameters}" value="Download data" >
              <f:param name="modelID" value="#{param['modelID']}"/>
            </h:commandLink>
            </h:panelGroup>

            <br/>

            <h:outputText value="#{TaskBean.errorMessage}" rendered="#{TaskBean.error}" styleClass="error"/>
            
            </div>
            
            <%--
            <h:commandLink action="back" value="Back" >
                <f:param name="modelID" value="#{param['modelID']}"/>
            </h:commandLink>
            --%>
            
            <b>Choose optimisation target:</b>
            
            <tiles:insertAttribute name="reactionTable" />
            
            <br/>
            
            <tiles:insertAttribute name="speciesTable" />
            
            <br/>
            
            <tiles:insertAttribute name="genesTable" />
        </h:form>
    </h:panelGroup>
</div>
