package netgest.bo.xwc.xeo.workplaces.admin.viewersbeans;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

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

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;

public class HomeBean extends XEOBaseBean {

	private Sigar sigar = new Sigar();

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
		this.lastCreatedObjects = new ObjectsDataListConnector("BOUI desc");
		this.lastSavedObjects = new ObjectsDataListConnector("SYS_DTSAVE desc");
		this.jvmIBarchartConf = new JVMIBarCharConf();
	}

	public String getSysInfo() throws IOException, SigarException {	
		StringBuffer sysInfo = new StringBuffer();

		double uptime = sigar.getUptime().getUptime();

		sysInfo.append("<table style='width: 100%;'>");
	    sysInfo.append("<tr>");
	    // begin system cell 
	    sysInfo.append("<th>");

		sysInfo.append("Up Time: " + netgest.bo.xwc.xeo.workplaces.admin.Utils.formatTimeSeconds(uptime));

		sysInfo.append("<br>CPU Usage : " + CpuPerc.format(this.sigar.getCpuPerc().getCombined()));
		
		Mem mem   = this.sigar.getMem();
	    Swap swap = this.sigar.getSwap();
		
        //sysInfo.append("<br>RAM:"+ mem.getRam() + " MB" );
		
        sysInfo.append("<table>");
        sysInfo.append("<tr>");
        sysInfo.append("<th>Memory (MB)</th>");
        sysInfo.append("<th>Total</th>");
        sysInfo.append("<th>Used</th>");
        sysInfo.append("<th>Free</th>");
        sysInfo.append("</tr>"); 

        sysInfo.append("<tr>");
        sysInfo.append("<td>Real</td>");
        sysInfo.append("<td> "+netgest.bo.xwc.xeo.workplaces.admin.Utils.formatBytesMB(mem.getTotal())+"</td>");
        sysInfo.append("<td> "+netgest.bo.xwc.xeo.workplaces.admin.Utils.formatBytesMB(mem.getUsed())+"</td>");
        sysInfo.append("<td> "+netgest.bo.xwc.xeo.workplaces.admin.Utils.formatBytesMB(mem.getFree())+"</td>");
        sysInfo.append("</tr>");
        
        sysInfo.append("<tr>");
        sysInfo.append("<td>Swap</td>");
        sysInfo.append("<td> "+netgest.bo.xwc.xeo.workplaces.admin.Utils.formatBytesMB(swap.getTotal())+"</td>");
        sysInfo.append("<td> "+netgest.bo.xwc.xeo.workplaces.admin.Utils.formatBytesMB(swap.getUsed())+"</td>");
        sysInfo.append("<td> "+netgest.bo.xwc.xeo.workplaces.admin.Utils.formatBytesMB(swap.getFree())+"</td>");
        sysInfo.append("</tr>");
        

        //e.g. linux
        if ((mem.getUsed() != mem.getActualUsed()) ||
            (mem.getFree() != mem.getActualFree()))
        {
            sysInfo.append("<tr>");
            sysInfo.append("<td>Actual</td>");
            sysInfo.append("<td></td>");
            sysInfo.append("<td> "+netgest.bo.xwc.xeo.workplaces.admin.Utils.formatBytesMB(mem.getActualUsed())+"</td>");
            sysInfo.append("<td> "+netgest.bo.xwc.xeo.workplaces.admin.Utils.formatBytesMB(mem.getActualFree())+"</td>");
            sysInfo.append("</tr>");
        }
        sysInfo.append("</table>");
        
        //end system cell 
        sysInfo.append("</th>");
        
        sysInfo.append("<th></th>");
        
        // begin process cell 
	    sysInfo.append("<th>");
        sysInfo.append("<br><b>JVM Process</b>");
        
        String pid = String.valueOf(sigar.getPid());
        sysInfo.append("<br>pid: " + pid);
        try {
        	sysInfo.append("<br>CPU Usage: " + CpuPerc.format(sigar.getProcCpu(pid).getPercent()));
        } catch (SigarException e) {}
        
        try {
        	sysInfo.append("<br>Memory: " 
        			+ netgest.bo.xwc.xeo.workplaces.admin.Utils.formatBytesMB(sigar.getProcMem(pid).getResident())
        			+ " MB");
        } catch (SigarException e) {}
        
        
        try {
        	sysInfo.append("<br>Total CPU Time: " 
        			+ netgest.bo.xwc.xeo.workplaces.admin.Utils.formatTimeMiliSeconds(sigar.getProcCpu(pid).getTotal()));
        } catch (SigarException e) {}

        try {
        	sysInfo.append("<br>Threads: " + sigar.getProcState(pid).getThreads());
        } catch (SigarException e) {}
        
        //end process cell 
        sysInfo.append("</th>");
        sysInfo.append("</tr>");
		sysInfo.append("</table>");
	    
		return sysInfo.toString(); 	

	}
	
	public Date getTime() {
		return new Date();
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
		Long freeMemory = netgest.bo.xwc.xeo.workplaces.admin.Utils.formatBytesMB(Runtime.getRuntime().freeMemory());
		Long totalMemory = netgest.bo.xwc.xeo.workplaces.admin.Utils.formatBytesMB(Runtime.getRuntime().totalMemory());

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
