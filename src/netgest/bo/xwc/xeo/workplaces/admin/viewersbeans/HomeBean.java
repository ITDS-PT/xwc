package netgest.bo.xwc.xeo.workplaces.admin.viewersbeans;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.sql.SQLException;

import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.classic.charts.configurations.IBarChartConfiguration;
import netgest.bo.xwc.components.classic.charts.configurations.IPieChartConfiguration;
import netgest.bo.xwc.components.classic.charts.datasets.PieDataSet;
import netgest.bo.xwc.components.classic.charts.datasets.SeriesDataSet;
import netgest.bo.xwc.components.classic.charts.datasets.impl.SeriesDataSetImpl;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.framework.XUIComponentPlugIn;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;
import netgest.bo.xwc.xeo.workplaces.admin.charts.JVMIBarCharConf;
import netgest.bo.xwc.xeo.workplaces.admin.charts.ObjectsPieChartConf;
import netgest.bo.xwc.xeo.workplaces.admin.charts.PieChartDataSet;
import netgest.bo.xwc.xeo.workplaces.admin.connectors.ObjectsDataListConnector;
import netgest.bo.xwc.xeo.workplaces.admin.connectors.SessionsDataListConnector;
import netgest.bo.xwc.xeo.workplaces.admin.connectors.ThreadsDataListConnector;
import netgest.bo.xwc.xeo.workplaces.admin.localization.MainAdminBeanMessages;
import netgest.utils.IOUtils;
import netgest.utils.StringUtils;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;

public class HomeBean extends XEOBaseBean {

	private final static int packageObjectsExpirationTime = 10; // minutes
	private final static int objectInstancesExpirationTime = 30; // minutes
	
	private Sigar sigar;
	private static PieChartDataSet packageObjects;
	private static PieChartDataSet objectInstances;
	private SeriesDataSetImpl jvmMemory;
	private IBarChartConfiguration jvmIBarchartConf;
	private DataListConnector sessions;
	private ThreadsDataListConnector threads;
	private ObjectsDataListConnector lastSavedObjects;
	private ObjectsDataListConnector lastCreatedObjects;

	public HomeBean() throws Exception {
		super();
		this.refreshJvmMemoryGraphs();

		if (packageObjects==null) 
			this.refreshPackageObjects();

		this.sigar = new Sigar();
		this.sessions = new SessionsDataListConnector();
		this.threads = new ThreadsDataListConnector();
		
		this.jvmIBarchartConf = new JVMIBarCharConf();
	}
	
	public Boolean getLastSavedObjectsEnabled() {
		return (lastSavedObjects == null) ? false : true; 
	}
	
	public void enableLastSavedObjects() {
		lastSavedObjects = new ObjectsDataListConnector("SYS_DTSAVE desc");
	}
	
	public Boolean getLastCreatedObjectsEnabled() {
		return (lastCreatedObjects == null) ? false : true; 
	}
	
	public void enableLastCreatedObjects() {
		lastCreatedObjects = new ObjectsDataListConnector("BOUI desc");
	}

	private String getInstalledAppsVersions() {
		String appsVersions="";
		String xeohome=boApplication.getDefaultApplication().getApplicationConfig().getNgtHome();
		
		File [] verFiles=new File(xeohome).listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if (pathname.getName().endsWith(".ver") && !pathname.getName().equals("xeocore.ver")) {
					return true;
				}
				return false;
			}
		});
		
		
		for (File verFile:verFiles) {
			try {
				String version=IOUtils.readFileAsString(verFile);
				if (StringUtils.isEmpty( version )){
					version = "?";
				} else {
					version = version.replaceAll( "_" , "." );
				}
				appsVersions+=", <b>"+verFile.getName().replaceAll(".ver", "").toUpperCase()+": </b>"+version;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return appsVersions;
	}
	
	public String getSysInfo() throws IOException, SigarException {	
		StringBuffer sysInfo = new StringBuffer();

		String version = boApplication.getDefaultApplication().getBuildVersion();
		if (StringUtils.isEmpty( version )){
			version = "?";
		} else {
			version = version.replaceAll( "_" , "." );
		}
		
		sysInfo.append("<b>XEO : </b>" + version +getInstalledAppsVersions());
		try {
			double uptime = sigar.getUptime().getUptime();

			sysInfo.append("<table style='width: 100%;'>");
			sysInfo.append("<tr>");
			// begin system cell 
			sysInfo.append("<th>");

			sysInfo.append(MainAdminBeanMessages.UP_TIME.toString()+": " + netgest.bo.xwc.xeo.workplaces.admin.Utils.formatTimeSeconds(uptime));

			sysInfo.append("<br>"+MainAdminBeanMessages.CPU_USAGE.toString()+" : " + CpuPerc.format(this.sigar.getCpuPerc().getCombined()));

			Mem mem   = this.sigar.getMem();
			Swap swap = this.sigar.getSwap();
			

			sysInfo.append("<table>");
			sysInfo.append("<tr>");
			sysInfo.append("<th>"+MainAdminBeanMessages.MEMORY.toString()+" (MB)</th>");
			sysInfo.append("<th>"+MainAdminBeanMessages.TOTAL.toString()+"</th>");
			sysInfo.append("<th>"+MainAdminBeanMessages.USED.toString()+"</th>");
			sysInfo.append("<th>"+MainAdminBeanMessages.FREE.toString()+"</th>");
			sysInfo.append("</tr>"); 

			sysInfo.append("<tr>");
			sysInfo.append("<td>"+MainAdminBeanMessages.REAL.toString()+"</td>");
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
				sysInfo.append("<td>"+MainAdminBeanMessages.ACTUAL.toString()+"</td>");
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
			sysInfo.append("<br><b>"+MainAdminBeanMessages.JVM_PROCESS.toString()+"</b>");

			String pid = String.valueOf(sigar.getPid());
			sysInfo.append("<br>pid: " + pid);
	
			sysInfo.append("<br>"+MainAdminBeanMessages.CPU_USAGE.toString()+": " + CpuPerc.format(sigar.getProcCpu(pid).getPercent()));

			sysInfo.append("<br>"+MainAdminBeanMessages.MEMORY.toString()+": " 
					+ netgest.bo.xwc.xeo.workplaces.admin.Utils.formatBytesMB(sigar.getProcMem(pid).getResident())
					+ " MB");

			sysInfo.append("<br>"+MainAdminBeanMessages.TOTAL_CPU_TIME.toString()+": " 
					+ netgest.bo.xwc.xeo.workplaces.admin.Utils.formatTimeMiliSeconds(sigar.getProcCpu(pid).getTotal()));

			sysInfo.append("<br>Threads: " + sigar.getProcState(pid).getThreads());
	
			//end process cell 
			sysInfo.append("</th>");
			sysInfo.append("</tr>");
			sysInfo.append("</table>");
			
		} catch (Throwable e) {
			sysInfo = new StringBuffer();
			sysInfo.append("Sigar Library error");
			e.printStackTrace();
		}
		
		return sysInfo.toString(); 	

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
	
	public Boolean getObjectInstancesPieChartRendered() {
		if (objectInstances==null) 
			return false;
		
		return true;
	}
	
	public Boolean getObjectInstancesPieChartExpired() {
		if (objectInstances==null) 
			return true;
		
		return objectInstances.isExpired();
	}
	
	public String getPackageObjectsLastUpdated() {
		return packageObjects.getLastUpdated();
	}

	public String getObjectInstancesLastUpdated() {
		if (getObjectInstancesPieChartRendered())
			return objectInstances.getLastUpdated();
		return "";
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
		return objectInstances;
	}

	public void refreshObjectInstances() {
		if (this.getObjectInstancesPieChartExpired()) {
			final int limit = 10;

			String sql = "SELECT CLSID as name,count(CLSID) as total" 
				+ " FROM OEEBO_REGISTRY"
				+ " WHERE CLSID <> 'Ebo_TextIndex'"
				+ " GROUP BY CLSID" 
				+ " ORDER BY total DESC";

			objectInstances = new PieChartDataSet(sql,"name","total",limit,"Instances by object",objectInstancesExpirationTime);
			objectInstances.setiPieChartConfiguration(new ObjectsPieChartConf("Instances"));
			getRequestContext().getScriptContext().add(XUIScriptContext.POSITION_FOOTER
					, "refreshObjectInstances"
					, "reloadChart('form:objectInstances');");
		}
	}

	private void refreshPackageObjects() {
		final int limit = 10;

		String sql = "SELECT OEBO_PACKAGE.name as name ,count(OEBO_CLSREG.xeopackage$) as total"  
			+" from OEBO_CLSREG,OEBO_PACKAGE" 
			+" where OEBO_CLSREG.xeopackage$ = OEBO_PACKAGE.BOUI"
			+" GROUP BY OEBO_PACKAGE.name ORDER BY total DESC";

		packageObjects = new PieChartDataSet(sql,"name","total",limit,"Objects by package",packageObjectsExpirationTime);
		packageObjects.setiPieChartConfiguration(new ObjectsPieChartConf("Objects"));
	}

	public DataListConnector getLastSavedObjects() {
		return lastSavedObjects;
	}

	public DataListConnector getLastCreatedObjects() {
		return lastCreatedObjects;
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
