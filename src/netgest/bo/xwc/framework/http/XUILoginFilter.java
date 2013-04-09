package netgest.bo.xwc.framework.http;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import netgest.bo.xwc.components.security.ViewerAccessPolicyBuilder;
import netgest.bo.xwc.components.util.JavaScriptUtils;

public class XUILoginFilter implements Filter {

	private String 		loginPage = "/Login.xvw";
	private String 		mainPage = "Main.xvw";
	private boolean 	showUserProfiles = true;
	
	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest oRequest = (HttpServletRequest)request;
		HttpServletResponse oResponse = (HttpServletResponse)response;
		
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
				String redirectUrl = loginPageUrl(oRequest);
				if (oRequest.getContentType() != null && oRequest.getContentType().startsWith("text/xml")) {
				    oResponse.setHeader("login-url", redirectUrl + "?msg=1");
				    oResponse.sendError(401);
				} else {
				    oResponse.reset();
				    oResponse.resetBuffer();
				    oResponse.setContentType("text/html;charset=utf-8");
				    PrintWriter out = oResponse.getWriter();
				    out.println("<script>parent.top.location.href='" + JavaScriptUtils.safeJavaScriptWrite(redirectUrl, '\'') + "';</script>");
				}
				oResponse.sendRedirect( servletPath + "/" + loginPage );
			}
		}
	}
	
	private String loginPageUrl(HttpServletRequest oRequest) {
		StringBuilder sb = new StringBuilder();

		if (oRequest.isSecure()) {
		    sb.append("https://");
		    sb.append(oRequest.getServerName());

		    if (oRequest.getServerPort() != 443) {
			sb.append(':');
			sb.append(oRequest.getServerPort());
		    }
		} else {
		    sb.append("http://");
		    sb.append(oRequest.getServerName());

		    if (oRequest.getServerPort() != 80) {
			sb.append(':');
			sb.append(oRequest.getServerPort());
		    }
		}

		String ctxPath = oRequest.getContextPath();

		if (!ctxPath.startsWith("/")) {
		    ctxPath = "/" + ctxPath;
		}

		sb.append(ctxPath);

		if (!loginPage.startsWith("/")) {
		    sb.append("/");
		}

		sb.append(loginPage);

		return sb.toString();
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
