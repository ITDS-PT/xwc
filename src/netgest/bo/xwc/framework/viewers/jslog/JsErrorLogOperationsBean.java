package netgest.bo.xwc.framework.viewers.jslog;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import netgest.bo.data.oracle.OracleDBM;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.XEO;
import netgest.bo.system.boApplication;
import netgest.bo.system.boSession;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;

public class JsErrorLogOperationsBean extends XEOBaseBean {
	
	private static boolean initialized = Boolean.FALSE;
	
	@SuppressWarnings("unchecked")
	public void logError(){
		
		EboContext ctx = boApplication.currentContext().getEboContext();
		boSession session = null;
		boolean created = false;
		try {
			if (ctx == null){
				session = XEO.loginAs("ROBOT");
				ctx = session.createEboContext();
				created = true;
			} 
			if (!initialized){
				init(ctx);
			}

			HttpServletRequest request = (HttpServletRequest) getRequestContext().getRequest();
			Map<?,?> parameters = request.getParameterMap();
			insertRecords(ctx,(Map<String,String[]>)parameters,request);
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

	private void insertRecords(EboContext ctx, Map<String,String[]> parameters, HttpServletRequest request){
		
		long userBoui = ctx.getBoSession().getPerformerBoui();
		long profileBoui = ctx.getBoSession().getPerformerIProfileBoui();
		JsErrorLogger jsLogger = new JsErrorLogger(ctx.getConnectionData(), LoggerConstants.JS_ERROR_LOG_TABLE_NAME);
		jsLogger.insertNewRecord(userBoui,profileBoui,parameters, request.getLocalName());
		
	}

	private synchronized void init(EboContext ctx) {
		
		Connection connection = null;
		try {
			if (!initialized){
				connection = ctx.getConnectionData();
				JsErrorLogger logger = new JsErrorLogger(connection, LoggerConstants.JS_ERROR_LOG_TABLE_NAME);
				OracleDBM dbm = ctx.getBoSession().getRepository().getDriver().getDBM();
				logger.init(LoggerConstants.getJSErrorsTableCreateScript(dbm.getDatabase()));
				initialized = Boolean.TRUE;
			}
		} finally {
			if (connection != null){
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}

	
}
