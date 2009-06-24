package netgest.bo.xwc.components.security;

import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.beans.XEOBaseList;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUISessionContext;

public class XVWAccessPolicyListBean extends XEOBaseList {

	public void build() throws boRuntimeException {
		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
		XUISessionContext oSessionContext = oRequestContext.getSessionContext();
		try {
			ViewerAccessPolicyBuilder accessPolicyBuilder = new ViewerAccessPolicyBuilder();
			accessPolicyBuilder.buildAccessPolicy( getEboContext(), oSessionContext );
		} catch ( Exception e) {
			throw new boRuntimeException( "", "", e );
		}
	}
	
}
