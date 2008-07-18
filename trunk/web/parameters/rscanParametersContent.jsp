<%-- 
    Document   : RSCANParametersContent
    Created on : 2008-04-18, 19:42:53
    Author     : sg236027
--%>

<%@taglib prefix="tiles" uri="WEB-INF/tiles-jsp.tld"%>

<tiles:insertTemplate template="/parameters/parametersTemplate.jsp">
    <tiles:putAttribute name="methodTitle" value="Reaction Essentiality Scan Parameters" />
    <tiles:putAttribute name="reactionTable" value="/parameters/paramsReactionTable.jsp" />
    <tiles:putAttribute name="speciesTable" value="/parameters/paramsSpeciesTable.jsp" />
    <tiles:putAttribute name="genesTable" value="/parameters/empty.jsp" />
</tiles:insertTemplate>
