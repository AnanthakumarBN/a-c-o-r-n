<%-- 
    Document   : taskDetails
    Created on : 2008-03-14, 13:09:08
    Author     : lukasz
--%>

<%@ taglib prefix="tiles" uri="WEB-INF/tiles-jsp.tld"  %>

<tiles:insertTemplate template="/template.jsp">
    <tiles:putAttribute name="title" value="Acorn - task details" />
    <tiles:putAttribute name="logo" value="/logo.jsp" />
    <tiles:putAttribute name="menu" value="/menu.jsp" />
    <tiles:putAttribute name="content" value="/taskDetails/taskDetailsContent.jsp" />
</tiles:insertTemplate>
