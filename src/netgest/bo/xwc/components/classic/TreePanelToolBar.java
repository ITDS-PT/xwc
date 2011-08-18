package netgest.bo.xwc.components.classic;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtConfigArray;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.classic.scripts.XVWServerActionWaitMode;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.components.security.SecurableComponent;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class TreePanelToolBar extends ViewerSecurityBase {

	private XUIBindProperty<String> text = 
		new XUIBindProperty<String>("text", this, String.class, "ToolBar" );
	
	private XUIBindProperty<String> icon = 
		new XUIBindProperty<String>("icon", this, String.class, "icons/favorite.png" );
	
	public String getText(){
		return text.getEvaluatedValue();
	}
	
	public void setText(String textExpr){
		this.text.setExpressionText(textExpr);
	}
	
	public String getIcon(){
		return icon.getEvaluatedValue();
	}
	
	public void setIcon(String iconExpr){
		this.icon.setExpressionText(iconExpr);
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
	
	@Override
	public COMPONENT_TYPE getViewerSecurityComponentType() {
		return SecurableComponent.COMPONENT_TYPE.TOOLBAR;
	}

	@Override
	public String getViewerSecurityId() {
		return null;
	}
	
	@Override
	public String getViewerSecurityLabel() {
		return getViewerSecurityComponentType().toString();
	}

	@Override
	public boolean isContainer() {
		return true;
	}
	
	public void getExtJsConfig(ExtConfigArray config) {
        
        
        ExtConfigArray oItemsCfg = config;
        
        Menu parent = new Menu();
        parent.setText(getText());
        parent.setIcon(getIcon());

        parent.getChildren().addAll(getChildren());
        
        this.getChildren().clear();
        this.getChildren().add(parent);
        
        Iterator<UIComponent> childs =  getChildren().iterator();
        while( childs.hasNext() ) {
            
            ExtConfig oItemCfg = null;
            
            UIComponent currentItem = childs.next();
            if (currentItem instanceof Menu){
            
            Menu oMenuChild = (Menu)currentItem;
            oMenuChild.setRenderedOnClient( true );
            
            if( oMenuChild.isRendered() ) {
            
	            	String sText = oMenuChild.getText();
	
	            	if( "-".equals( sText ) ) {
            			ExtConfig sep = oItemsCfg.addChild( "ExtXeo.Toolbar.Separator" );
            			sep.addJSString( "id", "ext-" + oMenuChild.getClientId() );
            		}
	            	else if ( oMenuChild.getEffectivePermission( SecurityPermissions.READ ) ) {
	                        oItemCfg = oItemsCfg.addChild(  );
	                    	configExtMenu( this , oMenuChild, oItemCfg);
		                    if( oMenuChild.getChildCount() > 0) {
		                    	//If our top Menu has an action, make it a split button with default action
		                    	if (oMenuChild.serverAction != null && oMenuChild.serverAction.getValue() != null) 
			                    	oItemCfg.addJSString( "xtype", "splitbutton" );
		                    	if( oItemCfg != null ) {
		    	                    encodeSubMenuJS( this,oItemCfg.addChild( "menu" ), oMenuChild );
		                    	}
		                    }
	                }
                }
            }
          
        }
        
    }
	
	public String composeUrlWithWebContext( String resourcePath ) {
        StringBuffer retUrl;
        FacesContext context;
        
        context = getFacesContext();
        retUrl = new StringBuffer();
        retUrl.append( context.getExternalContext().getRequestContextPath() );
        
        if( resourcePath == null || resourcePath.length() == 0 || resourcePath.charAt( 0 ) != '/' ) {
        	retUrl.append( '/' );
        }
        retUrl.append( resourcePath );
        return retUrl.toString();
        
    }
    
    public final void configExtMenu( TreePanelToolBar toolBar,  Menu oMenuChild, ExtConfig  oItemCfg ) {
        oItemCfg.addJSString( "id", "ext-" + oMenuChild.getClientId() );
        oItemCfg.addJSString( "text", oMenuChild.getText() );
        if( !oMenuChild.isVisible() )
            oItemCfg.add( "hidden", true );

        if( oMenuChild.isDisabled() || !oMenuChild.getEffectivePermission(SecurityPermissions.EXECUTE) )
            oItemCfg.add( "disabled", true );
        
        String icon = oMenuChild.getIcon(); 
        if( icon != null && icon.length() > 0 ) {
        	oItemCfg.addJSString( "icon", composeUrlWithWebContext( icon ) );
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
        
        XVWServerActionWaitMode waitMode = oMenuChild.getServerActionWaitMode();
        if( waitMode == null ) {
        	waitMode = XVWServerActionWaitMode.STATUS_MESSAGE;
        }
        
        if (oMenuChild.serverAction != null && oMenuChild.serverAction.getValue() != null){
        oItemCfg.add( "handler", "function(){" +
        		XVWScripts.getCommandScript( oMenuChild.getTarget(), oMenuChild, waitMode.ordinal() )+"}" 
        	);
        }
    }
    
    
    public void encodeSubMenuJS( TreePanelToolBar tool, ExtConfig oMenu, Menu oSubMenu ) {
        ExtConfigArray  oSubChildCfg;

        oMenu.setComponentType( "Ext.menu.Menu" );
        //oMenu.addJSString("id", oSubMenu.getClientId() );
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
    			sep.add( "hidden", !oMenuChild.isVisible()  );
            }
            else if ( oMenuChild.getEffectivePermission(SecurityPermissions.READ) ) {
            	oItemCfg = oSubChildCfg.addChild();
            	
            	configExtMenu( this, oMenuChild, oItemCfg);
            	
            	if( oMenuChild.getChildCount() > 0 ) {
            		encodeSubMenuJS( tool, oItemCfg.addChild( "menu" ), oMenuChild );
            	}
            }
        }
    }

    public static final class XEOHTMLRenderer extends XUIRenderer {

        @Override
        public void encodeChildren(XUIComponentBase component) throws IOException {
        }

        @Override
		public boolean getRendersChildren() {
			
			return true;
			
		}

    }

}
