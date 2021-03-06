package netgest.bo.xwc.framework;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import netgest.bo.data.oracle.OracleDBM.Database;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.Logger;
import netgest.bo.system.XEO;
import netgest.bo.system.boApplication;
import netgest.bo.system.boApplicationConfig;
import netgest.bo.system.boSession;
import netgest.bo.xwc.framework.http.XUIHttpRequest;
import netgest.bo.xwc.framework.viewers.jslog.LogRecord;
import netgest.bo.xwc.framework.viewers.jslog.LoggerConstants;
import netgest.utils.StringUtils;

public class XUIErrorLogger {
	
	private static final Logger logger = Logger.getLogger(XUIErrorLogger.class);
	private String dataSource = null;
	
	public static final XUIErrorLogger getLogger(){
			boApplicationConfig boconfig = boApplication.getXEO().getApplicationConfig();
			String alternative = boconfig.getProperty(LoggerConstants.LOGGING_ALTERNATIVE_DATASOURCE_PROPERTY);
			if (StringUtils.hasValue(alternative)){
				return new XUIErrorLogger(alternative);
			}else{
				return new XUIErrorLogger();
			}
	}
	
	private XUIErrorLogger(String dataSource){
		this.dataSource = dataSource;
	}
	
	private XUIErrorLogger(){
		this.dataSource = null;
	}
	
	/**
	 * 
	 * Adds debug info in the current default {@link EboContext} 
	 * and {@link XUIRequestContext}. Searches the EboContext and XUIRequest context
	 * for information such as the currently logged user, his profile, the current viewId
	 * and searches the beans for the current context
	 * 
	 * @param customContext
	 */
	public static void addDebugInfo(String customContext){
		XUIRequestContext request = XUIRequestContext.getCurrentContext();
		request.addCustomDebugContext(customContext);
	}
	
	private Connection getConnection(EboContext ctx){
		if (StringUtils.isEmpty(dataSource)){
			if (ctx != null)
				return ctx.getConnectionData();
		} else {
			try {
				InitialContext ic = new InitialContext();
				DataSource databaseSource = (DataSource) ic.lookup(dataSource);
				return databaseSource.getConnection();
			} catch (NamingException e) {
				logger.warn("Could not find JNDI name %s" , dataSource, e);
			} catch (SQLException e) {
				logger.warn("Could not get new connection from JNDI name %s" , dataSource, e);
			}
		}
		return null;
	}
	
	public void logViewError(HttpServletRequest request, EboContext ctx, XUIRequestContextDebugInfo debug, String customContext, Exception e){
		Connection conn = getConnection(ctx);
		logViewError(request, ctx, debug, customContext, e, conn);
	}
	
	/**
	 * 
	 * Logs an error in the current execution, extracts information from EboContext 
	 * (logged user, profile, hostname) and XUIRequestContext (viewId, beanContext
	 * eventContext, etc...) 
	 * 
	 * @param ctx
	 * @param debug
	 * @param customContext
	 * @param e
	 */
	void logViewError(HttpServletRequest request, EboContext ctx, XUIRequestContextDebugInfo debug, String customContext, Exception e, Connection connection){
		
		long requestId = debug.getRequestId();
		long userBoui = -1;
		long profileBoui = -1;
		boolean createdContext = false;
		
		if (ctx != null){
			boSession session = ctx.getBoSession();
			if (session != null){
				userBoui = session.getPerformerBoui();
				profileBoui = session.getPerformerIProfileBoui();
			}
		} else {
			boSession session = XEO.loginAs("ROBOT");
			ctx = session.createEboContext();
			userBoui = session.getPerformerBoui();
			profileBoui = session.getPerformerIProfileBoui();
			createdContext = true;
		}
		String viewId = debug.getMainViewId();
		boolean isAjax = debug.isAjaxRequest();
		String hostname = "";
		String ipAddress = "";
		if (request != null){
			try{
				hostname = request.getLocalName();
			} catch (Throwable e1){
				hostname = request.getServerName();
			}
			ipAddress = XUIHttpRequest.
					getClientIpFromRequest(request);
		}
		String beanContext = debug.getBeanContext();
		String eventContext = debug.getEventContext();
		
		if (StringUtils.isEmpty(hostname))
			hostname = "";
		
		StringWriter s = new StringWriter();
		PrintWriter pw = new PrintWriter(s);
		if (e != null){
			e.printStackTrace(pw);
		}
		
		List<String> customContextContent = debug.getCustomContext();
		if (StringUtils.hasValue(customContext)){
			customContextContent.add(customContext);
		}
		
		String customContextToLog = "";
		for (String currentContext : customContextContent){
			customContextToLog += currentContext + ";";
		}
		
		try {
			if (connection == null){
				connection = ctx.getConnectionData();
			}
			
			Database db = ctx.getBoSession().getRepository().getDriver().getDBM().getDatabase();
			insertRecord(connection
					, db
					, userBoui
					, profileBoui
					, requestId
					, hostname
					, isAjax
					, viewId
					, beanContext.toString()
					, eventContext
					, customContextToLog
					, s.toString()
					, ipAddress);
		} catch (SQLException e1) {
			logger.warn("Could not insert new record into debug table", e1);
		} finally {
			if (connection != null){
				try {
					connection.close();
				} catch (SQLException e1) {
					logger.warn("Closing connection",e1);
				}
			}
			if (createdContext){
				if (ctx != null){
					ctx.close();
				}
			}
		}
		
	}
	
	static final int TEXT_COLUMN_SIZE = 250;
	public static final String XEO_ERROR_WITH_CONTEXT_LOG = "XEO_DEBUG_INFO_ON_ERROR";
	
	boolean isInit(Connection connection) throws SQLException {
			
			ResultSet result = connection.getMetaData().getTables(null, null, XEO_ERROR_WITH_CONTEXT_LOG , null);
			ResultSet tableCount = null;
			Statement tableStatement = null;
			try {
				result.next();
				tableStatement = connection.createStatement(); 
				tableCount = tableStatement.executeQuery( String.format("select count(*) from %s",XEO_ERROR_WITH_CONTEXT_LOG) );
				return true;
			} catch (SQLException e){
				e.printStackTrace();
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
	
	void init(Connection connection, String tableScript){
		Statement createTable = null;
		try {
			if (!isInit(connection)){
				createTable = connection.createStatement();
				createTable.executeUpdate(tableScript);
			}
		} catch (SQLException e ){
			logger.warn("Could not check if debug table %s was created or could not create the table"
					, XEO_ERROR_WITH_CONTEXT_LOG ,e);
		} finally {
			closeStatement(createTable);
		}
	}
	
	void closeStatement(Statement st){
		try {
			if (st != null){
				st.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally { 
			
		}
	}
	
	void insertRecord(Connection connection, Database db, long userBoui, long profileBoui,
			long requestId, String hostname, boolean isAjax, String viewId, String beanContext,
			String eventContext, String customContext, String exceptionStack, String ipAddress) throws SQLException{
		
		if (!isInit(connection)){
			init(connection,LoggerConstants.getDebugInfoTableCreateScript(db));
		}
		
		List<LogRecord> values = new ArrayList<LogRecord>();
		values.add(new LogRecord("USER_BOUI", userBoui));
		values.add(new LogRecord("PROFILE_BOUI", profileBoui));
		values.add(new LogRecord("VIEW_ID", truncateTo(viewId,TEXT_COLUMN_SIZE)));
		values.add(new LogRecord("REQUEST_ID", requestId));
		if (isAjax){
			values.add(new LogRecord("IS_AJAX", "1"));
		} else {
			values.add(new LogRecord("IS_AJAX", "0"));
		}
		values.add(new LogRecord("DATE_EVENT", new Timestamp(System.currentTimeMillis())));
		values.add(new LogRecord("BEAN_CONTEXT", beanContext));
		values.add(new LogRecord("EVENT_CONTEXT", eventContext));
		values.add(new LogRecord("CUSTOM_CONTEXT", customContext));
		values.add(new LogRecord("STACK_TRACE", exceptionStack));
		values.add(new LogRecord("HOST", truncateTo(hostname,TEXT_COLUMN_SIZE)));
		values.add(new LogRecord("IP_ADDRESS", truncateTo(ipAddress,TEXT_COLUMN_SIZE)));
		
		StringBuilder s = new StringBuilder(200);
		s.append("INSERT INTO ");
		s.append(XEO_ERROR_WITH_CONTEXT_LOG);
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
			logger.warn("Could not insert new record on table %s "
					, XEO_ERROR_WITH_CONTEXT_LOG ,e);
		} finally {
			closeStatement(newRecord);
		}
		
	}
	
	private String truncateTo(String value, int max) {
		if (max > value.length())
			max = value.length();
		return value.substring(0, max);
	}

}
