package netgest.bo.xwc.framework.viewers;

import netgest.bo.xwc.framework.XUIRequestContext;

public class SystemOperationsBean {

	public void closeView() {
		XUIRequestContext rc = XUIRequestContext.getCurrentContext();
		/*
		String viewId = rc.getRequestParameterMap().get( "viewId" );
		if( viewId != null ) {
            XUIStateManagerImpl oStateManagerImpl = (XUIStateManagerImpl)Util.getStateManager( FacesContext.getCurrentInstance() );
            oStateManagerImpl.closeView( viewId );
		}
		*/
		rc.responseComplete();
		rc.getViewRoot().dispose();
		
	}
	
	public void releaseAll() {
		XUIRequestContext rc = XUIRequestContext.getCurrentContext();
		rc.getTransactionManager().releaseAll();
	}
	

}
