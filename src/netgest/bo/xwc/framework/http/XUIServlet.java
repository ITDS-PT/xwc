package netgest.bo.xwc.framework.http;

import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;
import netgest.bo.system.boSession;
import netgest.bo.xwc.components.util.JavaScriptUtils;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.jsf.XUIViewHandler;
import netgest.bo.xwc.framework.localization.XUICoreMessages;
import netgest.bo.xwc.framework.localization.XUILocalization;

import netgest.utils.StringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.faces.application.ViewExpiredException;
import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

public class XUIServlet extends HttpServlet
{
	
	private String loginPage = "Login.xvw";
	
	Logger				logger = Logger.getLogger( XUIServlet.class ); 
    FacesServlet        facesServlet;
//    boSession           oXEOSession;
    boApplication       oBoApplication;
    boolean             bIsInitialized;
    String				defaultLang;
    Locale 				defaultLocale;
    boolean				useBrowserLanguage = false;
    /**
     * RenderKit used in this web context
     */
    String				renderKit;
    
    public XUIServlet()
    {
        facesServlet = new javax.faces.webapp.FacesServlet();
    }

    public void init(ServletConfig servletConfig) throws ServletException
    {
    	String loginPageParam = servletConfig.getInitParameter("LoginPageWhenExpired");
        if (loginPageParam != null && !"".equals(loginPageParam))
        	this.loginPage = loginPageParam;
        
        if (!loginPage.startsWith("/")) {
              loginPage = "/" + loginPage;
        }

    	defaultLang = servletConfig.getInitParameter("DefaultLanguage");
    	if( defaultLang != null && defaultLang.length() > 0 ) {
        	defaultLocale = new Locale( defaultLang ); 
    	}
    	else {
    		defaultLocale = Locale.getDefault();
    	}
    	
    	String useBrowserLanguage = servletConfig.getInitParameter("UseBrowserLanguage");
    	if (StringUtils.hasValue( useBrowserLanguage )){
    		this.useBrowserLanguage = Boolean.parseBoolean( useBrowserLanguage );
    	}
    	
    	String useCacheParameter = servletConfig.getInitParameter("UseViewerCache");
    	if (StringUtils.hasValue( useCacheParameter )){
    		boolean useCache = Boolean.parseBoolean( useCacheParameter );
    		if (!useCache) {
    			XUIViewHandler.disableCache();
    		}
    	}
    	
    	renderKit = servletConfig.getInitParameter("renderKit");
    	
        facesServlet.init(servletConfig);
        initializeXeo();
    }
    
    public synchronized void initializeXeo() {
        if( oBoApplication == null ) {
            oBoApplication = boApplication.getApplicationFromStaticContext("XEO");
        }
        bIsInitialized = true;
    }

    public ServletConfig getServletConfig()
    {
        return facesServlet.getServletConfig();
    }
    
    public void service(ServletRequest servletRequest, ServletResponse servletResponse)
        throws ServletException, IOException
    {
        HttpServletRequest oRequest;
        HttpServletResponse oResponse;
        EboContext oEboContext;
        
        oEboContext = null;
        oRequest = (HttpServletRequest)servletRequest;
        oResponse = (HttpServletResponse)servletResponse;
        
        boolean isAjax = false;
        
        // Initialize XEO....        
        if( !bIsInitialized ) 
            initializeXeo();
        
        HttpSession oHttpSession = oRequest.getSession();
        boSession	oXEOSession = null;
        if( oHttpSession != null ) {
        	oXEOSession = (boSession)oHttpSession.getAttribute( "boSession" );
        }
        
    	
	
		if( oXEOSession != null ) {
			oXEOSession.getApplication().removeContextFromThread();
			oEboContext = oXEOSession.createRequestContextInServlet( oRequest, oResponse, getServletContext() );
			boApplication.currentContext().addEboContext( oEboContext );
		}
		
		Locale userLocale = XUILocalization.getUserLocale();
		Locale requestLocale = XUILocalization.getCurrentLocale();
		if ( useBrowserLanguage ){
			Locale browserLocale = oRequest.getLocale();
			if (browserLocale != null && userLocale == null){
				requestLocale = browserLocale;
				XUILocalization.setCurrentLocale( requestLocale );
			} 
		} 
		if( oXEOSession != null ) {
			oXEOSession.setDefaultLocale( requestLocale );
		}
		
    	if (StringUtils.hasValue( renderKit )){
    		oRequest.setAttribute( "__renderKit" , renderKit );
    	}
    	
        try {
    		oResponse.setHeader("Pragma", "No-Cache");
    		oResponse.setHeader("cache-control", "max-age=0");
            if( oRequest.getContentType() != null && oRequest.getContentType().startsWith( "text/xml" )  )
            {
                isAjax = true;
                facesServlet.service( new XUIAjaxRequestWrapper( oRequest ), servletResponse );
            } 
            else if( oRequest.getContentType() != null && oRequest.getContentType().startsWith( "multipart/form-data" ) && StringUtils.isEmpty( oRequest.getParameter( "xwc-upload" ))  )
            {
                facesServlet.service( new XUIMultiPartRequestWrapper( oRequest ) , servletResponse );
            }
            else { 
                facesServlet.service( servletRequest, servletResponse );
            }
        }
        catch( RuntimeException e ) {
        	throw e;
        }
        catch( ServletException e ) {
        	
        	if( e.getRootCause() != null )
        		logger.error( "", e.getRootCause() );
        	else
        		logger.error( "", e.getRootCause() );
        	
        	if( !Boolean.parseBoolean( (String)oRequest.getAttribute("xvw.portlet") ) ) {
	        	if( e.getRootCause() instanceof ViewExpiredException && oRequest.getSession() != null && oRequest.getSession().isNew() ) {
	        		// Handle expired view
            		StringBuilder sb = new StringBuilder();
            		if( oRequest.isSecure() ) { 
            			sb.append( "https://" );
            			sb.append( oRequest.getServerName() );
            			if( oRequest.getServerPort() != 443 ) {
            				sb.append( ':' );
            				sb.append( oRequest.getServerPort() );
            			}
            		}
            		else {
            			sb.append( "http://" );
            			sb.append( oRequest.getServerName() );
            			if( oRequest.getServerPort() != 80 ) {
            				sb.append( ':' );
            				sb.append( oRequest.getServerPort() );
            			}
            		}
            		
            		String ctxPath = oRequest.getContextPath();

                    if (!ctxPath.startsWith("/")) {
                          ctxPath = "/" + ctxPath;
                    }

                    sb.append(ctxPath);
                    sb.append(loginPage);
                    sb.append("?msg=1");

            		if( isAjax ) {
		        		oResponse.setHeader( "login-url" , sb.toString() );
		        		oResponse.sendError(401);
	        		}
	        		else {
	            		oResponse.reset();
	            		oResponse.resetBuffer();
	            		oResponse.setContentType("text/html;charset=utf-8");
	            		PrintWriter out = oResponse.getWriter();
	            		out.println( "<script>window.top.location.href='" 
	            				+ JavaScriptUtils.safeJavaScriptWrite( sb.toString() , '\'')
	            				+ "';</script>" );
	        		}
	        	}
	        	else {
	        		if( isAjax ) {
	        			throw e;
	        		}
	        		oResponse.reset();
	        		oResponse.resetBuffer();
	        		oResponse.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
	        		oResponse.setContentType("text/html;charset=utf-8");
	        		PrintWriter out = oResponse.getWriter();
	        		out.println( "<h2>" + XUICoreMessages.REQUEST_ERROR.toString() +  "</h2><br>" );
	        		out.println( "<a onclick='document.getElementById(\"errorDetails\").style.display=\"\"'  href='javascript:void(0)'>" + XUICoreMessages.ERROR_DETAILS.toString() +  "</a>" );
	        		out.println( "<div id='errorDetails' style='display:none;overflow:auto;widht:100%;height:80%'><pre>");
	        		out.println( "<b>Error:</b>" );
	        		e.printStackTrace( out );
	        		if( e.getRootCause() != null ) {
		        		out.println( "<b>Cause By:</b>" );
		        		e.getRootCause().printStackTrace( out );
	        		}
	        		out.println("</pre></div>" );
	        	}
        	}
        }
        catch( IOException e ) {
        	throw e;
        } 
        finally {
            if (XUIRequestContext.getCurrentContext() != null )
            {
                XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
                try {
                	oRequestContext.release();
                } catch( Throwable e ) {};
            }
            
            try {
	            if( oEboContext != null ) 
	                oEboContext.close();
            } catch( Throwable e ) {};

            try {
	            if( oXEOSession != null )
	                oXEOSession.getApplication().removeContextFromThread();    
            } catch( Throwable e ) {};

        }
    }

    public String getServletInfo()
    {
        return facesServlet.getServletInfo();
    }

    public void destroy()
    {
        facesServlet.destroy();
    }
}
