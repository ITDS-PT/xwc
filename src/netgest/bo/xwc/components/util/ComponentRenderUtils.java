package netgest.bo.xwc.components.util;

import javax.servlet.http.HttpServletRequest;

import netgest.bo.xwc.framework.XUIRequestContext;

/**
 * 
 * Class with utility methods to Aid with rendering of component
 * 
 * 
 * @author PedroRio
 *
 */
public class ComponentRenderUtils {

	
	/**
	 * Retrieves the URL to use when one needs to access
	 * the Component as a servlet, returns the complete URL
	 * with hostname and port
	 * 
	 * @param reqContext The request context
	 * @param clientID The identifier of the client
	 * 
	 * @return The URL to access the component as servlet
	 */
	public static String getCompleteServletURL(XUIRequestContext reqContext, String clientID)
	{
		String url = reqContext.getAjaxURL();
		if (url.indexOf('?') == -1)
			{ url += '?'; }
		else
			{ url += '&'; }
		
		HttpServletRequest req = (HttpServletRequest)reqContext.getFacesContext().getExternalContext().getRequest();
		String link = 
        	(req.isSecure()?"https":"http") + "://" + 
        	req.getServerName() +
        	(req.getServerPort()==80?"":":"+req.getServerPort());
		
		url += "javax.faces.ViewState=" + reqContext.getViewRoot().getViewState();
		url += "&xvw.servlet="+clientID;
		url = link + url;
		
		return url;
	}
	
	
	/**
	 * 
	 * Retrieves the URL Servlet, without the component reference
	 * 
	 * @param reqContext The context request
	 * 
	 * @return The Servlet Request URL for a component
	 */
	public static String getServletURL(XUIRequestContext reqContext)
	{
		String url = reqContext.getAjaxURL();
		if (url.indexOf('?') == -1)
			{ url += '?'; }
		else
			{ url += '&'; }
		
		HttpServletRequest req = (HttpServletRequest)reqContext.getFacesContext().getExternalContext().getRequest();
		String link = 
        	(req.isSecure()?"https":"http") + "://" + 
        	req.getServerName() +
        	(req.getServerPort()==80?"":":"+req.getServerPort());
		
		url += "javax.faces.ViewState=" + reqContext.getViewRoot().getViewState();
		url += "&xvw.servlet=";
		url = link + url;
		
		return url;
	}
	
	/**
	 * Retrieves the URL to use when one needs to access
	 * the Component as a servlet, returns a relative URL
	 *
	 * 
	 * @param reqContext The request context
	 * @param clientID The identifier of the client
	 * 
	 * @return The URL to access the component as servlet
	 */
	public static String getServletURL(XUIRequestContext reqContext, String clientID)
	{
		String url = reqContext.getAjaxURL();
		if (url.indexOf('?') == -1)
			{ url += '?'; }
		else
			{ url += '&'; }
		
		url += "javax.faces.ViewState=" + reqContext.getViewRoot().getViewState();
		url += "&xvw.servlet="+clientID;
		return url;
	}
	
}
