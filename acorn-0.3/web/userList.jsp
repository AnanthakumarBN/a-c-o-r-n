<%-- 
    Document   : model_list
    Created on : 2008-03-07, 13:09:28
    Author     : dl236088
--%>

<%@ taglib prefix="tiles" uri="WEB-INF/tiles-jsp.tld"  %>

<tiles:insertTemplate template="/template.jsp">
    <tiles:putAttribute name="title" value="User list" />
    <tiles:putAttribute name="logo" value="/logo.jsp" />
    <tiles:putAttribute name="menu" value="/menu.jsp" />
    <tiles:putAttribute name="content" value="/userList/userListContent.jsp" />
</tiles:insertTemplate>
