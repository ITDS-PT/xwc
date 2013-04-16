package netgest.bo.xwc.framework;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.Logger;
import netgest.bo.system.boApplication;
import netgest.bo.transaction.XTransactionManager;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.framework.def.XUIViewerDefinition;
import netgest.bo.xwc.framework.jsf.XUIViewHandler;
import netgest.bo.xwc.xeo.workplaces.admin.localization.ExceptionMessage;

import com.sun.faces.util.Util;

public class XUISessionContext {

	Logger log = Logger.getLogger( XUISessionContext.class.getName() );

	public static final String SESSION_ATTRIBUTE_ID = "XUI:SessionContext:";
	public static final String TRANSACTION_ATTRIBUTE_ID = "XUI:TransactionManager:";
	private static final String BEAN_ID_PREFIX = SESSION_ATTRIBUTE_ID + ":Bean:";

	public static final String RESTORED_VIEWS_PREFIX = "XUI:RestoredViews:";

	private XTransactionManager oTransactionManager = new XTransactionManager();

	public XUIViewRoot getView( String sViewStateId ) {
		// Restore a view from the Faces
		FacesContext facesContext;
		facesContext = FacesContext.getCurrentInstance();

		XUIViewRoot viewRoot;
		XUIViewHandler oViewHndlr;

		oViewHndlr = ( XUIViewHandler ) Util.getViewHandler( facesContext );

		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();

		viewRoot = ( XUIViewRoot ) oRequestContext.getAttribute( RESTORED_VIEWS_PREFIX + sViewStateId );

		if ( viewRoot == null ) {
			viewRoot = ( XUIViewRoot ) oViewHndlr.restoreView( facesContext, sViewStateId, sViewStateId );
			if ( viewRoot != null ) {
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

		assert facesContext.getViewRoot() != null : MessageLocalizer.getMessage( "CANT_CREATE_A_CHILD_VIEW_OF_NULL" );
		XUIViewRoot viewRoot;

		viewRoot = ( XUIViewRoot ) ( ( XUIViewHandler ) ( Util.getViewHandler( facesContext ) ) ).createView(
				facesContext, sViewName, ( ( XUIViewRoot ) facesContext.getViewRoot() ).getTransactionId() );
		return viewRoot;
	}

	public XUIViewRoot createChildView( String sViewName, InputStream viewerInputStream ) {
		FacesContext facesContext;
		facesContext = FacesContext.getCurrentInstance();

		assert facesContext.getViewRoot() != null : MessageLocalizer.getMessage( "CANT_CREATE_A_CHILD_VIEW_OF_NULL" );
		XUIViewRoot viewRoot;

		XUIViewHandler viewHandler;
		viewHandler = ( XUIViewHandler ) Util.getViewHandler( facesContext );
		viewRoot = ( XUIViewRoot ) viewHandler.createView( facesContext, sViewName, viewerInputStream,
				( ( XUIViewRoot ) facesContext.getViewRoot() ).getTransactionId() );

		return viewRoot;
	}

	public XUIViewRoot createChildView( String sViewName, XUIViewerDefinition viewerDefinition ) {
		FacesContext facesContext;
		facesContext = FacesContext.getCurrentInstance();

		assert facesContext.getViewRoot() != null : MessageLocalizer.getMessage( "CANT_CREATE_A_CHILD_VIEW_OF_NULL" );
		XUIViewRoot viewRoot;

		XUIViewHandler viewHandler;
		viewHandler = ( XUIViewHandler ) Util.getViewHandler( facesContext );
		viewRoot = ( XUIViewRoot ) viewHandler.createView( facesContext, sViewName, null,
				( ( XUIViewRoot ) facesContext.getViewRoot() ).getTransactionId(), viewerDefinition );

		return viewRoot;
	}

	public XUIViewRoot createView( String sViewName ) {
		FacesContext facesContext;
		facesContext = FacesContext.getCurrentInstance();

		XUIViewRoot viewRoot;
		viewRoot = ( XUIViewRoot ) ( Util.getViewHandler( facesContext ) ).createView( facesContext, sViewName );

		return viewRoot;
	}

	public XUIViewRoot createView( String sViewName, InputStream viewerInputStream ) {
		FacesContext facesContext;
		facesContext = FacesContext.getCurrentInstance();

		XUIViewRoot viewRoot;
		XUIViewHandler viewHandler;
		viewHandler = ( XUIViewHandler ) Util.getViewHandler( facesContext );
		viewRoot = ( XUIViewRoot ) viewHandler.createView( facesContext, sViewName, viewerInputStream, null );

		return viewRoot;
	}

	public StringBuilder renderViewToBuffer( String renderKit, String viewStateId ) throws IOException {
		XUIRequestContext r;
		StringBuilder sb = null;

		// Release the objects to another thread can use
		r = XUIRequestContext.getCurrentContext();
		r.getTransactionManager().release();

		// Release the objects to another thread can use
		EboContext ctx = boApplication.currentContext().getEboContext();
		ctx.releaseObjects();

		String urlCtx = r.getAjaxURL();
		String url = "";
		if ( !url.toLowerCase().startsWith( "http" ) ) {
			HttpServletRequest request = ( HttpServletRequest ) r.getRequest();
			String host = request.getServerName();
			String protocol = "http";
			int port = request.getServerPort();

			if ( request.isSecure() ) {
				url = protocol + "s://" + host + ( port != 80 ? ":" + Integer.toString( port ) : "" );
				if ( !urlCtx.startsWith( "/" ) ) {
					url += "/";
				}
				url += urlCtx;
			} else {
				url = protocol + "://" + host + ( port != 443 ? ":" + Integer.toString( port ) : "" );
				if ( !urlCtx.startsWith( "/" ) ) {
					url += "/";
				}
				url += urlCtx;

			}

			if ( viewStateId != null ) {
				if ( url.indexOf( '?' ) == -1 ) {
					url += "?" + "javax.faces.ViewState=" + viewStateId + "&__renderKit=" + renderKit;
				}
			}

			URL oUrl = new URL( url );
			HttpURLConnection con = ( HttpURLConnection ) oUrl.openConnection();
			con.addRequestProperty( "Cookie", request.getHeader( "Cookie" ) );

			con.setDoInput( true );
			con.setDoOutput( false );

			sb = new StringBuilder();

			InputStream is = con.getInputStream();
			byte[] buffer = new byte[8192];
			int br;
			while ( ( br = is.read( buffer ) ) > 0 ) {
				sb.append( new String( buffer, 0, br, "utf-8" ) );
			}

		}
		return sb;
	}

	public Object getAttribute( String sName ) {
		Map<String, Object> oExternalSession = getExternalSessionMap();
		if ( oExternalSession != null ) {
			return oExternalSession.get( sName );
		} else {
			log.warn( MessageLocalizer.getMessage( "CANT_FIND_HTTPSESSION_TO_GET_ATTRIBUTE" ) + " [" + sName + "]" );
		}
		return null;
	}

	public Object removeAttribute( String sName ) {
		Map<String, Object> oExternalSession = getExternalSessionMap();
		if ( oExternalSession != null ) {
			return oExternalSession.remove( sName );
		} else {
			log.warn( MessageLocalizer.getMessage( "CANT_FIND_HTTPSESSION_TO_GET_ATTRIBUTE" ) + " [" + sName + "]" );
		}
		return null;
	}

	public void setAttribute( String sName, Object oValue ) {
		Map<String, Object> oExternalSession = getExternalSessionMap();
		if ( oExternalSession != null ) {
			oExternalSession.put( sName, oValue );
		} else {
			throw new IllegalStateException( ExceptionMessage.CANNOT_FIND_USER_HTTPSESSION__.toString() );
		}
	}
	
	public Map<String,Object> getSessionMap(){
		return getExternalSessionMap();
	}
	
	private static final Map<String, Object> getExternalSessionMap() {

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
