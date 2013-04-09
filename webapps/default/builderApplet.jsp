<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="netgest.bo.def.boDefHandler"%>
<%@page import="netgest.bo.builder.boBuilder"%>
<%@page import="netgest.bo.system.boApplication"%>
<%@page import="netgest.bo.system.boSession"%>
<%@page import="netgest.bo.system.boLoginBean"%><html>
<%
	if( boBuilder.requireAuthentication() && request.getSession().getAttribute("boSession") == null ) {
%>		
		<jsp:forward page="/Login.xvw"></jsp:forward>
<%
		return; 
	}
	if( session.getAttribute("boSession") == null ) {
		// Do a System Login
		boApplication boApp 	= boApplication.getApplicationFromStaticContext("XEO");
		boSession     boSession = boApp.boLogin( "SYSTEM", boLoginBean.getSystemKey() );
		
		request.getSession().setAttribute("boSession", boSession );
	}
%>
<jsp:forward page="/netgest/bo/xwc/components/viewers/Builder.xvw"></jsp:forward>
 