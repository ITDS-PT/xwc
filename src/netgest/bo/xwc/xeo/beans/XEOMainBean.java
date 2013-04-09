package netgest.bo.xwc.xeo.beans;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import netgest.bo.runtime.EboContext;
import netgest.bo.xwc.components.annotations.Visible;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.components.util.JavaScriptUtils;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUISessionContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.xeo.localization.BeansMessages;

import org.json.JSONObject;

public class XEOMainBean extends XEOBaseBean {

	protected XEOMainBean(EboContext ctx){
		super(ctx);
	}
	
	public XEOMainBean() {
		if( getEboContext() != null ) {
			XUIRequestContext oRequestContext = getRequestContext();
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
			if (cookies != null){
				for( Cookie cookie : cookies ) {
					if( "xeodmstate".equals( cookie.getName() ) ) {
						xeodmactive = Boolean.parseBoolean( cookie.getValue() );
						break;
					}
				}
			}
			oRequestContext.getScriptContext().add( 
					XUIScriptContext.POSITION_HEADER, 
					"xeodmstate",
					"window.xeodmstate=" + xeodmactive 
			);
			
		}
	}
	
	@Visible
	public void editObject() throws Exception {
        XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;
        XEOEditBean         oBaseBean;
        
        oRequestContext = getRequestContext();
        oSessionContext = oRequestContext.getSessionContext();
        
        JSONObject o = new JSONObject( 
                (String)((XUICommand)oRequestContext.getEvent().getSource()).getValue() 
        );
        ViewerConfig oViewerConfig = new ViewerConfig( o );
        
        String sViewerName = oViewerConfig.getViewerName();  

        oViewRoot = oSessionContext.createView( sViewerName );
        oBaseBean = (XEOEditBean)oViewRoot.getBean( "viewBean" );
        
        if( oBaseBean == null ) {
        	throw new  RuntimeException( BeansMessages.VIEWER_WITHOUT_VIEWBEAN.toString() );
        }
        oBaseBean.setCurrentObjectKey( Long.toString( oViewerConfig.getBoui() ) );
        // Diz a que a view corrente � a criada.
        oRequestContext.setViewRoot( oViewRoot );
        
        // TODO: This action must be automatic on the platform
        // initialize components
        oViewRoot.processInitComponents();
    }

	@Visible
    public void createObject() throws Exception {

        XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;
        XEOEditBean         oBaseBean;
        
        oRequestContext = getRequestContext();
        oSessionContext = oRequestContext.getSessionContext();
        
        JSONObject o = new JSONObject( 
                (String)((XUICommand)oRequestContext.getEvent().getSource()).getValue() 
        );
        
        ViewerConfig oViewerConfig = new ViewerConfig( o );
        
        String sViewerName = oViewerConfig.getViewerName(); 
        String sObjectName = oViewerConfig.getObjectName();

        oViewRoot = oSessionContext.createView( sViewerName );
        oBaseBean = (XEOEditBean)oViewRoot.getBean( "viewBean" );
        oBaseBean.createNew( sObjectName );
        
        // Diz a que a view corrente � a criada.
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
        XEOEditBean         oBaseBean;
        
        oRequestContext = getRequestContext();
        oSessionContext = oRequestContext.getSessionContext();
        
        // Obtem a bean do objecto a ser editado
        // e associa o objecto do parametro

        oViewRoot = oSessionContext.createView( sView );
        oBaseBean = (XEOEditBean)oViewRoot.getBean( "viewBean" );
        oBaseBean.setCurrentObjectKey( sKey );
        
        // Diz a que a view corrente � a criada.
        oRequestContext.setViewRoot( oViewRoot );
        
        // TODO: This action must be automatic on the platform
        // initialize components
        oViewRoot.processInitComponents();

        
    }

    public void showUserProperties(){
		 
		Long boui =getEboContext().getBoSession().getPerformerBoui();
		showProperties(boui);
	}
	
    
    
    @Visible
    public void showProperties(Long obj) {
    	
        XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;

        oRequestContext = getRequestContext();
        oSessionContext = oRequestContext.getSessionContext();
     
        oViewRoot = oSessionContext.createChildView("netgest/bo/xwc/components/viewers/UserProperties.xvw");
        ((XEOEditBean)oViewRoot.getBean("viewBean")).setCurrentObjectKey( obj );
        
        oRequestContext.setViewRoot( oViewRoot );
        oRequestContext.renderResponse();
    	
    }
    
    
    
    @Visible
    public void listObject( ) throws Exception {
        
        XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;
        XEOBaseList         oBaseBean;
        
        oRequestContext = getRequestContext();
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
        // Diz a que a view corrente � a criada.
        oRequestContext.setViewRoot( oViewRoot );
        
        // TODO: This action must be automatic on the platform
        // initialize components
        oViewRoot.processInitComponents();
        
        
    }
    
    @Visible
    public void openViewer()  throws Exception {
        XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;

        oRequestContext = getRequestContext();
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
    
    @Visible
    public void openLink() throws IOException {
        XUIRequestContext   oRequestContext;
        oRequestContext = getRequestContext();
    	String url = (String)((XUICommand)oRequestContext.getEvent().getSource()).getValue();
    	((HttpServletResponse)oRequestContext.getResponse()).sendRedirect( url );
    	oRequestContext.responseComplete();
    }
    
    @Visible
    public void logout() {
        try {
			XUIRequestContext oRequestContext;
			oRequestContext = getRequestContext();
			if( !oRequestContext.isAjaxRequest() ) {
				((HttpServletResponse) oRequestContext.getResponse())
						.sendRedirect("/LogoutXVW.jsp");
				oRequestContext.responseComplete();
			}
			else {
				oRequestContext.getScriptContext().add( XUIScriptContext.POSITION_HEADER , "logout", 
						"window.top.location.href='LogoutXVW.jsp'"
				);
				
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
    
    @Visible
    public void openXEO21Viewer() {
    	///__explorer.jsp?objectName=Ebo_Perf&form=explorer&label=Utilizadores&imagem=resources%2FEbo_Perf%2Fico16.gif&myIDX=0
        XUIRequestContext   oRequestContext;
        //XUISessionContext   oSessionContext;
       // XUIViewRoot         oViewRoot;

        oRequestContext = getRequestContext();
        //oSessionContext = oRequestContext.getSessionContext();
        try  {
	        JSONObject o = new JSONObject( 
	                (String)((XUICommand)oRequestContext.getEvent().getSource()).getValue() 
	        );
	        
	        String mode = o.optString("mode");
	        if( "explorer".equalsIgnoreCase( mode ) ) {
	        	String url = 
	        		"__explorer.jsp?objectName="+o.optString("object")+"&form="+o.optString("form")+"&label="+ 
	        			((Menu)oRequestContext.getEvent().getSource()).getText() + "&imagem=" + ((Menu)oRequestContext.getEvent().getSource()).getIcon();
	        	
	        	((HttpServletResponse)oRequestContext.getResponse()).sendRedirect( url );
	        	oRequestContext.responseComplete();
	        }
        }
        catch( Exception e ) {
        	throw new RuntimeException(e);
        }
    	
    }
}
