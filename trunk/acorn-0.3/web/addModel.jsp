<%-- 
    Document   : addModel
    Created on : 2008-03-07, 12:45:43
    Author     : lukasz
--%>

<%@ taglib prefix="tiles" uri="WEB-INF/tiles-jsp.tld"  %>

<tiles:insertTemplate template="/template.jsp">
    <tiles:putAttribute name="title" value="Acorn - add model" />
    <tiles:putAttribute name="logo" value="/logo.jsp" />
    <tiles:putAttribute name="menu" value="/menu.jsp" />
    <tiles:putAttribute name="content" value="/addModel/addModelContent.jsp" />
    
    <%--<tiles:putAttribute name="loginBox" value="/login.jsp" /> --%>
</tiles:insertTemplate>