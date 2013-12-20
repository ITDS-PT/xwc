package netgest.bo.xwc.xeo.workplaces.admin.viewersbeans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.text.html.HTMLDocument.HTMLReader.PreAction;

import netgest.bo.data.DriverUtils;
import netgest.bo.data.oracle.OracleDBM.Database;
import netgest.bo.system.XEO;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.sql.SQLDataListConnector;
import netgest.bo.xwc.framework.XUIErrorLogger;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;
import netgest.bo.xwc.xeo.workplaces.admin.viewersbeans.error.ResultSetPrinter;

public class ErrorLoggerBean extends XEOBaseBean {
	
	private DriverUtils getDatabaseUtils(){
		return XEO.getDriverUtils();
	}
	
	private String currentQuery = ErrorLoggerQuery.getDefaultQuery(getDatabaseUtils());
	
	public DataListConnector getErrorList(){
		return new SQLDataListConnector(currentQuery);
	}

	public void todayErrors(){
		query("select * from xeo_debug_info_on_error where trunc(date_event) = trunc(sysdate)");
	}
	
	private String result = "";
	
	public void errorsByDay(){
		query("select trunc(date_event) as date_event ,count(*) as total from xeo_debug_info_on_error group by trunc(date_event) order by trunc(date_event) desc");
	}
	
	public void mostCommonErrors(){
		query("select dbms_lob.substr(stack_trace,8000) as stack, count(*) as total from xeo_debug_info_on_error " +
			  "	where trunc(date_event)=trunc(sysdate)" +
			  "	group by dbms_lob.substr(stack_trace,8000)" +
			  "	order by 2 desc");
	}
	
	private void query(String query){
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = getEboContext().getConnectionData();
			ps = connection.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			result = new ResultSetPrinter(rs).print();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null){
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String getPreMadeQueryResult(){
		return result;
	}
	
}
