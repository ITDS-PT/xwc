package netgest.bo.xwc.components.connectors.sql;

import java.util.Map;

import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;

public class SQLDataRecordConnector implements DataRecordConnector {
	
	private Map<String,DataFieldConnector> row = null;
	private int rowIndex = 0;
	
	public SQLDataRecordConnector(Map<String,DataFieldConnector> row, int rowIndex) {
		this.row = row;
		this.rowIndex = rowIndex;
	}
	
	@Override
	public DataFieldConnector getAttribute(String name) {
		if (row != null)
			return row.get(name.toLowerCase());
		else
			return null;
	}

	@Override
	public int getRowIndex() {
		return this.rowIndex;
	}

	@Override
	public byte getSecurityPermissions() {
		// TODO Auto-generated method stub
		return 0;
	}

	
}
