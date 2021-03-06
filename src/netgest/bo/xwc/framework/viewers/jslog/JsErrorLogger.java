package netgest.bo.xwc.framework.viewers.jslog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import netgest.bo.system.Logger;
import netgest.utils.StringUtils;

public class JsErrorLogger {
	
	private Connection connection;
	private String tableName = "";
	
	
	
	private static final Logger logger = Logger.getLogger(JsErrorLogger.class);
	
	public JsErrorLogger(Connection conn, String tableName){
		this.connection = conn;
		this.tableName = tableName;
	} 
	
	public boolean isInitialized() throws SQLException{
		ResultSet result = connection.getMetaData().getTables(null, null, tableName , null);
		ResultSet tableCount = null;
		Statement tableStatement = null;
		try {
			result.next();
			tableStatement = connection.createStatement(); 
			tableCount = tableStatement.executeQuery( String.format("select count(*) from %s",tableName) );
			return true;
		} catch (SQLException e){
			return false;
		}
		finally {
			if (result != null){
				result.close();
			}
			if (tableStatement != null){
				tableStatement.close();
			}
			if (tableCount != null){
				tableCount.close();
			}
		}
	}
	
	
	
	public boolean init(String tableScript){
		Statement createTable = null;
		try {
			if (!isInitialized()){
				createTable = connection.createStatement();
				createTable.executeUpdate(tableScript);
			}
			return true;
		} catch (SQLException e ){
			e.printStackTrace();
			logger.warn(e);
			return false;
		} finally {
			closeStatement(createTable);
		}
	}
	
	
	private void closeStatement(Statement st){
		try {
			if (st != null){
				st.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally { 
			
		}
	}
	
	public void insertNewRecord(long userBoui, long profileBoui, Map<String,String[]> reqParameters, String hostname, String ipAddress){
		
		List<LogRecord> values = new ArrayList<LogRecord>(6);
		String viewId = "";
		String jsErrorBlock = "";
		String jsErrorMessage = "";
		String userAgent = "";
		String lineNumberRaw = "";
		String jsFile = "";
		
		if (reqParameters.containsKey("VIEW_ID")){
			viewId = getAndTruncateTo(reqParameters,"VIEW_ID",250);
			if (!viewId.endsWith(".xvw")){
				viewId = "";
			}
		}
		if (reqParameters.containsKey("JS_ERROR_BLOCK")){
			jsErrorBlock = reqParameters.get("JS_ERROR_BLOCK")[0];
		} 
		if (reqParameters.containsKey("JS_ERROR_MESSAGE")){
			jsErrorMessage = reqParameters.get("JS_ERROR_MESSAGE")[0];
		} 
		if (reqParameters.containsKey("USER_AGENT")){
			userAgent = getAndTruncateTo(reqParameters,"USER_AGENT",250);
		} 
		if (reqParameters.containsKey("LINE")){
			lineNumberRaw = reqParameters.get("LINE")[0];
		} 
		if (reqParameters.containsKey("JS_FILE")){
			jsFile = reqParameters.get("JS_FILE")[0];
		}
		
		if (StringUtils.isEmpty(hostname))
			hostname = "";
		
		Timestamp currentDate = new Timestamp(System.currentTimeMillis());
		
		long lineNumber = Long.valueOf(0);
		if (StringUtils.hasValue(lineNumberRaw)){
			try {
				lineNumber = Long.valueOf(lineNumberRaw);
			} catch (NumberFormatException e) {
				lineNumber = -1;
			}
		}
		
		if (StringUtils.isEmpty(viewId.toString()) || StringUtils.isEmpty(jsErrorMessage))
			return ;
		
		values.add(new LogRecord("USER_BOUI",userBoui));
		values.add(new LogRecord("PROFILE_BOUI",profileBoui));
		values.add(new LogRecord("VIEW_ID",viewId));
		values.add(new LogRecord("DATE_EVENT",currentDate));
		values.add(new LogRecord("JS_ERROR_BLOCK",jsErrorBlock));
		values.add(new LogRecord("JS_ERROR_MESSAGE",jsErrorMessage));
		values.add(new LogRecord("USER_AGENT",userAgent));
		values.add(new LogRecord("LINE",lineNumber));
		values.add(new LogRecord("HOST",hostname));
		values.add(new LogRecord("IP_ADDRESS",ipAddress));
		values.add(new LogRecord("JS_FILE",jsFile));
		
		
		StringBuilder s = new StringBuilder(200);
		s.append("INSERT INTO ");
		s.append(tableName);
		s.append("(");
		String separator = "";
		for (LogRecord record : values){
			s.append(separator);
			s.append(record.getKey());
			separator = ",";
		}
		s.append(") ");
		s.append(" VALUES(");
		String separatorValues = "";
		for (int k = 0 ; k < values.size() ; k++){
			s.append(separatorValues);
			s.append("?");
			separatorValues = ",";
		}
		s.append(" )");
		
		String sql = s.toString();
		PreparedStatement newRecord = null;
		try {
			 newRecord = connection.prepareStatement(sql);
			int k = 1;
			for (LogRecord record : values){
				newRecord.setObject(k, record.getValue());
				k++;
			}
			newRecord.executeUpdate();
		} catch (SQLException e) {
			logger.warn("Could not insert records ", e);
			e.printStackTrace();
		} finally {
			closeStatement(newRecord);
		}
		
		
	}

	private String getAndTruncateTo(Map<String, String[]> reqParameters, String name, int max) {
		String value = reqParameters.get(name)[0];
		if (max > value.length())
			max = value.length();
		return value.substring(0, max);
	}

}
