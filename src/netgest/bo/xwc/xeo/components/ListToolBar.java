package netgest.bo.xwc.xeo.components;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;

import netgest.bo.def.boDefHandler;
import netgest.bo.def.v2.boDefInterfaceImpl;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.classic.scripts.XVWServerActionWaitMode;
import netgest.bo.xwc.components.connectors.XEOObjectListConnector;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.xeo.components.utils.XEOComponentStateLogic;
import netgest.bo.xwc.xeo.localization.XEOComponentMessages;

/**
 * 
 * Default {@link ToolBar} for a {@link netgest.bo.xwc.xeo.components.List} component
 * includes the button to create a new instance object
 * 
 * @author jcarreira
 *
 */
public class ListToolBar extends ToolBarMenuPositions {

	/**
	 * The list of objects {@link boObject} that is shown in the 
	 * parent {@link netgest.bo.xwc.xeo.components.List} component 
	 */
	private XUIBindProperty<XEOObjectListConnector> targetList = 
		new XUIBindProperty<XEOObjectListConnector>("targetList", this, XEOObjectListConnector.class, null );

	/**
	 * Whether or not the "Create New" button (to add to the list) is rendered
	 */
	private XUIViewStateBindProperty<Boolean>  renderCreateNewBtn    = 
		new XUIViewStateBindProperty<Boolean>( "renderCreateNewBtn", this, "true", Boolean.class );
	
	@Override
	public String getRendererType() {
		return "toolBar";
	}
	
	public void setTargetList( String expression ) {
		this.targetList.setExpressionText( expression );
	}
	
	public XEOObjectListConnector getTargetList() {
		UIComponent parentList = getParent();
		if( this.targetList.getValue() == null ) {
			if (parentList instanceof netgest.bo.xwc.xeo.components.List)
				return  ((netgest.bo.xwc.xeo.components.List) parentList).getTargetList();
		}
		return this.targetList.getEvaluatedValue();
	}

	public boolean getRenderCreateNewBtn() {
		return renderCreateNewBtn.getEvaluatedValue();
	}
	
	@Deprecated
	public void setRenderCreateNew(String expression ) {
		this.renderCreateNewBtn.setExpressionText( expression );
	}
	
	public void setRenderCreateNewBtn(String expression ) {
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
		
		menu = addCreateNew();
		if( menu != null ) {
			menu.setVisible( renderCreateNewBtn.getExpressionString() );
			getChildren().add(  currentMenuPos++, Menu.getMenuSpacer( renderCreateNewBtn.getExpressionString() ) );
			children.add(  currentMenuPos++, menu );
			getChildren().add(  currentMenuPos++, Menu.getMenuSpacer( renderCreateNewBtn.getExpressionString() ) );
		}
	}
	
	@Override
	public void preRender() {
		super.preRender();
	}
	
	private Menu addCreateNew() {
		ViewerMethod rootMenu;
		boDefHandler subClasseDef;
		rootMenu = null;
		
		try {
			if( getTargetList().getObjectList().getBoDef().getBoCanBeOrphan() ) {
				List<boDefHandler> subClassesDef = getObjectsForNewMenu( getTargetList().getObjectList().getBoDef() );
				if( subClassesDef.size() > 0 ) {
					subClasseDef = subClassesDef.get( 0 );
					rootMenu = new ViewerMethod();
					rootMenu.setText( XEOComponentMessages.BRIDGETB_NEW.toString("") );
					rootMenu.setToolTip(XEOComponentMessages.BRIDGETB_NEW.toString(subClasseDef.getLabel()) );
					rootMenu.setId( getId() + "_new_" + subClasseDef.getName() );
					rootMenu.setIcon( "resources/" + subClasseDef.getName() + "/ico16.gif" );
					rootMenu.setValue( subClasseDef.getName() );
					//Only add target if we have a concrete object (not abstract nor interface)
					//so that we can't create those types
					if (subClasseDef.getClassType() == boDefHandler.TYPE_CLASS ){
						rootMenu.setServerActionWaitMode( XVWServerActionWaitMode.DIALOG.toString()  );
						rootMenu.setTargetMethod("addNew");
					}
					
					if( subClassesDef.size() > 1 ) {
						ViewerMethod viewerMethod = new ViewerMethod();
						if (subClasseDef.getClassType() == boDefHandler.TYPE_CLASS ){
							viewerMethod.setText( XEOComponentMessages.BRIDGETB_NEW.toString(subClasseDef.getLabel()) );
							
							viewerMethod.setToolTip( XEOComponentMessages.BRIDGETB_NEW.toString(subClasseDef.getLabel()) );
							viewerMethod.setId( getId() + "_new1_" + subClasseDef.getName() );
							viewerMethod.setIcon( "resources/" + subClasseDef.getName() + "/ico16.gif" );
							viewerMethod.setValue( subClasseDef.getName() );
							viewerMethod.setServerActionWaitMode( XVWServerActionWaitMode.DIALOG.toString()  );
							viewerMethod.setTargetMethod("addNew");
		
							rootMenu.getChildren().add( viewerMethod );
						}
						
						for( int i=1; i < subClassesDef.size(); i++ ) {
							subClasseDef = subClassesDef.get( i );
							viewerMethod = new ViewerMethod();
							viewerMethod.setIcon( "resources/" + subClasseDef.getName() + "/ico16.gif" );
							viewerMethod.setText( XEOComponentMessages.BRIDGETB_NEW.toString(subClasseDef.getLabel()) );
							
							viewerMethod.setId( getId() + "_new_" + subClasseDef.getName() );
							if (subClasseDef.getClassType() == boDefHandler.TYPE_CLASS ){
								viewerMethod.setTargetMethod( "addNew" );
								viewerMethod.setServerActionWaitMode( XVWServerActionWaitMode.DIALOG.toString()  );
							}
							viewerMethod.setValue( subClasseDef.getName() );
							rootMenu.getChildren().add( viewerMethod );
						}
					}
				}
			}
		} catch (boRuntimeException e) {
			throw new RuntimeException( e );
		}
		return rootMenu;
	}

	protected static List<boDefHandler> getObjectsForNewMenu( boDefHandler refDef ) {
		if(  boDefHandler.TYPE_INTERFACE == refDef.getClassType()  ) {
			String[] refBy = ((boDefInterfaceImpl)refDef).getImplObjects();
			List<boDefHandler> subClassesDef = new ArrayList<boDefHandler>(1);
			for( String ref : refBy ) {
				boDefHandler intDef = boDefHandler.getBoDefinition( ref );
				if( refDef != null ) {
					addToMenu( subClassesDef, intDef );
				}
			}
			return subClassesDef;
			
		}
		else {
			List<boDefHandler> subClassesDef = new ArrayList<boDefHandler>(1);
			addToMenu( subClassesDef, refDef );
			return subClassesDef;
		}
	}
	
	private static void addToMenu(List<boDefHandler>  subClassesDef ,boDefHandler refDef ) {
		boDefHandler[] subDefs = refDef.getTreeSubClasses();
		String		objectsType	= refDef.getName();
		if( refDef.getBoCanBeOrphan() ) {
			if( XEOComponentStateLogic.canCreateNew( objectsType ) ) {
				subClassesDef.add( refDef );
			}
		}
		for( boDefHandler subDef : subDefs ) { 
			if( subDef.getBoCanBeOrphan() ) {
				if( XEOComponentStateLogic.canCreateNew( objectsType ) ) {
					subClassesDef.add( subDef );
				}
			}
		}
	}
	
	
}
