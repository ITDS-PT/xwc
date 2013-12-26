<%@page import="netgest.bo.system.boSession"%>
<%@page import="netgest.bo.security.SecurityLogger"%>
<%@page import="netgest.bo.system.boSessionUser"%>
<%@page import="netgest.bo.runtime.EboContext"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="netgest.bo.system.boApplication, java.util.*"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title></title>
</head>
<body>

<PRE>
</PRE>


<%

boSession bosession = (boSession)request.getSession().getAttribute("boSession");
if( bosession != null) {
	boSessionUser user = bosession.getUser();
	if (user.isAdministrator()){

	netgest.bo.system.boPoolManager p = boApplication.getApplicationFromStaticContext("XEO").getMemoryArchive().getPoolManager();
	%>
<PRE>
	Tamanho da Pool: <%=p.ObjectPool.size()%>
	Tamanho Weak Pool: <%=p.WeakObjectPool.size()%>
	Tamanho Owned Objects: <%=p.OwnedObjects.size()%>
	Tamanho Objects UserNames : <%=p.ObjectPoolUSERNAMES.size()%>
	Tamanho Context Keys: <%=p.ContextKeys.size()%>
	
	Detalhes da Context Keys:<br> 	
	<%
		Enumeration enumKeys = p.ContextKeys.keys();
		while( enumKeys.hasMoreElements() ) {
			Object key = enumKeys.nextElement();
			Hashtable keyTable = (Hashtable)p.ContextKeys.get( key );
			
			out.println( key + " Size: " + keyTable.size() );
			
		}
	
	%>
	Detalhes da Owned Objects:<br> 	
	<%
		enumKeys = p.OwnedObjects.keys();
		while( enumKeys.hasMoreElements() ) {
			Object key = enumKeys.nextElement();
			HashMap keyTable = (HashMap)p.OwnedObjects.get( key );
			
			out.println( key + " Size: " + keyTable.size() );
			
		}
	
		if (request.getParameter("clear") != null) {
					p.ContextKeys.clear();
					p.ObjectPool.clear();
					p.ObjectPoolUSERNAMES.clear();
					p.OwnedObjects.clear();
					p.TimeoutContext.clear();
		} 
		if ("y".equalsIgnoreCase(request.getParameter("detail"))) {
				p.dumpPool(out);
		}

	} else {
		out.println("Permission Denied, not System Admin");
		SecurityLogger.severe(String.format("Attempt to show the pool by a user not sysuser (%s)",
									user.getUserName()));

	}
} else {
	out.println("Permission Denied, not logged in");
	SecurityLogger.severe("Attempt to show the pool by a user not logged in");
}
%>

</body>
</html>