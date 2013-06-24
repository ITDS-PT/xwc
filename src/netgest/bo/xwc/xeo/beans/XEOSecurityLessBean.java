package netgest.bo.xwc.xeo.beans;

import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.security.ComponentSecurityMap;

public abstract class XEOSecurityLessBean {

	private ComponentSecurityMap componentSecurityMap;

	public XEOSecurityLessBean() {
	}
	
	public ComponentSecurityMap getViewerPermissions() throws boRuntimeException {
		componentSecurityMap = new ComponentSecurityMap( null, null );
		return componentSecurityMap;
	}
	
	
}
