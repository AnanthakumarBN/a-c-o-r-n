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
    Click
    <h:commandLink action="#{TaskBean.downloadTaskParameters}" value="here" >
      <f:param name="modelID" value="#{param['modelID']}"/>
    </h:commandLink>
    to download task parameters.
    <br />
    <br />
    Click
    <h:commandLink action="#{TaskBean.downloadModelDescription}" value="here" >
      <f:param name="modelID" value="#{param['modelID']}"/>
    </h:commandLink>
    to download a model description.
  </h:form>
</div>