package netgest.bo.xwc.framework.viewers;

import javax.faces.context.FacesContext;

import com.sun.faces.util.Util;

import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.jsf.XUIStateManagerImpl;

public class SystemOperationsBean {

	public void closeView() {
		
		XUIRequestContext rc = XUIRequestContext.getCurrentContext();
		String viewId = rc.getRequestParameterMap().get( "viewId" );
		if( viewId != null ) {
            XUIStateManagerImpl oStateManagerImpl = (XUIStateManagerImpl)Util.getStateManager( FacesContext.getCurrentInstance() );
            oStateManagerImpl.closeView( viewId );
		}
		rc.responseComplete();
		rc.getViewRoot().dispose();
		
	}
	
	public void releaseAll() {
		XUIRequestContext rc = XUIRequestContext.getCurrentContext();
		rc.getTransactionManager().releaseAll();
	}
	

}
