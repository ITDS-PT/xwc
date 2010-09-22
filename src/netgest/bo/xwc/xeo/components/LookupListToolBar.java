package netgest.bo.xwc.xeo.components;
import java.util.Arrays;
import java.util.List;

import netgest.bo.xwc.components.classic.ToolBar;
import netgest.bo.xwc.components.connectors.XEOObjectListConnector;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.xeo.localization.XEOComponentMessages;

/**
 * 
 * The default {@link ToolBar} for a {@link LookupList}
 * Renders two buttons: A "Confirm" selection button and a "Cancel Button"
 * 
 * @author jcarreira
 *
 */
public class LookupListToolBar extends ListToolBar {
	
	/**
	 * Reference to the list of objects being shown in 
	 * the parent {@link LookupList} component
	 */
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
		
		getChildren().add( currentMenuPos++, Menu.getMenuSpacer() );
		createViewerBeanMethod( 
				XEOComponentMessages.LOOKUPLISTTB_CONFIRM.toString(), 
				XEOComponentMessages.LOOKUPLISTTB_CONFIRM_TTIP.toString(),
				"ext-xeo/images/menus/confirmar.gif", "confirm", null );
		getChildren().add( currentMenuPos++, Menu.getMenuSpacer() );
		createViewerBeanMethod( 
				XEOComponentMessages.LOOKUPLISTTB_CANCEL.toString(),
				XEOComponentMessages.LOOKUPLISTTB_CANCEL_TTIP.toString(), 
				"ext-xeo/images/menus/applications.gif", "canCloseTab", null );
		
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
		getChildren().add( currentMenuPos++, xeoMethod );
	
		return xeoMethod;
	}
	
	@Override
	public void preRender() {
		super.preRender();
	}
}
