package netgest.bo.xwc.xeo.workplaces.admin.viewersbeans;

import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;
import netgest.bo.xwc.xeo.workplaces.admin.connectors.SessionsDataListConnector;
import netgest.bo.xwc.xeo.workplaces.admin.connectors.UsersDataListConnector;
import netgest.bo.xwc.xeo.workplaces.admin.localization.MainAdminViewerMessages;


public class SessionsBean extends XEOBaseBean  {
	
	private DataListConnector sessions;
	
	public SessionsBean() {
		super();
		this.sessions = new SessionsDataListConnector();
	}

	public DataListConnector getSessions()  {
		return this.sessions;
	}
	
	public DataListConnector getUsers()  {
		return new UsersDataListConnector();
	}
	
	public String getTotalUsers()
	{
		UsersDataListConnector users=new UsersDataListConnector();
		users.refresh();
		return MainAdminViewerMessages.SESSIONS_TOTALUSERS.toString() +": "
			+ users.getRecordCount();
	}

}
