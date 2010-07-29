package netgest.bo.xwc.xeo.workplaces.admin.viewersbeans;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.List;

import netgest.bo.xwc.xeo.beans.XEOBaseBean;

public class JVMBean extends XEOBaseBean  {



	public String getSomething() {
		StringBuffer a = new StringBuffer();

		RuntimeMXBean RuntimemxBean = ManagementFactory.getRuntimeMXBean();
		List<String> aList=RuntimemxBean.getInputArguments();

		for(int i=0;i<aList.size();i++) {

			if (aList.get(i).trim().toUpperCase().startsWith("-D") || aList.get(i).trim().toUpperCase().startsWith("-X"))
				a.append("<br>"+aList.get(i));

		}
		
		
		 /* Total number of processors or cores available to the JVM */
	    System.out.println("Available processors (cores): " + 
	        Runtime.getRuntime().availableProcessors());

	    /* Total amount of free memory available to the JVM */
	    System.out.println("Free memory (bytes): " + 
	        Runtime.getRuntime().freeMemory()/1024);

	    /* This will return Long.MAX_VALUE if there is no preset limit */
	    long maxMemory = Runtime.getRuntime().maxMemory();
	    /* Maximum amount of memory the JVM will attempt to use */
	    System.out.println("Maximum memory (bytes): " + 
	        (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory/1024));

	    /* Total memory currently in use by the JVM */
	    System.out.println("Total memory (bytes): " + 
	        Runtime.getRuntime().totalMemory()/1024);
	    
	    

	    /* Get a list of all filesystem roots on this system */
	    File[] roots = File.listRoots();

	    /* For each filesystem root, print some info */
	    for (File root : roots) {
	      System.out.println("File system root: " + root.getAbsolutePath());
	      System.out.println("Total space (bytes): " + root.getTotalSpace());
	      System.out.println("Free space (bytes): " + root.getFreeSpace());
	      System.out.println("Usable space (bytes): " + root.getUsableSpace());
	    }
	    
	    
	    final OperatingSystemMXBean osbean = 
            ManagementFactory.getOperatingSystemMXBean();
	    
		a.append("<br>"+osbean.getVersion());
		a.append("<br>"+osbean.getArch());
		a.append("<br>"+osbean.getAvailableProcessors());
		a.append("<br>"+osbean.getName());
		a.append("<br>"+osbean.getSystemLoadAverage());
		
		a.append("<br>");
		a.append("<br>");
		a.append("<br>");
		a.append("<br>");
		
		a.append("<br>"+ManagementFactory.getMemoryMXBean().getHeapMemoryUsage());
		a.append("<br>"+ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage());
		
		
		a.append("<br>");
		a.append("<br>");
		a.append("<br>");
		a.append("<br>");
		
		a.append("<br>"+" Max "+ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getMax()/1024/1024);
		a.append("<br>"+" Used "+ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed()/1024/1024);
		a.append("<br>"+" Init "+ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getInit()/1024/1024);
		a.append("<br>"+" Committed "+ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getCommitted()/1024/1024);

		a.append("<br>");
		a.append("<br>");
		a.append("<br>");
		a.append("<br>");
		
		a.append("<br>"+" Max "+ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax()/1024/1024);
		a.append("<br>"+" Used "+ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed()/1024/1024);
		a.append("<br>"+" Init "+ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getInit()/1024/1024);
		a.append("<br>"+" Committed "+ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getCommitted()/1024/1024);
		
		
		a.append("<br>");
		a.append("<br>");
		a.append("<br>");
		a.append("<br>");
		
	    /* Total amount of free memory available to the JVM */
		a.append("<br>"+"Free memory (bytes): " + 
	        Runtime.getRuntime().freeMemory()/1024/1024);

	    /* This will return Long.MAX_VALUE if there is no preset limit */
	   maxMemory = Runtime.getRuntime().maxMemory();
	    /* Maximum amount of memory the JVM will attempt to use */
		a.append("<br>"+"Maximum memory (bytes): " + 
	        (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory/1024/1024));

	    /* Total memory currently in use by the JVM */
		a.append("<br>"+"Total memory (bytes): " + 
	        Runtime.getRuntime().totalMemory()/1024/1024);

		
	    
		
		return a.toString();
	}



}
