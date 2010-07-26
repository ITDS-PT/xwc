package netgest.bo.xwc.xeo.workplaces.admin.viewersbeans;

import java.sql.SQLException;

import netgest.bo.xwc.components.classic.charts.configurations.IPieChartConfiguration;
import netgest.bo.xwc.components.classic.charts.datasets.PieDataSet;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.framework.XUIComponentPlugIn;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;
import netgest.bo.xwc.xeo.workplaces.admin.charts.HomeChartDataSet;
import netgest.bo.xwc.xeo.workplaces.admin.charts.JVMMmemPieChartConf;
import netgest.bo.xwc.xeo.workplaces.admin.charts.ObjectsPieChartConf;
import netgest.bo.xwc.xeo.workplaces.admin.connectors.ObjectsDataListConnector;
import netgest.bo.xwc.xeo.workplaces.admin.connectors.SessionsDataListConnector;
import netgest.bo.xwc.xeo.workplaces.admin.connectors.ThreadsDataListConnector;

public class HomeBean extends XEOBaseBean {

	private static HomeChartDataSet packageObjects;
	private static HomeChartDataSet objectInstances;
	private HomeChartDataSet jvmTotalMemory;
	private HomeChartDataSet jvmAllocatedMemory;
	private DataListConnector sessions;
	private ThreadsDataListConnector threads;
	private DataListConnector lastSavedObjects;
	private DataListConnector lastCreatedObjects;

	public HomeBean() throws SQLException {
		super();
		this.refreshJvmMemoryGraphs();
	
		if (packageObjects==null) 
			this.refreshPackageObjects();

		if (objectInstances==null) 
			this.refreshObjectInstances();
		
		this.sessions = new SessionsDataListConnector();
		this.threads = new ThreadsDataListConnector();
		this.lastCreatedObjects = new ObjectsDataListConnector("SYS_DTCREATE desc");
		this.lastSavedObjects = new ObjectsDataListConnector("SYS_DTSAVE desc");
	}

	public long getMaxMemory() {
		return Runtime.getRuntime().maxMemory()/1024/1024;
	}

	public long getTotalMemory() {
		return Runtime.getRuntime().totalMemory()/1024/1024;
	}

	public long getFreeMemory() {
		return Runtime.getRuntime().freeMemory()/1024/1024;
	}

	public PieDataSet getJvmTotalMemory() {
		return jvmTotalMemory;
	}

	public PieDataSet getJvmAllocatedMemory() {
		return jvmAllocatedMemory;
	}

	public IPieChartConfiguration getJvmTotalMemoryPieChartConf() {	
		return jvmTotalMemory.getiPieChartConfiguration();
	}

	public IPieChartConfiguration getJvmAllocatedMemoryPieChartConf() {	
		return jvmAllocatedMemory.getiPieChartConfiguration();
	}

	public IPieChartConfiguration getPackageObjectsPieChartConf() {	
		return packageObjects.getiPieChartConfiguration();
	}

	public IPieChartConfiguration getObjectInstancesPieChartConf() {	
		return objectInstances.getiPieChartConfiguration();
	}

	public void refreshJvmMemoryGraphs() {
		Long max = this.getMaxMemory();
		Long total = this.getTotalMemory();;
		Long free = this.getFreeMemory();

		IPieChartConfiguration conf  = new JVMMmemPieChartConf();

		jvmTotalMemory = new HomeChartDataSet();
		jvmTotalMemory.setiPieChartConfiguration(conf);
		jvmTotalMemory.addCategory("Free", (max-(total+free)) );
		jvmTotalMemory.addCategory("Occupied", (total+free) );

		jvmAllocatedMemory = new HomeChartDataSet();
		jvmAllocatedMemory.setiPieChartConfiguration(conf);
		jvmAllocatedMemory.addCategory("Free", (total-free));
		jvmAllocatedMemory.addCategory("Occupied", free);

	}

	public PieDataSet getPackageObjects() throws SQLException {
		if (packageObjects.isExpired()) 
			this.refreshPackageObjects();

		return packageObjects; 
	}

	public PieDataSet getObjectInstances() throws SQLException {
		if (objectInstances.isExpired()) 
			this.refreshObjectInstances();

		return objectInstances;
	}

	private void refreshObjectInstances() throws SQLException {
		final int limit = 10;

		String sql = "SELECT CLSID as name,count(CLSID) as total" 
			+ " FROM OEEBO_REGISTRY"
			+ " WHERE CLSID <> 'Ebo_TextIndex'"
			+ " GROUP BY CLSID" 
			+ " ORDER BY total DESC";

		objectInstances = new HomeChartDataSet(sql,"name","total",limit);
		objectInstances.setiPieChartConfiguration(new ObjectsPieChartConf("Instances"));
	}

	private void refreshPackageObjects() throws SQLException {
		final int limit = 10;

		String sql = "SELECT OEBO_PACKAGE.name as name ,count(OEBO_CLSREG.xeopackage$) as total"  
			+" from OEBO_CLSREG,OEBO_PACKAGE" 
			+" where OEBO_CLSREG.xeopackage$ = OEBO_PACKAGE.BOUI"
			+" GROUP BY OEBO_PACKAGE.name ORDER BY total DESC";

		packageObjects = new HomeChartDataSet(sql,"name","total",limit);
		packageObjects.setiPieChartConfiguration(new ObjectsPieChartConf("Objects"));
	}

	public DataListConnector getLastSavedObjects() {
		return this.lastSavedObjects;
	}

	public DataListConnector getLastCreatedObjects() {
		return this.lastCreatedObjects;
	}

	public DataListConnector getSessions() {		
		return this.sessions;
	}
	
	public DataListConnector getThreads() {		
		return this.threads;
	}
	
	public XUIComponentPlugIn getThreadsColPlugIn() {
		return this.threads.getColPlugin();
	}


}
