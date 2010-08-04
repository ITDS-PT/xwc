package netgest.bo.xwc.xeo.workplaces.admin.viewersbeans;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import netgest.bo.xwc.xeo.beans.XEOBaseBean;

public class JVMBean extends XEOBaseBean  {

	public String getSettings() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer out = new StringBuffer();

		try {
			final OperatingSystemMXBean osbean = ManagementFactory.getOperatingSystemMXBean();
			final RuntimeMXBean runtimemxBean = ManagementFactory.getRuntimeMXBean();
			final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

			out.append("<h1>Java</h1><br>");

			out.append("Name :" + runtimemxBean.getVmName()+"<br>");
			out.append("Vendor :" + runtimemxBean.getVmVendor()+"<br>");
			out.append("Version :" + runtimemxBean.getVmVersion() +"<br>");

			out.append("<br><br>");

			out.append("<h1>Operating System</h1><br>");

			out.append("Name :" + osbean.getName()+"<br>");
			out.append("Version :" + osbean.getVersion()+"<br>");
			out.append("Architecture :" + osbean.getArch()+"<br>");
			out.append("Available Processors :" + osbean.getAvailableProcessors()+"<br>");

			out.append("<br><br>");

			out.append("<h1>File System </h1>");

			File[] roots = File.listRoots();
			for (File root : roots) {
				out.append("<br><h2>"+root.getAbsolutePath()+"</h2><br>");
				out.append("Total space : " + root.getTotalSpace()/1024/1024+" mb <br>");
				out.append("Free space : " + root.getFreeSpace()/1024/1024+" mb <br>");
				out.append("Usable space : " + root.getUsableSpace()/1024/1024+" mb <br>");
			}

			out.append("<br><br>");

			out.append("<h1>Memory</h1><br>");

			out.append("<h2>Heap Memory Usage :</h2><br>");

			out.append("Max : " + memoryMXBean.getHeapMemoryUsage().getMax()/1024/1024+" mb <br>");
			out.append("Used : " + memoryMXBean.getHeapMemoryUsage().getUsed()/1024/1024+" mb <br>");
			out.append("Initial : " + memoryMXBean.getHeapMemoryUsage().getInit()/1024/1024+" mb <br>");
			out.append("Committed : " + memoryMXBean.getHeapMemoryUsage().getCommitted()/1024/1024+" mb <br>");

			out.append("<br>");
			
			out.append("<h2>Non Heap Memory Usage :</h2><br>");

			out.append("Max : " + memoryMXBean.getNonHeapMemoryUsage().getMax()/1024/1024+" mb <br>");
			out.append("Used : " + memoryMXBean.getNonHeapMemoryUsage().getUsed()/1024/1024+" mb <br>");
			out.append("Initial : " + memoryMXBean.getNonHeapMemoryUsage().getInit()/1024/1024+" mb <br>");
			out.append("Committed : " + memoryMXBean.getNonHeapMemoryUsage().getCommitted()/1024/1024+" mb <br>");

			out.append("<br><br>");

			out.append("<h1>Runtime</h1><br>");
			
			out.append("Start Time : " + dateFormat.format(new Date(runtimemxBean.getStartTime()))+"<br>");
			out.append("Up Time : " +formatUptime(runtimemxBean.getUptime())+"<br>");

			out.append("<br>");

			out.append("<h2>Input Arguments :</h2><br>");
			List<String> aList=runtimemxBean.getInputArguments();
			for(int i=0;i<aList.size();i++) {

				if (aList.get(i).trim().toUpperCase().startsWith("-D") 
						|| aList.get(i).trim().toUpperCase().startsWith("-X"))
					out.append(aList.get(i)+"<br>");

			}
			out.append("<br>");

			out.append("<h2>Library Path :</h2><br>" + runtimemxBean.getLibraryPath()+"<br>");
			out.append("<br>");

			out.append("<h2>ClassPath :</h2><br>" + runtimemxBean.getClassPath()+"<br>");
			out.append("<br>");

			out.append("<h2>Other System Properties :</h2><br>");

			for (Iterator<String> iterator = runtimemxBean.getSystemProperties().keySet().iterator(); iterator.hasNext();) {
				String type = iterator.next();

				if (type.equalsIgnoreCase("java.class.path") ||
						type.equalsIgnoreCase("java.library.path") )	
					continue;
				
				out.append("<b> "+type+ "</b> : " +runtimemxBean.getSystemProperties().get(type)+"<br>");
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.append("<br><br>");
			out.append("<b>Error:      </b>" +e.getMessage());
		}
		
		return out.toString();
	}
	// mileSec				
	private static String formatUptime(long uptime) {
		String retval = "";

		// covert to seconds
		uptime = uptime / 1000;


		int days = (int)uptime / (60*60*24);
		int minutes, hours;

		if (days != 0) {
			retval += days + " " + ((days > 1) ? "days" : "day") + ", ";
		}

		minutes = (int)uptime / 60;
		hours = minutes / 60;
		hours %= 24;
		minutes %= 60;

		if (hours != 0) {
			retval += hours + ":" + minutes;
		}
		else {
			retval += minutes + " minutes";
		}

		return retval;
	}



}
