package netgest.bo.xwc.components.model;

import java.util.Arrays;
import java.util.List;

import javax.el.ValueExpression;

import org.json.JSONException;
import org.json.JSONObject;

import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.classic.ViewerCommandSecurityBase;
import netgest.bo.xwc.components.classic.scripts.XVWServerActionWaitMode;
import netgest.bo.xwc.components.security.SecurableComponent;
import netgest.bo.xwc.components.security.ViewerAccessPolicyBuilder;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.xeo.beans.ViewerConfig;

public class Menu extends ViewerCommandSecurityBase {
    
	
    public XUIStateProperty<String> text = new XUIStateProperty<String>( "text", this );
    public XUIStateProperty<String> toolTip = new XUIStateProperty<String>( "toolTip", this );
    public XUIStateProperty<String> iconCls = new XUIStateProperty<String>( "iconCls", this );
    public XUIStateProperty<String> icon = new XUIStateProperty<String>( "icon", this );
    
    public XUIStateProperty<String> serverAction = new XUIStateProperty<String>( "serverAction", this );
    public XUIBindProperty<String> 	serverActionWaitMode = 
    	new XUIBindProperty<String>( "serverActionWaitMode", this ,String.class );
    
    public XUIStateProperty<String> target = new XUIStateProperty<String>( "target", this );

    private XUIStateBindProperty<Boolean> disabled = new XUIStateBindProperty<Boolean>( "disabled", this, "false",Boolean.class );
    private XUIStateBindProperty<Boolean> visible  = new XUIStateBindProperty<Boolean>( "visible", this, "true",Boolean.class );
    private XUIStateBindProperty<Boolean> expanded = new XUIStateBindProperty<Boolean>( "expanded", this, "false",Boolean.class );

    private XUIStateBindProperty<String> roles = new XUIStateBindProperty<String>( "roles", this, String.class );
    private XUIStateBindProperty<String> workQueues = new XUIStateBindProperty<String>( "workQueues", this, String.class );
    private XUIStateBindProperty<String> groups = new XUIStateBindProperty<String>( "groups", this, String.class );
    private XUIStateBindProperty<String> profiles = new XUIStateBindProperty<String>( "profiles", this, String.class );
    private XUIStateBindProperty<String> profile = new XUIStateBindProperty<String>( "profile", this, String.class );
    
    public Menu() {
    	
    }
    
    public static final Menu getMenuSpacer() {
    	return new Menu("-");
    }
    
    private Menu( String sText ) {
    	setText( sText );
    }
    
    public void setServerActionWaitMode( String waitModeName ) {
    	this.serverActionWaitMode.setExpressionText( waitModeName );
    }
    
    public XVWServerActionWaitMode getServerActionWaitMode() {
    	String value = this.serverActionWaitMode.getEvaluatedValue();
    	if( value != null ) {
    		return XVWServerActionWaitMode.valueOf( value );
    	}
    	return null;
    }
    
    public void setRoles( String sExpression ) {
    	this.roles.setExpressionText( sExpression );
    }

    public String getRoles() {
    	return this.roles.getEvaluatedValue();
    }
    
    public void setGroups( String sExpression ) {
    	this.groups.setExpressionText( sExpression );
    }

    public String getGroups() {
    	return this.groups.getEvaluatedValue();
    }

    public void setWorkQueues( String sExpression ) {
    	this.workQueues.setExpressionText( sExpression );
    }

    public String getWorkQueues() {
    	return this.workQueues.getEvaluatedValue();
    }
    
    public void setProfiles( String sExpression ) {
    	this.profiles.setExpressionText( sExpression );
    }

    public String getProfiles() {
    	return this.profiles.getEvaluatedValue();
    }

    public void setProfile( String sExpression ) {
    	this.profile.setExpressionText( sExpression );
    }

    public String getProfile() {
    	return this.profile.getEvaluatedValue();
    }
    
    public void setText(String sText) {
        this.text.setValue( sText );
    }

    public String getText() {
        return text.getValue();
    }

    public void setTarget(String sText) {
        this.target.setValue( sText );
    }

    public String getTarget() {
        return target.getValue();
    }

    public void setToolTip(String sToolTip) {
        this.toolTip.setValue( sToolTip );
    }

    public String getToolTip() {
        return toolTip.getValue();
    }

    public void setIconCls(String sIconCls) {
        this.iconCls.setValue( sIconCls );
    }

    public String getIconCls() {
        return iconCls.getValue();
    }

    public void setIcon(String sIcon) {
        this.icon.setValue( sIcon );
    }

    public String getIcon() {
        return icon.getValue();
    }
    
    public void setServerAction(String sActionExpr ) {
        this.serverAction.setValue( sActionExpr );
        setActionExpression( createMethodBinding( sActionExpr ) );
    }

    public void setDisabled(String disabled) {
        this.disabled.setExpressionText( disabled );
    }

    public void setDisabled(boolean disabled) {
        this.disabled.setExpressionText( Boolean.toString( disabled ) );
    }

    public boolean isDisabled() {
        return disabled.getEvaluatedValue();
    }

    public void setVisible(String visible) {
        this.visible.setExpressionText( visible );
    }

    public boolean isVisible() {
        return this.visible.getEvaluatedValue();
    }
    
    public void setExpanded( String sExpressionText ) {
    	this.expanded.setExpressionText( sExpressionText );
    }
    
    public boolean getExpanded() {
    	return this.expanded.getEvaluatedValue();
    }
    
    public boolean canAcess() {
		Boolean ret = null;
    	try {
			ret = check( ret, "userRoles", getRoles() );
			ret = ret==null||ret==false?check( ret, "userWorkQueues", getWorkQueues() ):ret;
			ret = ret==null||ret==false?check( ret, "userGroups", getGroups() ):ret;
			ret = ret==null||ret==false?check( ret, "userProfiles", getProfiles() ):ret;
			
			ret = checkProfile( ret );
			
			if( ret == null ) {
				ret = true;
			}
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			return ret;
		}
    }
    
    private Boolean check( Boolean ret, String varName, String roles ) {
    	if( roles != null ) {
	    	ValueExpression v = createValueExpression( "#{viewBean.isAdministrator}" ,  Boolean.TYPE );
	    	if( !((Boolean)v.getValue( getELContext() )).booleanValue() ) {
	    		String[] rolesA = roles.split( "," );
	    		if( rolesA.length > 0  ) { 
			    	v = createValueExpression( "#{viewBean."+varName+"}",  String[].class );
			    	String[] 		values = (String[])v.getValue( getELContext() );
			    	List<String>	valuesList = Arrays.asList( values );
			    	for( String role : rolesA ) {
			    		if( valuesList.contains( role ) )
			    			return true;
			    	}
			    	return false;
	    		}
	    	}
    		return true;
    	}
    	return ret;
    }

    private Boolean checkProfile( Boolean ret ) {
    	String profile = getProfile();
    	if( profile != null && profile.length() > 0 ) {
    		if( boApplication.currentContext() != null ) {
    			EboContext ctx = boApplication.currentContext().getEboContext();
    			if( ctx != null ) {
    				String userProfile = ctx.getBoSession().getPerformerIProfileName();
    				List<String> profiles = Arrays.asList( profile.split(",") );
    				if( !profiles.contains( userProfile ) ) {
    					ret = false;
    				}
    			}
    		}
    		
    	}
    	return ret;
    }
    
    //
    // Methods from SecurableComponent
    //
    
	public COMPONENT_TYPE getViewerSecurityComponentType() {
		return SecurableComponent.COMPONENT_TYPE.MENU;
	}

	public String getViewerSecurityId() {
		String securityId = null;
 		if ( getText()!=null && getText().length()>0 &&
 				getActionExpression()!=null &&
 				getActionExpression().getExpressionString()!=null && 
 				getActionExpression().getExpressionString().length()>0 ) {
 			securityId = getText();
 			securityId += ViewerAccessPolicyBuilder.cleanElExpression( getActionExpression().getExpressionString() );
 		}
		return securityId;
	}

	public String getChildViewers() {
		String childViewers = null;
		try {
			Object v = getValue();
			if( v != null ) {
				ViewerConfig c;
					c = new ViewerConfig( new JSONObject( v.toString() ) );
				childViewers = c.getViewerName();
			}
		} catch (JSONException e) { }
		if( childViewers == null ) {
			childViewers = super.getChildViewers();
		}
		
		return childViewers;
	}
	
	public String getViewerSecurityLabel() {
		return getViewerSecurityComponentType().toString()+" "+getText();
	}

	public boolean isContainer() {
		return true;
	}
	
}
