package netgest.bo.xwc.components.beans;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import netgest.bo.xwc.components.localization.BeansMessages;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.components.util.JavaScriptUtils;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUISessionContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIViewRoot;

import org.json.JSONObject;

public class XEOMainBean extends XEOBase {

	public XEOMainBean() {
		if( getEboContext() != null ) {
			XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
			oRequestContext.getScriptContext().add( 
						XUIScriptContext.POSITION_HEADER, 
						"userName",
						"window.xeoUserDisplayName='" +
						JavaScriptUtils.safeJavaScriptWrite( getEboContext().getSysUser().getName(), '\'') +
						"'"						
				);
			boolean xeodmactive = false;
			HttpServletRequest oServletRequest = (HttpServletRequest)oRequestContext.getRequest();
			Cookie cookies [] = oServletRequest.getCookies();
			for( Cookie cookie : cookies ) {
				if( "xeodmstate".equals( cookie.getName() ) ) {
					xeodmactive = Boolean.parseBoolean( cookie.getValue() );
					break;
				}
			}
			oRequestContext.getScriptContext().add( 
					XUIScriptContext.POSITION_HEADER, 
					"xeodmstate",
					"window.xeodmstate=" + xeodmactive 
			);
			
		}
	}
	
	public void editObject() throws Exception {
        XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;
        XEOBaseBean         oBaseBean;
        
        oRequestContext = XUIRequestContext.getCurrentContext();
        oSessionContext = oRequestContext.getSessionContext();
        
        JSONObject o = new JSONObject( 
                (String)((XUICommand)oRequestContext.getEvent().getSource()).getValue() 
        );
        ViewerConfig oViewerConfig = new ViewerConfig( o );
        
        String sViewerName = oViewerConfig.getViewerName();  

        oViewRoot = oSessionContext.createView( sViewerName );
        oBaseBean = (XEOBaseBean)oViewRoot.getBean( "viewBean" );
        
        if( oBaseBean == null ) {
        	throw new  RuntimeException( BeansMessages.VIEWER_WITHOUT_VIEWBEAN.toString() );
        }
        oBaseBean.setCurrentObjectKey( Long.toString( oViewerConfig.getBoui() ) );
        // Diz a que a view corrente é a criada.
        oRequestContext.setViewRoot( oViewRoot );
        
        // TODO: This action must be automatic on the platform
        // initialize components
        oViewRoot.processInitComponents();
    }

    public void createObject() throws Exception {

        XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;
        XEOBaseBean         oBaseBean;
        
        oRequestContext = XUIRequestContext.getCurrentContext();
        oSessionContext = oRequestContext.getSessionContext();
        
        JSONObject o = new JSONObject( 
                (String)((XUICommand)oRequestContext.getEvent().getSource()).getValue() 
        );
        
        ViewerConfig oViewerConfig = new ViewerConfig( o );
        
        String sViewerName = oViewerConfig.getViewerName(); 
        String sObjectName = oViewerConfig.getObjectName();

        oViewRoot = oSessionContext.createView( sViewerName );
        oBaseBean = (XEOBaseBean)oViewRoot.getBean( "viewBean" );
        oBaseBean.createNew( sObjectName );
        
        // Diz a que a view corrente é a criada.
        oRequestContext.setViewRoot( oViewRoot );
        
        // TODO: This action must be automatic on the platform
        // initialize components
        oViewRoot.processInitComponents();
        
    }
    
    // Platform edit object
    public void editObject( String sKey, String sView ) {
        // Cria view
        XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;
        XEOBaseBean         oBaseBean;
        
        oRequestContext = XUIRequestContext.getCurrentContext();
        oSessionContext = oRequestContext.getSessionContext();
        
        // Obtem a bean do objecto a ser editado
        // e associa o objecto do parametro

        oViewRoot = oSessionContext.createView( sView );
        oBaseBean = (XEOBaseBean)oViewRoot.getBean( "viewBean" );
        oBaseBean.setCurrentObjectKey( sKey );
        
        // Diz a que a view corrente é a criada.
        oRequestContext.setViewRoot( oViewRoot );
        
        // TODO: This action must be automatic on the platform
        // initialize components
        oViewRoot.processInitComponents();

        
    }

    public void listObject( ) throws Exception {
        
        XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;
        XEOBaseList         oBaseBean;
        
        oRequestContext = XUIRequestContext.getCurrentContext();
        oSessionContext = oRequestContext.getSessionContext();
        
        JSONObject o = new JSONObject( 
                (String)((XUICommand)oRequestContext.getEvent().getSource()).getValue() 
        );
        ViewerConfig oViewerConfig = new ViewerConfig( o );
        
        String sViewerName = oViewerConfig.getViewerName(); 

        oViewRoot = oSessionContext.createView( sViewerName );
        oBaseBean = (XEOBaseList)oViewRoot.getBean( "viewBean" );
        
        XUIComponentBase c =  oRequestContext.getEvent().getComponent();
        if( c instanceof Menu ) {
        	String sText = ((Menu)c).getText();
        	if( sText != null && sText.length() > 0 ) {
        		oBaseBean.setTitle( sText );
        	}
        }
        
        oBaseBean.executeBoql( oViewerConfig.getBoql() );
        // Diz a que a view corrente é a criada.
        oRequestContext.setViewRoot( oViewRoot );
        
        // TODO: This action must be automatic on the platform
        // initialize components
        oViewRoot.processInitComponents();
        
        
    }
    
    public void openViewer()  throws Exception {
        XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;

        oRequestContext = XUIRequestContext.getCurrentContext();
        oSessionContext = oRequestContext.getSessionContext();

        JSONObject o = new JSONObject( 
                (String)((XUICommand)oRequestContext.getEvent().getSource()).getValue() 
        );
        ViewerConfig oViewerConfig = new ViewerConfig( o );
        
        String sViewerName = oViewerConfig.getViewerName(); 

        oViewRoot = oSessionContext.createView( sViewerName );
        
        oRequestContext.setViewRoot( oViewRoot );
        oViewRoot.processInitComponents();
    	
    }
    
    public void logout() {
        try {
			XUIRequestContext oRequestContext;
			oRequestContext = XUIRequestContext.getCurrentContext();
			if( !oRequestContext.isAjaxRequest() ) {
				((HttpServletResponse) oRequestContext.getResponse())
						.sendRedirect("/logout.jsp");
				oRequestContext.responseComplete();
			}
			else {
				oRequestContext.getScriptContext().add( XUIScriptContext.POSITION_HEADER , "logout", 
						"window.top.location.href='Logout.jsp'"
				);
				
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
    
}
