package netgest.bo.xwc.xeo.components;

import netgest.bo.xwc.components.classic.GridPanel;


public class LookupList extends List {
	
	@Override
	public String getRendererType() {
		return "gridPanel";
	}
	
	@Override
	public void applyComponentProperties() {
		
		if( getStateProperty("dataSource").isDefaultValue() )
			super.setDataSource( "#{viewBean.dataList}" );

		if( super.getStateProperty( "rowSelectionMode" ).isDefaultValue() )
			setRowSelectionMode( GridPanel.SELECTION_MULTI_ROW );
		
		if( super.getStateProperty( "onRowDoubleClick" ).isDefaultValue() )
			setOnRowDoubleClick( "#{viewBean.rowDoubleClick}" );
		
	}
	
	@Override
	public void createToolBar(int pos) {
		LookupListToolBar toolBar = new LookupListToolBar();
		getChildren().add(pos, toolBar );
	}

	
	
}