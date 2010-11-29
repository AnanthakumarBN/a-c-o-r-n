<%-- 
    Document   : downloadParameters
    Created on : 2010-11-17, 11:57:11
    Author     : lukasz
--%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="a4j" uri="https://ajax4jsf.dev.java.net/ajax"%>
<%@taglib prefix="tiles" uri="WEB-INF/tiles-jsp.tld"%>

<div id="content" class="content">
  <h:form id="taskDetailsInitForm">
    <h:commandLink action="#{TaskBean.downloadTaskParameters}" value="Download task parameters" >
      <f:param name="modelID" value="#{param['modelID']}"/>
    </h:commandLink>
    <br />
    <br />
    <h:commandLink action="#{TaskBean.downloadModelDescription}" value="Download model description" >
      <f:param name="modelID" value="#{param['modelID']}"/>
    </h:commandLink>
    <br/>
    You can use the above files for running simulations locally, using jwlfba command-line tool.
    TODO: give a link to jwlfba
    <pre>
        # ./jwlfba --model &lt;model_descrption_file&gt; --xml-task &lt;task_parameters_file&gt;
    </pre>
  </h:form>
</div>