package netgest.bo.xwc.components.classic;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.annotations.Values;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtConfigArray;
import netgest.bo.xwc.components.classic.mainRegions.ExtJSRegionRenderer;
import netgest.bo.xwc.components.classic.mainRegions.UserToolBar;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.classic.scripts.XVWServerActionWaitMode;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIViewBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIForm;
import netgest.bo.xwc.framework.components.XUIViewRoot;


/**
 * 
 * The {@link TreePanel} component enables the creation
 * of tree-like structures to organizes menus (such as seen
 * in a 'Main' viewer
 * 
 * @author jcarreira
 * 
 *
 */
public class TreePanel extends XUIComponentBase implements ExtJSRegionRenderer {

	/**
	 * To create a tree panel with dynamic content.
	 *	This property should return a {@link Menu} instance holding
	 * the structure of the represent
	 */
	private XUIBindProperty<Menu> root = 
		new XUIBindProperty<Menu>( "root", this,  Menu.class );
	
	/**
	 * If the "root" property was defined, this property allows hiding the root
	 * menu (adding only it's children) or the adding the complete {@link Menu}
	 */
	private XUIViewBindProperty<Boolean> hideRoot = 
		new XUIViewBindProperty<Boolean>( "hideRoot", this,  Boolean.class );
	
	private XUIBindProperty<Boolean> reload = 
		new XUIBindProperty<Boolean>( "reload", this,  false, Boolean.class );
	
	/**
	 * The default tab to open when displaying the tree
	 * Id of the Menu which will be invoked for the default tab
	 */
	private XUIViewBindProperty<String> defaultTab =
		new XUIViewBindProperty<String>( "defaultTab", this,  String.class );
	
	/**
	 * The placement of the toolbar
	 */
	@Values({"top","bottom"})
	private XUIBindProperty<String> toolBarPlacement = 
		new XUIBindProperty<String>("toolBarPlacement", this, String.class, "bottom" );
	
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
	 * EL Expression to a boolean property or a literal true or false
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
	public StateChanged wasStateChanged2() {
		return (super.wasStateChanged2() == StateChanged.FOR_RENDER	|| localReload) 
				? StateChanged.FOR_RENDER 
				: StateChanged.NONE;
	}
	
	/**
	 * 
	 * Sets the id of the menu for the default tab
	 * 
	 * @param defaultTabVal The id of the menu that opens the default tab
	 */
	public void setDefaultTab(String defaultTabVal){
		this.defaultTab.setValue(defaultTabVal);
	}
	
	/**
	 * 
	 * Retrieves the placement of the toolbar in the treepanel
	 * 
	 * @return A string with "top" / "bottom"
	 */
	public String getToolBarPlacement(){
		return toolBarPlacement.getEvaluatedValue();
	}
	
	/**
	 * 
	 * Sets the placement of the toolbar
	 * 
	 * @param placementExpr
	 */
	public void setToolBarPlacement(String placementExpr){
		toolBarPlacement.setExpressionText(placementExpr);
	}
	
	/**
	 * 
	 * Retrieves the id of the menu which will be used to open the default tab
	 * 
	 * @return A string with the identifier of the menu
	 */
	public String getDefaultTab()
	{
		return (String) this.defaultTab.getEvaluatedValue();
	}

	@Override
	public void preRender() {
		super.preRender();
		localReload = getReload();

		//If we have a default tab, open
		if (getDefaultTab() != null){
		
			XUIForm form = (XUIForm) this.findParentComponent(XUIForm.class);
			String formIdenfier = form.getClientId();
			
			Menu targetDefaultMenu = (Menu) this.findComponent(getDefaultTab());
			
			if (targetDefaultMenu == null)
				throw new RuntimeException(
						"The TreePanel component has property 'defaultTab' with value '" + getDefaultTab() +
						"' but there's no child menu with id = '" + getDefaultTab() + "'");
			
			if (targetDefaultMenu.getTarget().equalsIgnoreCase("tab")){
				
				String sFrameName =  "Frame_" + getDefaultTab();
				
				if (targetDefaultMenu != null)
				{
					String function = String.format("XVW.openCommandTab('%s','%s','%s','');", sFrameName, formIdenfier, getDefaultTab());
					String script = String.format("(function openDefaultTab(){ window.setTimeout( function ( ) { if (XVW.getXApp() != null){ %s } else { openDefaultTab() } }, 500 ); })();  ", function);
	
				getRequestContext().getScriptContext().add(
					XUIScriptContext.POSITION_FOOTER, "defaultTab", script);
				}
				
			}
		}
		
		
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
        	return oTreeComp.renderRegion();
        }
        
        public void service(ServletRequest request, ServletResponse response, XUIComponentBase comp) throws IOException {

        	response.setContentType( "text/plain;charset=utf-8" );
        	
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
                
                UIComponent comp = childs.next();
                if (comp instanceof Menu){
	                Menu oMenuChild = (Menu)comp;
	                
	                boolean visible = oMenuChild.isVisible();
	                if( oMenuChild.canAcess() && visible ) {
	                    if ( oMenuChild.getEffectivePermission(SecurityPermissions.READ) ) {
			                oItemCfg = oItemsCfg.addChild();
	                    	oItemCfg.addJSString( "id", oMenuChild.getClientId() );
	                    	oItemCfg.addJSString( "text", oMenuChild.getText() );
	                    	if( !visible )
	                    		oItemCfg.add( "visible", true );
	                    	
	                    	boolean disabled = false;
	                    	if( oMenuChild.isDisabled() || !oMenuChild.getEffectivePermission(SecurityPermissions.EXECUTE) ){
	                    		oItemCfg.add( "disabled", true );
	                    		disabled = true;
	                    	}
	                
			
			                oItemCfg.addJSString( "text", oMenuChild.getText() );
			                
			                String icon = oMenuChild.getIcon(); 
			                if( icon != null && icon.length() > 0 ) {
			                	oItemCfg.addJSString( "icon", composeUrlWithWebContext( icon ) );
			                }
			                oItemCfg.add( "expanded", oMenuChild.getExpanded() );
			                    
			                if( oMenuChild.getValue() instanceof Boolean ) {
			                    oItemCfg.add( "checked", oMenuChild.getValue() );
			                }
			
			                if( oMenuChild.getActionExpression() != null && !disabled) {
			                	ExtConfig oItemListeners = oItemCfg.addChild("listeners");
			                	
			                	int waitMode = XVWScripts.WAIT_DIALOG;
			                	waitMode = oMenuChild.getServerActionWaitMode() == XVWServerActionWaitMode.NONE?
			                			XVWScripts.WAIT_STATUS_MESSAGE:XVWScripts.WAIT_DIALOG;
			                	
			                	oItemListeners.add( "'click'", "function(){" +
			                    		XVWScripts.getCommandScript( oMenuChild.getTarget(), oMenuChild, waitMode )+"}" 
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
                    		oItemCfg.add( "disabled", true );
                    	
                    	boolean disabled = false;
                    	if( oMenuChild.isDisabled() || !oMenuChild.getEffectivePermission(SecurityPermissions.EXECUTE) ){
                    		oItemCfg.add( "disabled", true );
                    		disabled = true;
                    	}
	                
		                oItemCfg.addJSString( "text", oMenuChild.getText() );
		                oItemCfg.add( "expanded", oMenuChild.getExpanded() );
		                
		                String icon = oMenuChild.getIcon(); 
		                if( icon != null && icon.length() > 0 ) {
		                	oItemCfg.addJSString( "icon", composeUrlWithWebContext( icon ) );
		                	oItemCfg.addJSString("cls", "x-btn-text-icon");
		                }
		
		                if( oMenuChild.getActionExpression() != null && !disabled) {
		                	ExtConfig oItemListeners = oItemCfg.addChild("listeners");
		                	
		                	int waitMode = XVWScripts.WAIT_DIALOG;
		                	waitMode = oMenuChild.getServerActionWaitMode() == XVWServerActionWaitMode.NONE?~
		                			XVWScripts.WAIT_STATUS_MESSAGE:XVWScripts.WAIT_DIALOG;
		                	
		                	
		                	oItemListeners.add( "'click'", "function(){" +
		                    		XVWScripts.getCommandScript( oMenuChild.getTarget(), oMenuChild, waitMode )+"}" 
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


	@Override
	public ExtConfig renderRegion() {
		ExtConfig oTreeConfig = new ExtConfig("Ext.tree.TreePanel");
    	
    	oTreeConfig.addJSString( "id" , getClientId() );
		StringBuilder sActionUrl = new StringBuilder( getRequestContext().getAjaxURL() );
		if( sActionUrl.indexOf("?") == -1 ) {
			sActionUrl.append("?");
		}
		else {
			sActionUrl.append("&");
		}
		sActionUrl.append( "javax.faces.ViewState=").append( XUIRequestContext.getCurrentContext().getViewRoot().getViewState() );
		sActionUrl.append("&");
		sActionUrl.append( "xvw.servlet=" ).append( getClientId() );
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
    	oTreeConfig.addJSString( "layout" , "fit" );
    	oTreeConfig.addJSString( "dataUrl" , sActionUrl.toString() );
    	ExtConfig oRootConfig = oTreeConfig.addChild("root");
    	oRootConfig.addJSString("nodeType", "async");
    	
    	Iterator<UIComponent> it = getChildren().iterator();
    	while (it.hasNext()){
    		UIComponent childComp = it.next();
    		if (childComp instanceof UserToolBar)
    		{
    			ExtConfigArray toolbar = null;
    			if (getToolBarPlacement().equalsIgnoreCase("top"))
    				toolbar = oTreeConfig.addChildArray("tbar");
    			else
    				toolbar = oTreeConfig.addChildArray("bbar");
    			((UserToolBar)childComp).getExtJsConfig(toolbar);
    			break;
    		}
    		if (childComp instanceof TreePanelToolBar){
    			ExtConfigArray toolbar = null;
    			if (getToolBarPlacement().equalsIgnoreCase("top"))
    				toolbar = oTreeConfig.addChildArray("tbar");
    			else
    				toolbar = oTreeConfig.addChildArray("bbar");
    			
    			TreePanelToolBar t = (TreePanelToolBar) childComp;
    			t.getExtJsConfig(toolbar);
    			
    		}
    	}
    	return oTreeConfig;
	}

	@Override
	public ExtConfig getListeners() {
		return null;
	}
}
