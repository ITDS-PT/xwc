package netgest.bo.xwc.xeo.components;

import netgest.bo.runtime.boObject;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.xwc.components.annotations.RequiredAlways;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.ToolBar;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIViewBindProperty;

/**
 * 
 * A component to render a bridge (see {@link bridgeHandler} between
 * XEO Objects 
 * 
 * @author jcarreira
 *
 */
public class Bridge extends GridPanel {

	/**
	 * The target XEO Object {@link boObject} from which 
	 * the bridge should be selected
	 */
	private XUIBindProperty<boObject> 	targetObject 			= 
		new XUIBindProperty<boObject>("targetObject", this, boObject.class, "#{viewBean.XEOObject}" );
		
	/**
	 * The name of the bridge to select from the 
	 * <code>targetObject</code> property
	 */
	@RequiredAlways
	private XUIBindProperty<String>  bridgeName 	= 
		new XUIBindProperty<String>( "bridgeName", this, String.class );

	/**
	 * Whether or not the default {@link BridgeToolBar} should be rendered
	 */
	private XUIViewBindProperty<Boolean> renderToolBar = 
		new XUIViewBindProperty<Boolean>("renderToolBar", this, true, Boolean.class);
	
	public boolean getRenderToolBar() {
		return renderToolBar.getEvaluatedValue();
	}

	public void setRenderToolBar(boolean renderEditToolbar) {
		this.renderToolBar.setValue( renderEditToolbar );
	}

	public void setRenderToolBar(String renderEditToolbarExpr ) {
		this.renderToolBar.setExpressionText( renderEditToolbarExpr );
	}
	
	@Override
	public String getRendererType() {
		return "gridPanel";
	}
	
	public void setTargetObject( String sExprText ) {
		this.targetObject.setExpressionText( sExprText );
	}
	
	public boObject getTargetObject() {
		return this.targetObject.getEvaluatedValue();
	}
	
    public void setBridgeName(String sExpressionText ) {
        this.bridgeName.setValue( createValueExpression( sExpressionText, String.class ) );
    }
    
    public String getBridgeName(  ) 
    {
        /*if ( this.bridgeName.getValue().isLiteralText() ) 
        {
            return String.valueOf( this.bridgeName.getEvaluatedValue() );
        }*/
        return (String)this.bridgeName.getEvaluatedValue();
    }
	
	@Override
	public void initComponent() {
		
		if( getStateProperty("rowSelectionMode").isDefaultValue() )
			this.setRowSelectionMode( GridPanel.SELECTION_MULTI_ROW );
		
		if( getStateProperty( "dataSource" ).isDefaultValue() )
			this.setDataSource( "#{viewBean.currentData." + getBridgeName() + ".dataList}" );
		
		if( getStateProperty( "rowClass" ).isDefaultValue() )
			this.setRowClass( "#{viewBean.rowClass}" );

		if( getStateProperty( "onRowDoubleClick" ).isDefaultValue() )
			this.setOnRowDoubleClick( "#{viewBean.editBridge}" );
		
		if( getStateProperty( "rowDblClickTarget" ).isDefaultValue() )
			this.setRowDblClickTarget("self");
		
		if( getStateProperty( "objectAttribute" ).isDefaultValue() )
			this.setObjectAttribute( getBridgeName() );
		
		if( getRenderToolBar() )
			createToolbar();
    	
		
        super.initComponent();
	}
	
	private ToolBar createToolbar() {
		BridgeToolBar bridgeToolbar  = new BridgeToolBar();
		bridgeToolbar.setId( getId() + "_bridgeToolbar" );
		bridgeToolbar.setTargetObject( this.targetObject.getExpressionString() );
		bridgeToolbar.setBridgeName( getBridgeName() );
        getChildren().add( bridgeToolbar );        
		return bridgeToolbar;
		
	}
	
}
