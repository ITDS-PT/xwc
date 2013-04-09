package netgest.bo.xwc.components.security;

import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * Stores and manipulates all security variables
 * Used by all classes that implement SecurableComponent
 */
public class ComponentSecurityHandler {

	private String instanceId;
	
	private XUIStateBindProperty<Byte> securityPermissions;
    private XUIBindProperty<Byte> viewerPermissions;
    
	public ComponentSecurityHandler( XUIComponentBase oComponent ) {
		securityPermissions = 
	    	new XUIStateBindProperty<Byte>( "securityPermissions", oComponent, 
				Byte.toString( SecurityPermissions.FULL_CONTROL ),
				Byte.class );

		viewerPermissions = 
	    	new XUIBindProperty<Byte>( "viewerSecurityPermissions", oComponent,
				SecurityPermissions.FULL_CONTROL,
				Byte.class );
	}

	public Byte getEffectivePermission() {
		Byte effectivePermission = getSecurityPermissions()<getViewerSecurityPermissions() ? 
			getSecurityPermissions() : getViewerSecurityPermissions();
		return effectivePermission;
	}

	public boolean getEffectivePermission(byte securityPermision) {
		byte value = getEffectivePermission();
		if (!securityPermissions.wasEvaluated() || !viewerPermissions.wasEvaluated())
			return true;
		return (value & securityPermision)!=0;
	}

    public void setSecurityPermissions( String sExpressionText ) {
    	this.securityPermissions.setExpressionText( sExpressionText );
    }
    
    public byte getSecurityPermissions() {
    	return this.securityPermissions.getEvaluatedValue();
    }
    
	public byte getViewerSecurityPermissions() {
		return this.viewerPermissions.getEvaluatedValue();
	}

	public void setViewerSecurityPermissions(String expressionText) {
		this.viewerPermissions.setExpressionText( expressionText );
	}

    public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

}
