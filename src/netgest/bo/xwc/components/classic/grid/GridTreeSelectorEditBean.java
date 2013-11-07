package netgest.bo.xwc.components.classic.grid;

import netgest.bo.xwc.components.classic.ColumnAttribute;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.model.Column;
import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.xeo.beans.XEOEditBean;
import netgest.bo.xwc.xeo.localization.BeansMessages;

public class GridTreeSelectorEditBean extends XEOEditBean {

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
		sb.s("	XVW.Wait(1);" );
		sb.s("	toggleCheck(root, true)");
		sb.s("	var unSelNodes = tree2.getChecked('id',root)");
		
		sb.s("	toggleCheck(root2, true)");
		sb.s("	var selNodes = tree2.getChecked('id',root2)");
		
		sb.s("	var grid = Ext.getCmp('"+getGridPanelId()+"')");
		sb.s("	grid.suspendUploadCondig = true;");
		sb.s("	var cm = grid.getColumnModel()");
		//sb.s("	cm.suspendEvents();");
		sb.s("	toggleCheck(root2, true)");
		sb.s("	var colsCount = cm.getColumnCount(false)");
		sb.l("	if( selNodes.length > 1 ) {");
		sb.l("		for( k = 0; k < unSelNodes.length; k++){");
		sb.s("			var i = cm.findColumnIndex( unSelNodes[k] )");
		sb.s("			if(i>-1 && !cm.isHidden(i)) cm.setHidden(i,true)");
		sb.l("		}");
		sb.l("		for( k = 0; k < selNodes.length; k++){");
		sb.s("			var i = cm.findColumnIndex( selNodes[k] )");
		sb.s("			if(i>-1 && cm.isHidden(i)) cm.setHidden(i,false)");
		sb.l("		}");

		sb.s("  	// Ordernar Colunas");
		sb.l("		for( k = 0; k < selNodes.length; k++){");
		sb.s("			var i = cm.findColumnIndex( selNodes[k] )");
		sb.l("			if( i > -1 )");
		sb.l("				cm.moveColumn( i,k+1);");
		sb.l("		}");
		
		sb.s("		grid.suspendUploadCondig = false;");
		sb.s("		grid.updateColumnConfig(true);");
		sb.s("		grid.getStore().reload();");
		sb.s("		var w = Ext.getCmp('" + getParentWindowId() + "')");
		sb.s("  	w.close()");
		sb.l("	} else { ");
		sb.l("		Ext.Msg.alert('"+BeansMessages.TREE_SHUTTLE_COLUMN_SELECTION.toString()+"'," +
				" '"+BeansMessages.TREE_SHUTTLE_COLUMN_SELECTION_MUST_SELECT.toString()+"');");
		sb.l("	}");
		sb.l("	XVW.NoWait();");
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
						availableCols.append("{\"checked\" : false, \"text\" : \"" + label + "\", \"id\" : \"" + c.getDataField() + "\", \"leaf\" : true, \"cls\" : \"file\"}");
						hidden = true;
					}
					else
					{
						if(show)
						{
							selectedCols.append(",");
						}
						
						selectedCols.append("{\"checked\" : false, \"text\" : \"" + label + "\", \"id\" : \"" + c.getDataField() + "\", \"leaf\" : true, \"cls\" : \"file\"}");
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
		GridPanel panel = (GridPanel) getParentBean().getViewRoot().findComponent(
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
		GridPanel panel = (GridPanel) getParentBean().getViewRoot().findComponent(
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
