package netgest.bo.xwc.framework;

import netgest.bo.xwc.framework.components.XUIViewRoot;

public interface XUIViewBean {

	public void setViewRoot( String sViewStateId );
	
	public XUIViewRoot getViewRoot();
	
}
