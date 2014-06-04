package netgest.bo.xwc.xeo.workplaces.admin.viewersbeans;

import javax.servlet.http.HttpServletRequest;

import netgest.bo.xwc.components.classic.GridColumnRenderer;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.framework.XUIComponentPlugIn;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.messages.XUIMessageSender;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;
import netgest.bo.xwc.xeo.workplaces.admin.connectors.ThreadsDataListConnector;


public class ThreadsBean extends XEOBaseBean  {
	
	private ThreadsDataListConnector threads;
	
	public ThreadsBean() {
		super();
		this.threads = new ThreadsDataListConnector();
	}

	public DataListConnector getThreads()  {
		return this.threads;
	}
	
	public String getTitle()  {
		return this.threads.getType();
	}
	
	public XUIComponentPlugIn getColPlugIn() {
		return this.threads.getColPlugin();
	}
	
	public void startAgents() {
		getEboContext().getBoSession().getApplication().startAgents();
	}
	
	public void suspendAgents() {
		getEboContext().getBoSession().getApplication().suspendAgents();
	}
	
	public void startThread() {
		XUIRequestContext req = getRequestContext();
		String threadName = 
			((HttpServletRequest)req.getRequest()).getParameter("form:startButton");
		
		StringBuilder errorMessage = new StringBuilder();
		if (!this.threads.startThread(threadName,errorMessage)){
			XUIMessageSender.alertCritical(  errorMessage.toString(),  errorMessage.toString() );
		}
		
	}
	
	public void stopThread() {
		XUIRequestContext req = getRequestContext();
		String threadName = 
			((HttpServletRequest)req.getRequest()).getParameter("form:stopButton");
		
		StringBuilder errorMessage = new StringBuilder();
		if (!this.threads.stopThread(threadName,errorMessage)){
			XUIMessageSender.alertCritical( errorMessage.toString() );
		}
	}
	
	public ButtonGridRendeder getButtonGridRenderer() {
		return new ButtonGridRendeder();
	}
	
	public class ButtonGridRendeder implements GridColumnRenderer {

		@Override
		public String render(GridPanel grid, DataRecordConnector record, DataFieldConnector field) {
			String compId;
			String icon;
			
			String threadName = (String)record.getAttribute("NAME").getValue();
			boolean threadActive = threads.isThreadActive(threadName);

			if (threadActive) {
				compId = "form:stopButton";
			
				icon = "stop.gif";
			} else {
				compId = "form:startButton";
				icon = "start.gif";
			}
				
			XUICommand comp = (XUICommand)getViewRoot().findComponent(compId);
			
			return "<input type='image' width='16' height='16' src='ext-xeo/admin/"+icon+"' onclick=\""+
			XVWScripts.getAjaxCommandScript(comp, threadName,XVWScripts.WAIT_DIALOG)+
			"\" \">	";
		}
		
	}
}
