package netgest.bo.xwc.framework.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.faces.application.ViewExpiredException;
import javax.faces.webapp.FacesServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;
import netgest.bo.system.boSession;
import netgest.bo.xwc.components.util.JavaScriptUtils;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.localization.XUICoreMessages;
import netgest.bo.xwc.framework.localization.XUIMessagesLocalization;

public class XUIServlet implements Servlet
{
    FacesServlet        facesServlet;
//    boSession           oXEOSession;
    boApplication       oBoApplication;
    boolean             bIsInitialized;
    
    public XUIServlet()
    {
        facesServlet = new javax.faces.webapp.FacesServlet();
    }

    public void init(ServletConfig servletConfig) throws ServletException
    {
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
    		if( oXEOSession.getUser().getBoui() != 0 ) {
    			if( boApplication.currentContext().getEboContext() == null ) {
			        oEboContext = oXEOSession.createRequestContext( oRequest, oResponse, null );
			        boApplication.currentContext().addEboContext( oEboContext );
    			}
    		}
    	}
        XUIMessagesLocalization.setThreadCurrentLocale( new Locale( "en" ) );
    	
        try {
    		oResponse.setHeader("Pragma", "No-Cache");
    		oResponse.setHeader("cache-control", "max-age=0");
            if( oRequest.getContentType() != null && oRequest.getContentType().startsWith( "text/xml" )  )
            {
                isAjax = true;
                facesServlet.service( new XUIAjaxRequestWrapper( oRequest ), servletResponse );
            } 
            else if( oRequest.getContentType() != null && oRequest.getContentType().startsWith( "multipart/form-data" )  )
            {
                facesServlet.service( new XUIMultiPartRequestWrapper( oRequest ), servletResponse );
            }
            else { 
                facesServlet.service( servletRequest, servletResponse );
            }
        }
        catch( RuntimeException e ) {
        	throw e;
        }
        catch( ServletException e ) {
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
        			sb.append( "/" );
            		sb.append( oRequest.getContextPath() );
            		sb.append( "/Login.xvw?msg=1" );
	        		
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
                oRequestContext.release();
            }
            
            if( oEboContext != null ) 
                oEboContext.close();

            if( oXEOSession != null )
                oXEOSession.getApplication().removeContextFromThread();    

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
