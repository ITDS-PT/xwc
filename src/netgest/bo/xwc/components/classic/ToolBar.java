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
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;


public class ToolBar extends ViewerSecurityBase {
	
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
        List<UIComponent> kids = oCompBase.getChildren();
        for (int i = 0; i < kids.size(); i++) {
            kid = kids.get( i );
            if( kid instanceof XUIComponentBase ) {
                if( ((XUIComponentBase)kid).wasStateChanged() ) {
                    return true;
                }
            }
            wasStateChangedOnChilds( kid );
        }
        return false;
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

    		w.startElement( HTMLTag.DIV, component );
    		w.writeAttribute( HTMLAttr.ID, component.getClientId(), null );
    		
    		ExtConfig toolBar = renderExtJs( component );
    		toolBar.addJSString( "renderTo", component.getClientId() );
    		w.getScriptContext().add(XUIScriptContext.POSITION_FOOTER, 
    				"toolbar:"+component.getId(),
    				toolBar.renderExtConfig()
    		);

        }

        @Override
        public void encodeEnd(XUIComponentBase component) throws IOException {
    		super.encodeEnd(component);
    		XUIResponseWriter w = getResponseWriter();
    		w.endElement("div");
        }

        public ExtConfig renderExtJs( XUIComponentBase component ) {
            
            //boolean bFirstOption;
            
            //bFirstOption = true;
            
            //FastStringWriter sOut = new FastStringWriter( 500 );
            
            //sOut.write( "Ext.onReady( function() { " ); sOut.write("\n");
                                                        
            //sOut.write( "var " + component.getId() + " = new Ext.Toolbar({"); sOut.write("\n");
            ExtConfig oToolBarCfg = new ExtConfig( "Ext.Toolbar" );
            oToolBarCfg.addJSString("style","border:0px solid black;" );     
            oToolBarCfg.add( "width" , "'auto'" );
            
            //sOut.write( "id:'" + component.getClientId() +"'," ); sOut.write("\n");
            oToolBarCfg.addJSString( "id", component.getClientId() );

            //sOut.write( "items: [" ); sOut.write("\n");
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
	                else if ( oMenuChild.getEffectivePermission(SecurityPermissions.READ) ) {
	                        oItemCfg = oItemsCfg.addChild(  );
	                        
	                    	configExtMenu(oMenuChild, oItemCfg);
		                        
		                    if( oMenuChild.getChildCount() > 0 ) {
		                    	oItemCfg.addJSString( "xtype", "splitbutton" );
		                    	if( oItemCfg != null ) {
		    	                    encodeSubMenuJS( oItemCfg.addChild( "menu" ), oMenuChild );
		                    	}
		                    }
	                }
                }
            }

//            sOut.write( "]\n" );
            
//            sOut.write( "});\n" );            

//            sOut.write( component.getId() + ".render('" + component.getClientId() + "');"  );
            
//            sOut.write( "});\n" );            
            
            // Clean Script, not needed because there is not options
//            if( bFirstOption ) {
//                sOut.reset();
//            }
//            return sOut.toString();
            return oToolBarCfg;
            
        }
        
        public static final void configExtMenu( Menu oMenuChild, ExtConfig  oItemCfg ) {
            oItemCfg.addJSString( "text", oMenuChild.getText() );
            if( !oMenuChild.isVisible() )
                oItemCfg.add( "hidden", true );

            if( oMenuChild.isDisabled() || !oMenuChild.getEffectivePermission(SecurityPermissions.EXECUTE) )
                oItemCfg.add( "disabled", true );
            
            if( oMenuChild.getIcon() != null ) {
            	oItemCfg.addJSString( "icon", oMenuChild.getIcon() );
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
        
        
        public void encodeSubMenuJS( ExtConfig oMenu, Menu oSubMenu ) {
            ExtConfigArray  oSubChildCfg;

            //sOut.write(" new Ext.menu.Menu({ "); sOut.write("\n");
            oMenu.setComponentType( "Ext.menu.Menu" );
            //sOut.write(" id: '" + oSubMenu.getClientId() + "'"); sOut.write(",\n");
            oMenu.addJSString("id", oSubMenu.getClientId() );
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
                	
                	configExtMenu(oMenuChild, oItemCfg);
                	
                	if( oMenuChild.getChildCount() > 0 ) {
                		encodeSubMenuJS( oItemCfg.addChild( "menu" ), oMenuChild );
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
