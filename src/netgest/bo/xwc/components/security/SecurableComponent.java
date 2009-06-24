package netgest.bo.xwc.components.security;

public interface SecurableComponent {

	public static enum COMPONENT_TYPE { TOOLBAR, MENU, AREA, GRID, ATTRIBUTE };

    public void setSecurityPermissions( String sExpressionText );
    public byte getSecurityPermissions(); 

    public void setViewerSecurityPermissions( String sExpressionText );
    public byte getViewerSecurityPermissions();
    
    public String getViewerSecurityId();
    public String getViewerSecurityLabel();
    public COMPONENT_TYPE getViewerSecurityComponentType();
    
    public Byte getEffectivePermission();
    public boolean getEffectivePermission( byte securityPermision );
	
    public void setInstanceId( String instanceId );
    public String getInstanceId();

    public String getChildViewers();
    
    public boolean isContainer();
    
}
