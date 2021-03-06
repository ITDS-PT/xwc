package netgest.bo.xwc.components.classic;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.annotations.Values;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtConfigArray;
import netgest.bo.xwc.components.classic.extjs.ExtJsRenderer;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.classic.scripts.XVWServerActionWaitMode;
import netgest.bo.xwc.components.classic.toolbar.IToolbarGroup;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.components.security.SecurableComponent;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.def.XUIComponentStore;
import netgest.bo.xwc.framework.def.XUIRendererDefinition;
import netgest.utils.StringUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.faces.component.UIComponent;


/**
 * 
 * The {@link ToolBar} component menu tool bar inside a viewer. 
 * The purpose of this component is to organize buttons (that invoke actions)
 * in groups have have them placed in the same area.
 * 
 * To place entries (buttons) in the Toolbar, {@link Menu} components should be used
 * Example:
 * 
 * <xvw:toolBar>
 *  <xvw:menu text='Button1'/>
 *  <xvw:menu text='Button2'/>
 *</xvw:toolBar>	
 * 
 * @author Joao Carreira
 *
 */
public class ToolBar extends ViewerSecurityBase {

	
    /**
     * Whether or not the tool bar is disabled (buttons can't be clicked)	
     */
    private XUIViewStateBindProperty<Boolean> disabled = new XUIViewStateBindProperty<Boolean>( "disabled", this, "false" ,Boolean.class );
    /**
     * Whether or not the tool bar is visible	
     */
    private XUIViewStateBindProperty<Boolean> visible  = new XUIViewStateBindProperty<Boolean>( "visible", this, "true" ,Boolean.class );
	
    
    
    public void setDisabled(String disabled) {
        this.disabled.setExpressionText( disabled );
    }

    public boolean isDisabled() {
        return disabled.getEvaluatedValue();
    }

    public void setVisible(String visible) {
        this.visible.setExpressionText( visible );
    }

    public boolean isVisible() {
        return this.visible.getEvaluatedValue();
    }
    
    @Values({"left","top"})
    private XUIBaseProperty<String> iconPosition = new XUIBaseProperty<String>( "iconPosition", this, "left" );
    
    public void setIconPosition(String position){
    	this.iconPosition.setValue( position );
    }
    
    public String getPosition(){
    	return iconPosition.getValue();
    }
    
    public IconPosition getIconPosition(){
    	return IconPosition.fromString( getPosition() );
    }
    
    public enum IconPosition{
    	TOP,
    	LEFT;
    	
    	public static IconPosition fromString(String value){
    		for (IconPosition p : values()){
    			if (p.name().equalsIgnoreCase( value ))
    				return p;
    		}
    		return LEFT;
    	}
    }
    
    public ToolBar()
    {
    	
    }
    
    @Override
    public void initComponent(){
    	
    	super.initComponent();
    	
    	//This entire cycle basically gets all IToolBarGroups
    	//and tries to add their children to the current toolbar
    	//in the respective positions
    	List<UIComponent> child = getChildren();
    	List<UIComponent> finalList = new LinkedList<UIComponent>();
    	
    	
		Iterator<UIComponent> it = child.iterator();
		while (it.hasNext()){
			boolean addRegularMenu = true;
			UIComponent curr = it.next();
			if (curr instanceof IToolbarGroup){
				IToolbarGroup group = (IToolbarGroup) curr;
				finalList.addAll(group.getComponentList());
				addRegularMenu = false;
			}
			if (curr instanceof AttributeBase || curr instanceof AttributeLabel)
				((XUIComponentBase)curr).setRenderComponent(false);
			
			if (addRegularMenu)
				finalList.add(curr);
		}

		getChildren().clear();
		getChildren().addAll(finalList);
		
		initializeTemplate( "templates/components/toolbar.ftl" );
		
		if (rendererType.isDefaultValue( ))
			rendererType.setValue( "toolBar" );
		
    }
    
    @Override
    public String getFamily() {
    	return "toolBar";
    }

    @Override
    public boolean getRendersChildren() {
    	if ( !getEffectivePermission(SecurityPermissions.READ) ) {
    		return false;
    	}
        return true;
    }


    @Override
    public void restoreState(Object oState) {
    	super.restoreState(oState);
    }

	@Override
	public boolean isRendered() {
		if ( !getEffectivePermission(SecurityPermissions.READ) ) {
			return false;
		}
		return super.isRendered();
	}

    public static class XEOHTMLRenderer extends XUIRenderer implements ExtJsRenderer {

    	public boolean menuWasChanged(Menu menu){
    		Set<Entry<String,XUIBaseProperty<?>>> props = menu.getStateProperties();
    		Iterator<Entry<String,XUIBaseProperty<?>>> it = props.iterator();
    		while (it.hasNext()){
    			Entry<String,XUIBaseProperty<?>> entry = it.next();
    			if (entry.getValue().wasChanged()){
    				return true;
    			}
    		}
    		return false;
    	}
    	
    	@Override
    	public StateChanged wasStateChanged( XUIComponentBase component, List<XUIBaseProperty<?>> updateProperties ) {
    		updateProperties.add( component.getStateProperty( "disabled" ) );
    		updateProperties.add( component.getStateProperty( "visible" ) );
    		StateChanged changed = super.wasStateChanged( component, updateProperties );
    		
    		Iterator<UIComponent> childs =  component.getChildren().iterator();
            while( childs.hasNext() ) {
            	UIComponent currChild = childs.next();
            	if (currChild instanceof Menu){
	                Menu oMenuChild = (Menu)currChild;
	                if( oMenuChild.isRendered() ) {
	                	if( menuWasChanged( oMenuChild ) ) {
	                		if (changed != StateChanged.FOR_RENDER)
	                			changed = StateChanged.FOR_UPDATE;
	                	}
	                	if (oMenuChild.getChildCount() > 0)
	                		changed = recursiveWasStateChangedForChildren( oMenuChild, changed );
	                }
                }
            }
            return changed;
    	}
    	
    	private StateChanged recursiveWasStateChangedForChildren(Menu m, StateChanged changed){
    		Iterator<UIComponent> childs =  m.getChildren().iterator();
            while( childs.hasNext() ) {
            	UIComponent currChild = childs.next();
            	if (currChild instanceof Menu){
	                Menu oMenuChild = (Menu)currChild;
	                if( oMenuChild.isRendered() ) {
	                	if( menuWasChanged( oMenuChild ) ) {
	                		if (changed != StateChanged.FOR_RENDER)
	                			changed = StateChanged.FOR_UPDATE;
	                	}
	                	if (oMenuChild.getChildCount() > 0)
	                		changed = recursiveWasStateChangedForChildren( oMenuChild, changed );
	                	
	                }
                }
            }
            return changed;
    	}
    	
    	@Override
    	public void encodeComponentChanges( XUIComponentBase component,
    			List<XUIBaseProperty<?>> propertiesWithChangedState ) throws IOException {

    		// update menu options
			ScriptBuilder sb = new ScriptBuilder();
        	generateToolBarUpdateScript( sb, (ToolBar) component );
        	ScriptBuilder sb2 = ToolBar.XEOHTMLRenderer.updateMenuItems( (ToolBar) component );
        	sb.w( sb2 );
            if( sb.length() > 0 ) {
            	getResponseWriter().getScriptContext().add( 
            			XUIScriptContext.POSITION_FOOTER, 
            			((ToolBar)component).getClientId(), 
            			sb.toString()
            	);
            }
    		
    	}
    	
        @Override
        public void encodeBegin(XUIComponentBase component) throws IOException {
            XUIResponseWriter w = getResponseWriter();

    		w.startElement( HTMLTag.DIV, component );
			w.writeAttribute( HTMLAttr.ID, component.getClientId(), null );
		
    		ExtConfig toolBar = renderExtJs( component );
    		toolBar.addJSString( "renderTo", component.getClientId() );
    		w.getScriptContext().add(XUIScriptContext.POSITION_FOOTER, 
    				"toolbar:"+component.getId(),
    				toolBar.renderExtConfig()
    		);
    		

        }
        
        public static final ScriptBuilder updateMenuItems( ToolBar toolBar ) {
            Iterator<UIComponent> childs =  toolBar.getChildren().iterator();
        	ScriptBuilder sb = new ScriptBuilder();
        	sb.w( " window.setTimeout( function(){ " );
            while( childs.hasNext() ) {
            	UIComponent currChild = childs.next();
            	if (currChild instanceof Menu){
	                Menu oMenuChild = (Menu)currChild;
	                if( oMenuChild.isRendered() ) {
                		sb.startBlock();
                		sb.w( "try{" );
                    	generateUpdateScript(sb, oMenuChild );
                    	sb.w("} catch (e) {}");
                    	sb.endBlock();
	                	if( oMenuChild.getChildCount() > 0 ) {
	                		updateChildMenuItems(sb, oMenuChild);
	                	}
	                }
                }
            }
            sb.w("  },0);");
            return sb;
        }

        
        
        public static final void updateChildMenuItems( ScriptBuilder sb, Menu menu ) {
            Iterator<UIComponent> childs =  menu.getChildren().iterator();
            while( childs.hasNext() ) {
                Menu oMenuChild = (Menu)childs.next();
                if( oMenuChild.isRendered() ) {
                	sb.startBlock();
                	//sb.w( " window.setTimeout( function(){ " );
                	generateUpdateScript(sb, oMenuChild );
                	//sb.w(" },0);");
                	sb.endBlock();

                }
            	if( oMenuChild.getChildCount() > 0 ) {
            		updateChildMenuItems(sb, oMenuChild);
            	}
            }
        }
         
        public static final void generateToolBarUpdateScript( ScriptBuilder sb, ToolBar toolBar ) {
        	boolean vChged = toolBar.visible.wasChanged();
        	boolean dChged = toolBar.disabled.wasChanged();
        	
        	if( vChged || dChged ) {
	        	sb.w( "var m=Ext.getCmp('ext-").writeValue( toolBar.getClientId() ).l("'); if (m) {" );
	        	if( vChged ) {
		        	if( !toolBar.isVisible() )
		        		sb.w( "m.hide();" );
		        	else
		        		sb.w( "m.show();" );
	        	}
	        	
	        	if( dChged )
	        		sb.w( "m.setDisabled(").w( toolBar.isDisabled() ).l( ");" );
	        	sb.w("};");
        	}
        }
        
        public static final void generateUpdateScript( ScriptBuilder sb, Menu oMenuChild ) {
        	sb.w( "var m=Ext.getCmp('ext-").writeValue( oMenuChild.getClientId() ).l("'); if (m) {" );
        	if( !oMenuChild.isVisible() )
        		sb.w( "m.hide();" );
        	else
        		sb.w( "m.show();" );
        	
        	if (oMenuChild.getParent() instanceof ToolBar){
        		if (((ToolBar)oMenuChild.getParent()).isDisabled())
            		sb.w( "m.setDisabled(").w( true ).l( ");" );
            	else
            		sb.w( "m.setDisabled(").w( oMenuChild.isDisabled() ).l( ");" );
        	} else{
        		sb.w( "m.setDisabled(").w( oMenuChild.isDisabled() ).l( ");" );
        	}
        	
        	if (oMenuChild.wasTextChanged() && !oMenuChild.isSpacer())
        		sb.w( "if (m.setText) {m.setText('").w( oMenuChild.getText() ).l( "'); }" );
        	
        	
        	sb.w("};");
        }
        
        
        
        @Override
        public void encodeChildren(XUIComponentBase component) throws IOException {
        }

        @Override
        public void encodeEnd(XUIComponentBase component) throws IOException {
    		super.encodeEnd(component);
			XUIResponseWriter w = getResponseWriter();
    		w.endElement("div");
    		
        }

        public ExtConfig renderExtJs( XUIComponentBase component ) {
                ToolBar toolBar = (ToolBar)component;
                
                ExtConfig oToolBarCfg = new ExtConfig( "Ext.Toolbar" );
                oToolBarCfg.addJSString("style","border:0px solid black;" );     
                oToolBarCfg.add( "width" , "'auto'" );
                oToolBarCfg.add( "hidden" , !toolBar.isVisible() );
                oToolBarCfg.add( "disabled" , toolBar.isDisabled() );
                oToolBarCfg.addJSString( "id", "ext-" + component.getClientId() );
                
                

                ExtConfigArray oItemsCfg = oToolBarCfg.addChildArray( "items" );

                Iterator<UIComponent> childs =  component.getChildren().iterator();
                while( childs.hasNext() ) {
                    ExtConfig oItemCfg = null;
                    
                    UIComponent currentItem = childs.next();
                    if (currentItem instanceof Menu){
                    
                        Menu oMenuChild = (Menu)currentItem;
	                    if (oMenuChild.canAcess()){ //Check Permissions
	                    	if ( oMenuChild.getEffectivePermission(SecurityPermissions.READ) ) {
			                    oMenuChild.setRenderedOnClient( true );
			                    
			                    if( oMenuChild.isRendered() ) {
		    		            	String sText = oMenuChild.getText();
		    		
		    		            	if( "-".equals( sText ) ) {
		    	            			ExtConfig sep = oItemsCfg.addChild( "ExtXeo.Toolbar.Separator" );
		    	            			sep.addJSString( "id", "ext-" + oMenuChild.getClientId() );
		    	            			sep.add( "hidden", !toolBar.isVisible() || !oMenuChild.isVisible()  );
		    	            		}
		    		            	else if ("->".equals( sText ) ){
		    		            		ExtConfig sep = oItemsCfg.addChild();
		    		            		sep.addJSString("xtype", "tbfill");
		    		            		sep.addJSString( "id", "ext-" + oMenuChild.getClientId() );
		    		            	}
		    		            	else if (" ".equals( sText ) ){
		    		            		ExtConfig sep = oItemsCfg.addChild();
		    		            		sep.addJSString("xtype", "tbspacer");
		    		            		sep.addJSString( "id", "ext-" + oMenuChild.getClientId() );
		    		            	}
		    		            	else if ( oMenuChild.getEffectivePermission( SecurityPermissions.READ ) ) {
		    		                        oItemCfg = oItemsCfg.addChild(  );
		    		                    	configExtMenu( this, toolBar , oMenuChild, oItemCfg);
		    			                    if( oMenuChild.getChildCount() > 0) {
		    			                    	//If our top Menu has an action, make it a split button with default action
		    			                    	//if (oMenuChild.serverAction != null && oMenuChild.serverAction.getValue() != null) 
		    				                    oItemCfg.addJSString( "xtype", "splitbutton" );
		    				                    if (oItemCfg.getConfigValue( "handler" ) == null){
		    				                    	oItemCfg.add( "handler" , "function () { this.showMenu(); }"); 
		    				                    }
		    			                    	if( oItemCfg != null ) {
		    			    	                    encodeSubMenuJS( toolBar,oItemCfg.addChild( "menu" ), oMenuChild );
		    			                    	}
		    			                    }
		    		                }
		    	                }
	                    	}
	                    }
                    }
                    else if ( currentItem instanceof XUIExtJsComponent ) {
                	   oItemsCfg.add( ((XUIExtJsComponent)currentItem).getExtConfig() );
                    }
                    //We may have other things, like form fields
					else {
						if (currentItem instanceof XUIComponentBase){
							XUIComponentBase currentComponent = (XUIComponentBase) currentItem;
							StateChanged state = currentComponent.wasStateChanged2(); 
							if (state == StateChanged.FOR_RENDER || state == StateChanged.FOR_UPDATE){
		                    	XUIRequestContext req = XUIRequestContext.getCurrentContext();
		                    	XUIComponentStore compStore = req.getApplicationContext().getComponentStore();
		                    	Map<String,XUIRendererDefinition> def = compStore.getMapOfRenderKit("XEOHTML");
		                    	XUIRendererDefinition definition = def.get(currentItem.getFamily()+":"+currentItem.getRendererType());
		                    	if (definition != null){ //For this to be null we probably have a component
		                    		//without a renderer class, like an instance of IToolBarGroup
		                    		String className = definition.getClassName();
		                    		try {
		    							//Instantiate the class and render the component
		    							//to a string (remove the renderTo property, because
		    							//it does not apply in this situation and totally screws up rendering)
		    	                		Object newInstance = Class.forName(className).newInstance();
		    	                		if (newInstance instanceof ExtJsRenderer)
		    	                		{
		    	                			ExtJsRenderer render = (ExtJsRenderer) newInstance;
		    								ExtConfig config = render.getExtJsConfig((XUIComponentBase)currentItem);
		    								if (currentItem instanceof AttributeBase)
		    									config.add("width", ((AttributeBase)currentItem).getWidth());
		    								config.removeConfig("renderTo");
		    								config.removeConfig("validator");
		    								oItemsCfg.addChild(config);
		    							}
		    						}  catch (Exception e) {
		    							e.printStackTrace();
		    						}
		    					}
							}
						}
                    }
                }
                return oToolBarCfg;
        }
        
        public static final void configExtMenu( XUIRenderer renderer, ToolBar toolBar,  Menu oMenuChild, ExtConfig  oItemCfg ) {
            oItemCfg.addJSString( "id", "ext-" + oMenuChild.getClientId() );
            oItemCfg.addJSString( "text", oMenuChild.getText() );
            if( !oMenuChild.isVisible() )
                oItemCfg.add( "hidden", true );

            if( toolBar.isDisabled() || oMenuChild.isDisabled() || !oMenuChild.getEffectivePermission(SecurityPermissions.EXECUTE) )
                oItemCfg.add( "disabled", true );
            
            String icon = oMenuChild.getIcon(); 
            if( icon != null && icon.length() > 0 ) {
            	oItemCfg.addJSString( "icon", renderer.composeUrlWithWebContext( icon ) );
            	if( StringUtils.isEmpty(oMenuChild.getText())) {
                	oItemCfg.addJSString("cls", "x-btn-icon");
            	}
            	else {
                	oItemCfg.addJSString("cls", "x-btn-text-icon");
            	}
            }

            String toolTip = oMenuChild.getToolTip();
            String shortCut = oMenuChild.getShortCut();
            
            if( shortCut != null ) {
            	toolTip = (toolTip!=null?toolTip + " ":"") + "[" + shortCut + "]";
            }
            
            if( toolTip != null ) {
            	oItemCfg.addJSString("tooltip", toolTip );
            }
                
            if( oMenuChild.getValue() instanceof Boolean ) {
                oItemCfg.add( "checked", oMenuChild.getValue() );
            }
            
            XVWServerActionWaitMode waitMode = oMenuChild.getServerActionWaitMode();
            if( waitMode == null ) {
            	waitMode = XVWServerActionWaitMode.STATUS_MESSAGE;
            }
            
            String handler = "";
            //Menus can have an action, or implement the actionPerformed method. We need to generate the handler
            //for the two situations (and don't generate when neither is present). Problem is we can't detect if the
            //action performed method is implemented in a subclass, thus we need to check if it's an instance of Menu (
            //and check for actions) or an instance of a subclass (and generate anyway)
            if ( isBaseMenu( oMenuChild ) ){
            	if ( menuHasAction( oMenuChild ) ){
            		handler = "function(){" + XVWScripts.getCommandScript( oMenuChild.getTarget(), oMenuChild, waitMode.ordinal() ) +"}";
                	oItemCfg.add( "handler", handler  );
            	}
            } else {
            	handler = "function(){" + XVWScripts.getCommandScript( oMenuChild.getTarget(), oMenuChild, waitMode.ordinal() ) +"}";
            	oItemCfg.add( "handler", handler  );
            }
            
            if( shortCut != null ) {
            	XUIScriptContext sc = XUIRequestContext.getCurrentContext().getScriptContext();
            	sc.add( 
            		XUIScriptContext.POSITION_FOOTER, 
            		oMenuChild.getClientId()+"_scut",
            		"shortcut.add( '"+ shortCut +"' , " + handler + " );"
            	);
            }
        }
        
        
        /**
         * 
         * Checks if a menu has an action associated.
         * It's only guaranteed to work with instances of Menu (and not with instances
         * of descendants which may implement the actionPerformed method)
         * 
         * @param oMenuChild The menu to check
         * @return True if the menu has actions and false otherwise
         */
        private static boolean menuHasAction(Menu oMenuChild) {
			if (oMenuChild.getActionExpression() != null || oMenuChild.getActionListener() != null || 
					(oMenuChild.getActionListeners() != null && oMenuChild.getActionListeners().length > 0) )
				return true;
			else
				return false;
		}

		/**
		 * 
		 * Checks whether the Menu is a subclass of the base Menu or a base menu
		 * 
		 * @param oMenuChild The menu to test
		 * @return True if it's an instance of menu, false if it's a instance of a subclasss
		 */
		private static boolean isBaseMenu(Menu oMenuChild) {
			return oMenuChild.getClass().equals( Menu.class );
		}

		public void encodeSubMenuJS( ToolBar tool, ExtConfig oMenu, Menu oSubMenu ) {
            ExtConfigArray  oSubChildCfg;

            oMenu.setComponentType( "Ext.menu.Menu" );
            oSubChildCfg = oMenu.addChildArray( "items" );
            
            Iterator<UIComponent> oSubChildren = oSubMenu.getChildren().iterator();
            
            while( oSubChildren.hasNext() ) {
                
                ExtConfig oItemCfg;
                
                Menu oMenuChild = (Menu)oSubChildren.next();
                oMenuChild.setRenderedOnClient( true );
                
                String sText = oMenuChild.getText();
                if( "-".equals( sText ) ) {
        			ExtConfig sep = oSubChildCfg.addChild( "ExtXeo.Toolbar.Separator" );
        			sep.addJSString( "id", "ext-" + oMenuChild.getClientId() );
        			sep.add( "hidden", !tool.isVisible() || !oMenuChild.isVisible()  );
                }
                else if ( oMenuChild.getEffectivePermission(SecurityPermissions.READ) ) {
                	oItemCfg = oSubChildCfg.addChild();
                	
                	configExtMenu( this, tool, oMenuChild, oItemCfg);
                	
                	if( oMenuChild.getChildCount() > 0 ) {
                		encodeSubMenuJS( tool, oItemCfg.addChild( "menu" ), oMenuChild );
                	}
                }
            }
        }

        @Override
        public ExtConfig getExtJsConfig(XUIComponentBase oComp) {
            return renderExtJs( oComp );
        }

		@Override
		public boolean getRendersChildren() {
			
			return true;
			
		}

    }

    //
    // Methods from SecurableComponent
    //
    
	public COMPONENT_TYPE getViewerSecurityComponentType() {
		return SecurableComponent.COMPONENT_TYPE.TOOLBAR;
	}

	public String getViewerSecurityId() {
		return null;
	}

	public String getViewerSecurityLabel() {
		return getViewerSecurityComponentType().toString();
	}

	public boolean isContainer() {
		return true;
	}
	
}
