<%-- 
    Document   : homepage
    Created on : Feb 29, 2008, 1:43:39 PM
    Author     : kuba
--%>

<%@taglib prefix="tiles" uri="WEB-INF/tiles-jsp.tld"%>

<tiles:insertTemplate template="/template.jsp">
    <tiles:putAttribute name="title" value="Acorn homepage" />
    <tiles:putAttribute name="logo" value="/logo.jsp" />
    <tiles:putAttribute name="menu" value="/menu.jsp" />
    <tiles:putAttribute name="content" value="/homepage/homepageContent.jsp" />
    <!-- TODO -->
    <tiles:putAttribute name="loginBox" value="/login.jsp" /> 
</tiles:insertTemplate>