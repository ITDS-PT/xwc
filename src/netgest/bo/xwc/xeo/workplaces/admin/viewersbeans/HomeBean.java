package netgest.bo.xwc.xeo.workplaces.admin.viewersbeans;

import java.sql.SQLException;

import netgest.bo.xwc.components.classic.charts.configurations.IBarChartConfiguration;
import netgest.bo.xwc.components.classic.charts.configurations.IPieChartConfiguration;
import netgest.bo.xwc.components.classic.charts.datasets.PieDataSet;
import netgest.bo.xwc.components.classic.charts.datasets.SeriesDataSet;
import netgest.bo.xwc.components.classic.charts.datasets.impl.SeriesDataSetImpl;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.framework.XUIComponentPlugIn;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;
import netgest.bo.xwc.xeo.workplaces.admin.charts.JVMIBarCharConf;
import netgest.bo.xwc.xeo.workplaces.admin.charts.ObjectsPieChartConf;
import netgest.bo.xwc.xeo.workplaces.admin.charts.PieChartDataSet;
import netgest.bo.xwc.xeo.workplaces.admin.connectors.ObjectsDataListConnector;
import netgest.bo.xwc.xeo.workplaces.admin.connectors.SessionsDataListConnector;
import netgest.bo.xwc.xeo.workplaces.admin.connectors.ThreadsDataListConnector;

public class HomeBean extends XEOBaseBean {

	private static PieChartDataSet packageObjects;
	private static PieChartDataSet objectInstances;
	private SeriesDataSetImpl jvmMemory;
	private IBarChartConfiguration jvmIBarchartConf;
	private DataListConnector sessions;
	private ThreadsDataListConnector threads;
	private DataListConnector lastSavedObjects;
	private DataListConnector lastCreatedObjects;

	public HomeBean() throws Exception {
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
		this.jvmIBarchartConf = new JVMIBarCharConf();
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
	
	public IBarChartConfiguration getJvmIBarchartConf() {
		return jvmIBarchartConf;
	}

	public SeriesDataSet getJvmMemory() {
		return this.jvmMemory;
	}

	public IPieChartConfiguration getPackageObjectsPieChartConf() {	
		return packageObjects.getiPieChartConfiguration();
	}

	public IPieChartConfiguration getObjectInstancesPieChartConf() {	
		return objectInstances.getiPieChartConfiguration();
	}

	public void refreshJvmMemoryGraphs() throws Exception {
		Long freeMemory = Runtime.getRuntime().freeMemory()/1024/1024;
		Long totalMemory = Runtime.getRuntime().totalMemory()/1024/1024;
		
		Long maxMemory =  Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? 
				totalMemory : Runtime.getRuntime().maxMemory()/1024/1024; 
		
		this.jvmMemory = new SeriesDataSetImpl();
		
		this.jvmMemory.addColumn("");

		
		this.jvmMemory.addSeries("Max");
		this.jvmMemory.addSeries("Total");
		this.jvmMemory.addSeries("Free");
		
		this.jvmMemory.addValue("Max","", maxMemory);
		this.jvmMemory.addValue("Total","", totalMemory);
		this.jvmMemory.addValue("Free","", freeMemory);

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

		objectInstances = new PieChartDataSet(sql,"name","total",limit);
		objectInstances.setiPieChartConfiguration(new ObjectsPieChartConf("Instances"));
	}

	private void refreshPackageObjects() throws SQLException {
		final int limit = 10;

		String sql = "SELECT OEBO_PACKAGE.name as name ,count(OEBO_CLSREG.xeopackage$) as total"  
			+" from OEBO_CLSREG,OEBO_PACKAGE" 
			+" where OEBO_CLSREG.xeopackage$ = OEBO_PACKAGE.BOUI"
			+" GROUP BY OEBO_PACKAGE.name ORDER BY total DESC";

		packageObjects = new PieChartDataSet(sql,"name","total",limit);
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
