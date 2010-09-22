package netgest.bo.xwc.xeo.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.component.UIComponent;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.ToolBar;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIViewBindProperty;
import netgest.bo.xwc.xeo.components.utils.XEOComponentStateLogic;
import netgest.bo.xwc.xeo.localization.XEOComponentMessages;

/**
 * 
 * A default {@link ToolBar} for the {@link Bridge} component, includes
 * buttons to add an existing {@link boObject} instance to the bridge, remove an object from the bridge
 * and create a new object to add to the bridge
 * 
 * @author jcarreira
 *
 */
public class BridgeToolBar extends ToolBarMenuPositions {
	
	/**
	 * Allows to change the object that will be used to retrieve the bridge from
	 */
	private XUIBindProperty<boObject> 	targetObject 	= 
		new XUIBindProperty<boObject>("targetObject", this, boObject.class, "#{viewBean.XEOObject}" );

	/**
	 * The name of the bridge to where the toolbar is connected.
	 * By default it searches a parent {@link Bridge} component and uses the bridgeName
	 * from that parent
	 */
	private XUIBindProperty<String>  bridgeName    = 
		new XUIBindProperty<String>( "bridgeName", this, String.class );
	
	/**
	 * Whether or nor the default "Add" button should be rendered or not
	 */
	private XUIViewBindProperty<Boolean>  renderAddBtn    = 
		new XUIViewBindProperty<Boolean>( "renderAddBtn", this, true, Boolean.class );

	/**
	 * Whether or nor the default "Remove" button should be rendered or not
	 */
	private XUIViewBindProperty<Boolean>  renderRemoveBtn    = 
		new XUIViewBindProperty<Boolean>( "renderRemoveBtn", this, true, Boolean.class );
	
	/**
	 * Whether or nor the default "Create New" button should be rendered or not
	 */
	private XUIViewBindProperty<Boolean>  renderCreateNewBtn    = 
		new XUIViewBindProperty<Boolean>( "renderCreateNewBtn", this, true, Boolean.class );

	
	public boolean getRenderAddBtn() {
		return renderAddBtn.getEvaluatedValue();
	}

	public void setRenderAddBtn( String expression ) {
		this.renderAddBtn.setExpressionText( expression );
	}

	public boolean getRenderRemoveBtn() {
		return renderRemoveBtn.getEvaluatedValue();
	}

	public void setRenderRemoveBtn(String expression) {
		this.renderRemoveBtn.setExpressionText( expression );
	}

	public boolean getRenderCreateNewBtn() {
		return renderCreateNewBtn.getEvaluatedValue();
	}

	public void setRenderCreateNewBtn(String expression ) {
		this.renderCreateNewBtn.setExpressionText( expression );
	}
	
	@Deprecated
	public void setRenderCreateNew(String expression ) {
		this.renderCreateNewBtn.setExpressionText( expression );
	}

	public void setTargetObject( String sExprText ) {
		this.targetObject.setExpressionText( sExprText );
	}
	
	public boObject getTargetObject() {
		return this.targetObject.getEvaluatedValue();
	}
	
	@Override
	public String getRendererType() {
		return "toolBar";
	}

    public void setBridgeName(String sExpressionText ) {
        this.bridgeName.setExpressionText( sExpressionText );
    }

    public String getBridgeName() {
    	if (this.bridgeName.getEvaluatedValue() == null)
    	{
    		Bridge bridge = (Bridge) this.getParent();
    		if (bridge != null){
    			if (bridge.getBridgeName() != null)
    				return bridge.getBridgeName();
    		}
    	}
    	return this.bridgeName.getEvaluatedValue();
    }
	
	
	@Override
	public void initComponent() {
		
		super.initComponent();
		createToolBar();
	}
	
	public void createToolBar() {
		
		Menu menu;
		List<UIComponent> children = getChildren();
		
		menu = addAddMenu();
		if( menu != null ) {
			children.add( currentMenuPos++, menu );
			children.add( currentMenuPos++, Menu.getMenuSpacer() );
		}

		menu = createViewerBeanMethod(  "remove", 
			XEOComponentMessages.BRIDGETB_REMOVE.toString(), 
			XEOComponentMessages.BRIDGETB_REMOVE_SELECTED.toString(),
			"ext-xeo/images/menus/remover-bridge.gif", 
			getParent().getClientId( getFacesContext() ), 
			"removeFromBridge", "self")
		;
	
		menu = addCreateNew();
		if( menu != null ) {
			children.add( currentMenuPos++, Menu.getMenuSpacer() );
			children.add( currentMenuPos++, menu );
			children.add( currentMenuPos++, Menu.getMenuSpacer() );
		}
	}
	
	@Override
	public void preRender() {
		
		//boolean renderToolBar = false;
		
		boObject 		targetObject = getTargetObject();
		bridgeHandler 	targetBridge = targetObject.getBridge( getBridgeName() );
		
		boolean separatorRendered = false;
		boolean isMySeparator 	  = false;
		for( UIComponent comp : getChildren() ) {
			if( comp instanceof ViewerMethod ) {
				isMySeparator = true; 
				separatorRendered = false;
				ViewerMethod viewerMethod = (ViewerMethod)comp;
				if( "addNewToBridge".equals( viewerMethod.getTargetMethod() ) ) {
					separatorRendered = getRenderCreateNewBtn() && XEOComponentStateLogic.isBridgeNewVisible( targetBridge ); 
					viewerMethod.setVisible( Boolean.toString( separatorRendered ));
					viewerMethod.setDisabled( XEOComponentStateLogic.isBridgeNewEnabled(targetBridge) );
				}				
				else if( "lookupBridge".equals( viewerMethod.getTargetMethod() ) ) {
					separatorRendered = getRenderAddBtn() && XEOComponentStateLogic.isBridgeAddVisible( targetBridge );
					viewerMethod.setVisible( Boolean.toString( separatorRendered ) );
					viewerMethod.setDisabled( XEOComponentStateLogic.isBridgeAddEnabled(targetBridge) );
				}
				else if( "removeFromBridge".equals( viewerMethod.getTargetMethod() ) ) {
					separatorRendered = getRenderRemoveBtn() && XEOComponentStateLogic.isBridgeRemoveVisible( targetBridge ); 
					viewerMethod.setVisible( Boolean.toString( separatorRendered ));
					viewerMethod.setDisabled( XEOComponentStateLogic.isBridgeRemoveEnabled(targetBridge) );
				}
			}
			else if ( isMySeparator && comp instanceof Menu ) {
				if( "-".equals( ((Menu) comp).getText() ) ) {
					((Menu) comp).setVisible( Boolean.toString( separatorRendered ) );
				}
			}
			else {
				isMySeparator = false;
			}
		}
		//setRendered( renderToolBar );
		
	}
	
	private Menu addCreateNew() {
		ViewerMethod rootMenu;
		boDefHandler subClasseDef;

		
		rootMenu = null;

		boObject 		targetObject = getTargetObject();
		bridgeHandler 	targetBridge = targetObject.getBridge( getBridgeName() );
		
		if( targetBridge.getDefAttribute().getChildIsOrphan() ) {
			
			List<boDefHandler> subClassesDef = getObjectsForNewMenu();
			
			if( subClassesDef.size() > 0 ) {
				subClasseDef = subClassesDef.get( 0 );
				rootMenu = new ViewerMethod();
				rootMenu.setText( XEOComponentMessages.BRIDGETB_NEW.toString("") );
				rootMenu.setToolTip(  XEOComponentMessages.BRIDGETB_NEW.toString( subClasseDef.getLabel() ) );
				rootMenu.setId( getId() + "_new_" + subClasseDef.getName() );
				rootMenu.setIcon( "resources/" + subClasseDef.getName() + "/ico16.gif" );
				rootMenu.setValue( subClasseDef.getName() );
				rootMenu.setTargetMethod("addNewToBridge");
				
				if( subClassesDef.size() > 1 ) {
					ViewerMethod viewerMethod = new ViewerMethod();
					viewerMethod.setText( XEOComponentMessages.BRIDGETB_NEW.toString( subClasseDef.getLabel() ) );
					viewerMethod.setToolTip(  XEOComponentMessages.BRIDGETB_NEW.toString( subClasseDef.getLabel() ) );
					viewerMethod.setId( getId() + "_new1_" + subClasseDef.getName() );
					viewerMethod.setIcon( "resources/" + subClasseDef.getName() + "/ico16.gif" );
					viewerMethod.setValue( subClasseDef.getName() );
					viewerMethod.setTargetMethod("addNewToBridge");

					rootMenu.getChildren().add( viewerMethod );
					
					for( int i=1; i < subClassesDef.size(); i++ ) {
						subClasseDef = subClassesDef.get( i );
						viewerMethod = new ViewerMethod();
						viewerMethod.setIcon( "resources/" + subClasseDef.getName() + "/ico16.gif" );
						viewerMethod.setText(  XEOComponentMessages.BRIDGETB_NEW.toString( subClasseDef.getLabel() ) );
						
						viewerMethod.setId( getId() + "_new_" + subClasseDef.getName() );
						viewerMethod.setTargetMethod( "addNewToBridge" );
						viewerMethod.setValue( subClasseDef.getName() );
						rootMenu.getChildren().add( viewerMethod );
					}
				}
			}
		}
		return rootMenu;
	}

	private Menu addAddMenu() {
		
		ViewerMethod rootMenu;
		
		rootMenu = null;
		
		List<boDefHandler> subClassesDef = getObjectsForAddMenu();
		
		boDefHandler subClasseDef;
		
		if( subClassesDef.size() > 0 ) {
			subClasseDef = subClassesDef.get( 0 );
			
			rootMenu = new ViewerMethod();
			rootMenu.setText( XEOComponentMessages.BRIDGETB_ADD.toString("") );
			rootMenu.setToolTip( XEOComponentMessages.BRIDGETB_ADD.toString( subClasseDef.getLabel() ) );
			rootMenu.setIcon( "resources/" + subClasseDef.getName() + "/ico16.gif" );
			rootMenu.setId( getId() + "_add_" + subClasseDef.getName() );
			rootMenu.setValue( subClasseDef.getName() );
			rootMenu.setTargetMethod( "lookupBridge" );
	
			if( subClassesDef.size() > 1 ) {
				
				ViewerMethod viewerMethod = new ViewerMethod();
				viewerMethod.setText( subClasseDef.getLabel() );
				viewerMethod.setToolTip(XEOComponentMessages.BRIDGETB_ADD.toString( subClasseDef.getLabel() ));
				viewerMethod.setIcon( "resources/" + subClasseDef.getName() + "/ico16.gif" );
				viewerMethod.setId( getId() + "_add1_" + subClasseDef.getName() );
				viewerMethod.setValue( subClasseDef.getName() );
				viewerMethod.setTargetMethod( "lookupBridge" );

				rootMenu.getChildren().add( viewerMethod );

				for( int i=1; i < subClassesDef.size(); i++ ) {
					subClasseDef = subClassesDef.get( i );
					viewerMethod = new ViewerMethod();
					viewerMethod.setId( getId() + "_add_" + subClasseDef.getName() );
					viewerMethod.setIcon( "resources/" + subClasseDef.getName() + "/ico16.gif" );
					viewerMethod.setText( subClasseDef.getLabel() );
					viewerMethod.setTargetMethod( "lookupBridge" );
					viewerMethod.setValue( subClasseDef.getName() );
					rootMenu.getChildren().add( viewerMethod );
				}
			}
		}
		return rootMenu;
		
	}
	
	private List<boDefHandler> getObjectsForAddMenu() {
		boObject 		targetObject = getTargetObject();
		bridgeHandler 	targetBridge = targetObject.getBridge( getBridgeName() );
		
		boDefAttribute	defAttribute = targetBridge.getDefAttribute(); 
		boDefHandler refDef = defAttribute.getReferencedObjectDef();

		List<boDefHandler> subClassesDef = new ArrayList<boDefHandler>(1);

		if( defAttribute.getObjects() != null && defAttribute.getObjects().length > 0 ) {
			for( boDefHandler def : Arrays.asList( defAttribute.getObjects() ) ) {
				subClassesDef.add( def );
			}
		}
		else {
			boDefHandler[] subDefs = refDef.getTreeSubClasses();
			subClassesDef.add( refDef );
			for( boDefHandler subDef : subDefs ) { 
				subClassesDef.add( subDef );
			}
		}
		return subClassesDef;
	}

	private List<boDefHandler> getObjectsForNewMenu() {
		boObject 		targetObject = getTargetObject();
		bridgeHandler 	targetBridge = targetObject.getBridge( getBridgeName() );
		
		boDefAttribute	defAttribute = targetBridge.getDefAttribute(); 
		boDefHandler refDef = defAttribute.getReferencedObjectDef();

		List<boDefHandler> subClassesDef = new ArrayList<boDefHandler>(1);
		if( defAttribute.getChildIsOrphan() ) {
			if( defAttribute.getObjects() != null && defAttribute.getObjects().length > 0 ) {
				for( boDefHandler def : Arrays.asList( defAttribute.getObjects() ) ) {
					if( def.getBoCanBeOrphan() )
						subClassesDef.add( def );
				}
			}
			else {
				return ListToolBar.getObjectsForNewMenu( refDef );
//				boDefHandler[] subDefs = refDef.getTreeSubClasses();
//				if( refDef.getBoCanBeOrphan() )
//					subClassesDef.add( refDef );
//				for( boDefHandler subDef : subDefs ) { 
//					if( subDef.getBoCanBeOrphan() )
//						subClassesDef.add( subDef );
//				}
			}
		}
		return subClassesDef;
	}
	
	private Menu createViewerBeanMethod( String id, String label, String toolTip, String icon, String value, String action, String target ) {
		ViewerMethod toolBarOpt;
		toolBarOpt = new ViewerMethod();
		toolBarOpt.setId( getId() + "_" + id );
		
		if( target != null ) {
			toolBarOpt.setTarget( target );
		}
		toolBarOpt.setValue( value );
		toolBarOpt.setText( label );
		toolBarOpt.setTargetMethod( action );
		toolBarOpt.setIcon( icon );
		toolBarOpt.setValue( findParentComponent(GridPanel.class).getClientId() );
		getChildren().add( currentMenuPos++, toolBarOpt );
		
		return toolBarOpt;
	}

}
