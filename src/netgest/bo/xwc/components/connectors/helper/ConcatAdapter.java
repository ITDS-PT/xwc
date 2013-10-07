package netgest.bo.xwc.components.connectors.helper;

import java.util.List;

import netgest.bo.data.DriverUtils;

public class ConcatAdapter {
	
	private DriverUtils dbUtils;
	public ConcatAdapter(DriverUtils utils){
		this.dbUtils = utils;
	}
	
	public String concatColumnsWithSeparator(List< String > columns,
			String separator) {
		return dbUtils.concatColumnsWithSeparator(columns, separator);
	}

}
