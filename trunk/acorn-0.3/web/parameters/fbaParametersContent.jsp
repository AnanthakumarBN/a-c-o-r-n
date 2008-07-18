<%-- 
    Document   : FBAParametersContent
    Created on : 2008-03-12, 20:55:08
    Author     : szymon
--%>

<%@taglib prefix="tiles" uri="WEB-INF/tiles-jsp.tld"%>

<tiles:insertTemplate template="/parameters/parametersTemplate.jsp">
    <tiles:putAttribute name="methodTitle" value="Single Flux Balance Analysis Parameters" />
    <tiles:putAttribute name="reactionTable" value="/parameters/paramsReactionTable.jsp" />
    <tiles:putAttribute name="speciesTable" value="/parameters/paramsSpeciesTable.jsp" />
    <tiles:putAttribute name="genesTable" value="/parameters/empty.jsp" />
</tiles:insertTemplate>
