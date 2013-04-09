package netgest.bo.xwc.xeo.components;

import javax.faces.component.UIComponent;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.ToolBar;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.XEOObjectListConnector;
import netgest.bo.xwc.components.model.Columns;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIViewBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.xeo.workplaces.admin.localization.ExceptionMessage;

/**
 * 
 * Creates a list of XEO Objects to display in a form
 * 
 * @author jcarreira
 *
 */
public class List extends GridPanel {
	
	/**
	 * The list of XEO Objects to display
	 */
	private XUIBindProperty<XEOObjectListConnector> targetList = 
		new XUIBindProperty<XEOObjectListConnector>("targetList", this, XEOObjectListConnector.class);
	
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
		try{
			return targetList.getEvaluatedValue();
		}
		catch (Exception e)
		{
			DataListConnector connector = this.getDataSource();
			if (connector instanceof XEOObjectListConnector)
				return ((XEOObjectListConnector) connector);
		}
		
		throw new RuntimeException(ExceptionMessage.THERE_ISNT_A_VALID_LIST__.toString()+" "+ExceptionMessage.NOT_AN_INSTANCE_OF_XEOOBJECTLISTCONNECTOR.toString());
	}

	public void setTargetList(String targetListExpr) {
		this.targetList.setExpressionText(targetListExpr);
	}
	
	public boolean getRenderToolBar() {
		return renderToolBar.getEvaluatedValue();
	}

	public void setRenderToolBar(boolean renderToolbar) {
		this.renderToolBar.setValue( renderToolbar );
	}
	
	@Override
	public String getRendererType() {
		return super.getRendererType( );
	}
	
	@Override
	public void initComponent() {
		
		if( this.targetList.isDefaultValue() )
			this.setTargetList( "#{" + getBeanId() + ".dataList}" );
		
		applyComponentProperties();
		
		if( getRenderIconColumn() ) {
			addIconColumn();
		}
		createToolBar( 0 );
		super.initComponent();
		
		java.util.List<UIComponent> children = getChildren();
		for (UIComponent child : children){
			setBeanIdOnChildren((XUIComponentBase)child, getBeanId());
		}
	}
	
	private void setBeanIdOnChildren(XUIComponentBase comp, String beanId){
		comp.setBeanId(beanId);
		java.util.List<UIComponent> children = comp.getChildren();
		for (UIComponent child : children){
			if (child instanceof XUIComponentBase)
				setBeanIdOnChildren(((XUIComponentBase)child),beanId);
		}
	}
	
	public void applyComponentProperties() {
		if( super.getStateProperty( "dataSource" ).isDefaultValue() )
			setDataSource( this.targetList.getExpressionString() );

		if( super.getStateProperty( "rowSelectionMode" ).isDefaultValue() )
			setRowSelectionMode( GridPanel.SELECTION_CELL );
		
		if( super.getStateProperty( "onRowDoubleClick" ).isDefaultValue() )
			setOnRowDoubleClick( "#{"+getBeanId()+".rowDoubleClick}" );
	}
	
	public void addIconColumn() {
		Columns c = (Columns)findComponent(Columns.class);

//    	<xvw:columnAttribute width='10'  label='&nbsp;' hideable='false' sortable='false' groupable='false' searchable='false' dataField='SYS_OBJECT_ICON_16'/>
		ColumnAttribute iconColumn = new ColumnAttribute();
		iconColumn.setLabel("Icn");
		iconColumn.setWidth("25");
		iconColumn.setResizable("false");
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
