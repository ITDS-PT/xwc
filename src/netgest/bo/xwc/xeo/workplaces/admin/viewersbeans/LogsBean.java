package netgest.bo.xwc.xeo.workplaces.admin.viewersbeans;

import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;
import netgest.bo.xwc.xeo.workplaces.admin.connectors.LogsDataListConnector;


public class LogsBean extends XEOBaseBean  {
	
	private DataListConnector logs;
	
	public LogsBean() {
		super();
		this.logs = new LogsDataListConnector();
	}

	
	public DataListConnector getLogs()  {
		return this.logs;
	}

}
