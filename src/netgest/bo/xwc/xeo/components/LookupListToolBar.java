package netgest.bo.xwc.xeo.components;
import java.util.Arrays;
import java.util.List;

import netgest.bo.runtime.boObject;
import netgest.bo.xwc.components.classic.ToolBar;
import netgest.bo.xwc.components.connectors.XEOObjectListConnector;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class LookupListToolBar extends ListToolBar {

	XUIBindProperty<XEOObjectListConnector> 	targetList = 
		new XUIBindProperty<XEOObjectListConnector>("targetList", this ,XEOObjectListConnector.class, "#{viewBean.dataList}" );
		
	@Override
	public String getRendererType() {
		return "toolBar";
	}
	
	public static final List<String> staticMethods = Arrays.asList(
			new String[] {"cofirmar","cancelar" }
		);

	public void setTargetList( String expressionString ) {
		this.targetList.setExpressionText( expressionString );
	}
	
	public XEOObjectListConnector getTargetList() {
		return this.targetList.getEvaluatedValue();
	}
	
	@Override
	public void initComponent() { 
		// Render ToolBar Methods
		
		getChildren().add( Menu.getMenuSpacer() );
		createViewerBeanMethod( "Confirmar", "Confirmar","ext-xeo/images/menus/confirmar.gif", "confirm", null );
		getChildren().add( Menu.getMenuSpacer() );
		createViewerBeanMethod( "Cancelar", "Cancelar" , "ext-xeo/images/menus/applications.gif", "canCloseTab", null );
		//getChildren().add( Menu.getMenuSpacer() );
		
		super.initComponent();
	}
	
	private ViewerMethod createViewerBeanMethod( String label, String toolTip, String icon, String methodName, String target ) {
		ViewerMethod xeoMethod;
	
		xeoMethod = new ViewerMethod();
		xeoMethod.setId( getId() + "_" + methodName );
		if( target != null ) {
			xeoMethod.setTarget( target );
		}
		xeoMethod.setText( label );
		xeoMethod.setTargetMethod( methodName );
		xeoMethod.setIcon( icon );
		xeoMethod.setToolTip( toolTip );
		getChildren().add( xeoMethod );
	
		return xeoMethod;
	}
	
	@Override
	public void preRender() {
		super.preRender();
	}
}
