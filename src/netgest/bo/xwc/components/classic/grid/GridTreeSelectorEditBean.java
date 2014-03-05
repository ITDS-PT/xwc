package netgest.bo.xwc.components.classic.grid;

import netgest.bo.xwc.components.classic.ColumnAttribute;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.grid.utils.DataFieldDecoder;
import netgest.bo.xwc.components.model.Column;
import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;
import netgest.bo.xwc.xeo.localization.BeansMessages;

public class GridTreeSelectorEditBean extends XEOBaseBean {

	private Column[] columns;
	private String gridPanel;
	private String columnAvailableList;
	private String columnSelectedList;
	
	public String getColumnSelectedList() {
		return columnSelectedList;
	}

	public void setColumnSelectedList(String columnSelectedList) {
		this.columnSelectedList = columnSelectedList;
	}

	public void setGridPanelId(String grid )
	{
		this.gridPanel = grid;	
	}

	public String getGridPanelId()
	{
		return this.gridPanel;	
	}
	
	public String getParentWindowId()
	{
		return "formPopup:win";	
	}
	
	public String getSubmitAction()
	{
		ScriptBuilder sb = new ScriptBuilder();
		sb.l("function() {");
			sb.l(String.format("ExtXeo.grid.selectColumns('%s','%s',tree2,root,root2, '%s', '%s')", 
					getGridPanelId(), getParentWindowId(),
					BeansMessages.TREE_SHUTTLE_COLUMN_SELECTION.toString(),
					BeansMessages.TREE_SHUTTLE_COLUMN_SELECTION_MUST_SELECT.toString()));
		sb.l("}");
		return sb.toString();
		
	}
	
	public void setColumns(Column[] cols)
	{
		this.columns = cols;
				
		StringBuffer availableCols = new StringBuffer();			
		StringBuffer selectedCols = new StringBuffer();
		
		if(cols != null)
		{
			boolean hidden = false;
			boolean show = false;
			for(int i = 0 ; i<cols.length; i++)
			{
				ColumnAttribute c = (ColumnAttribute)cols[i];
				if( c.isHideable() ) {
					String label = c.getLabel();
					if( label == null || label.trim().length() == 0 ) {
						label = c.getDataField();
					}
					if(c.isHidden())
					{
						if(hidden)
						{
							availableCols.append(",");
						}
						String dataField = DataFieldDecoder.convertForGridPanel( c.getDataField() );
						String dataFieldJson = String.format("{\"checked\" : false, \"text\" : \"%s\", \"id\" : \"%s\", \"leaf\" : true, \"cls\" : \"file\"}", label, dataField);
						availableCols.append(dataFieldJson);
						hidden = true;
					}
					else
					{
						if(show)
						{
							selectedCols.append(",");
						}
						String dataField = DataFieldDecoder.convertForGridPanel( c.getDataField() );
						String dataFieldJson = String.format("{\"checked\" : false, \"text\" : \"%s\", \"id\" : \"%s\", \"leaf\" : true, \"cls\" : \"file\"}", label, dataField);
						selectedCols.append(dataFieldJson);
						show = true;
					}
				}
			}
			this.columnAvailableList = availableCols.toString();
			this.columnSelectedList  = selectedCols.toString();
		}		
	}
	
	public String getColumnAvailableList() {
		return columnAvailableList;
	}

	public void setColumnAvailableList(String columnAvailableList) {
		this.columnAvailableList = columnAvailableList;
	}

	public Column[] getColumns()
	{
		return columns;
	}
	
	public void clearParentColumns()
	{
		GridPanel panel = (GridPanel) getParentView().findComponent(
				GridPanel.class);
		Column[] cols = panel.getColumns();
		
		if(cols != null)
		{
			for(int i = 0 ; i < cols.length; i++)
			{
				ColumnAttribute c = (ColumnAttribute)cols[i];
				c.setHidden("true");
			}					
		}
	}
	
	public void setParentColumn(String id)
	{
		
		GridPanel panel = (GridPanel) getParentView().findComponent(
				GridPanel.class);
		Column[] cols = panel.getColumns();
		
		if(cols != null)
		{
			for(int i = 0 ; i < cols.length; i++)
			{
				ColumnAttribute c = (ColumnAttribute)cols[i];
				
				if(c.getDataField().equals(id))
				{
					c.setHidden("false");
					break;
				}
			}						
		}
	}
	
	@Override
	public boolean getIsChanged() {
		return false;
	}
}
