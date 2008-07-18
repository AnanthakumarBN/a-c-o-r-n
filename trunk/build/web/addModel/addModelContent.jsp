<%-- 
    Document   : addModelContent
    Created on : 2008-03-07, 12:45:51
    Author     : lukasz
--%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk"%>

<div id="content" class="content">   
        <h1>Add New Model</h1>
        <h:form id="add" enctype="multipart/form-data">
            <h:panelGrid columns="3" border="0">
                Path to SBML file: 
                <t:inputFileUpload id="file"
                    value="#{AddModelBean.file}"
                    storage="file"
                    required="true"/>
                <h:message for="add:file" style="color: red"/>
                
                Model name: 
                <h:inputText id="name"  
                    value="#{AddModelBean.name}"
                    required="true"
                    requiredMessage="*">
                        <f:validateLength minimum="3" maximum="255"/>
                </h:inputText>
                <h:message for="add:name" style="color: red"/>
                
                Organism: 
                <h:inputText id="organism"
                    value="#{AddModelBean.organism}"
                    required="true"
                    requiredMessage="*"/>
                <h:message for="add:organism" style="color: red"/>
                
                External gene description: 
                <h:inputText id="geneLink"
                    value="#{AddModelBean.geneLink}"/>
                <h:message for="add:geneLink" style="color: red"/>
            </h:panelGrid>
            <br>
            <h:commandButton id="submit" type="submit" value="Add Model" action="#{AddModelBean.addModel}"/>
            <h:messages style="color: red" globalOnly="true"/>
        </h:form>
</div>