<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="netgest.bo.def.boDefHandler"%>
<%@page import="netgest.bo.system.boApplication"%>
<%@page import="netgest.bo.system.boSession"%>
<%@page import="netgest.bo.system.boLoginBean"%>
<%
String xeodev=System.getProperty("xeo.development");
if (xeodev!=null && (xeodev.equalsIgnoreCase("true") ||
		xeodev.equalsIgnoreCase("yes")))
{
	// Do a System Login
	if (request.getSession().getAttribute("boSession")==null)
	{
		boApplication boApp 	= boApplication.getApplicationFromStaticContext("XEO");
		boSession     boSession = boApp.boLogin( "SYSUSER", boLoginBean.getSystemKey() );
		request.getSession().setAttribute("boSession", boSession );	
	} 
	String viewerName=request.getParameter("viewer");
	response.sendRedirect("netgest/bo/xwc/components/viewers/DummyRenderViewer.xvw?" +
		"viewer="+viewerName+"&openViewer=true&oldviewer="+request.getParameter("oldviewer"));
}
%>
