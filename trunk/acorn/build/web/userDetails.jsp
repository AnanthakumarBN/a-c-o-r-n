<%-- 
    Document   : register
    Created on : Feb 29, 2008, 5:48:32 PM
    Author     : kuba
--%>


<%@ taglib prefix="tiles" uri="WEB-INF/tiles-jsp.tld"  %>

<tiles:insertTemplate template="/template.jsp">
    <tiles:putAttribute name="title" value="User details" />
    <tiles:putAttribute name="logo" value="/logo.jsp" />
    <tiles:putAttribute name="menu" value="/menu.jsp" />
    <tiles:putAttribute name="content" value="/userDetails/userDetailsContent.jsp" />
</tiles:insertTemplate>

