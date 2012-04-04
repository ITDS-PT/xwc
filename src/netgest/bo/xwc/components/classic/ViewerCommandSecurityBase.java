package netgest.bo.xwc.components.classic;

import netgest.bo.xwc.components.security.ComponentSecurityHandler;
import netgest.bo.xwc.components.security.SecurableComponent;
import netgest.bo.xwc.framework.components.XUICommand;

public abstract class ViewerCommandSecurityBase extends XUICommand implements SecurableComponent {

	private String 	childViewers;
	
	private ComponentSecurityHandler componentSecurityHandler = 
		new ComponentSecurityHandler( this );
    
	@Override
	public void preRender() {
		super.preRender();
		String viewerSecurityId = getInstanceId();
		if ( viewerSecurityId!=null ) {
			setViewerSecurityPermissions( "#{" + getBeanId() + ".viewerPermissions."+viewerSecurityId+"}" );    		
		}
	}
	
	// 
	// Method requests redirected
	//

	public Byte getEffectivePermission() {
		return componentSecurityHandler.getEffectivePermission();
	}

	public boolean getEffectivePermission(byte securityPermision) {
		return componentSecurityHandler.getEffectivePermission( securityPermision );
	}

	public byte getSecurityPermissions() {
		return componentSecurityHandler.getSecurityPermissions();
	}

	public byte getViewerSecurityPermissions() {
		return componentSecurityHandler.getViewerSecurityPermissions();
	}

	public void setSecurityPermissions(String expressionText) {
		componentSecurityHandler.setSecurityPermissions( expressionText );
	}

	public void setViewerSecurityPermissions(String expressionText) {
		componentSecurityHandler.setViewerSecurityPermissions( expressionText );
	}

	public String getInstanceId() {
		return componentSecurityHandler.getInstanceId();
	}

	public void setInstanceId(String instanceId) {
		componentSecurityHandler.setInstanceId(instanceId);
	}

	public String getChildViewers() {
		return this.childViewers;
	}
	
	public void setChildViewers( String childViewers ) {
		this.childViewers = childViewers;
	}

}
