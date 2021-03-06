package netgest.bo.xwc.components.connectors.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import netgest.bo.runtime.boObjectList.SqlField;
import netgest.bo.xwc.components.classic.ColumnAttribute;
import netgest.bo.xwc.components.connectors.DataFieldMetaData;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.DataListIterator;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.FilterTerms;
import netgest.bo.xwc.components.connectors.SortTerms;
import netgest.bo.xwc.framework.XUIComponentPlugIn;
import netgest.bo.xwc.xeo.workplaces.admin.localization.ExceptionMessage;

public class GenericDataListConnector implements DataListConnector {

	protected LinkedHashMap<String,GenericDataFieldMetaData> cols = new LinkedHashMap<String, GenericDataFieldMetaData>();
	protected Collection<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
	protected Map<String,Object> currentRow = null;

	private XUIComponentPlugIn colPlugin;

	private int page=1;
	private int pageSize=Integer.MAX_VALUE;
	

	public void createColumn(String key, String label) {
		this.cols.put(key, new GenericDataFieldMetaData(label));
	}

	public void createColumn(String key, String label, Integer width) {
		this.cols.put(key, new GenericDataFieldMetaData(label,width));
	}

	public void createColumn(String key, String label, byte dataType) {
		this.cols.put(key, new GenericDataFieldMetaData(label,dataType));
	}
	
	public void createColumn(String key, String label, Integer width,byte dataType) {
		this.cols.put(key, new GenericDataFieldMetaData(label,width,dataType));
	}
	
	public void createRow() {
		this.currentRow = new HashMap<String, Object>();
		this.rows.add(this.currentRow);
	}

	public void createRowAttribute(String colKey, Object value) {
		this.currentRow.put(colKey, value);
	}

	public Collection<Map<String, Object>> getRows() {
		if (this.page==1 && this.pageSize==Integer.MAX_VALUE)
			return rows;
		else {
			Collection<Map<String,Object>> auxRows = new ArrayList<Map<String,Object>>();
			
			int rowNumber = 1;
			int firstRow = (page * pageSize - pageSize) +1;
			int lastRow = page * pageSize;
			
			Iterator<Map<String,Object>> itRows=rows.iterator();
			while (itRows.hasNext()) {
				Map<String,Object> currRow=itRows.next();
			
				if (rowNumber>=firstRow && rowNumber<=lastRow)
					auxRows.add(currRow);					
				rowNumber++;				
			}
			
			
			return auxRows;
		}
	}

	@Override
	public int dataListCapabilities() {
		return DataListConnector.CAP_PAGING;
	}

	@Override
	public DataRecordConnector findByUniqueIdentifier(String sUniqueIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataFieldMetaData getAttributeMetaData(String colKey) {
		return this.cols.get(colKey);
	}

	@Override
	public int getPage() {
		return this.page;
	}

	@Override
	public int getPageSize() {
		return this.pageSize;
	}

	@Override
	public int getRecordCount() {
		return rows.size();
	}

	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public int indexOf(String sUniqueIdentifier) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DataListIterator iterator() {
		return new GenericDataListIterator( this );
	}

	@Override
	public void refresh() {
		//Do nothing
		//this.rows = new ArrayList<Map<String,Object>>();
		//this.currentRow = null;
	}

	@Override
	public void setFilterTerms(FilterTerms sortTerms) {
	}

	@Override
	public void setPage(int pageNo) {
		this.page = pageNo;
	}

	@Override
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public void setSearchTerms(String[] columnName, Object[] sColumnValue) {
	}

	@Override
	public void setSearchText(String sSearchText) {
	}

	@Override
	public void setSortTerms(SortTerms sortTerms) {
		throw new IllegalArgumentException(ExceptionMessage.NOT_SUPPORTED_ORDER_TERMS.toString());
	}

	@Override
	public void setSqlFields(List<SqlField> sqlFields) {
		// TODO Auto-generated method stub

	}
	
	public XUIComponentPlugIn getColPlugin() {
		return this.getColPlugin(true);
	}

	public XUIComponentPlugIn getColPlugin(boolean addColsStart) {
		if (this.colPlugin==null)
			this.colPlugin = new GridPanelColPlugIn(addColsStart);

		return this.colPlugin;
	}

	private class GridPanelColPlugIn extends XUIComponentPlugIn {
		private boolean addColsStart = true;
		
		public GridPanelColPlugIn(boolean addColsStart) {
			super();
			this.addColsStart = addColsStart;
		}

		@Override
		public void beforePreRender() {
			getComponent().getChildren().clear();
			for (Map.Entry<String, GenericDataFieldMetaData> entry : cols.entrySet()) {
				ColumnAttribute ca = new ColumnAttribute();
				ca.setDataField(entry.getKey());
				ca.setLabel(entry.getValue().getLabel());
				if (entry.getValue().getColWidth()!=null)
					ca.setWidth(entry.getValue().getColWidth().toString());
				
				if (this.addColsStart)
					getComponent().getChildren().add(ca);
				else
					getComponent().getChildren().add(ca);
			}
		}
	}

	@Override
	public boolean hasMorePages() {
		int pageRows=this.getPage() * this.getPageSize();

		if (this.getRecordCount()>pageRows)
			return true;
		else
			return false;
	}

}
