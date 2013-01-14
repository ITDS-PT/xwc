package netgest.bo.xwc.components.model;

import java.util.Arrays;
import java.util.List;

import javax.el.ValueExpression;

import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.annotations.Values;
import netgest.bo.xwc.components.classic.ToolBar;
import netgest.bo.xwc.components.classic.TreePanel;
import netgest.bo.xwc.components.classic.ViewerCommandSecurityBase;
import netgest.bo.xwc.components.classic.scripts.XVWServerActionWaitMode;
import netgest.bo.xwc.components.security.SecurableComponent;
import netgest.bo.xwc.components.security.ViewerAccessPolicyBuilder;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.XUIViewStateProperty;
import netgest.bo.xwc.xeo.beans.ViewerConfig;
import netgest.bo.xwc.xeo.components.BridgeToolBar;
import netgest.bo.xwc.xeo.components.EditToolBar;
import netgest.bo.xwc.xeo.components.ListToolBar;
import netgest.bo.xwc.xeo.components.LookupListToolBar;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * The {@link Menu} component is a multi-purpose component to create entries (buttons)
 * in a {@link ToolBar} or {@link TreePanel} that invoke server-side actions.
 * 
 * Menus can be grouped inside menus to create hierarchies/groups
 * 
 * 
 * Menus can also be used in components like the {@link EditToolBar}, {@link ListToolBar},
 * {@link BridgeToolBar} or {@link LookupListToolBar}
 * 
 * Eg: Single Menu (In ToolBar)
 * 
 * <xvw:toolBar>
 *        <xvw:menu text="Export" toolTip="Export the form to HTML">
 *        </xvw:menu>
 *	</xvw:toolBar>
 *  Eg: Nested Menu
 *  
 *  <xvw:menu text='XEO Models' expanded='true'>
 *    <xvw:menu
 *       icon='resources/Ebo_Package/ico16.gif'
 *       text='Packages' 
 *       value="{viewerName:'Ebo_Package_list.xvw', boql:'select Ebo_Package where deployed=\'1\''}" 
 *       target='Tab' 
 *       serverAction="#{viewBean.listObject}" 
 *     />
 * </xvw:menu>
 * 
 * @author Jo�o Carreira
 *
 */
public class Menu extends ViewerCommandSecurityBase {
    
	
    /**
     * The text to be shown as label of the menu
     */
    public XUIViewStateProperty<String> text 	= new XUIViewStateProperty<String>( "text", this );
    /**
     * The text to be presented as a tool tip (when mouse is over the menu)
     */
    public XUIViewStateProperty<String> toolTip = new XUIViewStateProperty<String>( "toolTip", this );
    /**
     * CSS class to apply to the icon(requires that the icon property is used)
     */
    public XUIViewStateProperty<String> iconCls = new XUIViewStateProperty<String>( "iconCls", this );
    
    
    
    /**
     * Icon to display next to the label (path to the icon)
     */
    public XUIViewStateProperty<String> icon 	= new XUIViewStateProperty<String>( "icon", this, "" );
    /**
     * Which action to execute in the server
     */
    public XUIStateProperty<String> serverAction = new XUIStateProperty<String>( "serverAction", this );
    
    public XUIBindProperty<String> 	serverActionWaitMode = 
    	new XUIBindProperty<String>( "serverActionWaitMode", this ,String.class );
    
    /**
     * Target in which the action will be executed
     * (default value is 'self')
     */
    @Values({"blank","window","tab","alwaysNewTab","noCloseTab","download","self","top"})
    public XUIStateProperty<String> target = new XUIStateProperty<String>( "target", this );
    /**
     * Keyboard shortcut to execute the action associated 
     * to this menu (Example values: "Ctrl+s". "Alt+e")
     */
    public XUIStateProperty<String> shortCut = new XUIStateProperty<String>( "shortCut", this );

    /**
     * Whether or not the menu is disabled
     */
    private XUIViewStateBindProperty<Boolean> disabled = new XUIViewStateBindProperty<Boolean>( "disabled", this, "false",Boolean.class );
    /**
     * Whether or not the menu is visible
     */
    private XUIViewStateBindProperty<Boolean> visible  = new XUIViewStateBindProperty<Boolean>( "visible", this, "true",Boolean.class );
    /**
     * Whether or not the menu is expanded
     * Only works in nested menus
     */
    private XUIViewStateBindProperty<Boolean> expanded = new XUIViewStateBindProperty<Boolean>( "expanded", this, "false",Boolean.class );
    /**
     * Group where the logged user must be to see this menu
     */
    private XUIBaseProperty<String> 	  group 	= new XUIBaseProperty<String>( "group", this, null );

    /**
     * List of comma-separated role names required to see/use this menu
     */
    private XUIStateBindProperty<String> roles = new XUIStateBindProperty<String>( "roles", this, String.class );
    /**
     * List of comma-separated workqueue names required to see/use this menu
     */
    private XUIStateBindProperty<String> workQueues = new XUIStateBindProperty<String>( "workQueues", this, String.class );
    /**
     * List of comma-separated group names required to see/use this menu
     */
    private XUIStateBindProperty<String> groups = new XUIStateBindProperty<String>( "groups", this, String.class );
    /**
     * List of comma-separated profile names required to see/use this menu
     */
    private XUIStateBindProperty<String> profiles = new XUIStateBindProperty<String>( "profiles", this, String.class );
    /**
     * Name of the profile required to see/use this menu
     */
    private XUIStateBindProperty<String> profile = new XUIStateBindProperty<String>( "profile", this, String.class );
    
    public Menu() {
    	
    }
    
    public static final Menu getMenuSpacer() {
    	return new Menu("-");
    }
    
    public static final Menu getMenuFill(){
    	return new Menu("->");
    }
    
    public static final Menu getMenuSpacer( String visibleExpr ) {
    	Menu m = getMenuSpacer();
    	m.setVisible( visibleExpr );
    	return m;
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
    	return XVWServerActionWaitMode.DIALOG;
    	
    }
    
    public void setRoles( String sExpression ) {
    	this.roles.setExpressionText( sExpression );
    }

    public String getRoles() {
    	return this.roles.getEvaluatedValue();
    }
    
    public void setShortCut( String shortCutExpression ) {
    	this.shortCut.setValue( shortCutExpression );
    }

    public String getShortCut() {
    	return this.shortCut.getValue();
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

    public void setGroup(String sToolTip) {
        this.group.setValue( sToolTip );
    }

    public String getGroup() {
        return group.getValue();
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
	    	ValueExpression v = createValueExpression( "#{"+getBeanId()+".isAdministrator}" ,  Boolean.TYPE );
	    	if( !((Boolean)v.getValue( getELContext() )).booleanValue() ) {
	    		String[] rolesA = roles.split( "," );
	    		if( rolesA.length > 0  ) { 
			    	v = createValueExpression( "#{"+getBeanId()+"."+varName+"}",  String[].class );
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
	
	@Override
	public String toString(){
		if (this.getValue() != null)
			return this.getText() + ":"+ this.getValue().toString();
		else
			return this.getText();
	}
	
	
	/**
	 * 
	 * Checks whether the text was changed
	 * 
	 * @return
	 */
	public boolean wasTextChanged(){
		return text.wasChanged();
	}
	
}
