package netgest.bo.xwc.framework.errorLogging;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;




public class ViewStateDebug {
	
	public static final String VIEW_DEBUG_SESSION_NAME = "XUI:VIEWDEBUG";
    private static final Log log = LogFactory.getLog(ViewStateDebug.class);
	
	public static void viewCreated( String viewId, String stateId ) {
		ViewStateDebugInfo debugInfo = getDebugInfo(stateId);
		debugInfo.setViewId( viewId );
		debugInfo.setCreatedTime( System.currentTimeMillis() );
		debugInfo.setCreateStack( new Throwable().getStackTrace() );
	}

	public static void viewRestored( String stateId ) {
		ViewStateDebugInfo debugInfo = getDebugInfo(stateId);
		debugInfo.setLastUsedTime( System.currentTimeMillis() );
	}
	
	public static void viewClosed( String stateId ) {
		ViewStateDebugInfo debugInfo = getDebugInfo(stateId);
		debugInfo.setCloseTime( System.currentTimeMillis() );
		debugInfo.setCloseStack( new Throwable().getStackTrace() );
	}
	
	public static void viewNotFound( String stateId ) {
		ViewStateDebugInfo debugInfo = getDebugInfo(stateId);
		debugInfo.setNotfoundTime( System.currentTimeMillis() );
		debugNotFound( null,stateId );
	}
	
	public static void debugNotFound( String adicionalInfo, String stateId ) {
		ViewStateDebugInfo debugInfo = getDebugInfo(stateId);
		log.error( debugInfo.generateDebugInfo(  adicionalInfo ) );
	}
	
	public static void debugIfClosed( String adicionalInfo, String stateId ) {
		ViewStateDebugInfo debugInfo = getDebugInfo(stateId);
		if( debugInfo.getCloseTime() != 0 ) {
			log.error( debugInfo.generateDebugInfo(  adicionalInfo ) );
		}
	}
	
	private static synchronized ViewStateDebugInfo getDebugInfo( String stateId ) {
		Map<String,Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap();
			
		if( sessionMap != null ) {
			@SuppressWarnings("unchecked")
			LinkedHashMap<String,ViewStateDebugInfo> debugList = (LinkedHashMap<String,ViewStateDebugInfo>)sessionMap.get( VIEW_DEBUG_SESSION_NAME );
			if( debugList == null ) {
				debugList = createLimitedHashMap();
				sessionMap.put( VIEW_DEBUG_SESSION_NAME, debugList );
			}
			
			ViewStateDebugInfo debugInfo = debugList.get( stateId );
			
			if( debugInfo == null ) {
				debugInfo  = new ViewStateDebugInfo();
				debugInfo.setStateId(stateId);
				debugList.put( stateId, debugInfo );
				
			}
			return debugInfo;
		}
		return new ViewStateDebugInfo();
	}

	private static LinkedHashMap<String,ViewStateDebugInfo> createLimitedHashMap() {
		return new LinkedHashMap<String, ViewStateDebugInfo>(2000) {
			private static final long serialVersionUID = 4727290244531780711L;

			@Override
			protected boolean removeEldestEntry(java.util.Map.Entry<String, ViewStateDebugInfo> eldest) {
				return size() > 2000;
			}
		};
	}
	
}
