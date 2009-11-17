package netgest.bo.xwc.components.classic;

import java.io.IOException;
import java.util.Iterator;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtConfigArray;
import netgest.bo.xwc.components.classic.extjs.ExtJsRenderer;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIMethodBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class Window extends XUIComponentBase {
    
    
    public XUIStateProperty<Integer> width = new XUIStateProperty<Integer>( "width", this, 500 );
    public XUIStateProperty<Integer> height = new XUIStateProperty<Integer>( "height", this, 300 );
    
    public String animateTarget = null;
    
    public XUIStateProperty<String> title = new XUIStateProperty<String>( "title", this, "" );
    public XUIMethodBindProperty onclose = new XUIMethodBindProperty( "onclose", this );
    public XUIMethodBindProperty onbeforeclose = new XUIMethodBindProperty( "onbeforeclose", this, "#{viewBean.canCloseTab}" );
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

    public int getWidth() {
        return width.getValue();
    }

    public void setHeight(int height) {
        this.height.setValue( height );
    }

    public int getHeight() {
        return height.getValue();
    }
    
    public void close() {
    	destroy();
    }
    
    public void destroy() {
    	
        XUIRequestContext oRequestContext; 
        oRequestContext = XUIRequestContext.getCurrentContext();
        
        String namingContainerId = getNamingContainerId();

        oRequestContext.getScriptContext().add( XUIScriptContext.POSITION_FOOTER, this.getClientId() + "_closeWnd", 
		      "window.setTimeout( function() { " +
		      "XVW.closeWindow('" + namingContainerId +  "','" + getClientId() +"');" +
		      "},10);\n"
        );
        
        
//        oRequestContext.getScriptContext().add( XUIScriptContext.POSITION_FOOTER, this.getClientId() + "_closeLookup", 
//                "Ext.onReady( function() { " +
//                "if( "+this.getId()+" )" + this.getId() +  ".destroy();" +
//                "else if(window.parent."+this.getId()+") window.parent." + this.getId() +  ".destroy();" +
//                "});\n"
//        );
//        if( getOnClose() != null ) {
//        	getOnClose().invoke( getELContext(), null );
//        }
    }

    public static class XEOHTMLRenderer extends XUIRenderer {
        
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
            
            assert oExtConfig != null : "Config cannot be null";
            assert oComp != null : "Component cannot be null";
            
            Window oWnd = (Window)oComp;
            
            oExtConfig.addJSString("layout","fit");
            oExtConfig.add("width", oWnd.getWidth() );
            oExtConfig.add("height", oWnd.getHeight() );
            oExtConfig.add("plain",true);
            oExtConfig.addJSString( "id", oWnd.getClientId() );
            oExtConfig.addJSString("contentEl", oRequestContext.getViewRoot().getClientId() );
            oExtConfig.addJSString("title", oWnd.getTitle() );
            oExtConfig.add("allowDomMove",false);
            oExtConfig.add("modal","(Ext.isIE?false:true)");
            
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
	            .append( "');" )
	            .append( "if( o != null ){o.parentNode.removeChild( o )};" );
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
    }
}
