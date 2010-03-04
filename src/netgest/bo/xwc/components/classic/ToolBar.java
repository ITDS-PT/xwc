package netgest.bo.xwc.components.classic;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtConfigArray;
import netgest.bo.xwc.components.classic.extjs.ExtJsRenderer;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.components.security.SecurableComponent;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;


public class ToolBar extends ViewerSecurityBase {

	
    private XUIStateBindProperty<Boolean> disabled = new XUIStateBindProperty<Boolean>( "disabled", this, "false",Boolean.class );
    private XUIStateBindProperty<Boolean> visible  = new XUIStateBindProperty<Boolean>( "visible", this, "true",Boolean.class );
	
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
    
    
    public ToolBar()
    {
    }

    @Override
    public boolean getRendersChildren() {
    	if ( !getEffectivePermission(SecurityPermissions.READ) ) {
    		return false;
    	}
        return true;
    }


    @Override
    public boolean wasStateChanged() {
        if( super.wasStateChanged() ) {
            return true;
        }
        return wasStateChangedOnChilds( this );
    }
    
    private boolean wasStateChangedOnChilds( UIComponent oCompBase ) {
        UIComponent kid;
        boolean ret = false;
        List<UIComponent> kids = oCompBase.getChildren();
        for (int i = 0; i < kids.size(); i++) {
            kid = kids.get( i );
            if( kid instanceof XUIComponentBase ) {
                if( ((XUIComponentBase)kid).wasStateChanged() ) {
                    ret = true;
                    break;
                }
            }
            ret = wasStateChangedOnChilds( kid );
            if( ret ) {
            	break;
            }
        }
        return ret;
    }

	@Override
	public boolean isRendered() {
		if ( !getEffectivePermission(SecurityPermissions.READ) ) {
			return false;
		}
		return super.isRendered();
	}

    public static final class XEOHTMLRenderer extends XUIRenderer implements ExtJsRenderer {

        @Override
        public void encodeBegin(XUIComponentBase component) throws IOException {
            XUIResponseWriter w = getResponseWriter();

    		if( !component.isRenderedOnClient() ) {
    			w.startElement( HTMLTag.DIV, component );
    			w.writeAttribute( HTMLAttr.ID, component.getClientId(), null );
    		
	    		ExtConfig toolBar = renderExtJs( component );
	    		toolBar.addJSString( "renderTo", component.getClientId() );
	    		w.getScriptContext().add(XUIScriptContext.POSITION_FOOTER, 
	    				"toolbar:"+component.getId(),
	    				toolBar.renderExtConfig()
	    		);
    		}
    		else {
    			// update menu options
            	ScriptBuilder sb = ToolBar.XEOHTMLRenderer.updateMenuItems( (ToolBar) component );
                if( sb.length() > 0 ) {
                	getResponseWriter().getScriptContext().add( 
                			XUIScriptContext.POSITION_FOOTER, 
                			((ToolBar)component).getClientId(), 
                			sb.toString()
                	);
                }
    		}

        }
        
        public static final ScriptBuilder updateMenuItems( ToolBar toolBar ) {
            Iterator<UIComponent> childs =  toolBar.getChildren().iterator();
        	ScriptBuilder sb = new ScriptBuilder();
            while( childs.hasNext() ) {
                Menu oMenuChild = (Menu)childs.next();
                if( oMenuChild.isRendered() ) {
                	if( oMenuChild.wasStateChanged() ) {
                    	sb.startBlock();
                    	generateUpdateScript(sb, oMenuChild );
                    	sb.endBlock();
                	}
                }
            }
            return sb;
        }

        public static final void updateChildMenuItems( ScriptBuilder sb, Menu menu ) {
            Iterator<UIComponent> childs =  menu.getChildren().iterator();
            while( childs.hasNext() ) {
                Menu oMenuChild = (Menu)childs.next();
                if( oMenuChild.isRendered() ) {
	            	if( oMenuChild.wasStateChanged() ) {
	                	sb.startBlock();
	                	generateUpdateScript(sb, oMenuChild );
	                	sb.endBlock();
	            	}
                }
            }
        }
        
        public static final void generateUpdateScript( ScriptBuilder sb, Menu oMenuChild ) {
        	sb.w( "var m=Ext.getCmp('ext-").writeValue( oMenuChild.getClientId() ).l("');" );
        	if( !oMenuChild.isVisible() )
        		sb.w( "m.hide();" );
        	else
        		sb.w( "m.show();" );
        		
        	sb.w( "m.setDisabled(").w( oMenuChild.isDisabled() ).l( ");" );
        	
        }
        
        @Override
        public void encodeChildren(XUIComponentBase component) throws IOException {
        }

        @Override
        public void encodeEnd(XUIComponentBase component) throws IOException {
    		super.encodeEnd(component);
    		if( !component.isRenderedOnClient() ) {
	    		XUIResponseWriter w = getResponseWriter();
	    		w.endElement("div");
    		}
        }

        public ExtConfig renderExtJs( XUIComponentBase component ) {
            ExtConfig oToolBarCfg = new ExtConfig( "Ext.Toolbar" );
            oToolBarCfg.addJSString("style","border:0px solid black;" );     
            oToolBarCfg.add( "width" , "'auto'" );
            
            oToolBarCfg.addJSString( "id", "ext-" + component.getClientId() );

            ExtConfigArray oItemsCfg = oToolBarCfg.addChildArray( "items" );

            Iterator<UIComponent> childs =  component.getChildren().iterator();
            while( childs.hasNext() ) {
                
                ExtConfig oItemCfg = null;
                
                Menu oMenuChild = (Menu)childs.next();
                
                if( oMenuChild.isRendered() ) {
                
	            	String sText = oMenuChild.getText();
	
	            	if( "-".equals( sText ) ) {
	                	oItemsCfg.add("'-'");
	                }
	                else if ( oMenuChild.getEffectivePermission( SecurityPermissions.READ ) ) {
	                        oItemCfg = oItemsCfg.addChild(  );
	                    	configExtMenu( this, (ToolBar)component , oMenuChild, oItemCfg);
		                    if( oMenuChild.getChildCount() > 0 ) {
		                    	oItemCfg.addJSString( "xtype", "splitbutton" );
		                    	if( oItemCfg != null ) {
		    	                    encodeSubMenuJS( (ToolBar)component,oItemCfg.addChild( "menu" ), oMenuChild );
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
            if( !toolBar.isVisible() || !oMenuChild.isVisible() )
                oItemCfg.add( "hidden", true );

            if( toolBar.isDisabled() || oMenuChild.isDisabled() || !oMenuChild.getEffectivePermission(SecurityPermissions.EXECUTE) )
                oItemCfg.add( "disabled", true );
            
            String icon = oMenuChild.getIcon(); 
            if( icon != null && icon.length() > 0 ) {
            	oItemCfg.addJSString( "icon", renderer.composeUrlWithWebContext( icon ) );
            	if( oMenuChild.getText() == null ) {
                	oItemCfg.addJSString("cls", "x-btn-icon");
            	}
            	else {
                	oItemCfg.addJSString("cls", "x-btn-text-icon");
            	}
            }
            
            if( oMenuChild.getToolTip() != null ) {
            	oItemCfg.addJSString("tooltip", oMenuChild.getToolTip() );
            }
                
            if( oMenuChild.getValue() instanceof Boolean ) {
                oItemCfg.add( "checked", oMenuChild.getValue() );
            }
            
            oItemCfg.add( "handler", "function(){" +
            		XVWScripts.getCommandScript( oMenuChild.getTarget(), oMenuChild, XVWScripts.WAIT_DIALOG )+"}" 
            	);
                
        	
        }
        
        
        public void encodeSubMenuJS( ToolBar tool, ExtConfig oMenu, Menu oSubMenu ) {
            ExtConfigArray  oSubChildCfg;

            //sOut.write(" new Ext.menu.Menu({ "); sOut.write("\n");
            oMenu.setComponentType( "Ext.menu.Menu" );
            //sOut.write(" id: '" + oSubMenu.getClientId() + "'"); sOut.write(",\n");
            //oMenu.addJSString("id", oSubMenu.getClientId() );
            //sOut.write(" items: [ "); sOut.write("\n");
            oSubChildCfg = oMenu.addChildArray( "items" );
            
            Iterator<UIComponent> oSubChildren = oSubMenu.getChildren().iterator();
            
            while( oSubChildren.hasNext() ) {
                
                ExtConfig oItemCfg;
                
                Menu oMenuChild = (Menu)oSubChildren.next();
                
                String sText = oMenuChild.getText();
                if( "-".equals( sText ) ) {
                	oSubChildCfg.add("'-'");
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
