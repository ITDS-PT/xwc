package netgest.bo.xwc.xeo.components;

import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;

import netgest.bo.runtime.boObject;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.xwc.components.annotations.RequiredAlways;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.ToolBar;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.FilterTerms;
import netgest.bo.xwc.components.connectors.SortTerms;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIViewBindProperty;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

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
		new XUIBindProperty<boObject>("targetObject", this, boObject.class);
		
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
	
	/**
	 * Whether or not bridge ordering is activ
	 */
	private XUIViewBindProperty<Boolean> enableBridgeReorder = 
		new XUIViewBindProperty<Boolean>("enableBridgeReorder", this, false, Boolean.class);
	
	/**
     * Retrieves the list of bouis to show for the favorites
     */
    private XUIViewStateBindProperty<List<Long>> listFavorites = 
    	new XUIViewStateBindProperty<List<Long>>( "listFavorites", this, List.class);
    
    
    public Bridge(){
    	super();
    	
    }
    
    /**
     * 
     * Retrieves a list of bouis to show as favorites
     * 
     * @return A list of bouis
     */
    public List<Long> getListFavorites(){
    	return this.listFavorites.getEvaluatedValue();
    }
    
    public void setListFavorites(String lstFavoritesExpr){
    	this.listFavorites.setExpressionText(lstFavoritesExpr);
    }
	
	public boolean getRenderToolBar() {
		return renderToolBar.getEvaluatedValue();
	}

	public void setRenderToolBar(boolean renderEditToolbar) {
		this.renderToolBar.setValue( renderEditToolbar );
	}

	public void setRenderToolBar(String renderEditToolbarExpr ) {
		this.renderToolBar.setExpressionText( renderEditToolbarExpr );
	}
	
	public boolean getEnableBridgeReorder() {
		return enableBridgeReorder.getEvaluatedValue();
	}

	public void setEnableBridgeReorder(String enableBridgeReorder) {
		this.enableBridgeReorder.setExpressionText(enableBridgeReorder);
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
		
		if( targetObject.isDefaultValue() ) 
			this.setTargetObject("#{" + getBeanId() + ".XEOObject}");
		
		if( getStateProperty("rowSelectionMode").isDefaultValue() )
			this.setRowSelectionMode( GridPanel.SELECTION_MULTI_ROW );
		
		if( getStateProperty( "dataSource" ).isDefaultValue() )
			this.setDataSource( "#{"+getBeanId()+".currentData." + getBridgeName() + ".dataList}" );
		
		if( getStateProperty( "rowClass" ).isDefaultValue() )
			this.setRowClass( "#{"+getBeanId()+".rowClass}" );

		if( getStateProperty( "onRowDoubleClick" ).isDefaultValue() )
			this.setOnRowDoubleClick( "#{"+getBeanId()+".editBridge}" );
		
		if( getStateProperty( "rowDblClickTarget" ).isDefaultValue() )
			this.setRowDblClickTarget("self");
		
		if( getStateProperty( "objectAttribute" ).isDefaultValue() )
			this.setObjectAttribute( getBridgeName() );
		
		if( getRenderToolBar() )
			createToolbar();
    	
		
        super.initComponent();
        
        //Set the beanId for all children that are dynamically created
		List<UIComponent> children = getChildren();
		for (UIComponent child : children){
			setBeanIdOnChildren((XUIComponentBase)child, getBeanId());
		}
	}
	
	private void setBeanIdOnChildren(XUIComponentBase comp, String beanId){
		comp.setBeanId(beanId);
		List<UIComponent> children = comp.getChildren();
		for (UIComponent child : children){
			if (child instanceof XUIComponentBase)
				setBeanIdOnChildren(((XUIComponentBase)child),beanId);
		}
	}
	
	private ToolBar createToolbar() {
		BridgeToolBar bridgeToolbar  = new BridgeToolBar();
		bridgeToolbar.setId( getId() + "_bridgeToolbar" );
		bridgeToolbar.setTargetObject( this.targetObject.getExpressionString() );
		bridgeToolbar.setBridgeName( getBridgeName() );
		bridgeToolbar.setRenderOrderBridgeBtn( new Boolean(getEnableBridgeReorder()).toString() );
        getChildren().add( bridgeToolbar );        
		return bridgeToolbar;
		
	}
	
}
