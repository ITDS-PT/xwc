package netgest.bo.xwc.framework.viewers.jslog;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import netgest.bo.data.oracle.OracleDBM;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.Logger;
import netgest.bo.system.XEO;
import netgest.bo.system.boApplication;
import netgest.bo.system.boApplicationConfig;
import netgest.bo.system.boSession;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.annotations.XUIWebDefaultCommand;
import netgest.bo.xwc.framework.http.XUIHttpRequest;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;
import netgest.utils.StringUtils;

public class JsErrorLogOperationsBean extends XEOBaseBean {
	
	private static boolean initialized = Boolean.FALSE;
	private static String dataSource = null;
	private static final Logger logger = Logger.getLogger(TimingOperationsBean.class);
	private boolean isActive = true;
	
	@XUIWebDefaultCommand
	public void init(){
		boApplicationConfig boconfig = boApplication.getXEO().getApplicationConfig();
		if (dataSource == null){
			String alternative = boconfig.getProperty(LoggerConstants.LOGGING_ALTERNATIVE_DATASOURCE_PROPERTY);
			if (StringUtils.hasValue(alternative)){
				dataSource = alternative;
			}
		}
		String isActiveRaw = boconfig.getProperty(LoggerConstants.JS_ERROR_LOG_ACTIVE_PROPERTY);
		if (StringUtils.hasValue(isActiveRaw)){
			if (!Boolean.parseBoolean( isActiveRaw )){
				isActive = false;
			}
		}
	}
	
	public void logError(){
		
		Connection connection = null;
		try {
			if (isActive){
				EboContext ctx = boApplication.currentContext().getEboContext();
				boolean log = true;


				if (StringUtils.isEmpty(dataSource)){
					if (ctx != null)
						connection = ctx.getConnectionData();
				} else {
					try {
						InitialContext ic = new InitialContext();
						DataSource databaseSource = (DataSource) ic.lookup(dataSource);
						connection = databaseSource.getConnection();
					} catch (NamingException e) {
						log = false;
						logger.warn(e);
					} catch (SQLException e) {
						log = false;
						logger.warn(e );
					}
				}
				if (log)
					logError(ctx, connection);
			} 
		} finally { 
			disposeView();
			if (connection != null){
				try {
					connection.close();
				} catch (SQLException e) {
					logger.warn("Error Closing connection",e );
				}
			}
		}
		
	}

	private void disposeView() { 
		try {
			XUIRequestContext rc = getRequestContext();
			rc.responseComplete();
			rc.getViewRoot().dispose();
		} catch (Exception e) {
			logger.warn("Error while disposing of view ",e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void logError(EboContext ctx, Connection connection){
		
		boSession session = null;
		boolean created = false;
		try {
			if (ctx == null){
				session = XEO.loginAs("ROBOT");
				ctx = session.createEboContext();
				created = true;
			} 
			
			if (!initialized){
				init(ctx,connection);
			}
			

			HttpServletRequest request = (HttpServletRequest) getRequestContext().getRequest();
			Map<?,?> parameters = request.getParameterMap();
			insertRecords(ctx,connection,(Map<String,String[]>)parameters,request);
		} finally {
			if (created){
				if (ctx != null){
					ctx.close();
				}
				if (session != null){
					session.closeSession();
				}
			}
		}
	}

	private void insertRecords(EboContext ctx, Connection connection, Map<String,String[]> parameters, HttpServletRequest request){
		
		long userBoui = ctx.getBoSession().getPerformerBoui();
		long profileBoui = ctx.getBoSession().getPerformerIProfileBoui();
		JsErrorLogger jsLogger = new JsErrorLogger(connection, LoggerConstants.JS_ERROR_LOG_TABLE_NAME);
		String ipAddress = XUIHttpRequest.getClientIpFromRequest(request);
		jsLogger.insertNewRecord(userBoui,profileBoui,parameters, request.getLocalName(),ipAddress);
		
	}

	private synchronized void init(EboContext ctx, Connection connection) {
		boolean initOk = false;
		try {
			 if (connection != null){
				 JsErrorLogger logger = new JsErrorLogger(connection, LoggerConstants.JS_ERROR_LOG_TABLE_NAME);
				 OracleDBM dbm = ctx.getBoSession().getRepository().getDriver().getDBM();
				 logger.init(LoggerConstants.getJSErrorsTableCreateScript(dbm.getDatabase()));
				 initOk = true;
			 }
		} catch (Exception e){
			initOk = false;
		} finally {
			if (initOk){
				initialized = Boolean.TRUE;
			}
		}
		
		
	}

	
}
