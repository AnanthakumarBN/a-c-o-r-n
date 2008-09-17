<%-- 
    Document   : AddVisualizationContent
    Created on : 2008-08-27, 17:14:47
    Author     : Rosomak
--%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk"%>

<div id="content" class="content">   
    <h1>Add New Visualization</h1>
    <h:form id="add" enctype="multipart/form-data">
        <h:panelGrid columns="3" border="0">
            Path to visualization file (XML): 
            <t:inputFileUpload id="file"
                               value="#{AddVisualizationBean.file}"
                               storage="file"
                               required="true"/>
            <h:message for="add:file" style="color: red"/>
            
            Visualization's name: 
            <h:inputText id="name"  
                         value="#{AddVisualizationBean.name}"
                         required="true"
                         requiredMessage="*">
                <f:validateLength minimum="3" maximum="255"/>
            </h:inputText>
            <h:message for="add:name" style="color: red"/>
             Model's name: 
            <h:inputText id="modelName"  
                         value="#{AddVisualizationBean.modelName}"
                         required="true"
                         requiredMessage="*">
                <f:validateLength minimum="3" maximum="255"/>
            </h:inputText>
            <h:message for="add:modelName" style="color: red"/>           
            
        </h:panelGrid>
        <br>
        <h:commandButton id="submit" type="submit" value="Add Visualisation" action="#{AddVisualizationBean.addVisualization}"/>
        <h:messages style="color: red" globalOnly="true"/>
    </h:form>
</div>