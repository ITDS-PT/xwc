package netgest.bo.xwc.framework;

import com.sun.faces.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import netgest.bo.system.Logger;

import javax.faces.context.FacesContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import netgest.bo.transaction.XTransactionManager;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.framework.jsf.XUIViewHandler;

public class XUISessionContext {

    Logger log = Logger.getLogger( XUISessionContext.class.getName() );

    public static final String SESSION_ATTRIBUTE_ID = "XUI:SessionContext:";
    public static final String TRANSACTION_ATTRIBUTE_ID = "XUI:TransactionManager:";
    private static final String BEAN_ID_PREFIX = SESSION_ATTRIBUTE_ID + ":Bean:";
    
    private static final String RESTORED_VIEWS_PREFIX = "XUI:RestoredViews:";
    

    private XTransactionManager oTransactionManager = new XTransactionManager();

    public XUIViewRoot getView( String sViewStateId ) {
        // Restore a view from the Faces
        FacesContext facesContext;
        facesContext = FacesContext.getCurrentInstance();
        
        XUIViewRoot viewRoot;
        XUIViewHandler oViewHndlr;
        
        oViewHndlr = (XUIViewHandler)Util.getViewHandler(facesContext);
        
        XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
        
        viewRoot = (XUIViewRoot)oRequestContext.getAttribute( RESTORED_VIEWS_PREFIX + sViewStateId );
        
        if( viewRoot == null ) {
        	viewRoot =  (XUIViewRoot)oViewHndlr.restoreView(facesContext, sViewStateId, sViewStateId );
        	if( viewRoot != null ) {
        		oRequestContext.setAttribute( RESTORED_VIEWS_PREFIX + sViewStateId, viewRoot );
        	}
        }
        return viewRoot;
    }
    
    public Object getBean( String sBeanName ) {
        return getAttribute( BEAN_ID_PREFIX + sBeanName );
    }
    
    public void setBean( String sBeanName, Object oBean ) {
        setAttribute( BEAN_ID_PREFIX + sBeanName, oBean );
    }
    
    public XUIViewRoot createChildView( String sViewName ) {
        FacesContext facesContext;
        facesContext = FacesContext.getCurrentInstance();
        
        assert facesContext.getViewRoot() != null : "Can't create a child view of null";
        XUIViewRoot viewRoot;
        
        viewRoot = (XUIViewRoot)((XUIViewHandler)(Util.getViewHandler(facesContext))).createView( 
            facesContext, sViewName, ((XUIViewRoot)facesContext.getViewRoot()).getTransactionId() 
        );
        return viewRoot;
    }

    public XUIViewRoot createChildView( String sViewName, InputStream viewerInputStream ) {
        FacesContext facesContext;
        facesContext = FacesContext.getCurrentInstance();
        
        assert facesContext.getViewRoot() != null : "Can't create a child view of null";
        XUIViewRoot viewRoot;
        
        XUIViewHandler viewHandler;
        viewHandler = (XUIViewHandler)Util.getViewHandler(facesContext);
        viewRoot = (XUIViewRoot)viewHandler.
	        createView( 
	                facesContext, sViewName, viewerInputStream,((XUIViewRoot)facesContext.getViewRoot()).getTransactionId()
	        );        
        
        return viewRoot;
    }

    
    public XUIViewRoot createView( String sViewName ) {
        FacesContext facesContext;
        facesContext = FacesContext.getCurrentInstance();
        
        XUIViewRoot viewRoot;
        viewRoot = (XUIViewRoot)(Util.getViewHandler(facesContext)).
              createView(facesContext, sViewName);        

        return viewRoot;
    }

    public XUIViewRoot createView( String sViewName, InputStream viewerInputStream ) {
        FacesContext facesContext;
        facesContext = FacesContext.getCurrentInstance();
        
        XUIViewRoot viewRoot;
        XUIViewHandler viewHandler;
        viewHandler = (XUIViewHandler)Util.getViewHandler(facesContext);
        viewRoot = (XUIViewRoot)viewHandler.
              createView(facesContext, sViewName, viewerInputStream, null );        

        return viewRoot;
    }
    
    public StringBuilder renderViewToBuffer( String renderKit, String viewStateId ) throws IOException {
    	XUIRequestContext r;
    	StringBuilder sb = null;
    	r = XUIRequestContext.getCurrentContext();
    	String url = r.getAjaxURL();
    	if( !url.toLowerCase().startsWith( "http" ) ) {
    		HttpServletRequest request =  (HttpServletRequest)r.getRequest();
    		String host = request.getServerName();
    		String protocol = request.getProtocol();
    		int port = request.getServerPort();
    		
    		if( request.isSecure() ) {
    			url = protocol + "://" + host + ( port != 80?Integer.toString(port):"" ) + "/" + url;
    		} else {
    			url = protocol + "://" + host + ( port != 443?Integer.toString(port):"" ) + "/" + url;
    		}
    		
    		if( viewStateId != null ) {
    			if( url.indexOf( '?' ) == -1 ) {
    				url += "?" + "javax.faces.ViewState=" + viewStateId; 
    			}
    		}
    		
    		URL oUrl = new URL( url );
    		URLConnection con = oUrl.openConnection();
    		con.setDoInput( true );
    		con.setDoOutput( false );
    		
    		sb = new StringBuilder();
    		
    		InputStream is = con.getInputStream();
    		byte[] buffer = new byte[8192];
    		int br;
    		while( (br = is.read( buffer )) > 0 ) {
    			sb.append( new String( buffer, "utf-8" ) );
    		}
    	}
    	return sb;
    }
    
    public Object getAttribute( String sName ) {
        Map<String,Object> oExternalSession = getExternalSessionMap();
        if( oExternalSession != null ) {
            return oExternalSession.get( sName );
        }
        else {
            log.warn( "Cant find HttpSession to get attribute ["+ sName +"]" );
        }
        return null;
    }

    public Object removeAttribute( String sName ) {
        Map<String,Object> oExternalSession = getExternalSessionMap();
        if( oExternalSession != null ) {
            return oExternalSession.remove( sName );
        }
        else {
            log.warn( "Cant find HttpSession to get attribute ["+ sName +"]" );
        }
        return null;
    }
    
    public void setAttribute( String sName, Object oValue ) {
        Map<String,Object> oExternalSession = getExternalSessionMap();
        if( oExternalSession != null ) {
            oExternalSession.put( sName, oValue );
        }
        else {
            throw new IllegalStateException("Cannot find user HttpSession, maybe session was expired");
        }
    }
    
    private static final Map<String,Object> getExternalSessionMap() {

        // Get a bean from the User Context
        return XUIRequestContext.getCurrentContext().getFacesContext().getExternalContext().getSessionMap();
    }

    public XTransactionManager getTransactionManager() {
        return oTransactionManager;
    }

    public void close() {
		this.oTransactionManager.releaseAll();
    }
    
	@Override
	protected void finalize() throws Throwable {
		close();
	}
    

}
