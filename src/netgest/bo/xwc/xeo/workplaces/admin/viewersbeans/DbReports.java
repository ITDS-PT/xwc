package netgest.bo.xwc.xeo.workplaces.admin.viewersbeans;

import netgest.bo.report.BDReport2;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.Window;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.XEOObjectListConnector;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;

public class DbReports extends XEOBaseBean {

	private String packageReport = null;
	private String objectReport = null;
	
	public String getPackageReport() {
		return packageReport;
	}

	public String getObjectReport() {
		return objectReport;
	}

	private String getSelected(String gridPanelId) {
		GridPanel gp = (GridPanel) XUIRequestContext.
		getCurrentContext().getEvent().getComponent().findComponent("form:"+gridPanelId);
		DataRecordConnector selectedRow = gp.getActiveRow();

		return selectedRow == null ? null :  selectedRow.getAttribute("name").getDisplayValue();
	}


	public void createPackageReport() throws boRuntimeException {
		String selected = this.getSelected("packageList");

		if (selected!=null) {
			
			  BDReport2 x = new BDReport2(getEboContext(),
					  selected, null);
		        
			
			this.packageReport = x.createHtmlReport();
			x = null;
		}
	}

	public void createObjectReport() {
		String selected = this.getSelected("objectList");

		if (selected!=null) {
			//this.objectReport = 
		}
	}

	public void newPackageReport() {
		this.packageReport = null;
	}

	public boolean getHasPackageReport() {
		return this.packageReport == null ? false : true;
	}

	public boolean getHasObjectReport() {
		return this.objectReport == null ? false : true;
	}
	
	public DataListConnector getPackages()  {
		String boQl = "select Ebo_Package";

		return  new XEOObjectListConnector( 
				boObjectList.list(getEboContext(),boQl)
		);
	}
	
	public DataListConnector getObjects()  {
		String boQl = "select Ebo_ClsReg";

		return  new XEOObjectListConnector( 
				boObjectList.list(getEboContext(),boQl)
		);
	}
	
	public void canCloseTab() {

		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
		XUIViewRoot viewRoot = oRequestContext.getViewRoot();
		Window xWnd = (Window)viewRoot.findComponent(Window.class);
		if( xWnd != null ) {
			if( xWnd.getOnClose() != null ) {
				xWnd.getOnClose().invoke( oRequestContext.getELContext(), null);
			}
		}
		XVWScripts.closeView( viewRoot );
		oRequestContext.getViewRoot().setRendered( false );
		oRequestContext.getViewRoot().setTransient( true );
		oRequestContext.renderResponse();
	}

}
