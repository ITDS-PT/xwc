package netgest.bo.xwc.framework.viewers;

import javax.faces.context.FacesContext;

import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.errorLogging.ViewStateDebug;
import netgest.bo.xwc.framework.errorLogging.ViewStateDebugInfo;
import netgest.bo.xwc.framework.jsf.XUIStateManagerImpl;

import com.sun.faces.util.Util;

public class SystemOperationsBean {

	public void closeView() {
		XUIRequestContext rc = XUIRequestContext.getCurrentContext();
		String viewId = rc.getRequestParameterMap().get( "viewId" );
		if( viewId != null ) {
			XUIStateManagerImpl oStateManagerImpl = (XUIStateManagerImpl)Util.getStateManager( FacesContext.getCurrentInstance() );
			if( oStateManagerImpl.existsView( viewId ) ) {
				
				ViewStateDebugInfo info = ViewStateDebug.getDebugInfo( viewId );
				info.setCloseStack( (new Throwable()).getStackTrace() );
				info.setCloseTime( System.currentTimeMillis() );
				
//	            XUIViewRoot viewToClose = rc.getSessionContext().getView( viewId );
//	            if( viewToClose != null ) {
//	            	viewToClose.dispose();
//	            }
			}
		}
		rc.responseComplete();
		rc.getViewRoot().dispose();
	}
	
	public void releaseAll() {
		XUIRequestContext rc = XUIRequestContext.getCurrentContext();
		rc.getTransactionManager().releaseAll();
	}
	

}
