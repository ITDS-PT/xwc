package netgest.bo.xwc.framework.viewers.jslog;


import netgest.bo.data.oracle.OracleDBM.Database;
import netgest.bo.xwc.framework.XUIErrorLogger;

public class LoggerConstants {
	
	/**
	 * Name of the table where javascript error logs are stored
	 */
	static final String JS_ERROR_LOG_TABLE_NAME = "XEO_JS_ERROR_LOGS";
	
	/**
	 * Name of the table where load times are logged (uses HTML5 Timing API from the browser)
	 */
	static final String JS_TIMMING_LOG_TABLE_NAME = "XEO_JS_TIMMING_LOGS";
	
	/**
	 * Name of the table where ajax load times are logged
	 */
	static final String JS_AJAX_TIMMING_LOG_TABLE_NAME = "XEO_JS_AJAX_TIMMING_LOGS";
	
	/**
	 * Name of the property in boConfig which has the alternative datasource to use 
	 * when logging timings / javascript errors and application errors
	 */
	public static final String LOGGING_ALTERNATIVE_DATASOURCE_PROPERTY = "AlternativeDataSource";
	
	/**
	 * Name of the property which controls whether the javascript error log is active or not (values true/false)
	 */
	static final String JS_ERROR_LOG_ACTIVE_PROPERTY = "JavascriptErrorLogsActive";
	
	/**
	 * Name of the property which controls whether the browser load time log is active or not (values true/false)
	 */
	static final String JS_TIMMING_LOG_ACTIVE_PROPERTY = "LoadTimeLogsActive";
	
	/**
	 * Name of the property which controls whether the browser ajax load time log is active or not (values true/false)
	 */
	static final String JS_AJAX_TIMMING_LOG_ACTIVE_PROPERTY = "AjaxLoadTimeLogsActive";
	
	/**
	 * 
	 * Given a database returns the SQL Script  necessary to create the table to keep javascript error logs
	 * 
	 * @param db The current database
	 * 
	 * @return The SQL Script to create the table
	 */
	static String getJSErrorsTableCreateScript(Database db){
		switch (db){
			case ORACLE : 
				StringBuilder oracle = new StringBuilder(400);
				oracle.append("CREATE TABLE ");
				oracle.append(JS_ERROR_LOG_TABLE_NAME);
				oracle.append(" (");
				oracle.append(" USER_BOUI NUMBER,");
				oracle.append(" PROFILE_BOUI NUMBER,");
				oracle.append(" VIEW_ID VARCHAR(250),");
				oracle.append(" DATE_EVENT DATE,");
				oracle.append(" JS_ERROR_BLOCK CLOB,");
				oracle.append(" JS_ERROR_MESSAGE CLOB,");
				oracle.append(" LINE NUMBER,");
				oracle.append(" USER_AGENT VARCHAR(250),");
				oracle.append(" HOST VARCHAR(250),");
				oracle.append(" IP_ADDRESS VARCHAR(100),");
				oracle.append(" JS_FILE VARCHAR(250)");
				oracle.append(")");
				return oracle.toString();
			case MYSQL : 
				StringBuilder mysql = new StringBuilder(400);
				mysql.append("CREATE TABLE ");
				mysql.append(JS_ERROR_LOG_TABLE_NAME);
				mysql.append(" (");
				mysql.append(" USER_BOUI BIGINT,");
				mysql.append(" PROFILE_BOUI BIGINT,");
				mysql.append(" VIEW_ID VARCHAR(250),");
				mysql.append(" DATE_EVENT DATE,");
				mysql.append(" JS_ERROR_BLOCK TEXT,");
				mysql.append(" JS_ERROR_MESSAGE TEXT,");
				mysql.append(" LINE BIGINT,");
				mysql.append(" USER_AGENT VARCHAR(250),");
				mysql.append(" HOST VARCHAR(250),");
				mysql.append(" IP_ADDRESS VARCHAR(100),");
				mysql.append(" JS_FILE VARCHAR(250)");
				mysql.append(")");
				return mysql.toString();
			case MSSQL :
				StringBuilder sqlserver = new StringBuilder(400);
				sqlserver.append("CREATE TABLE ");
				sqlserver.append(JS_ERROR_LOG_TABLE_NAME);
				sqlserver.append(" (");
				sqlserver.append(" USER_BOUI BIGINT,");
				sqlserver.append(" PROFILE_BOUI BIGINT,");
				sqlserver.append(" VIEW_ID VARCHAR(250),");
				sqlserver.append(" DATE_EVENT DATE,");
				sqlserver.append(" JS_ERROR_BLOCK TEXT,");
				sqlserver.append(" JS_ERROR_MESSAGE TEXT,");
				sqlserver.append(" LINE BIGINT,");
				sqlserver.append(" USER_AGENT VARCHAR(250),");
				sqlserver.append(" HOST VARCHAR(250),");
				sqlserver.append(" IP_ADDRESS VARCHAR(100),");
				sqlserver.append(" JS_FILE VARCHAR(250)");
				sqlserver.append(")");
				return sqlserver.toString();
			case POSTGRES : 
				StringBuilder postgres = new StringBuilder(400);
				postgres.append("CREATE TABLE ");
				postgres.append(JS_ERROR_LOG_TABLE_NAME);
				postgres.append(" (");
				postgres.append(" USER_BOUI BIGINT,");
				postgres.append(" PROFILE_BOUI BIGINT,");
				postgres.append(" VIEW_ID VARCHAR(250),");
				postgres.append(" DATE_EVENT DATE,");
				postgres.append(" JS_ERROR_BLOCK TEXT,");
				postgres.append(" JS_ERROR_MESSAGE TEXT,");
				postgres.append(" LINE BIGINT,");
				postgres.append(" USER_AGENT VARCHAR(250),");
				postgres.append(" HOST VARCHAR(250),");
				postgres.append(" IP_ADDRESS VARCHAR(100),");
				postgres.append(" JS_FILE VARCHAR(250)");
				postgres.append(")");
				return postgres.toString();
			default :
				StringBuilder defaultDb = new StringBuilder(400);
				defaultDb.append("CREATE TABLE ");
				defaultDb.append(JS_ERROR_LOG_TABLE_NAME);
				defaultDb.append(" (");
				defaultDb.append(" USER_BOUI BIGINT,");
				defaultDb.append(" PROFILE_BOUI BIGINT,");
				defaultDb.append(" VIEW_ID VARCHAR(250),");
				defaultDb.append(" DATE_EVENT DATE,");
				defaultDb.append(" JS_ERROR_BLOCK CLOB,");
				defaultDb.append(" JS_ERROR_MESSAGE CLOB,");
				defaultDb.append(" LINE BIGINT,");
				defaultDb.append(" USER_AGENT VARCHAR(250),");
				defaultDb.append(" HOST VARCHAR(250),");
				defaultDb.append(" IP_ADDRESS VARCHAR(100),");
				defaultDb.append(" JS_FILE VARCHAR(250)");
				defaultDb.append(")");
				return defaultDb.toString();
		}
	}
	
	
	/**
	 * 
	 * Given a database returns the SQL Script  necessary to create the table to keep javascript ajax timing logs
	 * 
	 * @param db The current database
	 * 
	 * @return The SQL Script to create the table
	 */
	static String getJSAjaxTimingTableCreateScript(Database db){
		switch (db){
		case ORACLE : 
			StringBuilder oracle = new StringBuilder(400);
			oracle.append("CREATE TABLE ");
			oracle.append(JS_AJAX_TIMMING_LOG_TABLE_NAME);
			oracle.append(" (");
			oracle.append(" USER_BOUI NUMBER,");
			oracle.append(" PROFILE_BOUI NUMBER,");
			oracle.append(" VIEW_ID VARCHAR(250),");
			oracle.append(" DATE_EVENT DATE,");
			oracle.append(" LOAD_TIME NUMBER,");
			oracle.append(" PROCESS_TIME NUMBER,");
			oracle.append(" USER_AGENT VARCHAR(250),");
			oracle.append(" HOST VARCHAR(250),");
			oracle.append(" IP_ADDRESS VARCHAR(100)");
			oracle.append(")");
			return oracle.toString();
		case MYSQL : 
			StringBuilder mysql = new StringBuilder(400);
			mysql.append("CREATE TABLE ");
			mysql.append(JS_AJAX_TIMMING_LOG_TABLE_NAME);
			mysql.append(" (");
			mysql.append(" USER_BOUI BIGINT,");
			mysql.append(" PROFILE_BOUI BIGINT,");
			mysql.append(" VIEW_ID VARCHAR(250),");
			mysql.append(" DATE_EVENT DATE,");
			mysql.append(" LOAD_TIME BIGINT,");
			mysql.append(" PROCESS_TIME BIGINT,");
			mysql.append(" USER_AGENT VARCHAR(250),");
			mysql.append(" HOST VARCHAR(250),");
			mysql.append(" IP_ADDRESS VARCHAR(100)");
			mysql.append(")");
			return mysql.toString();
		case MSSQL :
			StringBuilder sqlServer = new StringBuilder(400);
			sqlServer.append("CREATE TABLE ");
			sqlServer.append(JS_AJAX_TIMMING_LOG_TABLE_NAME);
			sqlServer.append(" (");
			sqlServer.append(" USER_BOUI BIGINT,");
			sqlServer.append(" PROFILE_BOUI BIGINT,");
			sqlServer.append(" VIEW_ID VARCHAR(250),");
			sqlServer.append(" DATE_EVENT DATE,");
			sqlServer.append(" LOAD_TIME BIGINT,");
			sqlServer.append(" PROCESS_TIME BIGINT,");
			sqlServer.append(" USER_AGENT VARCHAR(250),");
			sqlServer.append(" HOST VARCHAR(250),");
			sqlServer.append(" IP_ADDRESS VARCHAR(100)");
			sqlServer.append(")");
			return sqlServer.toString();
		case POSTGRES : 
			StringBuilder postgres = new StringBuilder(400);
			postgres.append("CREATE TABLE ");
			postgres.append(JS_AJAX_TIMMING_LOG_TABLE_NAME);
			postgres.append(" (");
			postgres.append(" USER_BOUI BIGINT,");
			postgres.append(" PROFILE_BOUI BIGINT,");
			postgres.append(" VIEW_ID VARCHAR(250),");
			postgres.append(" DATE_EVENT DATE,");
			postgres.append(" LOAD_TIME BIGINT,");
			postgres.append(" PROCESS_TIME BIGINT,");
			postgres.append(" USER_AGENT VARCHAR(250),");
			postgres.append(" HOST VARCHAR(250),");
			postgres.append(" IP_ADDRESS VARCHAR(100)");
			postgres.append(")");
			return postgres.toString();
		case UNKNOWN :
		default : 
			StringBuilder defaultDatabase = new StringBuilder(400);
			defaultDatabase.append("CREATE TABLE ");
			defaultDatabase.append(JS_AJAX_TIMMING_LOG_TABLE_NAME);
			defaultDatabase.append(" (");
			defaultDatabase.append(" USER_BOUI BIGINT,");
			defaultDatabase.append(" PROFILE_BOUI BIGINT,");
			defaultDatabase.append(" VIEW_ID VARCHAR(250),");
			defaultDatabase.append(" DATE_EVENT DATE,");
			defaultDatabase.append(" LOAD_TIME BIGINT,");
			defaultDatabase.append(" PROCESS_TIME BIGINT,");
			defaultDatabase.append(" USER_AGENT VARCHAR(250),");
			defaultDatabase.append(" HOST VARCHAR(250),");
			defaultDatabase.append(" IP_ADDRESS VARCHAR(100)");
			defaultDatabase.append(")");
			return defaultDatabase.toString();
		}
	}
	
	/**
	 * 
	 * Given a database returns the SQL Script  necessary to create the table to keep load time logs
	 * 
	 * @param db The current database
	 * 
	 * @return The SQL Script to create the table
	 */
	static String getJSTimmingTableCreateScript(Database db){
		switch (db){
		case ORACLE : 
			StringBuilder oracle = new StringBuilder(400);
			oracle.append("CREATE TABLE ");
			oracle.append(JS_TIMMING_LOG_TABLE_NAME);
			oracle.append(" (");
			oracle.append(" USER_BOUI NUMBER,");
			oracle.append(" PROFILE_BOUI NUMBER,");
			oracle.append(" VIEW_ID VARCHAR(250),");
			oracle.append(" DATE_EVENT DATE,");
			oracle.append(" NAVIGATIONSTART NUMBER,");
			oracle.append(" UNLOADEVENTSTART NUMBER,");
			oracle.append(" UNLOADEVENTEND NUMBER,");
			oracle.append(" REDIRECTSTART NUMBER,");
			oracle.append(" REDIRECTEND NUMBER,");
			oracle.append(" FETCHSTART NUMBER,");
			oracle.append(" DOMAINLOOKUPSTART NUMBER,");
			oracle.append(" DOMAINLOOKUPEND NUMBER,");
			oracle.append(" CONNECTSTART NUMBER,");
			oracle.append(" CONNECTEND NUMBER,");
			oracle.append(" SECURECONNECTIONSTART NUMBER,");
			oracle.append(" REQUESTSTART NUMBER,");
			oracle.append(" RESPONSESTART NUMBER,");
			oracle.append(" RESPONSEEND NUMBER,");
			oracle.append(" DOMLOADING NUMBER,");
			oracle.append(" DOMINTERACTIVE NUMBER,");
			oracle.append(" DOMCONTENTLOADEDEVENTSTART NUMBER,");
			oracle.append(" DOMCONTENTLOADEDEVENTEND NUMBER,");
			oracle.append(" DOMCOMPLETE NUMBER,");
			oracle.append(" LOADEVENTSTART NUMBER,");
			oracle.append(" LOADEVENTEND NUMBER,");
			oracle.append(" USER_AGENT VARCHAR(250),");
			oracle.append(" HOST VARCHAR(250),");
			oracle.append(" IP_ADDRESS VARCHAR(100)");
			oracle.append(")");
			return oracle.toString();
		case MYSQL :
		case MSSQL :
		case POSTGRES :
		default : 
			StringBuilder defaultDb = new StringBuilder(400);
			defaultDb.append("CREATE TABLE ");
			defaultDb.append(JS_TIMMING_LOG_TABLE_NAME);
			defaultDb.append(" (");
			defaultDb.append(" USER_BOUI BIGINT,");
			defaultDb.append(" PROFILE_BOUI BIGINT,");
			defaultDb.append(" VIEW_ID VARCHAR(250),");
			defaultDb.append(" DATE_EVENT DATE,");
			defaultDb.append(" NAVIGATIONSTART BIGINT,");
			defaultDb.append(" UNLOADEVENTSTART BIGINT,");
			defaultDb.append(" UNLOADEVENTEND BIGINT,");
			defaultDb.append(" REDIRECTSTART BIGINT,");
			defaultDb.append(" REDIRECTEND BIGINT,");
			defaultDb.append(" FETCHSTART BIGINT,");
			defaultDb.append(" DOMAINLOOKUPSTART BIGINT,");
			defaultDb.append(" DOMAINLOOKUPEND BIGINT,");
			defaultDb.append(" CONNECTSTART BIGINT,");
			defaultDb.append(" CONNECTEND BIGINT,");
			defaultDb.append(" SECURECONNECTIONSTART BIGINT,");
			defaultDb.append(" REQUESTSTART BIGINT,");
			defaultDb.append(" RESPONSESTART BIGINT,");
			defaultDb.append(" RESPONSEEND BIGINT,");
			defaultDb.append(" DOMLOADING BIGINT,");
			defaultDb.append(" DOMINTERACTIVE BIGINT,");
			defaultDb.append(" DOMCONTENTLOADEDEVENTSTART BIGINT,");
			defaultDb.append(" DOMCONTENTLOADEDEVENTEND BIGINT,");
			defaultDb.append(" DOMCOMPLETE BIGINT,");
			defaultDb.append(" LOADEVENTSTART BIGINT,");
			defaultDb.append(" LOADEVENTEND BIGINT,");
			defaultDb.append(" USER_AGENT VARCHAR(250),");
			defaultDb.append(" HOST VARCHAR(250),");
			defaultDb.append(" IP_ADDRESS VARCHAR(100)");
			defaultDb.append(")");
			return defaultDb.toString();
		}
	}
	
	 /** 
	 * Given a database returns the SQL Script  necessary to create the table to keep application logic errors
	 * @param db The current database
	 * 
	 * @return The SQL Script to create the table
	 */
	public static String getDebugInfoTableCreateScript(Database db){
		switch (db) {
		case ORACLE : 
			StringBuilder oracle = new StringBuilder(400);
			oracle.append("CREATE TABLE ");
			oracle.append(XUIErrorLogger.XEO_ERROR_WITH_CONTEXT_LOG);
			oracle.append(" (");
			oracle.append(" USER_BOUI NUMBER,");
			oracle.append(" PROFILE_BOUI NUMBER,");
			oracle.append(" VIEW_ID VARCHAR(250),");
			oracle.append(" REQUEST_ID NUMBER,");
			oracle.append(" IS_AJAX VARCHAR(1),");
			oracle.append(" DATE_EVENT DATE,");
			oracle.append(" BEAN_CONTEXT CLOB,");
			oracle.append(" EVENT_CONTEXT CLOB,");
			oracle.append(" CUSTOM_CONTEXT CLOB,");
			oracle.append(" STACK_TRACE CLOB,");
			oracle.append(" HOST VARCHAR(250),");
			oracle.append(" IP_ADDRESS VARCHAR(100)");
			oracle.append(")");
			return oracle.toString();
		case MYSQL : 
			StringBuilder mysql = new StringBuilder(400);
			mysql.append("CREATE TABLE ");
			mysql.append(XUIErrorLogger.XEO_ERROR_WITH_CONTEXT_LOG);
			mysql.append(" (");
			mysql.append(" USER_BOUI BIGINT,");
			mysql.append(" PROFILE_BOUI BIGINT,");
			mysql.append(" VIEW_ID VARCHAR(250),");
			mysql.append(" REQUEST_ID BIGINT,");
			mysql.append(" IS_AJAX VARCHAR(1),");
			mysql.append(" DATE_EVENT DATE,");
			mysql.append(" BEAN_CONTEXT TEXT,");
			mysql.append(" EVENT_CONTEXT TEXT,");
			mysql.append(" CUSTOM_CONTEXT TEXT,");
			mysql.append(" STACK_TRACE TEXT,");
			mysql.append(" HOST VARCHAR(250),");
			mysql.append(" IP_ADDRESS VARCHAR(100)");
			mysql.append(")");
			return mysql.toString();
		case MSSQL :
			StringBuilder sqlserver = new StringBuilder(400);
			sqlserver.append("CREATE TABLE ");
			sqlserver.append(XUIErrorLogger.XEO_ERROR_WITH_CONTEXT_LOG);
			sqlserver.append(" (");
			sqlserver.append(" USER_BOUI BIGINT,");
			sqlserver.append(" PROFILE_BOUI BIGINT,");
			sqlserver.append(" VIEW_ID VARCHAR(250),");
			sqlserver.append(" REQUEST_ID BIGINT,");
			sqlserver.append(" IS_AJAX VARCHAR(1),");
			sqlserver.append(" DATE_EVENT DATE,");
			sqlserver.append(" BEAN_CONTEXT TEXT,");
			sqlserver.append(" EVENT_CONTEXT TEXT,");
			sqlserver.append(" CUSTOM_CONTEXT TEXT,");
			sqlserver.append(" STACK_TRACE TEXT,");
			sqlserver.append(" HOST VARCHAR(250),");
			sqlserver.append(" IP_ADDRESS VARCHAR(100)");
			sqlserver.append(")");
			return sqlserver.toString();
		case POSTGRES :
			StringBuilder postgres = new StringBuilder(400);
			postgres.append("CREATE TABLE ");
			postgres.append(XUIErrorLogger.XEO_ERROR_WITH_CONTEXT_LOG);
			postgres.append(" (");
			postgres.append(" USER_BOUI BIGINT,");
			postgres.append(" PROFILE_BOUI BIGINT,");
			postgres.append(" VIEW_ID VARCHAR(250),");
			postgres.append(" REQUEST_ID BIGINT,");
			postgres.append(" IS_AJAX VARCHAR(1),");
			postgres.append(" DATE_EVENT DATE,");
			postgres.append(" BEAN_CONTEXT TEXT,");
			postgres.append(" EVENT_CONTEXT TEXT,");
			postgres.append(" CUSTOM_CONTEXT TEXT,");
			postgres.append(" STACK_TRACE TEXT,");
			postgres.append(" HOST VARCHAR(250),");
			postgres.append(" IP_ADDRESS VARCHAR(100)");
			postgres.append(")");
			return postgres.toString();
		default :
			StringBuilder defaultDb = new StringBuilder(400);
			defaultDb.append("CREATE TABLE ");
			defaultDb.append(XUIErrorLogger.XEO_ERROR_WITH_CONTEXT_LOG);
			defaultDb.append(" (");
			defaultDb.append(" USER_BOUI BIGINT,");
			defaultDb.append(" PROFILE_BOUI BIGINT,");
			defaultDb.append(" VIEW_ID VARCHAR(250),");
			defaultDb.append(" REQUEST_ID BIGINT,");
			defaultDb.append(" IS_AJAX VARCHAR(1),");
			defaultDb.append(" DATE_EVENT DATE,");
			defaultDb.append(" BEAN_CONTEXT CLOB,");
			defaultDb.append(" EVENT_CONTEXT CLOB,");
			defaultDb.append(" CUSTOM_CONTEXT CLOB,");
			defaultDb.append(" STACK_TRACE CLOB,");
			defaultDb.append(" HOST VARCHAR(250),");
			defaultDb.append(" IP_ADDRESS VARCHAR(100)");
			defaultDb.append(")");
			return defaultDb.toString();
		}
	}
}
