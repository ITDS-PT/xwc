package netgest.bo.xwc.framework.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import netgest.bo.xwc.components.security.ViewerAccessPolicyBuilder;

public class XUILoginFilter implements Filter {

	private static final Logger log = Logger.getLogger( XUILoginFilter.class );

	
	private String 		loginPage = "/Login.xvw";
	private String 		mainPage = "Main.xvw";
	private boolean 	showUserProfiles = true;
	
	public void destroy() {
		// TODO Auto-generated method stub

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest oRequest = (HttpServletRequest)request;
		
		String servletPath = oRequest.getContextPath();
		if( !servletPath.startsWith("/") ) {
			servletPath = "/" + servletPath.substring(1);
		}
		
		String requestPath = oRequest.getRequestURI();
		
		if( requestPath.indexOf( loginPage ) != -1 ) {
			oRequest.setAttribute( "__xwcMainViewer" , mainPage );
			oRequest.setAttribute( "__xwcShowUserProfiles" , showUserProfiles );
			chain.doFilter( request , response );
		}
		else {
			HttpSession oHttpSession = oRequest.getSession();
			if( oHttpSession != null && oHttpSession.getAttribute("boSession") != null ) {
				chain.doFilter(request, response);
			}
			else {
				HttpServletResponse oResponse = (HttpServletResponse)response;
				oResponse.sendRedirect( servletPath + "/" + loginPage );
			}
		}
	}

	public void init(FilterConfig arg0) throws ServletException {

		if( arg0.getInitParameter("LoginPage") != null )
			loginPage = arg0.getInitParameter("LoginPage");
		
		if( arg0.getInitParameter("MainPage") != null )
			mainPage  = arg0.getInitParameter("MainPage");
		
		if( arg0.getInitParameter("ShowUserProfiles") != null )
			showUserProfiles = Boolean.parseBoolean( arg0.getInitParameter("ShowUserProfiles") );
		
		if( arg0.getInitParameter("ViewerSecurityMode") != null ) {
			String s = arg0.getInitParameter("ViewerSecurityMode");
			ViewerAccessPolicyBuilder.SecurityMode m = ViewerAccessPolicyBuilder.SecurityMode.valueOf( s );
			if( m != null ) {
				ViewerAccessPolicyBuilder.setSecurityMode( m );
			}
		}

	}

}
