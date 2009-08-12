package netgest.bo.xwc.xeo.components;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;

import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.ToolBar;
import netgest.bo.xwc.components.connectors.XEOObjectListConnector;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.xeo.components.utils.XEOComponentStateLogic;

public class ListToolBar extends ToolBar {

	private XUIBindProperty<XEOObjectListConnector> targetList = 
		new XUIBindProperty<XEOObjectListConnector>("targetList", this, XEOObjectListConnector.class, "#{viewBean.dataList}" );

	private XUIBindProperty<Boolean>  renderCreateNewBtn    = 
		new XUIBindProperty<Boolean>( "renderCreateNewBtn", this, Boolean.TRUE, Boolean.class );
	
//	private XUIBindProperty<Boolean>  renderDestroyBtn    = 
//		new XUIBindProperty<Boolean>( "renderDestroyBtn", this, Boolean.FALSE, Boolean.class );
	
	@Override
	public String getRendererType() {
		return "toolBar";
	}
	
	public void setTargetList( String expression ) {
		this.targetList.setExpressionText( expression );
	}
	
	public XEOObjectListConnector getTargetList() {
		return this.targetList.getEvaluatedValue();
	}

	public boolean getRenderCreateNewBtn() {
		return renderCreateNewBtn.getEvaluatedValue();
	}

	public void setRenderCreateNew(String expression ) {
		this.renderCreateNewBtn.setExpressionText( expression );
	}

//	public boolean getRenderDestroyBtn() {
//		return renderDestroyBtn.getEvaluatedValue();
//	}
//
//	public void setRenderDestroy(String expression ) {
//		this.renderDestroyBtn.setExpressionText( expression );
//	}
	
	@Override
	public void initComponent() {
		super.initComponent();
		
		createToolBar();
		
	}
	
	public void createToolBar() {
		Menu menu;
		List<UIComponent> children = getChildren();
		
		if( getRenderCreateNewBtn() ) {
			menu = addCreateNew();
			if( menu != null ) {
				getChildren().add( Menu.getMenuSpacer() );
				children.add( menu );
				getChildren().add( Menu.getMenuSpacer() );
			}
		}
//		if( getRenderDestroyBtn() ) {
//			menu = createViewerBeanMethod(  "remove", "Remover", "Remover selecionados","ext-xeo/images/menus/remover-bridge.gif", 
//							getParent().getClientId( getFacesContext() ), 
//							"removeFromBridge", "self");
//	
//			if( menu != null ) {
//				children.add( menu );
//				children.add( Menu.getMenuSpacer() );
//			}
//		}
	}
	
	@Override
	public void preRender() {
		super.preRender();
	}
	
	private Menu addCreateNew() {
		ViewerMethod rootMenu;
		boDefHandler subClasseDef;
		rootMenu = null;
		
		List<boDefHandler> subClassesDef = getObjectsForNewMenu();
		if( subClassesDef.size() > 0 ) {
			subClasseDef = subClassesDef.get( 0 );
			rootMenu = new ViewerMethod();
			rootMenu.setText( "Novo(a) " );
			rootMenu.setToolTip("Novo(a) " + subClasseDef.getLabel() );
			rootMenu.setId( getId() + "_new_" + subClasseDef.getName() );
			rootMenu.setIcon( "resources/" + subClasseDef.getName() + "/ico16.gif" );
			rootMenu.setValue( subClasseDef.getName() );
			rootMenu.setTargetMethod("addNew");
			
			if( subClassesDef.size() > 1 ) {
				ViewerMethod viewerMethod = new ViewerMethod();
				viewerMethod.setText( "Novo(a) " + subClasseDef.getLabel() );
				viewerMethod.setToolTip("Novo(a) " + subClasseDef.getLabel() );
				viewerMethod.setId( getId() + "_new1_" + subClasseDef.getName() );
				viewerMethod.setIcon( "resources/" + subClasseDef.getName() + "/ico16.gif" );
				viewerMethod.setValue( subClasseDef.getName() );
				viewerMethod.setTargetMethod("addNew");

				rootMenu.getChildren().add( viewerMethod );
				
				for( int i=1; i < subClassesDef.size(); i++ ) {
					subClasseDef = subClassesDef.get( i );
					viewerMethod = new ViewerMethod();
					viewerMethod.setIcon( "resources/" + subClasseDef.getName() + "/ico16.gif" );
					viewerMethod.setText( "Novo(a) " + subClasseDef.getLabel() );
					
					viewerMethod.setId( getId() + "_new_" + subClasseDef.getName() );
					viewerMethod.setTargetMethod( "addNew" );
					viewerMethod.setValue( subClasseDef.getName() );
					rootMenu.getChildren().add( viewerMethod );
				}
			}
		}
		return rootMenu;
	}

	private List<boDefHandler> getObjectsForNewMenu() {
		try {
			
			boDefHandler 	refDef 		= getTargetList().getObjectList().getBoDef();
			String			objectsType	= refDef.getName();
			
			List<boDefHandler> subClassesDef = new ArrayList<boDefHandler>(1);
			boDefHandler[] subDefs = refDef.getTreeSubClasses();
			if( refDef.getBoCanBeOrphan() )
				if( XEOComponentStateLogic.canCreateNew( objectsType ) ) {
					subClassesDef.add( refDef );
				}
				for( boDefHandler subDef : subDefs ) { 
					if( subDef.getBoCanBeOrphan() ) {
						if( XEOComponentStateLogic.canCreateNew( objectsType ) ) {
							subClassesDef.add( subDef );
						}
					}
				}
			return subClassesDef;
		} catch (boRuntimeException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException( e );
		}
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
