package netgest.bo.xwc.framework.errorLogging;

import netgest.bo.runtime.EboContext;
import netgest.bo.xwc.framework.XUIRequestContext;

public class ErrorLogger {
	
	public void logViewError(EboContext ctx, XUIRequestContext requestContext, String viewid, String context, Exception e){
		
		long newId = getNewId();
		
		//
		
	}

	private long getNewId() {
		// TODO Auto-generated method stub
		return 0;
	}

}
