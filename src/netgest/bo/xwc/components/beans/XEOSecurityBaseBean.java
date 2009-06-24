package netgest.bo.xwc.components.beans;

import java.util.Map;

import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectContainer;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.security.ComponentSecurityMap;
import netgest.bo.xwc.components.security.ViewerAccessPolicyBuilder;

public abstract class XEOSecurityBaseBean extends boObjectContainer {

	private ComponentSecurityMap componentSecurityMap;

	public XEOSecurityBaseBean(EboContext ctx) {
		super(ctx);
	}
	
	public ComponentSecurityMap getViewerPermissions() throws boRuntimeException {
		return componentSecurityMap;
	}

	public void initializeSecurityMap( ViewerAccessPolicyBuilder accessPolicyBuilder, String viewerName ) throws boRuntimeException {
		EboContext ctx = null;
		boObject userObj = null;
		Map<String, Byte> viewerPermissions = null;
		Map<String, String> idsByComponent = null;
		if ( ViewerAccessPolicyBuilder.applyViewerSecurity ) {
			ctx = boApplication.currentContext().getEboContext();
			userObj = boObject.getBoManager().loadObject( ctx, ctx.getSysUser().getBoui() );
			viewerPermissions = 
				accessPolicyBuilder.getPoliciesByViewer( getEboContext(), viewerName, userObj );
			idsByComponent = accessPolicyBuilder.getIdsByComponent();
			componentSecurityMap = new ComponentSecurityMap( viewerPermissions, idsByComponent );			
		} else {
			componentSecurityMap = new ComponentSecurityMap( null, null );
		}
	}
}
