package netgest.bo.xwc.xeo.components;

import netgest.bo.runtime.boObject;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.ToolBar;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIStateBindProperty;

public class Bridge extends GridPanel {

	private XUIBindProperty<boObject> 	targetObject 			= 
		new XUIBindProperty<boObject>("targetObject", this, boObject.class, "#{viewBean.XEOObject}" );
		
	private XUIStateBindProperty<String>  bridgeName 	= 
		new XUIStateBindProperty<String>( "bridgeName", this, String.class );

	private XUIBindProperty<Boolean> renderToolBar = 
		new XUIBindProperty<Boolean>("renderToolBar", this, true, Boolean.class);
	
	public boolean getRenderToolBar() {
		return renderToolBar.getEvaluatedValue();
	}

	public void setRenderToolBar(boolean renderEditToolbar) {
		this.renderToolBar.setValue( renderEditToolbar );
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
    
    public String getBridgeName(  ) {
        if ( this.bridgeName.getValue().isLiteralText() ) {
            return String.valueOf( this.bridgeName.getValue().getExpressionString() );
        }
        return (String)this.bridgeName.getValue().getValue( getELContext() );
    }
	
	@Override
	public void initComponent() {
		
		if( getStateProperty( "objectAttribute" ).isDefaultValue() )
			this.setObjectAttribute( getBridgeName() );

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
