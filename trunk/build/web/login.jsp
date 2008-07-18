<%-- 
    Document   : login
    Created on : 2008-03-07, 12:14:24
    Author     : dl236088
--%>

<%@ taglib prefix="tiles" uri="WEB-INF/tiles-jsp.tld"  %>

<tiles:insertTemplate template="/template.jsp">
    <tiles:putAttribute name="title" value="Login" />
    <tiles:putAttribute name="logo" value="/logo.jsp" />
    <tiles:putAttribute name="menu" value="/menu.jsp" />
    <tiles:putAttribute name="content" value="/login/loginContent.jsp" />
</tiles:insertTemplate>
