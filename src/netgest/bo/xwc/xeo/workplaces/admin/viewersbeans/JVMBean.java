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

import org.json.JSONArray;

import netgest.bo.xwc.xeo.beans.XEOBaseBean;

public class JVMBean extends XEOBaseBean  {

	
	/**
	 * 
	 * Generates the necessary Javascript to render a GridPanel for FileSystem
	 * 
	 * @return
	 */
	private String generateFileSystemJavascript(){
		String javasScript = "Ext.onReady(function(){"+

				"Ext.state.Manager.setProvider(new Ext.state.CookieProvider()); " +
				"var myData = " + getJSONFileSystem() + 
				";" +

				"var store = new Ext.data.SimpleStore({" +
				"    fields: [" +
				"       {name: 'storage'}," +
				"       {name: 'tspace'}," +
				"       {name: 'fspace'}," +
				"       {name: 'uspace'}" +
				"       " +
				"    ]" +
				"});" +

				"function change(val){" +
				"	        if(val > 0){" +
				"		            return '<span style=\"color:green;\">' + val + '</span>';" +
				"		        }else if(val == 0){" +
				"		            return '<span style=\"color:red;\">' + val + '</span>';" +
				"		        }" +
				"		        return val;" +
				"		    }" +

				// manually load local data
				"store.loadData(myData);" +

				// create the Grid
				"var grid = new Ext.grid.GridPanel({" +
				"    store: store," +
				"    columns: [" +
				"        {id:'storage',header: '', width: 75, sortable: true, dataIndex: 'storage'}," +
				"        {header: 'Total Space (Mb)', width: 120, sortable: true, dataIndex: 'tspace'}," +
				"        {header: 'Free Space (Mb)', width: 120, sortable: true, renderer: change, dataIndex: 'fspace'}," +
				"        {header: 'Usable Space (Mb)', width: 120, sortable: true, renderer: change, dataIndex: 'uspace'}" +
				"    ]," +
				"    stripeRows: true," +
				"    autoExpandColumn: 'storage'," +
				"	 title: 'File System Settings',	" +	
				"    height: 200," +
				"    width: 'auto'," +
				"    stateful: true," +
				"    stateId: 'grid'" +        
				"});" +

				// render the grid to the specified div in the page
				"grid.render('fsgrid');" +
				"	});";
		
		return javasScript;
	}
	
	
	/**
	 * 
	 * Generates the necessary Javascript to render a GridPanel for Memory Information
	 * 
	 * @return A string with the required javascript to render the gridPanel
	 */
	private String generateMemoryJavascript(MemoryMXBean memoryMXBean){
		String javasScript = "Ext.onReady(function(){"+

				"Ext.state.Manager.setProvider(new Ext.state.CookieProvider()); " +
				"var myData = " + getJSONMemory(memoryMXBean) + 
				";" +

				"var store = new Ext.data.SimpleStore({" +
				"    fields: [" +
				"       {name: 'memory'}," +
				"       {name: 'max'}," +
				"       {name: 'used'}," +
				"       {name: 'initial'}," +
				"       {name: 'commited'}" +
				"       " +
				"    ]" +
				"});" +

				// manually load local data
				"store.loadData(myData);" +

				// create the Grid
				"var grid = new Ext.grid.GridPanel({" +
				"    store: store," +
				"    columns: [" +
				"        {id:'memory',header: 'Memory', width: 75, sortable: true, dataIndex: 'memory'}," +
				"        {header: 'Max (Mb)', width: 120, sortable: true, dataIndex: 'max'}," +
				"        {header: 'Used (Mb)', width: 120, sortable: true, dataIndex: 'used'}," +
				"        {header: 'Initial (Mb)', width: 120, sortable: true, dataIndex: 'initial'}," +
				"        {header: 'Commited (Mb)', width: 120, sortable: true, dataIndex: 'commited'}," +
				"    ]," +
				"    stripeRows: true," +
				"	 title: 'Memory Settings', " +
				"    autoExpandColumn: 'memory'," +
				"    height: 200," +
				"    width: 'auto'," +
				"    stateful: true," +
				"    stateId: 'gridMem'" +        
				"});" +

				// render the grid to the specified div in the page
				"grid.render('memgrid');" +
				"	});";
		
		return javasScript;
	}
	
	
	/**
	 * 
	 * Generates the Javascript for the ClassPath GridPanel
	 * 
	 * @param memoryMXBean
	 * @return
	 */
	private String generateClassPathJavascript(RuntimeMXBean runtimeMXBean){
		String javasScript = "Ext.onReady(function(){"+

				"Ext.state.Manager.setProvider(new Ext.state.CookieProvider()); " +
				"var myData = " + getJSONClassPath(runtimeMXBean.getClassPath()) + 
				";" +

				"var store = new Ext.data.SimpleStore({" +
				"    fields: [" +
				"       {name: 'entry'}" +
				"    ]" +
				"});" +

				// manually load local data
				"store.loadData(myData);" +

				// create the Grid
				"var grid = new Ext.grid.GridPanel({" +
				"    store: store," +
				"    columns: [" +
				"        {id:'entry',header: 'ClassPath Entry', width: 300, sortable: true, dataIndex: 'entry'}" +
				"    ]," +
				"    stripeRows: true," +
				"	 title: 'Library Path', " +
				"    autoExpandColumn: 'entry'," +
				"    height: 2000," +
				"    width: '95%'," +
				"    stateful: true," +
				"    stateId: 'gridRuntime'" +        
				"});" +

				// render the grid to the specified div in the page
				"grid.render('classpathgrid');" +
				"	});";
		
		return javasScript;
	}
	
	
	/**
	 * 
	 * Generates the JavaScript for the Other System Properties GridPanel
	 * 
	 * @param runtimeMXBean
	 * 
	 * @return
	 */
	private String generateOtherPropertiesJavaScript(RuntimeMXBean runtimeMXBean){
		
			String javasScript = "Ext.onReady(function(){"+

					"Ext.state.Manager.setProvider(new Ext.state.CookieProvider()); " +
					"var myData = " + getJSONSystemProperties(runtimeMXBean) + 
					";" +

					"var store = new Ext.data.SimpleStore({" +
					"    fields: [" +
					"       {name: 'property'}," +
					"       {name: 'value'}" +
					"       " +
					"    ]" +
					"});" +

					// manually load local data
					"store.loadData(myData);" +

					// create the Grid
					"var grid = new Ext.grid.GridPanel({" +
					"    store: store," +
					"    columns: [" +
					"        {id:'property',header: 'Property', width: 150, sortable: true, dataIndex: 'property'}," +
					"        {id:'value', header: 'Value', width: 200, sortable: true, dataIndex: 'value'}" +
					"    ]," +
					"    stripeRows: true," +
					"	 title: 'Other System Properties', " +
					"    autoExpandColumn: 'value'," +
					"    height: 500," +
					"    width: '95%'," +
					"    stateful: true," +
					"    stateId: 'props'" +        
					"});" +

					// render the grid to the specified div in the page
					"grid.render('propsgrid');" +
					"	});";
			
			return javasScript;
		
	}
	
	/**
	 * 
	 * Generates the JSON data for File System Information
	 * 
	 * @return A JSON string with file system information for a grid panel
	 */
	private String getJSONFileSystem(){
		
		File[] roots = File.listRoots();
		JSONArray arr = new JSONArray();
		
		for (File root : roots) {
			JSONArray curr = new JSONArray();
			curr.put(root.getAbsolutePath());
			curr.put(root.getTotalSpace()/1024/1024);
			curr.put(root.getFreeSpace()/1024/1024);
			curr.put(root.getUsableSpace()/1024/1024);
			arr.put(curr);
		}
		return arr.toString();
	}
	
	/**
	 * 
	 * Generates the JSON data for the Memory Information
	 * 
	 * @return A JSON String with the Memory Information for a grid panel
	 */
	private String getJSONMemory(MemoryMXBean memoryMXBean){
		
		JSONArray arrFinal = new JSONArray();

		JSONArray heap = new JSONArray();
		heap.put("Heap Memory");
		heap.put(memoryMXBean.getHeapMemoryUsage().getMax()/1024/1024);
		heap.put(memoryMXBean.getHeapMemoryUsage().getUsed()/1024/1024);
		heap.put(memoryMXBean.getHeapMemoryUsage().getInit()/1024/1024);
		heap.put(memoryMXBean.getHeapMemoryUsage().getCommitted()/1024/1024);
		
		JSONArray nonHeap = new JSONArray();
		nonHeap.put("Non-Heap Memory");
		nonHeap.put(memoryMXBean.getNonHeapMemoryUsage().getMax()/1024/1024);
		nonHeap.put(memoryMXBean.getNonHeapMemoryUsage().getUsed()/1024/1024);
		nonHeap.put(memoryMXBean.getNonHeapMemoryUsage().getInit()/1024/1024);
		nonHeap.put(memoryMXBean.getNonHeapMemoryUsage().getCommitted()/1024/1024);
		
		arrFinal.put(heap);
		arrFinal.put(nonHeap);
		
		return arrFinal.toString();
	}
	
	
	/**
	 * 
	 * Generates the JSON for the ClassPath GridPanel
	 * 
	 * @param classPath The ClassPath string for the JVM
	 * 
	 * @return A JSON String
	 */
	private String getJSONClassPath(String classPath){
		
		JSONArray resultFinal = new JSONArray();
		
		String[] classPathEntries = classPath.split(File.pathSeparator);
		for (String classPathEntry : classPathEntries)
		{
			JSONArray result = new JSONArray();
			result.put(classPathEntry);
			resultFinal.put(result);
		}
		
		return resultFinal.toString();
	}
	
	
	private String getJSONSystemProperties(RuntimeMXBean runtimemxBean){
		
		JSONArray result = new JSONArray();
		
		for (Iterator<String> iterator = runtimemxBean.getSystemProperties().keySet().iterator(); iterator.hasNext();) {
			String type = iterator.next();

			if (type.equalsIgnoreCase("java.class.path") ||
					type.equalsIgnoreCase("java.library.path") )	
				continue;
			JSONArray current = new JSONArray();
			current.put(type);
			current.put(runtimemxBean.getSystemProperties().get(type));
			result.put(current);
		}
		
		return result.toString();
	}
	
	
	public String getSettings() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer out = new StringBuffer();

		try {
			final OperatingSystemMXBean osbean = ManagementFactory.getOperatingSystemMXBean();
			final RuntimeMXBean runtimemxBean = ManagementFactory.getRuntimeMXBean();
			final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

			//Append JavaScript for FileSystem GridPanel
			out.append("<script type='text/javascript'>");
			out.append(generateFileSystemJavascript());
			out.append("</script>");
			
			//Append Javascript for Memory GridPanel
			out.append("<script type='text/javascript'>");
			out.append(generateMemoryJavascript(memoryMXBean));
			out.append("</script>");
			
			//Append JavaScript for ClassPath GridPanel
			out.append("<script type='text/javascript'>");
			out.append(generateClassPathJavascript(runtimemxBean));
			out.append("</script>");
			
			//Append Javascript for Other System Properties GridPanel
			out.append("<script type='text/javascript'>");
			out.append(generateOtherPropertiesJavaScript(runtimemxBean));
			out.append("</script>");
			
			out.append("<div id='javaDiv' style='visibilty:hidden' class='mainText'> ");
			out.append("<table class='relations'>");
			out.append("<tr>");
				out.append("<th>Java Property</th>");
				out.append("<th>Value</th>");
			out.append("</tr>");
			out.append("<tr>");
				out.append("<td> Name </td>");
				out.append("<td> "+runtimemxBean.getVmName()+"</td>");
				out.append("</td>");
			out.append("</tr>");
			out.append("<tr>");
				out.append("<td> Vendor </td>");
				out.append("<td> "+runtimemxBean.getVmVendor()+"</td>");
			out.append("</tr>");
			out.append("<tr>");
				out.append("<td> Version </td> ");
				out.append("<td> "+runtimemxBean.getVmVersion()+"</td>");
			out.append("</tr>");
			
			
			
			out.append("</table>");
			out.append("</div>");
			out.append("<div id='javaPanel'> ");
			out.append("</div> ");

			//Operating System Information
			out.append("<div id='osDiv' style='visibilty:hidden' class='mainText'> ");

			out.append("<table class='relations'>");
			out.append("<tr>");
				out.append("<th>OS Property</th>");
				out.append("<th>Value</th>");
			out.append("</tr>");
			out.append("<tr>");
				out.append("<td> Name </td>");
				out.append("<td> "+osbean.getName()+"</td>");
				out.append("</td>");
			out.append("</tr>");
			out.append("<tr>");
				out.append("<td> Version </td>");
				out.append("<td> "+osbean.getVersion()+"</td>");
			out.append("</tr>");
			out.append("<tr>");
				out.append("<td> Architecture </td> ");
				out.append("<td> "+osbean.getArch()+"</td>");
			out.append("</tr>");
			out.append("<tr>");
				out.append("<td> Available Processors </td> ");
				out.append("<td> "+osbean.getAvailableProcessors()+"</td>");
			out.append("</tr>");
			out.append("</table>");
			
			out.append("</div>");
			out.append("<div id='osPanel'> ");
			out.append("</div> ");
			
			//File System Information And Memory Information
			out.append("<div id='fsmemDiv' style='visibilty:hidden' class='mainText'> ");
			//Div for the FileSystem GridPanel
			out.append("<div id='fsgrid'>");
			out.append("<br><br>");
			out.append("</div>");
			//Div for the Memory GridPanel
			out.append("<div id='memgrid'>");
			out.append("</div>");
			out.append("</div>");
			//Div where the File System and Memory Information are rendered
			out.append("<div id='fsmemPanel'> ");
			out.append("</div> ");


			//Runtime Information
			out.append("<div id='runtimeDiv' style='visibilty:hidden' class='mainText'> ");
			//Div for the FileSystem GridPanel
			//*************************************************
			out.append("Start Time : " + dateFormat.format(new Date(runtimemxBean.getStartTime()))+"<br>");
			out.append("Up Time : " +formatUptime(runtimemxBean.getUptime())+"<br>");

			out.append("<br>");

			out.append("<h2>Input Arguments :</h2><br><ul>");
			List<String> aList=runtimemxBean.getInputArguments();
			for(int i=0;i<aList.size();i++) {

				if (aList.get(i).trim().toUpperCase().startsWith("-D") 
						|| aList.get(i).trim().toUpperCase().startsWith("-X"))
					out.append("<li>"+aList.get(i)+"</li>");

			}
			out.append("</ul>");
			out.append("<br>");

			//*************************************************
			
			out.append("<div id='propsgrid'>");
			out.append("</div>");
			
			//Div for the ClassPath GridPanel
			out.append("<div id='classpathgrid'>");
			out.append("</div>");
			
			out.append("</div>");
			//Div where the Runtime Information is rendered
			out.append("<div id='runtimePanel'> ");
			out.append("</div> ");
			
			
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
