package netgest.bo.xwc.framework;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import javax.el.ELContext;
import javax.faces.application.StateManager;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import netgest.bo.transaction.XTransactionManager;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.framework.http.XUIAjaxRequestWrapper;
import netgest.bo.xwc.framework.jsf.XUIPhaseEvent;
import netgest.bo.xwc.framework.jsf.XUIViewHandlerImpl;

import com.sun.faces.io.FastStringWriter;
import com.sun.faces.util.Util;


public class XUIRequestContext {

	/**
	 * Map to hold all changed views during the request and save them at the end
	 */
	private Map<String,XUIViewRoot> changedViews = new HashMap<String, XUIViewRoot>();
    private XUIApplicationContext   oApplication;
    private Boolean                 bIsAjax;
    private boolean                 bIsClosed = false;
    private boolean                 bIsPostBack;

    private XUIScriptContext        oScriptContext;
    private XUIStyleContext        	oStyleContext;
    
    private UIViewRoot				oRenderedViewer;

    //TODO: It's not the right place, must be putted on SessionContext
    private XTransactionManager     oTransactionManager;

    private XUIActionEvent          oEvent;
    private XUIPhaseEvent			oPhaseEvent;

    private LinkedHashMap<String, XUIMessage> oMessages;

    private static final Iterator<XUIMessage> EMPTY_INTERATOR = (new Vector<XUIMessage>()).iterator();

    protected static ThreadLocal<XUIRequestContext> oCurrentContext = new ThreadLocal<XUIRequestContext>() {
        protected XUIRequestContext initialValue() {
            return null;
        }
    };
    
    /**
     * For testing purposes only 
     */
    protected XUIRequestContext(){
    	bIsClosed = false;
    }

    protected XUIRequestContext( XUIApplicationContext oApplication ) {
        if( bIsClosed ) throwCloseException();
        this.oApplication           = oApplication;
        this.oTransactionManager    = getSessionContext().getTransactionManager();
    }

    public XUIApplicationContext getApplicationContext() {
        if( bIsClosed ) throwCloseException();
        return oApplication;
    }

    public XUIScriptContext getScriptContext() {
        if( oScriptContext == null ) {
            oScriptContext = new XUIScriptContext();
        }
        return oScriptContext;
    }
    
    public void setScriptContext(XUIScriptContext ctx){
    	this.oScriptContext = ctx;
    }

    public XUIStyleContext getStyleContext() {
        if( oStyleContext == null ) {
            oStyleContext = new XUIStyleContext();
        }
        return oStyleContext;
    }

    public XUISessionContext getSessionContext() {
        Map<String,Object> oExternalSessionMap = getFacesContext().getExternalContext().getSessionMap();
        return (XUISessionContext)oExternalSessionMap.get(XUISessionContext.SESSION_ATTRIBUTE_ID );
    }

    public XUIResponseWriter getResponseWriter() {
        return (XUIResponseWriter)getFacesContext().getResponseWriter();
    }

    public FacesContext getFacesContext() {
        if( bIsClosed ) throwCloseException();
        return FacesContext.getCurrentInstance();
    }

    public static XUIRequestContext   getCurrentContext() {
        return oCurrentContext.get();
    }

    public ELContext getELContext() {
        return getFacesContext().getELContext();
    }

    public Map<String,String> getRequestParameterMap() {
        if( bIsClosed ) throwCloseException();
        return getFacesContext().getExternalContext().getRequestParameterMap();
    }

    public Iterator<String> getRequestParameterNames() {
        if( bIsClosed ) throwCloseException();
        return getFacesContext().getExternalContext().getRequestParameterNames();
    }

    public Map<String,String[]> getRequestParameterValuesMap() {
        if( bIsClosed ) throwCloseException();
        return getFacesContext().getExternalContext().getRequestParameterValuesMap();
    }

    public boolean isAjaxRequest() {
        if( bIsClosed ) throwCloseException();
        if( bIsAjax == null ) {
            bIsAjax = getFacesContext().getExternalContext().getRequest() instanceof XUIAjaxRequestWrapper;
        }
        return bIsAjax;
    }

    public boolean isIncludeRequest()
    {
    	ServletRequest oRequest = (ServletRequest)getFacesContext().getExternalContext().getRequest();
    	String sIsInclude = (String)oRequest.getAttribute("xvw.include");

    	if( Boolean.parseBoolean( sIsInclude ) ) {
    		return true;
    	}
    	return false;
    }

    public boolean isPortletRequest() {
    	ServletRequest oRequest = (ServletRequest)getFacesContext().getExternalContext().getRequest();
    	String sIsInclude = (String)oRequest.getAttribute("xvw.portlet");

    	if( Boolean.parseBoolean( sIsInclude ) ) {
    		return true;
    	}
    	return false;
    }
    
    public void close(FacesContext context){
    	if (changedViews != null){
	    	for (XUIViewRoot currentView : changedViews.values() ){
	    		try{
	    			serializeViewRoot( currentView, context );
	    		} catch (Throwable e){
	    			e.printStackTrace( );
	    		} 
	    	}
    	}
    }
    	

    public void release() {
    	
    	this.changedViews.clear();
    	
        try {
        	this.oTransactionManager.release();
        }
        finally { 
        	bIsClosed = true;
        	oCurrentContext.set( null );
        }
    }

    public void addMessage( String sClientId, XUIMessage oMessage ) {
        if( oMessages == null )   {
            oMessages = new LinkedHashMap<String,XUIMessage>();
        }
        oMessages.put( sClientId, oMessage );
    }

    public Iterator<XUIMessage> getMessages() {
        if( oMessages != null ) {
            return oMessages.values().iterator();
        }
        return EMPTY_INTERATOR;
    }

    public XUIMessage getMessages( String sClientId ) {
        if( oMessages != null ) {
            return oMessages.get( sClientId );
        }
        return null;
    }

    public XTransactionManager getTransactionManager() {
        return getSessionContext().getTransactionManager();
    }

    public void setAttribute( String sName, Object oValue ) {
        ((HttpServletRequest)getFacesContext().getExternalContext().getRequest()).setAttribute( sName, oValue );
    }

    public Object getAttribute( String sName ) {
        return ((HttpServletRequest)getFacesContext().getExternalContext().getRequest()).getAttribute( sName );
    }

    private void throwCloseException() {
        throw new RuntimeException( "XUIContext is closed" );
    }

    public XUIViewRoot getViewRoot() {
        return (XUIViewRoot)getFacesContext().getViewRoot();
    }

    protected void setPostBack(boolean bIsPostBack) {
        this.bIsPostBack = bIsPostBack;
    }

    public boolean isPostBack() {
        return bIsPostBack;
    }
    
    public ServletContext getServletContext() {
    	return (ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext();
    }

    public void setViewRoot( XUIViewRoot oViewRoot ) {
    	if (!oViewRoot.isTransient())
    		changedViews.put( oViewRoot.getViewId(), oViewRoot );
    	getFacesContext().setViewRoot( oViewRoot );
    }
    
    
	private void serializeViewRoot(UIViewRoot root, FacesContext context) {
		
    	UIViewRoot oldView = root;
        StateManager stateManager = Util.getStateManager( context );
        if( !oldView.isTransient() ) {
        	ResponseWriter oldWriter = context.getResponseWriter();
        	try {
        		XUIResponseWriter rw = new XUIResponseWriter(new FastStringWriter(), "text/html", "UTF-8");
        		context.setResponseWriter(rw);
				stateManager.writeState(context, stateManager.saveView(context));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
        	finally {
        		try {
        			if( oldWriter == null  ) {
						Field f = context.getClass().getDeclaredField("responseWriter");
						f.setAccessible(true);
						f.set( context, oldWriter );
        			}
        			else {
        				context.setResponseWriter( oldWriter );
        			}
				} catch (Exception e) { e.printStackTrace(); }
        	}
        }
	}

    public void setEvent(XUIActionEvent oEvent) {
        this.oEvent = oEvent;
    }

    public XUIActionEvent getEvent() {
        return oEvent;
    }

    public void setPhaseEvent(XUIPhaseEvent oPhaseEvent) {
        this.oPhaseEvent = oPhaseEvent;
    }

    public XUIPhaseEvent getPhaseEvent() {
        return oPhaseEvent;
    }

    public void renderResponse() {
    	getFacesContext().renderResponse();
    }
    
    public boolean getRenderResponse() {
    	return getFacesContext().getRenderResponse();
    }

    public void responseComplete() {
    	getFacesContext().responseComplete();
    }

    public Object getRequest() {
    	return getFacesContext().getExternalContext().getRequest();
    }

    public Object getResponse() {
    	return getFacesContext().getExternalContext().getResponse();
    }

    public String getActionUrl() {
        String actionURL =
            getFacesContext().getApplication().getViewHandler().
                  getActionURL(getFacesContext(),getViewRoot().getViewId() );
        return actionURL;
    }

    public String getActionUrl( String resourceId ) {
        String actionURL =
            getFacesContext().getApplication().getViewHandler().
                  getActionURL( getFacesContext(), resourceId );
        return actionURL;
    }
    
    public String getAjaxURL() {
    	FacesContext context;
    	context = getFacesContext();
        String actionURL =
            ((XUIViewHandlerImpl)context.getApplication().getViewHandler()).
                  getAjaxURL( context, context.getViewRoot().getViewId() );
        return actionURL;
    }

    public String getResourceUrl( String path ) {
    	FacesContext context;
    	context = getFacesContext();
        String actionURL =
            (context.getApplication().getViewHandler()).
                  getResourceURL(context, path );
    	return actionURL;
    }
    
    public void setRenderedViewer( UIViewRoot viewRoot ) {
    	this.oRenderedViewer = viewRoot;
	}
    
    public UIViewRoot getRenderedViewer() {
    	return this.oRenderedViewer;
    }
    
    public XUIApplicationConfig getXUIApplicationConfig(){
    	return oApplication.getXUIApplicationConfig();
    }
    
    /**
     * 
     * Adds a script to the footer
     * 
     * @param scriptId The script identifier
     * @param code The script source
     */
    public void addFooterScript(String scriptId, String code){
    	getScriptContext().add( XUIScriptContext.POSITION_FOOTER , scriptId , code );
    }

	
}

