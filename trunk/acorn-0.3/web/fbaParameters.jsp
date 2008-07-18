<%-- 
    Document   : FBAParameters
    Created on : 2008-03-12, 20:53:39
    Author     : szymon
--%>

<%@taglib prefix="tiles" uri="WEB-INF/tiles-jsp.tld"%>

<tiles:insertTemplate template="/template.jsp">
    <tiles:putAttribute name="title" value="Single Flux Balance Analysis Parameters" />
    <tiles:putAttribute name="logo" value="/logo.jsp" />
    <tiles:putAttribute name="menu" value="/menu.jsp" />
    <tiles:putAttribute name="content" value="/parameters/fbaParametersContent.jsp" />
</tiles:insertTemplate>
