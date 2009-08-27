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
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.xeo.components.utils.XEOComponentStateLogic;

public class BridgeToolBar extends ToolBar {
	
	private XUIBindProperty<boObject> 	targetObject 	= 
		new XUIBindProperty<boObject>("targetObject", this, boObject.class, "#{viewBean.XEOObject}" );

	private XUIBindProperty<String>  bridgeName    = 
		new XUIBindProperty<String>( "bridgeName", this, String.class );
	
	private XUIBindProperty<Boolean>  renderAddBtn    = 
		new XUIBindProperty<Boolean>( "renderAddBtn", this, true, Boolean.class );

	private XUIBindProperty<Boolean>  renderRemoveBtn    = 
		new XUIBindProperty<Boolean>( "renderRemoveBtn", this, true, Boolean.class );
	
	private XUIBindProperty<Boolean>  renderCreateNewBtn    = 
		new XUIBindProperty<Boolean>( "renderCreateNewBtn", this, true, Boolean.class );

	
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
		
		if( getRenderAddBtn() ) {
			menu = addAddMenu();
			if( menu != null ) {
				children.add( menu );
				children.add( Menu.getMenuSpacer() );
			}
		}
		
		if( getRenderRemoveBtn() ) {
			menu = createViewerBeanMethod(  "remove", "Remover", "Remover selecionados","ext-xeo/images/menus/remover-bridge.gif", 
							getParent().getClientId( getFacesContext() ), 
							"removeFromBridge", "self");
	
			if( menu != null ) {
				children.add( menu );
				children.add( Menu.getMenuSpacer() );
			}
		}
		
		if( getRenderCreateNewBtn() ) {
			menu = addCreateNew();
			if( menu != null ) {
				children.add( menu );
				getChildren().add( Menu.getMenuSpacer() );
			}
		}
	}
	
	@Override
	public void preRender() {
		
		boolean renderToolBar = false;
		
		boObject 		targetObject = getTargetObject();
		bridgeHandler 	targetBridge = targetObject.getBridge( getBridgeName() );
		
		boolean separatorRendered = false;
		boolean isMySeparator 	  = false;
		for( UIComponent comp : getChildren() ) {
			if( comp instanceof ViewerMethod ) {
				isMySeparator = true;
				separatorRendered = false;
				ViewerMethod viewerMethod = (ViewerMethod)comp;
				if( "lookupBridge".equals( viewerMethod.getTargetMethod() ) ) {
					separatorRendered = XEOComponentStateLogic.isBridgeAddVisible( targetBridge );
					viewerMethod.setVisible( Boolean.toString( separatorRendered ) );
					if( separatorRendered )
						renderToolBar = true;
				}
				else if( "removeFromBridge".equals( viewerMethod.getTargetMethod() ) ) {
					separatorRendered = XEOComponentStateLogic.isBridgeRemoveVisible( targetBridge ); 
					viewerMethod.setVisible( Boolean.toString( separatorRendered ));
					if( separatorRendered )
						renderToolBar = true;
				}
				else if( "addNewToBridge".equals( viewerMethod.getTargetMethod() ) ) {
					separatorRendered = XEOComponentStateLogic.isBridgeNewVisible( targetBridge ); 
					viewerMethod.setVisible( Boolean.toString( separatorRendered ));
					if( separatorRendered )
						renderToolBar = true;
				}
			}
			else if ( isMySeparator && comp instanceof Menu ) {
				if( "-".equals( ((Menu) comp).getText() ) ) {
					comp.setRendered( separatorRendered );
				}
			}
			else {
				renderToolBar = true;
				isMySeparator = false;
			}
		}
		setRendered( renderToolBar );
		
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
				rootMenu.setText( "Novo(a) " );
				rootMenu.setToolTip("Novo(a) " + subClasseDef.getLabel() );
				rootMenu.setId( getId() + "_new_" + subClasseDef.getName() );
				rootMenu.setIcon( "resources/" + subClasseDef.getName() + "/ico16.gif" );
				rootMenu.setValue( subClasseDef.getName() );
				rootMenu.setTargetMethod("addNewToBridge");
				
				if( subClassesDef.size() > 1 ) {
					ViewerMethod viewerMethod = new ViewerMethod();
					viewerMethod.setText( "Novo(a) " + subClasseDef.getLabel() );
					viewerMethod.setToolTip("Novo(a) " + subClasseDef.getLabel() );
					viewerMethod.setId( getId() + "_new1_" + subClasseDef.getName() );
					viewerMethod.setIcon( "resources/" + subClasseDef.getName() + "/ico16.gif" );
					viewerMethod.setValue( subClasseDef.getName() );
					viewerMethod.setTargetMethod("addNewToBridge");

					rootMenu.getChildren().add( viewerMethod );
					
					for( int i=1; i < subClassesDef.size(); i++ ) {
						subClasseDef = subClassesDef.get( i );
						viewerMethod = new ViewerMethod();
						viewerMethod.setIcon( "resources/" + subClasseDef.getName() + "/ico16.gif" );
						viewerMethod.setText( "Novo(a) " + subClasseDef.getLabel() );
						
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
			rootMenu.setText( " Adicionar " );
			rootMenu.setToolTip("Adicionar " + subClasseDef.getLabel() );
			rootMenu.setIcon( "resources/" + subClasseDef.getName() + "/ico16.gif" );
			rootMenu.setId( getId() + "_add_" + subClasseDef.getName() );
			rootMenu.setValue( subClasseDef.getName() );
			rootMenu.setTargetMethod( "lookupBridge" );
	
			if( subClassesDef.size() > 1 ) {
				
				ViewerMethod viewerMethod = new ViewerMethod();
				viewerMethod.setText( subClasseDef.getLabel() );
				viewerMethod.setToolTip("Adicionar " + subClasseDef.getLabel() );
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
				boDefHandler[] subDefs = refDef.getTreeSubClasses();
				if( refDef.getBoCanBeOrphan() )
					subClassesDef.add( refDef );
				for( boDefHandler subDef : subDefs ) { 
					if( subDef.getBoCanBeOrphan() )
						subClassesDef.add( subDef );
				}
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
		getChildren().add( toolBarOpt );
		
		return toolBarOpt;
	}

}