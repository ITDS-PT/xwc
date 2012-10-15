package netgest.bo.xwc.components.classic;

import java.io.IOException;
import java.util.Iterator;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtConfigArray;
import netgest.bo.xwc.components.classic.extjs.ExtJsRenderer;
import netgest.bo.xwc.components.classic.renderers.ComponentWebResourcesCleanup;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIMethodBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIViewBindProperty;
import netgest.bo.xwc.framework.XUIViewStateProperty;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIForm;

/**
 * 
 * A {@link Window} is a component that's used to open a view in a separate window.
 * 
 * @author Joao Carreira
 *
 */
public class Window extends XUIComponentBase {
    
    /**
     * The width of the window
     */
    public XUIViewBindProperty<Integer> width = new XUIViewBindProperty<Integer>( "width", this, 500, Integer.class );
    /**
     * The height of the the window
     */
    public XUIViewBindProperty<Integer> height = new XUIViewBindProperty<Integer>( "height", this, 300, Integer.class );

    /**
     * Whether the window should be presented modally
     */
    public XUIViewBindProperty<Boolean> modal = new XUIViewBindProperty<Boolean>( "modal", this, true, Boolean.class );
    
    /**
     * Whether the window should be displayed in full screen
     */
    private XUIBaseProperty<Boolean> fullWindow = new XUIBaseProperty<Boolean>("fullWindow", this, false);

    /**
     * Distance from the top margin
     */
    public XUIViewBindProperty<Integer> top = new XUIViewBindProperty<Integer>( "top", this, 0, Integer.class );
    /**
     * Distance from the left margin
     */
    public XUIViewBindProperty<Integer> left = new XUIViewBindProperty<Integer>( "left", this, 0, Integer.class );
    
    public String animateTarget = null;
    
    /**
     * The display name of the Window
     */
    public XUIViewStateProperty<String> title = new XUIViewStateProperty<String>( "title", this, "" );
    /**
     * The action that is executed when the window closes
     */
    public XUIMethodBindProperty onclose = new XUIMethodBindProperty( "onclose", this );
    /**
     * The action that is executed before the window closes	
     */
    public XUIMethodBindProperty onbeforeclose = new XUIMethodBindProperty( "onbeforeclose", this );

    /**
     * Whether or not the ExtJS renderer should be used
     */
    public XUIBaseProperty<Boolean> useExtJsRenderer = 
    	new XUIBaseProperty<Boolean>( "useExtJsRenderer", this, Boolean.TRUE );

    public void setUseExtJsRenderer( boolean useExtJsRenderer ) {
    	this.useExtJsRenderer.setValue( useExtJsRenderer );
    }
    
    public boolean getUseExtJsRenderer() {
    	return this.useExtJsRenderer.getValue();
    }
    
    public void setAnimateTarget( String elementClientId ) {
    	this.animateTarget = elementClientId;
    }
    
    public String getAnimateTarget( ) {
    	return this.animateTarget; 
    }
    
    
    public void setOnClose( String sExpressionText ) {
    	this.onclose.setExpressionText( sExpressionText );
    }

    public void setOnBeforeClose( String sExpressionText ) {
    	this.onbeforeclose.setExpressionText( sExpressionText );
    }
    
    public MethodExpression getOnClose() {
    	return this.onclose.getValue();
    }

    public MethodExpression getOnBeforeClose() {
    	return this.onbeforeclose.getValue();
    }
    
    public void setTop( String exprTop ) {
    	this.top.setExpressionText( exprTop );
    }

    public void setTop( int top ) {
    	this.top.setValue( top );
    }
    
    public int getTop() {
    	return this.top.getEvaluatedValue();
    }
    
    public void setLeft( String exprTop ) {
    	this.left.setExpressionText( exprTop );
    }

    public void setLeft( int top ) {
    	this.left.setValue( top );
    }
    
    public int getLeft() {
    	return this.left.getEvaluatedValue();
    }
    
    public void setModal( String exprModal ) {
    	this.modal.setExpressionText( exprModal );
    }

    public void setModal( boolean modal ) {
    	this.modal.setValue( modal );
    }
    
    public boolean getModal() {
    	return this.modal.getEvaluatedValue();
    }
    
    public void setFullWindow(Boolean val){
    	this.fullWindow.setValue(val);
    }
    
    public Boolean getFullWindow(){
    	return fullWindow.getValue();
    }
    
    @Override
	public void preRender() {
    	if( this.getOnClose() != null ) {
    		XUICommand cmd = (XUICommand)findComponent( this.getId() + "_closecmd" );
    		if( cmd == null ) {
    			cmd = new XUICommand();
    			cmd.setActionExpression( getOnClose() );
    			cmd.setId( this.getId() + "_closecmd" );
    			this.getChildren().add( cmd );
    		}
    	}
    	if( this.getOnBeforeClose() != null ) {
    		XUICommand cmd = (XUICommand)findComponent( this.getId() + "_bclosecmd" );
    		if( cmd == null ) {
    			cmd = new XUICommand();
    			cmd.setActionExpression( getOnBeforeClose() );
    			cmd.setId( this.getId() + "_bclosecmd" );
    			this.getChildren().add( cmd );
    		}
    	}
	}

    @Override
    public void initComponent(){
    	this.setOnBeforeClose("#{" + getBeanId() + ".canCloseTab}");
    	initializeTemplate( "templates/components/window.ftl" );
    }
    
	@Override
    public boolean wasStateChanged() {
        return super.wasStateChanged();
    }

    public void setTitle( String title) {
        this.title.setValue( title );
    }

    public String getTitle() {
        return title.getValue();
    }

    public void setWidth( int width) {
        this.width.setValue( width );
    }
    
    public void setWidth(String widthExpr){
    	this.width.setExpressionText(widthExpr);
    }

    public int getWidth() {
        return width.getEvaluatedValue();
    }

    public void setHeight(int height) {
        this.height.setValue( height );
    }
    
    public void setHeight(String heightExpr){
    	this.height.setExpressionText(heightExpr);
    }

    public int getHeight() {
        return height.getEvaluatedValue();
    }
    
    public void close() {
    	destroy();
    }
    
    protected String getCleanUpScript() {
    	String rendererType = getRendererType();
    	FacesContext context = getRequestContext().getFacesContext();
        Renderer result = null;
        if (rendererType != null) {
            result = context.getRenderKit().getRenderer(getFamily(),
                                                        rendererType);
            if (result instanceof ComponentWebResourcesCleanup){
            	String script = ((ComponentWebResourcesCleanup) result).getCleanupScript( this );
            	return script;
            }
        }
        return "";
        
    }
    
    public void destroy() {
    	
        XUIRequestContext oRequestContext; 
        oRequestContext = XUIRequestContext.getCurrentContext();
        
        Renderer renderer = getRenderer();
        if (renderer instanceof ComponentWebResourcesCleanup){
        	String script = ((ComponentWebResourcesCleanup)renderer).getCleanupScript( this );
        	oRequestContext.getScriptContext().add( XUIScriptContext.POSITION_FOOTER, 
        			this.getClientId() + "_closeWnd",
        			script);
        }
    }
    
    public Renderer getRenderer() {

    	FacesContext context = getRequestContext().getFacesContext();
        String rendererType = getRendererType();
        Renderer result = null;
        if (rendererType != null) {
            result = context.getRenderKit().getRenderer(getFamily(),
                                                        rendererType);
        }            
        return result;
    }
    
    

    public static class XEOHTMLRenderer extends XUIRenderer implements ComponentWebResourcesCleanup {
        
        private ExtConfigArray oItems;

        @Override
        public void encodeBegin(XUIComponentBase component) throws IOException {
            oItems = null;
            XUIResponseWriter w;
            super.encodeBegin(component);
            
            w = getResponseWriter();
            
            w.startElement( HTMLTag.DIV , component );
            w.writeAttribute( HTMLAttr.ID , component.getClientId(), null );            
            //w.writeAttribute( HTMLAttr.CLASS , "x-panel", null );            
        }

        @Override
        public void encodeEnd(XUIComponentBase component) throws IOException {
            XUIResponseWriter w = getResponseWriter();
            ExtConfig         oWndCfg;

            oWndCfg = renderExtComponent( component );
            oWndCfg.setVarName( component.getId() );
            
            w.endElement( HTMLTag.DIV );

            super.encodeEnd(component);
            
            if( isAjax() ) {
	            StringBuilder oSb = oWndCfg.renderExtConfig().append( ";" );
	            oSb.append( '\n' );
	            oSb.append( "window."+component.getId()+"="+component.getId()+";" );
	            w.getScriptContext().add( XUIScriptContext.POSITION_FOOTER,
	                                      component.getId(),
	                                      oSb
	                                      );
	            
	            w.getScriptContext().add( XUIScriptContext.POSITION_FOOTER,
	                                      component.getId() + "_show",
	                                      component.getId() + ".show();"
	                                      );
            }
        }

        public ExtConfig renderExtComponent(XUIComponentBase oComp) {
            return renderExtComponent( oComp, new ExtConfig("Ext.Window") );
        }

        public ExtConfig renderExtComponent(XUIComponentBase oComp, 
                                            ExtConfig oExtConfig) {
            
            XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
            
            assert oExtConfig != null : MessageLocalizer.getMessage("CONFIG_CANNOT_BE_NULL");
            assert oComp != null : MessageLocalizer.getMessage("COMPONENT_CANNOT_BE_NULL");
            
            Window oWnd = (Window)oComp;
            
            oExtConfig.addJSString("layout","fit");
            
            if (oWnd.getFullWindow()){
            	oExtConfig.add("width", "Ext.isIE?document.body.clientWidth-20:window.innerWidth-20" );
            	oExtConfig.add("height", "Ext.isIE?document.body.clientHeight-20:window.innerHeight-20" );
            } else {
            	oExtConfig.add("width", oWnd.getWidth() );
            	oExtConfig.add("height", oWnd.getHeight() );
            }
            oExtConfig.add("plain",true);
            oExtConfig.addJSString( "id", oWnd.getClientId() );
            oExtConfig.addJSString("contentEl", oRequestContext.getViewRoot().getClientId() );
            oExtConfig.addJSString("title", oWnd.getTitle() );
            oExtConfig.add("allowDomMove",false);
            oExtConfig.add("constrain",true);
            
            
            if( oWnd.getModal() )
            	oExtConfig.add("modal","true");
            
            if( oWnd.getTop() != 0 ) {
                oExtConfig.add("pageY", oWnd.getTop() );
                if( oWnd.getLeft() == 0 ) {
                	oExtConfig.add("pageX", "(document.body.clientWidth - " + oWnd.getWidth() +  ") / 2"  );
                }
            }
            if( oWnd.getLeft() != 0 ) {
                oExtConfig.add("pageX", oWnd.getLeft() );
                if( oWnd.getTop() == 0 ) {
                	oExtConfig.add("pageY", "(document.body.clientHeight - " + oWnd.getHeight() +  ") / 2"  );
                }
            }
              
//            if( oWnd.getAnimateTarget() != null ) {
//                oExtConfig.addJSString( "animateTarget", oWnd.getAnimateTarget() );
//            }

            ExtConfig oExtListeners = oExtConfig.addChild( "listeners" );
            
            if( oWnd.getOnClose() != null ) {
            	XUICommand closeCmd = (XUICommand)oWnd.findComponent( oWnd.getId() + "_closecmd" );
            	if( closeCmd != null ) {
	                ExtConfig oClose = oExtListeners.addChild( "'close'");
	                StringBuilder onclose = new StringBuilder();
	                onclose.append( "function(a1){" ); 
	            	onclose.append(  
	            			XVWScripts.getAjaxCommandScript( closeCmd , XVWScripts.WAIT_STATUS_MESSAGE )
	            	).append(';');
	                onclose.append( "}" );
	                oClose.add( "fn", onclose );
            	}
            }
            if( oWnd.getOnBeforeClose() != null ) {
                ExtConfig oClose = oExtListeners.addChild( "'beforeclose'");
                StringBuilder onclose = new StringBuilder();
                onclose.append( "function(a1){" ); 
            	XUICommand closeCmd = (XUICommand)oWnd.findComponent( oWnd.getId() + "_bclosecmd" );
            	onclose.append(  
            			XVWScripts.getAjaxCommandScript( closeCmd , XVWScripts.WAIT_STATUS_MESSAGE )
            	).append(';');
                onclose.append( "return false; }" );
                oClose.add( "fn", onclose );
            }
            
        	XUICommand closeCmd = (XUICommand)oWnd.findComponent( oWnd.getId() + "_closecmd" );
            if( closeCmd != null ) {
	            ExtConfig oDestroy = oExtListeners.addChild( "'destroy'");
	            StringBuilder destroy = new StringBuilder( "function() {");
		        	destroy.append(  
		        			XVWScripts.getAjaxCommandScript( closeCmd , XVWScripts.WAIT_STATUS_MESSAGE )
		        	).append(';');
	            destroy.append( "var o=document.getElementById('" )
	            .append( oRequestContext.getViewRoot().getClientId() )
	            .append( "'); " )
	            .append( "if( o != null ){ o.parentNode.removeChild( o )};" );
	            destroy.append( "}" );
	            oDestroy.add( "fn", destroy );
        	} 
            
            ExtConfig oResize = oExtListeners.addChild( "'resize'");
            StringBuilder onResizeJS = new StringBuilder();
            onResizeJS.append( "function(a1){" );
            onResizeJS.append( "ExtXeo.layoutMan.doLayout('" + XUIRequestContext.getCurrentContext().getViewRoot().getClientId() + "');" );
            onResizeJS.append( "}" );
            oResize.add( "fn", onResizeJS );
            
            if( this.oItems != null ) {
                oExtConfig.add( "items", this.oItems );
            }
            return oExtConfig;
        }
        
        @Override
        public void encodeChildren(XUIComponentBase oComp) throws IOException {
            Iterator<UIComponent> oChildIterator;
            UIComponent           oChildComp;
            FacesContext          oFacesContext;
            Window				  oWnd;
            
            oWnd = (Window)oComp;
            
            oFacesContext = XUIRequestContext.getCurrentContext().getFacesContext();
            
            oChildIterator = oComp.getChildren().iterator();
            while( oChildIterator.hasNext() ) {
                oChildComp = oChildIterator.next();

                if (!oChildComp.isRendered()) {
                    return;
                }
                
                String rendererType;
                if( oWnd.getUseExtJsRenderer() ) {
	                rendererType = oChildComp.getRendererType();
	                if (rendererType != null) {
	                    Renderer renderer = getRenderer( oChildComp, oFacesContext );
	                    if (  renderer != null && renderer instanceof ExtJsRenderer ) {
	                        if( this.oItems == null ) {
	                            oItems = new ExtConfigArray();
	                        }
	                        oItems.addChild(
	                                ((ExtJsRenderer)renderer).getExtJsConfig( (XUIComponentBase)oChildComp ) 
	                        );
	                    }
	                    else {
	                        oChildComp.encodeAll( oFacesContext );
	                    }
	                }
                }
                else {
                    oChildComp.encodeAll( oFacesContext );
                }
            }
        }

        protected Renderer getRenderer(UIComponent oComp, FacesContext context) {

            String rendererType = oComp.getRendererType();
            Renderer result = null;
            if (rendererType != null) {
                result = context.getRenderKit().getRenderer(oComp.getFamily(),
                                                            rendererType);
            }            
            return result;
        }


        @Override
        public boolean getRendersChildren() {
            return true;
        }

		@Override
		public String getCleanupScript( XUIComponentBase oWnd ) {
			
			String namingContainerId = oWnd.findParentComponent(XUIForm.class).getClientId();
	        return  
			      "window.setTimeout( function() { " +
			      "XVW.closeWindow('" + namingContainerId +  "','" + oWnd.getClientId() +"');" +
			      "},10);\n";
			
		}
    }
}
