package netgest.bo.xwc.xeo.components;

import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.ToolBar;
import netgest.bo.xwc.components.connectors.XEOObjectListConnector;
import netgest.bo.xwc.components.model.Columns;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIViewBindProperty;

/**
 * 
 * Creates a list of XEO Objects to display in a form
 * 
 * @author João Carreira
 *
 */
public class List extends GridPanel {
	
	/**
	 * The list of XEO Objects to display
	 */
	private XUIBindProperty<XEOObjectListConnector> targetList = 
		new XUIBindProperty<XEOObjectListConnector>("targetList", this, XEOObjectListConnector.class, "#{viewBean.dataList}" );
	
	/**
	 * Whether or not the default {@link ListToolBar} should be rendered
	 */
	private XUIViewBindProperty<Boolean> renderToolBar = 
		new XUIViewBindProperty<Boolean>("renderToolBar", this, true, Boolean.class);
	
	/**
	 * Whether or not the column with the icon relative to each object type 
	 * (XEOModel) should be rendered in the list
	 */
	private XUIViewBindProperty<Boolean> renderIconColumn = 
		new XUIViewBindProperty<Boolean>("renderIconColumn", this, true, Boolean.class);
	
	
	public boolean getRenderIconColumn() {
		return this.renderIconColumn.getEvaluatedValue();
	}

	public void setRenderIconColumn( boolean renderObjectIcon ) {
		this.renderIconColumn.setValue( renderObjectIcon );
	}
	
	public XEOObjectListConnector getTargetList() {
		return targetList.getEvaluatedValue();
	}

	public void setTargetList(boolean renderEditToolbar) {
		this.targetList.setValue( renderEditToolbar );
	}
	
	public boolean getRenderToolBar() {
		return renderToolBar.getEvaluatedValue();
	}

	public void setRenderToolBar(boolean renderToolbar) {
		this.renderToolBar.setValue( renderToolbar );
	}
	
	@Override
	public String getRendererType() {
		return "gridPanel";
	}
	
	@Override
	public void initComponent() {
		super.initComponent();
		
		applyComponentProperties();
		
		if( getRenderIconColumn() ) {
			addIconColumn();
		}

		createToolBar( 0 );
		
	}
	
	public void applyComponentProperties() {
		if( super.getStateProperty( "dataSource" ).isDefaultValue() )
			setDataSource( this.targetList.getExpressionString() );

		if( super.getStateProperty( "rowSelectionMode" ).isDefaultValue() )
			setRowSelectionMode( GridPanel.SELECTION_CELL );
		
		if( super.getStateProperty( "onRowDoubleClick" ).isDefaultValue() )
			setOnRowDoubleClick( "#{viewBean.rowDoubleClick}" );
	}
	
	public void addIconColumn() {
		Columns c = (Columns)findComponent(Columns.class);

//    	<xvw:columnAttribute width='10'  label='&nbsp;' hideable='false' sortable='false' groupable='false' searchable='false' dataField='SYS_OBJECT_ICON_16'/>
		ColumnAttribute iconColumn = new ColumnAttribute();
		iconColumn.setLabel("&nbsp;");
		iconColumn.setWidth("20");
		iconColumn.setHideable( "false" );
		iconColumn.setSortable( "false" );
		iconColumn.setGroupable( "false" );
		iconColumn.setSearchable( "false" );
		iconColumn.setDataField( "SYS_OBJECT_ICON_16" );
		iconColumn.setContentHtml( true );
		
		c.getChildren().add( 0, iconColumn );
		
		
	}
	
	public void createToolBar( int pos ) {
		ToolBar toolBar;
		toolBar = new ListToolBar();
		toolBar.setVisible( renderToolBar.getExpressionString() );
		toolBar.setId( getId() + "_listToolBar" );
		getChildren().add( pos, toolBar );
	}
	
}
