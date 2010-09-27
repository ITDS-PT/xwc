package netgest.bo.xwc.xeo.workplaces.admin.viewersbeans;

import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;
import netgest.bo.xwc.xeo.workplaces.admin.connectors.SessionsDataListConnector;


public class SessionsBean extends XEOBaseBean  {
	
	private DataListConnector sessions;
	
	public SessionsBean() {
		super();
		this.sessions = new SessionsDataListConnector();
	}

	public DataListConnector getSessions()  {
		return this.sessions;
	}

}
