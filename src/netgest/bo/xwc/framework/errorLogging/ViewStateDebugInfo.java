package netgest.bo.xwc.framework.errorLogging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import netgest.bo.xwc.framework.XUIRequestContext;

public class ViewStateDebugInfo {
	
	private String viewId;
	private String stateId;
	private long   createdTime;
	private long   lastUsedTime;
	private long   closeTime;
	private long   notfoundTime;
	
	private StackTraceElement[] createStack;
	private StackTraceElement[] closeStack;
	
	public String generateDebugInfo() {
		return generateDebugInfo( null );
	}
	
	public String generateDebugInfo( String adicionalInfo ) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		StringBuilder createStackString = new StringBuilder();
		StringBuilder closeStackString = new StringBuilder();
		StringBuilder actualStackString = new StringBuilder();
		
		if( createStack != null ) {
			for( StackTraceElement stackElement : this.createStack ) {
				createStackString.append( "\t" ).append( stackElement.toString() ).append( "\n" );
			}
		}
		
		if( closeStack != null ) {
			for( StackTraceElement stackElement : this.closeStack ) {
				closeStackString.append( "\t" + stackElement.toString() + "\n" );
			}
		}
		
		for( StackTraceElement stackElement : new Throwable().getStackTrace() ) {
			actualStackString.append( "\t" + stackElement.toString() + "\n" );
		}
		
		
		HttpServletRequest request = (HttpServletRequest)XUIRequestContext.getCurrentContext().getRequest();
		
		StringBuilder requestHttp = new StringBuilder();
		requestHttp.append(  
		"--------------------------------------------------------------------------------------" + "\n" +
	    "URL           :" + request.getRequestURL().toString() + "\n" +
	    "Query String  :" + request.getQueryString() + "\n" +
	    "Parameters    :\n");
		
		@SuppressWarnings("unchecked")
		Enumeration<String> enumName = request.getParameterNames();
		if( enumName != null ) {
		    for( ;enumName.hasMoreElements();  ) {
		    	String parName = enumName.nextElement();
		    	requestHttp.append( "                " );
		    	requestHttp.append( parName );
		    	requestHttp.append( ":" );
		    	String[] values = request.getParameterValues( parName );
		    	if( values != null ) {
		    		for( String value : values ) {
		    			requestHttp.append( '[' );
		    			requestHttp.append( value );
		    			requestHttp.append( ']' );
		    		}
		    	}
		    	requestHttp.append( '\n' );
		    }
		}
		
		
		String debugString = 
		"\n======================================================================================\n" +
	    "Adicional Info:" + adicionalInfo + "\n" +
	    "View Id       :" + this.viewId + "\n" +
	    "State Id      :" + this.stateId + "\n" +
		"Created       :" + sdf.format( new Date( this.createdTime ) ) + "\n" +
		"Last Used     :" + sdf.format( new Date( this.lastUsedTime ) ) + "\n" +
		"CloseTime     :" + sdf.format( new Date( this.closeTime ) ) + "\n" +
		"Not Found     :" + sdf.format( new Date( this.notfoundTime ) ) + "\n" +
		requestHttp +
		"--------------------------------------------------------------------------------------" + "\n" +
		"Creation Stack:\n" + createStackString + "\n" +
		"--------------------------------------------------------------------------------------" + "\n" +
		"Close    Stack:\n" + closeStackString + "\n" +
		"--------------------------------------------------------------------------------------" + "\n" +
		"Actual   Stack:\n" + actualStackString + "\n" +
		"======================================================================================\n";
			
		return debugString;
	}


	public String getViewId() {
		return viewId;
	}


	public void setViewId(String viewId) {
		this.viewId = viewId;
	}


	public String getStateId() {
		return stateId;
	}


	public void setStateId(String stateId) {
		this.stateId = stateId;
	}


	public long getCreatedTime() {
		return createdTime;
	}


	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}


	public long getLastUsedTime() {
		return lastUsedTime;
	}


	public void setLastUsedTime(long lastUsedTime) {
		this.lastUsedTime = lastUsedTime;
	}


	public long getCloseTime() {
		return closeTime;
	}


	public void setCloseTime(long closeTime) {
		this.closeTime = closeTime;
	}


	public long getNotfoundTime() {
		return notfoundTime;
	}


	public void setNotfoundTime(long notfoundTime) {
		this.notfoundTime = notfoundTime;
	}


	public StackTraceElement[] getCreateStack() {
		return createStack;
	}


	public void setCreateStack(StackTraceElement[] createStack) {
		this.createStack = createStack;
	}


	public StackTraceElement[] getCloseStack() {
		return closeStack;
	}


	public void setCloseStack(StackTraceElement[] closeStack) {
		this.closeStack = closeStack;
	}
	

}
