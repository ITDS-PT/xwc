package netgest.bo.xwc.components.classic;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtConfigArray;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIViewRoot;

public class TreePanel extends XUIComponentBase {

	private XUIBindProperty<Menu> root = new XUIBindProperty<Menu>( "root", this,  null, Menu.class );
	private XUIBindProperty<Boolean> reload = new XUIBindProperty<Boolean>( "reload", this,  "false", Boolean.class );
	
	private boolean localReload = false;
	
	
	public void setRoot( String sRootExpr ) {
		this.root.setExpressionText( sRootExpr );
	}
	
	public Menu getRoot( ) {
		return this.root.getEvaluatedValue();
	}
	
	public void setReload( String sReloadExpr ) {
		this.reload.setExpressionText( sReloadExpr );
	}
	
	public boolean getReload() {
		return this.reload.getEvaluatedValue();
	}
	
	@Override
	public boolean wasStateChanged() {
		return super.wasStateChanged() || localReload;
	}

	@Override
	public void preRender() {
		super.preRender();
		localReload = getReload();
		if ( localReload ) {
			XUIRequestContext oRequestContext;
			oRequestContext = XUIRequestContext.getCurrentContext();
	    	Menu root = getRoot();
	    	if( root != null ) {
	    		this.getChildren().clear();
	    		this.getChildren().add( root );
	    		createMenuIds(  oRequestContext.getViewRoot(), root );
	    	}
		}
	}
	
	private void createMenuIds( XUIViewRoot viewRoot, UIComponent component  ) {

		if( component.getId() == null ) {
			component.setId( viewRoot.createUniqueId() );
		}
		for( UIComponent childComp : component.getChildren() ) {
			createMenuIds( viewRoot, childComp);
		}
	}
	

	public static class XEOHTMLRenderer extends XUIRenderer implements XUIRendererServlet {

        @Override
		public void encodeEnd(XUIComponentBase component) throws IOException {
        	TreePanel oTreeComp = (TreePanel)component;

        	XUIResponseWriter w = getResponseWriter();
        	w.startElement(HTMLTag.DIV , oTreeComp );
        	w.writeAttribute( HTMLAttr.ID , oTreeComp.getClientId(), null );
        	w.endElement( HTMLTag.DIV );
        	
        	ExtConfig oTreeConfig = renderExtJs( oTreeComp );
        	oTreeConfig.addJSString( "renderTo", oTreeComp.getClientId() );
        	
        	w.getScriptContext().add( 
        			XUIScriptContext.POSITION_FOOTER, 
        			oTreeComp.getClientId(),  
        			oTreeConfig.renderExtConfig()
        	);
        	
		}
        
        public ExtConfig renderExtJs( TreePanel oTreeComp ) {
        	ExtConfig oTreeConfig = new ExtConfig("Ext.tree.TreePanel");
        	
			StringBuilder sActionUrl = new StringBuilder( getRequestContext().getAjaxURL() );
			if( sActionUrl.indexOf("?") == -1 ) {
				sActionUrl.append("?");
			}
			else {
				sActionUrl.append("&");
			}
			sActionUrl.append( "javax.faces.ViewState=").append( XUIRequestContext.getCurrentContext().getViewRoot().getViewState() );
			sActionUrl.append("&");
			sActionUrl.append( "xvw.servlet=" ).append( oTreeComp.getClientId() );
        	oTreeConfig.add( "border" , false );
        	oTreeConfig.add( "useArrows" , true );
        	oTreeConfig.add( "autoScroll" , true );
        	oTreeConfig.add( "animate" , true );
        	oTreeConfig.add( "enableDD" , false );
        	oTreeConfig.add( "containerScroll" , true );
        	oTreeConfig.add( "rootVisible" , false );
        	oTreeConfig.add( "frame" , false );
        	oTreeConfig.add( "collapsed" , false );
        	oTreeConfig.add( "enableDD" , false ); 
//        	oTreeConfig.addJSString( "layout" , "fit" );
        	oTreeConfig.addJSString( "dataUrl" , sActionUrl.toString() );
        	ExtConfig oRootConfig = oTreeConfig.addChild("root");
        	oRootConfig.addJSString("nodeType", "async");
        	return oTreeConfig;
        }
        
        public void service(ServletRequest request, ServletResponse response, XUIComponentBase comp) throws IOException {

        	response.setContentType( "text/plain" );
        	
        	PrintWriter w = response.getWriter();
        	
        	TreePanel oTreeComp = (TreePanel)comp;
        	ExtConfigArray json;

        	json = renderJSon( oTreeComp );
        	w.write( json.renderExtConfig().toString() );
        	
		}

		public ExtConfigArray renderJSon( XUIComponentBase component ) {
            
            //sOut.write( "items: [" ); sOut.write("\n");
            ExtConfigArray oItemsCfg = new ExtConfigArray();

            Iterator<UIComponent> childs =  component.getChildren().iterator();
            while( childs.hasNext() ) {
                
                ExtConfig oItemCfg;
                
                Menu oMenuChild = (Menu)childs.next();
                
                if( oMenuChild.canAcess() ) {
                    if ( oMenuChild.getEffectivePermission(SecurityPermissions.READ) ) {
		                oItemCfg = oItemsCfg.addChild();
                    	oItemCfg.addJSString( "text", oMenuChild.getText() );
                    	if( !oMenuChild.isVisible() )
                    		oItemCfg.add( "hidden", true );
                    	
                    	if( oMenuChild.isDisabled() || !oMenuChild.getEffectivePermission(SecurityPermissions.EXECUTE) )
                    		oItemCfg.add( "disabled", true );
                
		
		                oItemCfg.addJSString( "text", oMenuChild.getText() );
		
		                if( oMenuChild.getIcon() != null ) {
		                	oItemCfg.addJSString( "icon", oMenuChild.getIcon() );
		//                	oItemCfg.addJSString("cls", "x-btn-text-icon");
		                }
		                oItemCfg.add( "expanded", oMenuChild.getExpanded() );
		                    
		                if( oMenuChild.getValue() instanceof Boolean ) {
		                    oItemCfg.add( "checked", oMenuChild.getValue() );
		                }
		
		                if( oMenuChild.getActionExpression() != null ) {
		                	ExtConfig oItemListeners = oItemCfg.addChild("listeners");
		                    if( "window".equalsIgnoreCase( oMenuChild.getTarget() ) ) {
		                    	oItemListeners.add( "handler", "function() {" +
			                    		"var oForm=document.getElementById('" + oMenuChild.getNamingContainerId() +"');\n" +
			                    		"var oldTrg=oForm.target;\n" +
			                    		"oForm.target='opt_"+ oMenuChild.getClientId() +"';\n" +
			                    		XVWScripts.getCommandScript( oMenuChild, XVWScripts.WAIT_STATUS_MESSAGE ) +";\n" +
			                    		"oForm.target=oldTrg;\n" +
			                    		"}"
			                    );
		                    } 
		                    else if( "Tab".equalsIgnoreCase( oMenuChild.getTarget() ) )
		                    {
		                    	oItemListeners.add( "handler", "function(){"+XVWScripts.getOpenCommandTab( oMenuChild, "", oMenuChild.getText() )+"}" );
		                    }
		                    else {
		                    	oItemListeners.add( "handler", "function(){"+XVWScripts.getAjaxCommandScript( oMenuChild, XVWScripts.WAIT_STATUS_MESSAGE )+"}" );
		                    }
		                }
		                
		                if( oMenuChild.getChildCount() > 0 ) {
		                	ExtConfigArray childArray = oItemCfg.addChildArray( "children" ); 
		                    encodeSubMenuJS( childArray, oMenuChild );
		                    if( childArray.size() == 0 ) {
		                    	oItemCfg.add( "leaf", true );
		                    }
		                }
		                else {
		                	oItemCfg.add( "leaf", true );
		                }
                    }
                }
            }
            return oItemsCfg;
            
        }
        
        public void encodeSubMenuJS( ExtConfigArray oChildren, Menu oSubMenu ) {
            
            Iterator<UIComponent> oSubChildren = oSubMenu.getChildren().iterator();
            
            while( oSubChildren.hasNext() ) {
                
                ExtConfig oItemCfg;
                
                Menu oMenuChild = (Menu)oSubChildren.next();
                if( oMenuChild.canAcess() ) {
                    if ( oMenuChild.getEffectivePermission(SecurityPermissions.READ) ) {
		                oItemCfg = oChildren.addChild();
                    	oItemCfg.addJSString( "text", oMenuChild.getText() );
                    	if( !oMenuChild.isVisible() )
                    		oItemCfg.add( "hidden", true );
                    	
                    	if( oMenuChild.isDisabled() || !oMenuChild.getEffectivePermission(SecurityPermissions.EXECUTE) )
                    		oItemCfg.add( "disabled", true );
	                
		                oItemCfg.addJSString( "text", oMenuChild.getText() );
		                oItemCfg.add( "expanded", oMenuChild.getExpanded() );
		                /*
		                if( !oMenuChild.isVisible() )
		                    oItemCfg.add( "hidden", true );
		                     
		                if( oMenuChild.isDisabled() )
		                    oItemCfg.add( "disabled", true );
		                    
		                if( oMenuChild.getValue() instanceof Boolean ) {
		                    oItemCfg.add( "checked", oMenuChild.getValue() );
		                }
		                */
		
		                if( oMenuChild.getIcon() != null ) {
		                	oItemCfg.addJSString( "icon", oMenuChild.getIcon() );
		                	oItemCfg.addJSString("cls", "x-btn-text-icon");
		                }
		
		                if( oMenuChild.getActionExpression() != null ) {
		                	ExtConfig oItemListeners = oItemCfg.addChild("listeners");
		                	
		                    if( "window".equalsIgnoreCase( oMenuChild.getTarget() ) ) {
		                    	oItemListeners.add( "'click'", "function() {" +
			                    		"var oForm=document.getElementById('" + oMenuChild.getNamingContainerId() +"');\n" +
			                    		"var oldTrg=oForm.target;\n" +
			                    		"oForm.target='opt_"+ oMenuChild.getClientId() +"';\n" +
			                    		XVWScripts.getCommandScript( oMenuChild, XVWScripts.WAIT_STATUS_MESSAGE ) +";\n" +
			                    		"oForm.target=oldTrg;\n" +
			                    		"}"
			                    );
		                    } 
		                    else if( "Tab".equalsIgnoreCase( oMenuChild.getTarget() ) )
		                    {
		                    	oItemListeners.add( "'click'", "function(){"+XVWScripts.getOpenCommandTab( oMenuChild, "", oMenuChild.getText() )+"}" );
		                    }
		                    else {
		                    	String cmd = XVWScripts.getAjaxCommandScript( oMenuChild, XVWScripts.WAIT_DIALOG );
		                    	oItemListeners.add( "'click'", "function(){"+cmd+"}" );
		                    }
		                }
		                
		                if( oMenuChild.getChildCount() > 0 ) {
		                	ExtConfigArray childArray = oItemCfg.addChildArray( "children" ); 
		                    encodeSubMenuJS( childArray, oMenuChild );
		                    if( childArray.size() == 0 ) {
		                    	oItemCfg.add( "leaf", true );
		                    }
		                }
		                else {
		                	oItemCfg.add( "leaf", true );
		                }
                    }
                }
            }
        }

        @Override
		public boolean getRendersChildren() {
			
			return true;
			
		}
	}
}
