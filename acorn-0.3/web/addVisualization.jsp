<%-- 
    Document   : addVisualization
    Created on : 2008-08-27, 17:11:45
    Author     : Rosomak
--%>

<%@ taglib prefix="tiles" uri="WEB-INF/tiles-jsp.tld"  %>

<tiles:insertTemplate template="/template.jsp">
    <tiles:putAttribute name="title" value="Acorn - add model" />
    <tiles:putAttribute name="logo" value="/logo.jsp" />
    <tiles:putAttribute name="menu" value="/menu.jsp" />
    <tiles:putAttribute name="content" value="/addVisualization/addVisualizationContent.jsp" />
    <%--<tiles:putAttribute name="loginBox" value="/login.jsp" /> --%>
</tiles:insertTemplate>