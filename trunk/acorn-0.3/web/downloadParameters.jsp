<%-- 
    Document   : RSCANParameters
    Created on : 2008-04-18, 19:42:38
    Author     : sg236027
--%>

<%@taglib prefix="tiles" uri="WEB-INF/tiles-jsp.tld"%>

<tiles:insertTemplate template="/template.jsp">
    <tiles:putAttribute name="title" value="Download Parameters" />
    <tiles:putAttribute name="logo" value="/logo.jsp" />
    <tiles:putAttribute name="menu" value="/menu.jsp" />
    <tiles:putAttribute name="content" value="/parameters/downloadParameters.jsp" />
</tiles:insertTemplate>