package netgest.bo.xwc.framework.viewers.jslog;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import netgest.bo.runtime.EboContext;
import netgest.bo.system.XEO;
import netgest.bo.system.boApplication;
import netgest.bo.system.boSession;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;

public class TimingOperationsBean extends XEOBaseBean {
	
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
			insertRecords(ctx,(Map<String,String[]>)parameters);
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

	private void insertRecords(EboContext ctx, Map<String,String[]> parameters){
		
		long userBoui = ctx.getBoSession().getPerformerBoui();
		long profileBoui = ctx.getBoSession().getPerformerIProfileBoui();
		JsTimmingLogger jsLogger = new JsTimmingLogger(ctx.getConnectionData(), LoggerConstants.JS_TIMMING_LOG_TABLE_NAME);
		jsLogger.insertNewRecord(userBoui,profileBoui,parameters);
		
	}

	private synchronized void init(EboContext ctx) {
		
		Connection connection = null;
		try {
			if (!initialized){
				connection = ctx.getConnectionData();
				JsErrorLogger logger = new JsErrorLogger(connection, LoggerConstants.JS_ERROR_LOG_TABLE_NAME);
				logger.init(LoggerConstants.getJSErrorsTableCreateScript());
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
