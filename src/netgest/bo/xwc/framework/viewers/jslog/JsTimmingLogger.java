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

public class JsTimmingLogger {
	
	static final int TEXT_COLUMN_SIZE = 250;
	private Connection connection;
	private String tableName = "";
	
	private static final Logger logger = Logger.getLogger(JsTimmingLogger.class);
	
	public JsTimmingLogger(Connection conn, String tableName){
		this.connection = conn;
		this.tableName = tableName;
	} 
	
	public boolean isInitialized() throws SQLException{
		ResultSet result = connection.getMetaData().getTables(null, null, tableName , null);
		try {
			return result.next();
		} finally {
			if (result != null){
				result.close();
			}
		}
	}
	
	
	
	public void init(String tableScript){
		Statement createTable = null;
		try {
			if (!isInitialized()){
				createTable = connection.createStatement();
				createTable.executeUpdate(tableScript);
			}
		} catch (SQLException e ){
			e.printStackTrace();
			logger.warn(e);
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
	
	public void insertNewRecord(long userBoui, long profileBoui, Map<String,String[]> reqParameters, String hostname){
		
		
		List<LogRecord> values = new ArrayList<LogRecord>(6);
		String viewId = "";
		long navigationStart = -1;
		long unloadEventStart = -1;
		long unloadEventEnd = -1;
		long redirectStart = -1;
		long redirectEnd = -1;
		long fetchStart = -1;
		long domainLookupStart = -1;
		long domainLookupEnd = -1;
		long connectStart = -1;
		long connectEnd = -1;
		long secureConnectionStart = -1;
		long requestStart = -1;
		long responseStart = -1;
		long responseEnd = -1;
		long domLoading = -1;
		long domInteractive = -1;
		long domContentLoadedEventStart = -1;
		long domContentLoadedEventEnd = -1;
		long domComplete = -1;
		long loadEventStart = -1;
		long loadEventEnd = -1;
		
		String userAgent = "";
		
		if (reqParameters.containsKey("VIEW_ID")){
			viewId = getAndTruncateTo(reqParameters,"VIEW_ID",TEXT_COLUMN_SIZE);
			if (!viewId.endsWith(".xvw")){
				viewId = "";
			}
		}
		
		if (StringUtils.isEmpty(hostname)){
			hostname = "";
		}
		
		navigationStart = convertoToNumber(reqParameters.get("NAVIGATIONSTART"));
		unloadEventStart = convertoToNumber(reqParameters.get("UNLOADEVENTSTART"));
		unloadEventEnd = convertoToNumber(reqParameters.get("UNLOADEVENTEND"));
		redirectStart = convertoToNumber(reqParameters.get("REDIRECTSTART"));
		redirectEnd = convertoToNumber(reqParameters.get("REDIRECTEND"));
		fetchStart = convertoToNumber(reqParameters.get("FETCHSTART"));
		domainLookupStart = convertoToNumber(reqParameters.get("DOMAINLOOKUPSTART"));
		domainLookupEnd = convertoToNumber(reqParameters.get("DOMAINLOOKUPEND"));
		connectStart = convertoToNumber(reqParameters.get("CONNECTSTART"));
		connectEnd = convertoToNumber(reqParameters.get("CONNECTEND"));
		secureConnectionStart = convertoToNumber(reqParameters.get("SECURECONNECTIONSTART"));
		requestStart = convertoToNumber(reqParameters.get("REQUESTSTART"));
		responseStart = convertoToNumber(reqParameters.get("RESPONSESTART"));
		responseEnd = convertoToNumber(reqParameters.get("RESPONSEEND"));
		domLoading = convertoToNumber(reqParameters.get("DOMLOADING"));
		domInteractive = convertoToNumber(reqParameters.get("DOMINTERACTIVE"));
		domContentLoadedEventStart = convertoToNumber(reqParameters.get("DOMCONTENTLOADEDEVENTSTART"));
		domContentLoadedEventEnd = convertoToNumber(reqParameters.get("DOMCONTENTLOADEDEVENTEND"));
		domComplete = convertoToNumber(reqParameters.get("DOMCOMPLETE"));
		loadEventStart = convertoToNumber(reqParameters.get("LOADEVENTSTART"));
		loadEventEnd = convertoToNumber(reqParameters.get("LOADEVENTEND"));

		if (reqParameters.containsKey("USER_AGENT")){
			userAgent = getAndTruncateTo(reqParameters,"USER_AGENT",TEXT_COLUMN_SIZE);
		} 
		Timestamp currentDate = new Timestamp(System.currentTimeMillis());
		
		if (StringUtils.isEmpty(viewId.toString()))
			return ;
		
		values.add(new LogRecord("USER_BOUI",userBoui));
		values.add(new LogRecord("PROFILE_BOUI",profileBoui));
		values.add(new LogRecord("VIEW_ID",viewId));
		values.add(new LogRecord("USER_AGENT",userAgent));
		values.add(new LogRecord("DATE_EVENT",currentDate));
		values.add(new LogRecord("NAVIGATIONSTART",navigationStart));
		values.add(new LogRecord("UNLOADEVENTSTART",unloadEventStart));
		values.add(new LogRecord("UNLOADEVENTEND",unloadEventEnd));
		values.add(new LogRecord("REDIRECTSTART",redirectStart));
		values.add(new LogRecord("REDIRECTEND",redirectEnd));
		values.add(new LogRecord("FETCHSTART",fetchStart));
		values.add(new LogRecord("DOMAINLOOKUPSTART",domainLookupStart));
		values.add(new LogRecord("DOMAINLOOKUPEND",domainLookupEnd));
		values.add(new LogRecord("CONNECTEND",connectEnd));
		values.add(new LogRecord("CONNECTSTART",connectStart));
		values.add(new LogRecord("SECURECONNECTIONSTART",secureConnectionStart));
		values.add(new LogRecord("REQUESTSTART",requestStart));
		values.add(new LogRecord("RESPONSESTART",responseStart));
		values.add(new LogRecord("RESPONSEEND",responseEnd));
		values.add(new LogRecord("DOMLOADING",domLoading));
		values.add(new LogRecord("DOMINTERACTIVE",domInteractive));
		values.add(new LogRecord("DOMCONTENTLOADEDEVENTSTART",domContentLoadedEventStart));
		values.add(new LogRecord("DOMCONTENTLOADEDEVENTEND",domContentLoadedEventEnd));
		values.add(new LogRecord("DOMCOMPLETE",domComplete));
		values.add(new LogRecord("LOADEVENTSTART",loadEventStart));
		values.add(new LogRecord("LOADEVENTEND",loadEventEnd));
		values.add(new LogRecord("HOST",hostname));
		
		
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
	
	private long convertoToNumber(String[] number){
		if (number == null)
			return -1;
		try {
			return Long.valueOf(number[0]);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	private String getAndTruncateTo(Map<String, String[]> reqParameters, String name, int max) {
		String value = reqParameters.get(name)[0];
		if (max > value.length())
			max = value.length();
		return value.substring(0, max);
	}

}
