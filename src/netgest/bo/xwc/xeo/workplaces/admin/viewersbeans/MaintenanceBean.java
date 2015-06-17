package netgest.bo.xwc.xeo.workplaces.admin.viewersbeans;

import netgest.bo.runtime.EboContext;
import netgest.bo.utils.rebuilder.OperationStatus;
import netgest.bo.utils.rebuilder.RebuildEboReferences;
import netgest.bo.utils.rebuilder.RebuildSecurityKeys;
import netgest.bo.utils.rebuilder.RebuildTextIndex;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUISessionContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.xeo.beans.ViewerConfig;
import netgest.bo.xwc.xeo.beans.XEOBaseList;
import netgest.bo.xwc.xeo.components.List;
import netgest.bo.xwc.xeo.localization.BeansMessages;
import netgest.utils.MD5Utils;

import org.json.JSONObject;

public class MaintenanceBean extends XEOBaseList {

	// TODO close ctx threads 
	
	public MaintenanceBean() {

		executeBoql("SELECT Ebo_ClsReg ORDER BY Ebo_ClsReg.name");
	}

	private EboContext getNewEboContext(){
		EboContext ctx = getEboContext();
		return ctx.getBoSession().createRequestContext(ctx.getRequest(), ctx.getResponse(), null);
	}

	private String[] getSelectedObjects() {
		List list = (List) XUIRequestContext.
		getCurrentContext().getEvent().getComponent().findComponent("form:objList");
		DataRecordConnector[] selectedRows = list.getSelectedRows();
		String[] ret = new String[selectedRows.length];

		for (int i=0;i<selectedRows.length;i++) {
			ret[i]= selectedRows[i].getAttribute("name").getDisplayValue();
		}

		return ret;
	}

	public void rebuildReferences() throws Exception {  
		RebuildEboReferences refsEboRefs = new RebuildEboReferences( this.getNewEboContext(), this.getSelectedObjects());
		openLog(refsEboRefs);
		refsEboRefs.run();		
	}

	public void allOnSameTable() throws Exception {
		this.rebuildSecurityKeys(true);
	}
	
	public void notAllOnSameTable() throws Exception {
		this.rebuildSecurityKeys(false);
	}

	private void rebuildSecurityKeys(boolean allOnSameTable) throws Exception {
		RebuildSecurityKeys refsSecKeys = new RebuildSecurityKeys(
				this.getNewEboContext()
				,this.getSelectedObjects()
				,allOnSameTable 
		);
		openLog(refsSecKeys);
		refsSecKeys.run();
		
	}

	public void rebuildTextIndex() throws Exception {  
		RebuildTextIndex refsTextIndex = new RebuildTextIndex( this.getNewEboContext(), this.getSelectedObjects() );
		openLog(refsTextIndex);		
		refsTextIndex.run();		
	}
	
	private void successAlert(String operation)  {  
		XUIRequestContext.getCurrentContext().addMessage(
				"Bean",
				new XUIMessage(XUIMessage.TYPE_ALERT, XUIMessage.SEVERITY_INFO, 
						BeansMessages.TITLE_SUCCESS.toString(), 
						operation+" successful"
				)
		);
	}

	private void unSuccessAlert(String operation)  {  
		XUIRequestContext.getCurrentContext().addMessage(
				"Bean",
				new XUIMessage(XUIMessage.TYPE_ALERT, XUIMessage.SEVERITY_ERROR, 
						BeansMessages.TITLE_ERROR.toString(), 
						operation+" unsuccessful"
				)
		);
	}
	
	public void rebuildMD5()  {  
		if(MD5Utils.rebuildMD5UserCode(getEboContext())) 
			this.successAlert("Rebuild");
		else 
			this.unSuccessAlert("Rebuild");
	}


	public void clearHandler()  {  
		netgest.bo.def.boDefHandler.clearCache();
		this.successAlert("Clear ");
	}

	public void cleanCache() {  
		netgest.bo.runtime.cacheBouis.cleanCacheBoui();
		this.successAlert("Clean ");
	}
	
	public String getBouisSize() {  

		//TODO netgest.bo.runtime.cacheBouis.getSizeUsers()
		return String.valueOf(netgest.bo.runtime.cacheBouis.cacheBouisSize());
	}

	public String getCacheBouisHits() {  
		return String.valueOf(netgest.bo.runtime.cacheBouis.cacheBouisHits());
	}
	
	public String getInvalidBouis() {  
		return String.valueOf(netgest.bo.runtime.cacheBouis.cacheBouisInvalidsSize());
	}
	
	public void openLog(OperationStatus operation) throws Exception {
		XUIRequestContext   oRequestContext;
		XUISessionContext   oSessionContext;
		XUIViewRoot         oViewRoot;


		oRequestContext = XUIRequestContext.getCurrentContext();
		oSessionContext = oRequestContext.getSessionContext();

		JSONObject o = new JSONObject( 
				(String)((XUICommand)oRequestContext.getEvent().getSource()).getValue() 
		);
		ViewerConfig oViewerConfig = new ViewerConfig( o );

		String sViewerName = oViewerConfig.getViewerName();

		oViewRoot = oSessionContext.createView( sViewerName );

		MaintenanceLogBean bean = (MaintenanceLogBean)oViewRoot.getBean("viewBean");
		bean.setOperation(operation);

		oRequestContext.setViewRoot( oViewRoot );
		oViewRoot.processInitComponents();     
	}


}
