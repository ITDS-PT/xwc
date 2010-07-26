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
		
		this.threads.startThread(threadName);
		
	}
	
	public void stopThread() {
		XUIRequestContext req = getRequestContext();
		String threadName = 
			((HttpServletRequest)req.getRequest()).getParameter("form:stopButton");
		
		this.threads.stopThread(threadName);
	}
	
	public ButtonGridRendeder getButtonGridRenderer() {
		return new ButtonGridRendeder();
	}
	
	public class ButtonGridRendeder implements GridColumnRenderer {

		@Override
		public String render(GridPanel grid, DataRecordConnector record, DataFieldConnector field) {
			String compId;
			String label;
			
			String threadName = (String)record.getAttribute("NAME").getValue();
			boolean threadActive = threads.isThreadActive(threadName);
	
			if (threadActive && "START".equalsIgnoreCase(field.getLabel().trim())) 
				return "";
			
			if (!threadActive && "STOP".equalsIgnoreCase(field.getLabel().trim())) 
				return "";
					
			if (threadActive) {
				compId = "form:stopButton";
				label = "Stop";
			} else {
				compId = "form:startButton";
				label = "Start";
			}
				
			XUICommand comp = (XUICommand)getViewRoot().findComponent(compId);
			
			return "<input value='"+label+"' type='button' onclick=\""+
			XVWScripts.getAjaxCommandScript(comp, threadName,XVWScripts.WAIT_DIALOG)+
			"\" \">	";
		}
		
	}
}
