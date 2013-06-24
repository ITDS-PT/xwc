package netgest.bo.xwc.xeo.workplaces.admin.viewersbeans;

import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;
import netgest.bo.xwc.xeo.workplaces.admin.connectors.IndexQueueDataListConnector;


public class IndexQueueBean extends XEOBaseBean  {
	
	private DataListConnector queue;
	
	public IndexQueueBean() {
		super();
		this.queue = new IndexQueueDataListConnector();
	}

	public DataListConnector getQueue()  {
		return this.queue;
	}

}
