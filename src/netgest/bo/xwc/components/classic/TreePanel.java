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

/**
 * The Class TreePanel.
 */
public class TreePanel extends XUIComponentBase {

	private XUIBindProperty<Menu> root = 
		new XUIBindProperty<Menu>( "root", this,  Menu.class );
	
	private XUIBindProperty<Boolean> hideRoot = 
		new XUIBindProperty<Boolean>( "hideRoot", this,  Boolean.class );
	
	private XUIBindProperty<Boolean> reload = 
		new XUIBindProperty<Boolean>( "reload", this,  false, Boolean.class );
	
	private boolean localReload = false;
	
	/**
	 * To create a tree panel with dynamic content.
	 * This property accepts a EL Expression that return a Menu component who holds 
	 * the structure of the tree to be rendered
	 * 
	 * @param sRootExpr the new root element
	 */
	public void setRoot( String sRootExpr ) {
		this.root.setExpressionText( sRootExpr );
	}
	
	/**
	 * Gets the root Menu element.
	 * 
	 * @return the root
	 */
	public Menu getRoot( ) {
		return this.root.getEvaluatedValue();
	}
	
	/**
	 * Trigger the tree to be reloaded/refreshed at the end of the request
	 * EL Expression to a boolean property or a literal true or false
	 * 
	 * @param sReloadExpr the new reload EL Expression
	 */
	public void setReload( String sReloadExpr ) {
		this.reload.setExpressionText( sReloadExpr );
	}
	
	/**
	 * If the property root was specified when true hide the root Menu.
	 * EL Expression to a boolean propertu or a literal true or false
	 * 
	 * @param elExpr the new hide root EL Expression
	 */
	public void setHideRoot( String elExpr ) {
		this.hideRoot.setExpressionText( elExpr );
	}
	
	/**
	 * Same as setHideRoot(String) but accepts a boolean value
	 * 
	 * @param hideRoot true or false 
	 */
	public void setHideRoot( boolean hideRoot ) {
		this.hideRoot.setValue( hideRoot );
	}
	
	/**
	 * Gets the hide root mode.
	 * 
	 * @return the hide root
	 */
	public boolean getHideRoot() {
		return this.hideRoot.getEvaluatedValue();
	}
	
	/**
	 * Gets the if reload was triggered during the request
	 * 
	 * @return the reload
	 */
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
	    		if( getHideRoot() ) {
	    			this.getChildren().addAll( root.getChildren() );
	    		}
	    		else {
		    		this.getChildren().add( root );
	    		}
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
        	
        	oTreeConfig.add( "id" , oTreeComp.getClientId() );
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
                    	oItemCfg.addJSString( "id", oMenuChild.getClientId() );
                    	oItemCfg.addJSString( "text", oMenuChild.getText() );
                    	if( !oMenuChild.isVisible() )
                    		oItemCfg.add( "hidden", true );
                    	
                    	if( oMenuChild.isDisabled() || !oMenuChild.getEffectivePermission(SecurityPermissions.EXECUTE) )
                    		oItemCfg.add( "disabled", true );
                
		
		                oItemCfg.addJSString( "text", oMenuChild.getText() );
		                
		                String icon = oMenuChild.getIcon(); 
		                if( icon != null && icon.length() > 0 ) {
		                	oItemCfg.addJSString( "icon", composeUrlWithWebContext( icon ) );
		                }
		                oItemCfg.add( "expanded", oMenuChild.getExpanded() );
		                    
		                if( oMenuChild.getValue() instanceof Boolean ) {
		                    oItemCfg.add( "checked", oMenuChild.getValue() );
		                }
		
		                if( oMenuChild.getActionExpression() != null ) {
		                	ExtConfig oItemListeners = oItemCfg.addChild("listeners");
		                	oItemListeners.add( "'click'", "function(){" +
		                    		XVWScripts.getCommandScript( oMenuChild.getTarget(), oMenuChild, XVWScripts.WAIT_DIALOG )+"}" 
		                    	);
		                	
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
                if( oMenuChild.canAcess() && oMenuChild.isVisible() ) {
                    if ( oMenuChild.getEffectivePermission(SecurityPermissions.READ) ) {
		                oItemCfg = oChildren.addChild();
                    	oItemCfg.addJSString( "id", oMenuChild.getClientId() );
                    	oItemCfg.addJSString( "text", oMenuChild.getText() );
                    	if( !oMenuChild.isVisible() )
                    		oItemCfg.add( "hidden", true );
                    	
                    	if( oMenuChild.isDisabled() || !oMenuChild.getEffectivePermission(SecurityPermissions.EXECUTE) )
                    		oItemCfg.add( "disabled", true );
	                
		                oItemCfg.addJSString( "text", oMenuChild.getText() );
		                oItemCfg.add( "expanded", oMenuChild.getExpanded() );
		                
		                String icon = oMenuChild.getIcon(); 
		                if( icon != null && icon.length() > 0 ) {
		                	oItemCfg.addJSString( "icon", composeUrlWithWebContext( icon ) );
		                	oItemCfg.addJSString("cls", "x-btn-text-icon");
		                }
		
		                if( oMenuChild.getActionExpression() != null ) {
		                	ExtConfig oItemListeners = oItemCfg.addChild("listeners");
		                	oItemListeners.add( "'click'", "function(){" +
		                    		XVWScripts.getCommandScript( oMenuChild.getTarget(), oMenuChild, XVWScripts.WAIT_DIALOG )+"}" 
		                    	);
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
