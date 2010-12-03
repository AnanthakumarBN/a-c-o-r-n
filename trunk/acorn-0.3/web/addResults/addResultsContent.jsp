<%-- 
    Document   : addModelContent
    Created on : 2008-03-07, 12:45:51
    Author     : lukasz
--%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk"%>

<div id="content" class="content">   
        <h1>Add Results</h1>
        <h:form id="add" enctype="multipart/form-data">
            <h:panelGrid columns="3" border="0">
                Path to file containing results:
                <t:inputFileUpload id="file"
                    value="#{AddResultsBean.file}"
                    storage="file"
                    required="true"/>
                <h:message for="add:file" style="color: red"/>
            </h:panelGrid>
            <br>
            <h:commandLink id="submit" type="submit" value="Add Results" action="#{AddResultsBean.addResults}">
              <f:param name="taskID" value="#{AddResultsBean.taskID}"/>
            </h:commandLink>
            <h:messages style="color: red" globalOnly="true"/>
        </h:form>
</div>