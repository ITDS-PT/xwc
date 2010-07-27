package netgest.bo.xwc.xeo.workplaces.admin.viewersbeans;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import netgest.bo.system.boApplication;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIForm;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;
import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;

import com.uwyn.jhighlight.renderer.XmlXhtmlRenderer;

public class LogsBean extends XEOBaseBean  {

	private LinkedHashMap<String,String> logsLov = new LinkedHashMap<String, String>();
	private Map<String,ngtXMLHandler> logs = new HashMap<String, ngtXMLHandler>();

	private String selectedLog;
	private BigDecimal numLines = new BigDecimal(1000);
	private String logString;
	private String loggerDetails;

	public LogsBean() {
		super();
		this.logsLov.put(" ","");
		this.selectedLog="";

		ngtXMLHandler boConfDoc = new ngtXMLHandler(
				boApplication.currentContext().getEboContext().getApplication().getApplicationConfig().getXmldoc()
		);

		ngtXMLHandler logConfig = boConfDoc.getChildNode("bo-config").getChildNode("logConfig");

		if (logConfig != null) {
			ngtXMLHandler[] logs = logConfig.getChildNodes();

			for (int j=0;j<logs.length;j++) {
				if (logs[j].getNodeName().equals("logger")) {
					ngtXMLHandler file = logs[j].getChildNode("file");

					String logFile = file.getAttribute("logFile");
					if ( file != null && logFile != null ) {
						this.logsLov.put(logFile, logFile);
						this.logs.put(logFile, logs[j]);
					}
				}
			}
		}
	}

	public BigDecimal getNumLines() {
		return numLines;
	}

	public void setNumLines(BigDecimal numLines) {
		this.numLines = numLines;
	}

	public String getLogString() {

		XUIComponentBase f = getViewRoot().findComponent(XUIForm.class);
		XUIRequestContext.getCurrentContext().getScriptContext().add(
				XUIScriptContext.POSITION_HEADER,
				f.getClientId() + "_scrollDown",
				"window.setTimeout(function(){try{ document.getElementById('"+f.getClientId()+":logTab').scrollTop" 
				+"=document.getElementById('"+f.getClientId()+":logTab').scrollHeight;}catch(e){}},100);"
		);

		return logString;
	}

	public LinkedHashMap<String, String> getLogsLov() {
		return logsLov;
	}

	public String getSelectedLog() {
		return selectedLog;
	}

	public void setSelectedLog(String selectedLog) {
		this.selectedLog = selectedLog;
	}

	public String getLoggerDetails() {
		return this.loggerDetails;
	}


	public void downLoadLog() {
		this.logString = null;
		this.loggerDetails = null;

		if (!this.selectedLog.trim().equals("")) {
			try {
				HttpServletResponse response = (HttpServletResponse) getRequestContext().getResponse();
				
				File file = new File(this.selectedLog);
				DataInputStream is = new DataInputStream(new FileInputStream(file));
				response.setContentType("application/x-download");
				response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
				
				ServletOutputStream so = response.getOutputStream(); 
				
				int rb=0; 
	            try { 
	               
	                byte[] a=new byte[4*1024];
	                while ((rb=is.read(a)) > 0) { 
	                    so.write(a,0,rb); 
	                } 
	                is.close();
	            } 
	            catch (Exception e) 
	            {
	            }
	            finally
	            {
	                if( is != null ) is.close();
	            }
	            so.close();
		
				getRequestContext().responseComplete();
			} catch (Exception e) {
				this.logString = "<b>Error:      </b>"+e.getMessage();
			} finally {
				this.selectedLog="";
			}
		}
	}

	public void showLog() {
		this.logString = null;
		this.loggerDetails = null;
		if (!this.selectedLog.trim().equals("")) {
			try {
				long pos = (new File(this.selectedLog)).length()-1;
				RandomAccessFile raf = new RandomAccessFile(this.selectedLog,"r");
				int count = 0;
				StringBuffer lineBuffer = new StringBuffer();
				StringBuffer lines = new StringBuffer();

				while( count < this.numLines.intValue() && pos > 0 ) {
					raf.seek(pos-1); 
					byte[] b = new byte[1];
					raf.readFully( b );
					char c = (new String(b)).charAt(0); 
					lineBuffer.insert(0, c );
					if( c == '\n' ) {
						lines.insert(0,"<br>");
						count++;
						lines.insert(0,lineBuffer);
						lineBuffer.delete(0, lineBuffer.length());
					}
					pos--;
				}
				if( pos <= 0 ) {
					lines.insert(0,"<br>");
					lines.insert(0,lineBuffer);
				}
				this.logString = "<hr>" 
					+ "Last " + this.numLines + " lines of log" 
					+ "<hr>" 
					+ "<div id='log'>"
					+ lines.toString() 
					+ "</div>";

				StringBuffer out = new StringBuffer();
				ngtXMLUtils.print(this.logs.get(this.selectedLog).getNode(), out);
				ngtXMLHandler loggerXMlDoc = new ngtXMLHandler(out.toString());
				XmlXhtmlRenderer render = new XmlXhtmlRenderer();


				
				
				this.loggerDetails = render.highlight(
						this.selectedLog + " details"
						,ngtXMLUtils.getXML(loggerXMlDoc.getDocument())
						,this.logs.get(this.selectedLog).getDocument().getEncoding()
						,false);
				
				this.loggerDetails = 
					 "<div id='log'>"
						+ this.loggerDetails
						+ "</div>";

			} catch (Exception e) {
				this.logString = "<b>Error:      </b>"+e.getMessage();
			} finally {
				this.selectedLog="";
			}
		}
	}

}
