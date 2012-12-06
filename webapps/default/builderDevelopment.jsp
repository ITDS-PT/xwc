<%@page import="netgest.bo.http.XEOBuilderFilter"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="netgest.bo.def.boDefHandler"%>
<%@page import="netgest.bo.builder.boBuilder"%>
<%@page import="netgest.bo.system.boApplication"%>
<%@page import="netgest.bo.system.boSession"%>
<%@page import="netgest.bo.system.boLoginBean"%><html>
<%
	if (!XEOBuilderFilter.developmentMode)
		response.sendRedirect("Login.xvw");
	// Do a System Login
	if (request.getSession().getAttribute("boSession")==null)
	{
		boApplication boApp 	= boApplication.getApplicationFromStaticContext("XEO");
		boSession     boSession = boApp.boLogin( "SYSTEM", boLoginBean.getSystemKey() );
	
		request.getSession().setAttribute("boSession", boSession );
	}
%>
<jsp:forward page="/netgest/bo/xwc/components/viewers/builderDevelopment.xvw" >
	<jsp:param name="autoBuild" value="true" /> 
</jsp:forward>
