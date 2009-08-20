package netgest.bo.xwc.framework.components;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.classic.Form;
import netgest.bo.xwc.components.classic.Layouts;
import netgest.bo.xwc.components.classic.Window;
import netgest.bo.xwc.components.classic.theme.ExtJsTheme;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUISessionContext;
import netgest.bo.xwc.framework.XUITheme;
import netgest.bo.xwc.framework.jsf.XUIStateManagerImpl;

import com.sun.faces.util.LRUMap;
import com.sun.faces.util.RequestStateManager;
import com.sun.faces.util.TypedCollections;
import com.sun.faces.util.Util;


public class XUIViewRoot extends UIViewRoot 
{
    private static AtomicInteger   oInstanceIdCntr    = new AtomicInteger(0);
    
    private String   sInstanceId    = String.valueOf( oInstanceIdCntr.addAndGet( 1 ) );
    private String 	 sBeanIds		= "";
    private Object   oViewerBean;
    private String   sStateId       = null;
    private XUITheme oTheme;

    private String    sParentViewState   = null;
    private String    sTransactionId     = null;
    private boolean   bOwnsTransaction   = false;
    
    private XUIViewRoot  oParentView    = null;
    private boolean      isPostBack     = false;
    
    private boolean 	 wasInitComponentsProcessed = false;
    
    public void setOwnsTransaction( boolean owns ) {
    	this.bOwnsTransaction = owns;
    }
    
    public XUIViewRoot() {
        super();
        if( "XEOHTML".equals( getRenderKitId() ) )
        	oTheme = new ExtJsTheme();
        else if ( "XEOV2".equals( getRenderKitId() ) ) {
        	try {
				Class<XUITheme> theme = (Class<XUITheme>)Class.forName( "xeo.viewers.theme.XEOV2Theme" );
				oTheme = theme.newInstance();
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
        }
    }

    public Object getBean( String sBeanName ) {
        return XUIRequestContext.getCurrentContext().getSessionContext().getAttribute( getBeanPrefix() + sBeanName );
    }
    
    public String getBeanUniqueId( String sBeanName ) {
        return getBeanPrefix() + sBeanName;
    }

    public void addBean( String sBeanName, Object oBean ) {
    	this.sBeanIds += "|" + sBeanName;
        XUIRequestContext.getCurrentContext().getSessionContext().setAttribute( getBeanPrefix() + sBeanName, oBean );
    }
    
    public void dispose() {
    	
    	XUISessionContext sc = XUIRequestContext.getCurrentContext().getSessionContext();
    	String[] sBeans = this.sBeanIds.split("\\|");
    	for( String bean : sBeans ) {
    		sc.removeAttribute( getBeanPrefix() + bean );
    	}
    	
    	if( this.bOwnsTransaction ) {
            XUIRequestContext.
            getCurrentContext().
                getSessionContext().
                getTransactionManager().releaseTransaction( this.getTransactionId() );        
    	}
    	setTransient( true );
    }

    @Override
    public String getRenderKitId() {
        return "XEOHTML"; 
    } 

    public XUITheme getTheme() {
        return oTheme;
    }
    
    private final String getBeanPrefix() {
        return getViewId() + ":" + sInstanceId + ":";
    }
    

    @Override
    public Object saveState(FacesContext context) {
        Object  oSuperState;
        Object[] oMyState;

        oSuperState     = super.saveState(context);
        oMyState        = new Object[7];

        oMyState[0] = sInstanceId;
        oMyState[1] = sParentViewState;
        oMyState[2] = sTransactionId;
        oMyState[3] = bOwnsTransaction;
        oMyState[4] = sBeanIds;
        oMyState[5] = sStateId;
        
        oMyState[6] = oSuperState;

        return oMyState;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        Object[] oMyState;

        isPostBack = true; 

        oMyState = (Object[])state;
        sInstanceId = (String)oMyState[0];
        sParentViewState = (String)oMyState[1];
        sTransactionId = (String)oMyState[2];
        bOwnsTransaction = (Boolean)oMyState[3]; 
        sBeanIds = (String)oMyState[4]; 
        sStateId = (String)oMyState[5];
        super.restoreState(context, oMyState[6]);
    }

    public boolean wasStateChanged() {
        
        // If not a post back to this component, assume state changed
        // to force render of the component
        if( !isPostBack() ) {
            return true;
        }
        return false;
    }

    public boolean wasInitComponentsProcessed() {
        if( isPostBack() ) {
            return true;
        }
        return this.wasInitComponentsProcessed;
    }
    
    public void processStateChanged( List<XUIComponentBase> oRenderList ) {
        boolean bChanged;
        UIComponent oKid;

        List<UIComponent> oKids = this.getChildren();
        for (int i = 0; i < oKids.size(); i++) {
            bChanged = false;
            oKid = oKids.get( i );
            if( oKid instanceof XUIComponentBase ) {
                if( ((XUIComponentBase)oKid).wasStateChanged() ) {
                    oRenderList.add( (XUIComponentBase)oKid );
                    bChanged = true;                    
                }
                if( !bChanged ) {
                    ((XUIComponentBase)oKid).processStateChanged( oRenderList );    
                }

            }
        }
    }

    public void syncClientView() {
    	List<UIComponent> list;
    	Form oForm;
    	list = getChildren();
    	for (UIComponent component : list) {
    		oForm = (Form)((XUIComponentBase)component).findComponent( Form.class );
    		if( oForm != null ) {
    			XUIRequestContext.getCurrentContext().getScriptContext().add(
    					XUIScriptContext.POSITION_HEADER, 
    					oForm.getClientId() + "_syncView", 
    	                "XVW.syncView('" + oForm.getClientId() + "');"
    				);
    		}
		}
    }

    public XUIComponentBase findComponent( Class cType )
    {
    	List<UIComponent> list;
    	XUIComponentBase oComp;
    	
    	oComp = null;
    	
    	list = getChildren();
    	for (UIComponent component : list) {
    		oComp = ((XUIComponentBase)component).findComponent( cType );
    		if( oComp != null ) {
    			return oComp;
    		}
		}
		return oComp;
    }
    
    
    public void setParentViewState( String sParentViewId ) {
        this.oParentView = null;
        this.sParentViewState = sParentViewId;
    }

    public void getParentViewState( String sParentViewId ) {
        this.sParentViewState = sParentViewId;
    }
    
    public void setParentView( XUIViewRoot oViewRoot ) {
        setParentViewState( oViewRoot.getViewState() );
    }
    
    public XUIViewRoot getParentView() {
        if( sParentViewState != null && oParentView == null )
        {
            oParentView = XUIRequestContext.getCurrentContext().getSessionContext().getView( sParentViewState );
            oParentView.sStateId = sParentViewState;
        }
        return oParentView;
    }

    public Object getViewerBean() {
        return oViewerBean;
    }

    @Override
    public String getFamily() {
        return XUIViewRoot.class.getName();
    }

    @Override
    public String getRendererType() {
        return XUIViewRoot.class.getName();
    }

    public void processInitComponents() {
        // Process all facets and children of this component
        Iterator kids = getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent kid = (UIComponent) kids.next();
            if( kid instanceof UIComponent ) {
                ((XUIComponentBase)kid).processInitComponents();
            }
        }
        wasInitComponentsProcessed = true;        

    }
    
    public void processValidateModel() {
        // Process all facets and children of this component
        Iterator kids = getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent kid = (UIComponent) kids.next();
            if( kid instanceof XUIComponentBase ) {
                ((XUIComponentBase)kid).processValidateModel();
            }
        }
    }
    
    public void processPreRender() {
        // Process all facets and children of this component
        Iterator kids = getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent kid = (UIComponent) kids.next();
            if( kid instanceof UIComponent ) {
                ((XUIComponentBase)kid).processPreRender();
            }
        }
    }

    public String getInstanceId() {
        return this.sInstanceId;
    }

    public String getClientId() {
        return getViewId() + ":" + sInstanceId;
    }

    public boolean isPostBack() {
        return isPostBack;
    }

    public String getViewState() {
        
        if( sStateId == null )
        {
         
            FacesContext context = FacesContext.getCurrentInstance();
            XUIStateManagerImpl stateManager = (XUIStateManagerImpl)Util.getStateManager(context);
            
            ExternalContext externalContext = context.getExternalContext();
            Object sessionObj = externalContext.getSession(true);
            Map<String, Object> sessionMap = externalContext.getSessionMap();
    
            synchronized (sessionObj) {
                Map<String, Map> logicalMap = TypedCollections.dynamicallyCastMap(
                      (Map) sessionMap.get(XUIStateManagerImpl.LOGICAL_VIEW_MAP), String.class, Map.class);
    
                int logicalMapSize = stateManager.getNumberOfViewsParameter();
                    
                if (logicalMap == null) {
                    logicalMap = new LRUMap<String, Map>(logicalMapSize);
                    sessionMap.put(XUIStateManagerImpl.LOGICAL_VIEW_MAP, logicalMap);
                }
        
                String idInLogicalMap = (String)
                      RequestStateManager.get(context, RequestStateManager.LOGICAL_VIEW_MAP);
        
                if (idInLogicalMap == null) {
                    idInLogicalMap = stateManager.createUniqueRequestId(context);
                }
                assert(null != idInLogicalMap);
                
                String idInActualMap = stateManager.createUniqueRequestId(context);
                int actualMapSize = stateManager.getNumberOfViewsInLogicalViewParameter();
                
                Map<String, Object[]> actualMap = (Map<String, Object[]>)TypedCollections.dynamicallyCastMap(
                      logicalMap.get(idInLogicalMap), String.class, Object[].class);
                if (actualMap == null) {
                    actualMap = new LRUMap<String, Object[]>(actualMapSize);
                    logicalMap.put(idInLogicalMap, actualMap);
                }
        
                this.sStateId = idInLogicalMap + NamingContainer.SEPARATOR_CHAR +
                            idInActualMap;
            }
        }
        return this.sStateId;
    }

    public void setTransactionId(String sTransactionId) {
        this.sTransactionId = sTransactionId;
    }

    public String getTransactionId() {
        return sTransactionId;
    }

    @Override
    public String createUniqueId() {
        return "v" + sInstanceId + "_" + super.createUniqueId();
    }

    public static final boolean renderHead()
    {
    	XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
    	return !( oRequestContext.isAjaxRequest() || oRequestContext.isIncludeRequest() || oRequestContext.isPortletRequest() );
    }

    public static final boolean renderScripts()
    {
    	XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
    	return !( oRequestContext.isAjaxRequest() );
    }
    
    public static class XEOHTMLRenderer extends XUIRenderer {

        @Deprecated
        public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
            XUIResponseWriter w = getResponseWriter();

            XUIViewRoot viewRoot = (XUIViewRoot)component;
            
            if( renderHead() ) {

                // Add Scripts and Style
                XUIResponseWriter headerW = getResponseWriter().getHeaderWriter();
 
                // Write Header
                
            	headerW.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n");
            	
//                else
//                	headerW.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
                
                headerW.writeText('\n');
                headerW.startElement("html", component);
                //headerW.writeAttribute("SCROLL", "no", null );
                headerW.writeAttribute( "style", getTheme().getHtmlStyle(), "style" );

                headerW.writeText('\n');
                headerW.startElement("head", component );
                headerW.startElement("meta", component);
                headerW.writeAttribute( "http-equiv" , "X-UA-Compatible", null );
                headerW.writeAttribute( "content" , "IE=EmulateIE7", null );
                
                headerW.startElement("meta", component);
                headerW.writeAttribute( "http-equiv" , "X-UA-Compatible", null );
                headerW.writeAttribute( "content" , "IE=7", null );
                
                // Write Body
                w.startElement("body", component );
                if( getTheme().getBodyStyle() != null ) {
                    w.writeAttribute( "style", getTheme().getBodyStyle() + ";height:100%;width:100%", "style" );
                }
                headerW.writeText('\n');
            }
            
            if( getRequestContext().isPortletRequest() ) {
            	
            	if( !isAjax() ) {
            		w.getScriptContext().add(XUIScriptContext.POSITION_HEADER, "portalVar", "xvw_isPortal=true");
            	}
            	
            	String sWidth = (String)((HttpServletRequest)getRequestContext().getRequest()).getAttribute("xvw.width");
            	if( sWidth != null ) {
            		w.startElement( "div", null );
                    w.writeAttribute( "id", ((XUIViewRoot)component).getClientId(), "id" );
            		w.writeAttribute("style", "width:"+sWidth, null );
            	}
            	else if ( isAjax() ) {
                	w.startElement( "div", component );
                    w.writeAttribute( "id", ((XUIViewRoot)component).getClientId(), "id" );
                    
                    // Não sei se é necessário, foi criado a necessidade através 
                    if( viewRoot.findComponent( Window.class ) != null ) {
                    	w.writeAttribute( HTMLAttr.CLASS, "x-panel", "" );
                    }
                	w.writeAttribute("style", "width:100%;height:100%", null);
            	}
            }
            else {
            	
            	if( !isAjax() ) {
            		w.getScriptContext().add(XUIScriptContext.POSITION_HEADER, "portalVar", "xvw_isPortal=false");
            	}
            	
            	w.startElement( "div", component );
                w.writeAttribute( "id", ((XUIViewRoot)component).getClientId(), "id" );
                
                // Não sei se é necessário, foi criado a necessidade através 
                if( viewRoot.findComponent( Window.class ) != null ) {
                	w.writeAttribute( HTMLAttr.CLASS, "x-panel", "" );
                }
            	w.writeAttribute("style", "width:100%;height:100%", null);
            }
            
            if( renderScripts() ) {
                getTheme().addScripts( w.getScriptContext() );
                getTheme().addStyle( w.getStyleContext() );
            }

        }

        @Deprecated
        public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
            XUIRequestContext oRequestContext;

            
            oRequestContext = XUIRequestContext.getCurrentContext();
            XUIResponseWriter w = getResponseWriter();

            if( getRequestContext().isPortletRequest() ) {
            	String sWidth = (String)((HttpServletRequest)getRequestContext().getRequest()).getAttribute("xvw.width");
            	if( sWidth != null ) {
            		w.endElement( "div" );
            	}
            } else {
            	w.endElement( "div" );
            }
            
        	Layouts.doLayout(w);
            XUIResponseWriter footerW = getResponseWriter().getFooterWriter();
            XUIResponseWriter headerW = getResponseWriter().getHeaderWriter();

            if( renderScripts() ) {
    
                w.getScriptContext().render( headerW, w, footerW );
                w.getStyleContext().render( headerW, w, footerW );
                oRequestContext.getScriptContext().render( headerW, w, footerW );
            }

            if( renderHead() ) {
                // Write footer Elements
                if( getTheme().getHtmlStyle() != null ) {
                    w.writeAttribute( "style", getTheme().getHtmlStyle(), "style" );
                }
            	
            	// Write Head Elements
                headerW.writeText('\n');
                headerW.endElement("head");
                headerW.writeText('\n');
                    
                // End tag body
                w.writeText('\n');
                w.endElement("body");
                
                // End Tag HTML
                footerW.writeText('\n');
                footerW.endElement("html");
            }
        }

        @Override
        public boolean getRendersChildren() {
            return true;
        }
        
    }
}
