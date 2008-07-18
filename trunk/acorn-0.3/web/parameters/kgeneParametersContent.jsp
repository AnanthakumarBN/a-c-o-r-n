<%-- 
    Document   : KGENEParametersContent
    Created on : 2008-04-18, 19:43:13
    Author     : sg236027
--%>

<%@taglib prefix="tiles" uri="WEB-INF/tiles-jsp.tld"%>

<tiles:insertTemplate template="/parameters/parametersTemplate.jsp">
    <tiles:putAttribute name="methodTitle" value="Single Gene Knockout Parameters" />
    <tiles:putAttribute name="reactionTable" value="/parameters/paramsReactionTable.jsp" />
    <tiles:putAttribute name="speciesTable" value="/parameters/paramsSpeciesTable.jsp" />
    <tiles:putAttribute name="genesTable" value="/parameters/paramsGenesTable.jsp" />
</tiles:insertTemplate>

