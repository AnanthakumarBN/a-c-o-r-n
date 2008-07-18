<%-- 
    Document   : template
    Created on : Feb 29, 2008, 11:53:16 AM
    Author     : kuba
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="tiles" uri="WEB-INF/tiles-jsp.tld"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><tiles:insertAttribute name="title"/></title>
        <link rel="stylesheet" href="style/acornStyle.css" type="text/css">
        <script src="scripts/boxover.js"></script>
    </head>
    <body class="AcornStyle">
        <f:view>
            <f:subview id="logo">
                <tiles:insertAttribute name="logo" />
            </f:subview>
            <f:subview id="menu">
                <tiles:insertAttribute name="menu" />
            </f:subview>
            <f:subview id="content">
                <tiles:insertAttribute name="content" />
            </f:subview>
            <%-- TODO
            <f:subview id="footer">
                <tiles:insertAttribute name="footer" />
            </f:subview>
            --%>
        </f:view>
    </body>
</html>
