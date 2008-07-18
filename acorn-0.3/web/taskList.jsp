<%-- 
    Document   : taskList
    Created on : Mar 14, 2008, 2:53:02 PM
    Author     : kuba
--%>


<%@ taglib prefix="tiles" uri="WEB-INF/tiles-jsp.tld"  %>

<tiles:insertTemplate template="/template.jsp">
    <tiles:putAttribute name="title" value="Task list" />
    <tiles:putAttribute name="logo" value="/logo.jsp" />
    <tiles:putAttribute name="menu" value="/menu.jsp" />
    <tiles:putAttribute name="content" value="/taskList/taskListContent.jsp" />
</tiles:insertTemplate>
