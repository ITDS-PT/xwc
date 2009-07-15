package netgest.bo.xwc.framework.http;

import java.io.IOException;

import javax.faces.application.ViewExpiredException;
import javax.faces.webapp.FacesServlet;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import javax.servlet.http.HttpServletResponse;

import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boSession;
import netgest.bo.xwc.framework.XUIApplicationContext;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.jsf.XUIPhaseListener;

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
		        oEboContext = oXEOSession.createRequestContext( oRequest, oResponse, null );
		        boApplication.currentContext().addEboContext( oEboContext );
    		}
    	}
        
        try {
    		oResponse.setHeader("Pragma", "No-Cache");
    		oResponse.setHeader("cache-control", "max-age=0");
            if( oRequest.getContentType() != null && oRequest.getContentType().startsWith( "text/xml" )  )
            {
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
        	if( e.getRootCause() instanceof ViewExpiredException ) {
        		// Handle expired view
        		oResponse.sendError(401, "Session was expired!");
        	}
        	else {
        		throw e;
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
