package netgest.bo.xwc.framework.viewers.jslog;

public class LoggerConstants {
	
	static String JS_ERROR_LOG_TABLE_NAME = "XEO_JS_ERROR_LOGS";
	
	static String JS_TIMMING_LOG_TABLE_NAME = "XEO_JS_TIMMING_LOGS";
	
	static String getJSErrorsTableCreateScript(){
		StringBuilder b = new StringBuilder(400);
		b.append("CREATE TABLE ");
		b.append(JS_ERROR_LOG_TABLE_NAME);
		b.append(" (");
		b.append(" USER_BOUI NUMBER,");
		b.append(" PROFILE_BOUI NUMBER,");
		b.append(" VIEW_ID VARCHAR(250),");
		b.append(" DATE_EVENT DATE,");
		b.append(" JS_ERROR_BLOCK CLOB,");
		b.append(" JS_ERROR_MESSAGE CLOB,");
		b.append(" LINE NUMBER,");
		b.append(" USER_AGENT VARCHAR(250)");
		
		b.append(")");
		return b.toString();
	}
	
	static String getJSTimmingTableCreateScript(){
		StringBuilder b = new StringBuilder(400);
		b.append("CREATE TABLE ");
		b.append(JS_ERROR_LOG_TABLE_NAME);
		b.append(" (");
		b.append(" USER_BOUI NUMBER,");
		b.append(" PROFILE_BOUI NUMBER,");
		b.append(" VIEW_ID VARCHAR(250),");
		b.append(" DATE_EVENT DATE,");
		b.append(" NAVIGATIONSTART NUMBER,");
		b.append(" UNLOADEVENTSTART NUMBER,");
		b.append(" UNLOADEVENTEND NUMBER,");
		b.append(" REDIRECTSTART NUMBER,");
		b.append(" REDIRECTEND NUMBER,");
		b.append(" FETCHSTART NUMBER,");
		b.append(" DOMAINLOOKUPSTART NUMBER,");
		b.append(" DOMAINLOOKUPEND NUMBER,");
		b.append(" CONNECTSTART NUMBER,");
		b.append(" CONNECTEND NUMBER,");
		b.append(" SECURECONNECTIONSTART NUMBER,");
		b.append(" REQUESTSTART NUMBER,");
		b.append(" RESPONSESTART NUMBER,");
		b.append(" RESPONSEEND NUMBER,");
		b.append(" DOMLOADING NUMBER,");
		b.append(" DOMINTERACTIVE NUMBER,");
		b.append(" DOMCONTENTLOADEDEVENTSTART NUMBER,");
		b.append(" DOMCONTENTLOADEDEVENTEND NUMBER,");
		b.append(" DOMCOMPLETE NUMBER,");
		b.append(" LOADEVENTSTART NUMBER,");
		b.append(" LOADEVENTEND NUMBER,");
		b.append(" USER_AGENT VARCHAR(250)");
		
		b.append(")");
		return b.toString();
	}
	
}
