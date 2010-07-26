package netgest.bo.xwc.xeo.workplaces.admin.viewersbeans;

import java.io.File;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.util.LinkedHashMap;

import netgest.bo.system.boApplication;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;
import netgest.utils.ngtXMLHandler;

public class LogsBean extends XEOBaseBean  {
	
	private LinkedHashMap<String,String> logsLov = new LinkedHashMap<String, String>();
	
	private String selectedLog;
	private BigDecimal numLines = new BigDecimal(100);
	private String logString;
	
	public LogsBean() {
		super();
		this.logsLov.put(" ","");
		
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
					count++;
					lines.insert(0,lineBuffer);
					lines.insert(0,"\n");
					lineBuffer.delete(0, lineBuffer.length());
				}
				pos--;
			}
			if( pos <= 0 ) {
				lines.insert(0,lineBuffer);
			}
			//<div width='100%' height='100%'
			this.logString = lines.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		
		
		
	}

}
