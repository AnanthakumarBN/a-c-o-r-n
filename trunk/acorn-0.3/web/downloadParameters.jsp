<%-- 
    Document   : DownloadParameters
    Created on : 2011-11-17
    Author     : lukasz
--%>

<%@taglib prefix="tiles" uri="WEB-INF/tiles-jsp.tld"%>

<tiles:insertTemplate template="/template.jsp">
    <tiles:putAttribute name="title" value="Download Parameters" />
    <tiles:putAttribute name="logo" value="/logo.jsp" />
    <tiles:putAttribute name="menu" value="/menu.jsp" />
    <tiles:putAttribute name="content" value="/parameters/downloadParameters.jsp" />
</tiles:insertTemplate>
