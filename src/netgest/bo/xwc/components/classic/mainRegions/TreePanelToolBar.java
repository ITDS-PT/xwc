package netgest.bo.xwc.components.classic.mainRegions;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;

import netgest.bo.system.boApplication;
import netgest.bo.system.boSession;
import netgest.bo.utils.IProfileUtils;
import netgest.bo.xwc.components.annotations.Values;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtConfigArray;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.classic.scripts.XVWServerActionWaitMode;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * 
 * 
 * A toolbar for the treepanel component
 *
 */
public class TreePanelToolBar extends XUIComponentBase{

	
	/**
	 * Show the user properties button
	 */
	private XUIBindProperty<Boolean> showUserProperties = 
		new XUIBindProperty<Boolean>("showUserProperties", this, Boolean.class, "true" );
	
	/**
	 * Show all user profiles
	 */
	private XUIBindProperty<Boolean> showUserProfiles = 
		new XUIBindProperty<Boolean>("showUserProfiles", this, Boolean.class, "true" );
	
	/**
	 * Show the logout button
	 */
	private XUIBindProperty<Boolean> showLogout = 
		new XUIBindProperty<Boolean>("showLogout", this, Boolean.class, "true" );
	
	/**
	 * The placement of the toolbar
	 */
	@Values({"top","bottom"})
	private XUIBindProperty<String> placement = 
		new XUIBindProperty<String>("placement", this, String.class, "bottom" );
	
	/**
	 * 
	 * Whether to show the user profiles in the toolbar or not
	 * 
	 * @return True if the user profiles should be shown
	 */
	public Boolean getShowUserProfiles(){
		return showUserProfiles.getEvaluatedValue();
	}
	
	/**
	 * 
	 * Sets to show the user profiles
	 * 
	 * @param profilesExpr
	 */
	public void setShowUserProfiles(String profilesExpr){
		showUserProfiles.setExpressionText(profilesExpr);
	}
	
	/**
	 * 
	 * Whether to show the user properties or not
	 * 
	 * @return true if the user properties show be shown and false otherwise
	 */
	public Boolean getShowUserProperties(){
		return showUserProperties.getEvaluatedValue();
	}
	
	/**
	 * 
	 * Sets to show the user properties
	 * 
	 * @param userPropsExpr
	 */
	public void setShowUserProperties(String userPropsExpr){
		showUserProperties.setExpressionText(userPropsExpr);
	}
	
	/**
	 * 
	 * Whether or not the logout button shown
	 * 
	 * @return True if the logout button should be displayed and false otherwise
	 */
	public Boolean getShowLogout(){
		return showLogout.getEvaluatedValue();
	}
	
	/**
	 * 
	 * Sets to show the logout button
	 * 
	 * @param logoutExpr
	 */
	public void setShowLogout(String logoutExpr){
		showLogout.setExpressionText(logoutExpr);
	}
	
	
	/**
	 * 
	 * Retrieves the placement of the toolbar in the treepanel
	 * 
	 * @return A string with "top" / "bottom"
	 */
	public String getPlacement(){
		return placement.getEvaluatedValue();
	}
	
	/**
	 * 
	 * Sets the placement of the toolbar
	 * 
	 * @param placementExpr
	 */
	public void setPlacement(String placementExpr){
		placement.setExpressionText(placementExpr);
	}
	
	/**
	 * 
	 * Retrieves the profiles map
	 * 
	 * @return A map with the BOUI,Profile name
	 */
	public Map<Object, String> getProfileLovMap() {

		boSession currSession = boApplication.currentContext().getEboContext()
				.getBoSession();

		Map<Object, String> oProfilesMap = new LinkedHashMap<Object, String>();
		if (currSession != null) {
			String[] iProfiles;

			// SYSUSER should have access to all profiles configured on XEO
			// Instance
			if (currSession.getUser().getUserName().equals("SYSUSER"))
				iProfiles = IProfileUtils.getAllIProfiles(currSession);
			else
				iProfiles = IProfileUtils.getIProfiles(currSession);

			if (iProfiles != null) {
				for (String profileString : iProfiles) {
					String[] currProfile = profileString.split(";");
					oProfilesMap.put(currProfile[0], currProfile[1]);
				}
			}
		}
		return oProfilesMap;
	}

	
	
	public void getExtJsConfig(ExtConfig config) {
		
		ExtConfigArray treeMenu = null;
		if ("bottom".equalsIgnoreCase(getPlacement()))
			treeMenu = config.addChildArray("bbar");
		else if ("top".equalsIgnoreCase(getPlacement()))
			treeMenu = config.addChildArray("tbar");
		else
			treeMenu = config.addChildArray("bbar");
		
		ExtConfig submenu = treeMenu.addChild();
		
		submenu.addJSString("xtype", "splitbutton");
		submenu.addJSString("icon", "ext-xeo/images/menus/logout.gif");
		submenu.addJSString("cls", "x-btn-text-icon");
		submenu.add("text", "ExtXeo.Messages.LOGOUT_BTN");
		submenu.add("tooltip", "ExtXeo.Messages.LOGOUT_BTN");
		submenu.add("handler",
				"function() { document.location.href='LogoutXVW.jsp'}");

		ExtConfigArray arrayMenu = submenu.addChildArray("menu");
		if (getShowLogout()){
			ExtConfig arrayChild1 = arrayMenu.addChild();
			arrayChild1.addJSString("xtype", "button");
			arrayChild1.addJSString("icon", "ext-xeo/images/menus/logout.gif");
			arrayChild1.addJSString("cls", "x-btn-text-icon");
			arrayChild1.add("text", "ExtXeo.Messages.LOGOUT_BTN");
			arrayChild1.add("handler",
					"function() { document.location.href='LogoutXVW.jsp'}");
		}
		
		if (getShowUserProperties()){
			ExtConfig arrayChild2 = arrayMenu.addChild();// userProps
			arrayChild2.addJSString("xtype", "button");
			arrayChild2.addJSString("icon", "ext-xeo/admin/users.gif");
			arrayChild2.addJSString("cls", "x-btn-text-icon");
			arrayChild2.add("text", "ExtXeo.Messages.USER_PROPS");
			arrayChild2
					.add(
							"handler",
							"function() {XVW.AjaxCommand('formMain','showUserPropsCmd','showUserPropsCmd',2);}");
		}
		
		if (getShowUserProfiles()){
			Map<Object, String> mapProfiles = getProfileLovMap();
			Collection<String> valuesColl = mapProfiles.values();
			Iterator<String> valuesIt = valuesColl.iterator();
			Set<Object> keySet = mapProfiles.keySet();
			Iterator<Object> keyIt = keySet.iterator();
	
			// repeat for profiles
			if (valuesColl.size() > 1) {
				if (valuesColl.size() == keySet.size()) {
					while (valuesIt.hasNext()) {
	
						ExtConfig arrayChild3 = arrayMenu.addChild();
						arrayChild3.addJSString("xtype", "button");
						arrayChild3.addJSString("icon",
								"ext-xeo/admin/profiles.gif");
						arrayChild3.addJSString("cls", "x-btn-text-icon");
						arrayChild3.addJSString("text", valuesIt.next());
						arrayChild3.add("handler",
								"function() { document.location.href='Login.xvw?action=change_profile&boui="
										+ keyIt.next() + "'}");
	
					}
				}
			}
		}
		
		List<UIComponent> childMenus = getChildren();
		for (UIComponent menu: childMenus){
			if (menu instanceof Menu){
				Menu current = (Menu) menu;
				ExtConfig newMenu = arrayMenu.addChild();
				newMenu.addJSString("xtype", "button");
				newMenu.addJSString("icon", current.getIcon());
				newMenu.addJSString("cls", current.getIconCls());
				newMenu.addJSString("text", current.getText());
				newMenu.add( "handler", "function(){" +
		            XVWScripts.getCommandScript( current.getTarget(), current, XVWServerActionWaitMode.STATUS_MESSAGE.ordinal() )+"}" 
		           );
			}
		}
	}

	
	
	
	
	
}
