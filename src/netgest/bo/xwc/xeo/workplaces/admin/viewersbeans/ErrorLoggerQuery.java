package netgest.bo.xwc.xeo.workplaces.admin.viewersbeans;

import netgest.bo.data.DriverUtils;
import netgest.bo.xwc.framework.XUIErrorLogger;

public class ErrorLoggerQuery {
	
	public static final String getDefaultQuery(DriverUtils utils) {
		String query = "select USERNAME,VIEW_ID,DATE_EVENT,STACK_TRACE,BEAN_CONTEXT,EVENT_CONTEXT,CUSTOM_CONTEXT,PROFILE_BOUI,HOST,IS_AJAX,REQUEST_ID from " 
				+ XUIErrorLogger.XEO_ERROR_WITH_CONTEXT_LOG + " left join oixeouser on BOUI = USER_BOUI";
		return query;
	}

}
