package netgest.bo.xwc.components.classic.charts;

import java.awt.Color;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import netgest.bo.xwc.framework.XUIRequestContext;

/**
 * 
 * An utility class to help with charting operations
 * 
 * @author Pedro Rio
 *
 */
public class ChartUtils 
{

	
	/**
	 * A set of of dark colors to use in the charts
	 */
	public static final Color[] DEFAULT_COLORS = {Color.BLACK, Color.BLUE, 
		Color.CYAN, Color.DARK_GRAY, Color.GRAY, Color.MAGENTA, Color.red, Color.orange, Color.green
	};
	
	/**
	 * 
	 * Receives a Colors as parameter and returns the  RGB string
	 * for that color (e.g <code>#FFFFFF</code>)
	 * 
	 * @param color The Color to convert
	 * 
	 * @return The hexa-decimal string in RGB representing the color
	 */
	public static String ColorToRGB(Color color)
	{
		String rgb = Integer.toHexString(color.getRGB());
		rgb = rgb.substring(2, rgb.length());
		return rgb;
	}
	
	
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
	
	/**
	 * 
	 * Retrieves a dark color (i.e. in RGB each component is bellow 100)
	 * 
	 * @return A dark color
	 */
	public static Color getRandomDarkColor()
	{
		Random rand = new Random();
		int r = rand.nextInt(101);
		int g = rand.nextInt(101);
		int b = rand.nextInt(101);
		return new Color(r,g,b);
	}
	
	/**
	 * 
	 * Retrieves a light color (i.e. in RGB each component is above 140)
	 * 
	 * @return A light color
	 */
	public static Color getRandomLightColor()
	{
		Random rand = new Random();
		int base = 140;
		int r = rand.nextInt(110) + base;
		int g = rand.nextInt(110) + base;
		int b = rand.nextInt(110) + base;
		return new Color(r,g,b);
		
	}
}
