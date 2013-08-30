package netgest.bo.xwc.components.classic.mainRegions;

import static netgest.bo.xwc.components.localization.ComponentMessages.USER_TOOLBAR_FAVORITES_BTN_LBL;
import static netgest.bo.xwc.components.localization.ComponentMessages.USER_TOOLBAR_GROUP_BTN_LBL;
import static netgest.bo.xwc.components.localization.ComponentMessages.USER_TOOLBAR_LOGOUT_BTN_LBL;
import static netgest.bo.xwc.components.localization.ComponentMessages.USER_TOOLBAR_PROFILES_GRP_BTN_LBL;
import static netgest.bo.xwc.components.localization.ComponentMessages.USER_TOOLBAR_PROPERTIES_BTN_LBL;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.Logger;
import netgest.bo.system.boApplication;
import netgest.bo.system.boSession;
import netgest.bo.utils.IProfileUtils;
import netgest.bo.xwc.components.classic.TreePanel;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtConfigArray;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.classic.scripts.XVWServerActionWaitMode;
import netgest.bo.xwc.components.classic.toolbar.IToolbarGroup;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;

/**
 * 
 * A toolbar component (either for the tree panel or a regular toolbar) that has
 * a set of options regarding users (such as logout, favorites, user properties and profiles)
 *
 */
public class UserToolBar extends XUIComponentBase implements IToolbarGroup {

	/**
	 * Logger
	 */
	private  static final Logger log = Logger.getLogger( UserToolBar.class );
	
	/**
	 * Show the user properties button
	 */
	private XUIBindProperty<Boolean> showUserProperties = 
		new XUIBindProperty<Boolean>("showUserProperties", this, Boolean.class, "true" );
	
	/**
	 * Show all user profiles menu(s)
	 */
	private XUIBindProperty<Boolean> showUserProfiles = 
		new XUIBindProperty<Boolean>("showUserProfiles", this, Boolean.class, "true" );
	
	/**
	 * Group profiles in a menu (otherwise) display them all at the same level
	 */
	private XUIBindProperty<Boolean> groupProfiles = 
		new XUIBindProperty<Boolean>("groupProfiles", this, Boolean.class, "true" );
	
	/**
	 * Show the favorites menu
	 */
	private XUIBindProperty<Boolean> showFavorites = 
		new XUIBindProperty<Boolean>("showFavorites", this, Boolean.class, "true" );
	
	/**
	 * Show the logout button
	 */
	private XUIBindProperty<Boolean> showLogout = 
		new XUIBindProperty<Boolean>("showLogout", this, Boolean.class, "true" );
	
	/**
	 * Show the history button
	 */
	private XUIBindProperty<Boolean> showHistory = 
		new XUIBindProperty<Boolean>("showHistory", this, Boolean.class, "true" );
	
	/**
	 * Render the menus as one button (defaults to true) otherwise they'll be shown
	 * as menus at the same level
	 */
	private XUIBindProperty<Boolean> renderAsGroup = 
		new XUIBindProperty<Boolean>("renderAsGroup", this, Boolean.class, "true" );
	
	
	/**
	 * Sets the component to not have a default action
	 */
	private XUIBindProperty<Boolean> noDefaultAction = 
		new XUIBindProperty<Boolean>("noDefaultAction", this, Boolean.class, "false" );
	
	/**
	 * The text to display in the menu when its' rendered as a single button (i.e.
	 * the label for the container of the other menus)
	 */
	private XUIBaseProperty<String> groupText =
		new XUIBaseProperty<String>("groupText", this, USER_TOOLBAR_GROUP_BTN_LBL.toString());
	
	/**
	 * 
	 * Retrieves the text to display in the menu that contains the other entrie
	 * 
	 * @return The text to display in the menu
	 */
	public String getGroupText(){
		return groupText.getValue();
	}
	
	/**
	 * Sets the group text
	 * 
	 * @param text The text to display
	 */
	public void setGroupText(String text){
		this.groupText.setValue(text);
	}
	
	
	/**
	 * 
	 * Retrieves the flag to mark that the component should not have a default 
	 * action to logout
	 * 
	 * @return True if the logout should not be the default value and false otherwise
	 */
	public boolean getNoDefaultAction(){
		return noDefaultAction.getEvaluatedValue();
	}
	
	public void setNoDefaultAction(String actionExpr){
		noDefaultAction.setExpressionText(actionExpr);
	}
	
	/**
	 * 
	 * Whether to show the user profiles in the toolbar or not
	 * (defaults to true)
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
	 * Whether to show the user properties or not (defaults to true)
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
	 * Whether or not the logout button shown (defaults to true)
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
	 * Whether or not to group the profiles under a single menu 
	 * (defaults to true)
	 * 
	 * @return True if profiles should be under a single menu and
	 * false otherwise
	 */
	public Boolean getGroupProfiles(){
		return groupProfiles.getEvaluatedValue();
	}
	
	/**
	 * 
	 * Sets to show the user profiles under a single menu or not
	 * 
	 * @param groupProfilsExpr
	 */
	public void setGroupProfiles(String groupProfilsExpr){
		this.groupProfiles.setExpressionText(groupProfilsExpr);
	}
	
	/**
	 * 
	 * Whether or not to show the favorites menu (defaults to true)
	 * 
	 * @return True to show the button and false otherwise
	 */
	public Boolean getShowFavorites(){
		return showFavorites.getEvaluatedValue();
	}
	
	/**
	 * 
	 * Sets to show the favorites button or not
	 * 
	 * @param showFavExpr
	 */
	public void setShowFavorites(String showFavExpr){
		this.showFavorites.setExpressionText(showFavExpr);
	}
	
	/**
	 * 
	 * Whether the sets of buttons should be displayed under a single menu or not
	 * 
	 * @return True if the buttons should be under a single menu
	 * or at the top level
	 */
	public boolean getRenderAsGroup(){
		return renderAsGroup.getEvaluatedValue();
	}
	
	public void setRenderAsGroup(String renderAsGroupExpr){
		this.renderAsGroup.setExpressionText(renderAsGroupExpr);
	}
	
	/**
	 * 
	 * Whether to show the history menu or not
	 * 
	 * @return 
	 */
	public boolean getShowHistory(){
		return showHistory.getEvaluatedValue();
	}
	
	public void setShowHistory(String historyExpr){
		this.showHistory.setExpressionText(historyExpr);
	}
	
	@Override
	public void initComponent(){
		
		super.initComponent();
		
		//Check if the user has a set of preferences, if it has
		//add the Command to open the favorites viewer
		Menu showUserFavsCmd = (Menu) this
		.findComponent("showFavoritesCmd"  +"_" + getId());

		EboContext ctx = boApplication.currentContext().getEboContext();
		
		try {
			boObject preferences = boApplication.getDefaultApplication().getObjectManager().
			loadObject(ctx,"select Ebo_UserPreferences where owner = CTX_PERFORMER_BOUI");
			
			if (preferences.exists()){
				if (showUserFavsCmd == null) {
					showUserFavsCmd = new Menu();
					showUserFavsCmd.setId("showFavoritesCmd"  +"_" + getId());
					showUserFavsCmd.setActionExpression(createMethodBinding("#{" + getBeanId() + ".openViewer}"));
					showUserFavsCmd.setValue("{viewerName:'netgest/bo/xwc/xeo/viewers/UserFavorites.xvw',boui:'"+ctx.getBoSession().getPerformerBoui()+"'}");
					showUserFavsCmd.setTarget("tab");
					this.getChildren().add(showUserFavsCmd);
				}
			}
		} catch (boRuntimeException e) {
			e.printStackTrace();
		}
		
		//CHeck if the History command is present, if not add.
		Menu showUserHistory = (Menu) this.findComponent("showHistoryCmd" +"_" + getId());
		if (showUserHistory == null){
			showUserHistory = new Menu();
			showUserHistory.setId("showHistoryCmd" +"_" + getId());
			showUserHistory.setActionExpression(createMethodBinding("#{" + getBeanId() + ".openViewer}"));
			showUserHistory.setValue("{viewerName:'netgest/bo/xwc/xeo/viewers/UserHistory.xvw'}");
			showUserHistory.setTarget("tab");
			this.getChildren().add(showUserHistory);
		}
	}
	
	/**
	 * 
	 * Retrieves the profiles map (display in the list of profiles)
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

	
	
	/**
	 * 
	 * Configure the component as an ExtJS set of components to be usable in the tree panel
	 * 
	 * @param config The parent configuration (see {@link TreePanel}
	 */
	public void getExtJsConfig(ExtConfigArray config) {
		
		ExtConfig submenu = config.addChild();
		
		if (!getNoDefaultAction()){
			submenu.addJSString("xtype", "splitbutton");
			submenu.addJSString("icon", "ext-xeo/images/menus/logout.gif");
			
			submenu.add("text", "ExtXeo.Messages.LOGOUT_BTN");
			submenu.add("tooltip", "ExtXeo.Messages.LOGOUT_BTN");
			submenu.add("handler",
					"function() { document.location.href='LogoutXVW.jsp'}");
		}else{
			submenu.addJSString("icon", "ext-xeo/admin/users.gif");
			submenu.addJSString("text", "Options");
		}
		submenu.addJSString("cls", "x-btn-text-icon");
		

		ExtConfigArray arrayMenu = submenu.addChildArray("menu");
		//Add the logout
		if (getShowLogout()){
			ExtConfig arrayChild1 = arrayMenu.addChild();
			arrayChild1.addJSString("xtype", "button");
			arrayChild1.addJSString("icon", "ext-xeo/images/menus/logout.gif");
			arrayChild1.addJSString("cls", "x-btn-text-icon");
			arrayChild1.add("text", "ExtXeo.Messages.LOGOUT_BTN");
			arrayChild1.add("handler",
					"function() { document.location.href='LogoutXVW.jsp'}");
		}
		
		//Add the user properties 
		if (getShowUserProperties()){
			ExtConfig arrayChild2 = arrayMenu.addChild();// userProps
			arrayChild2.addJSString("xtype", "button");
			arrayChild2.addJSString("icon", "ext-xeo/admin/users.gif");
			arrayChild2.addJSString("cls", "x-btn-text-icon");
			arrayChild2.add("text", "ExtXeo.Messages.USER_PROPS");
			arrayChild2
					.add(
							"handler",
							"function() {XVW.AjaxCommand('formMain','showUserPropsCmd','showUserPropsCmd',1);}");
		}
		
		//Add the user profiles
		if (getShowUserProfiles()){
			
			
			Map<Object, String> mapProfiles = getProfileLovMap();
			Collection<String> valuesColl = mapProfiles.values();
			Iterator<String> valuesIt = valuesColl.iterator();
			Set<Object> keySet = mapProfiles.keySet();
			Iterator<Object> keyIt = keySet.iterator();
	
			ExtConfigArray toUse = arrayMenu;
			// repeat for profiles
			if (valuesColl.size() > 1) {
				if (valuesColl.size() == keySet.size()) {
					boolean groupProfiles = getGroupProfiles();
					if (groupProfiles){
						ExtConfig groupMenu = toUse.addChild();// userProps
						groupMenu.addJSString("xtype", "button");
						groupMenu.addJSString("icon", "ext-xeo/admin/profiles.gif");
						groupMenu.addJSString("cls", "x-btn-text-icon");
						groupMenu.addJSString("text", USER_TOOLBAR_PROFILES_GRP_BTN_LBL.toString());
						toUse = groupMenu.addChildArray("menu");
					}
					while (valuesIt.hasNext()) {
	
						ExtConfig arrayChild3 = toUse.addChild();
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
		
		//Add the favorites menus
		EboContext ctx = boApplication.currentContext().getEboContext();
		try {
			boObject preferences = boApplication.getDefaultApplication().getObjectManager().
				loadObject(ctx,"select Ebo_UserPreferences where owner = CTX_PERFORMER_BOUI");
			
			if (preferences.exists())
			{
				
				ExtConfig arrayChildFavorites = arrayMenu.addChild();// userProps
				arrayChildFavorites.addJSString("xtype", "button");
				arrayChildFavorites.addJSString("icon", "ext-xeo/icons/favorite.png");
				arrayChildFavorites.addJSString("cls", "x-btn-text-icon");
				arrayChildFavorites.addJSString("text", USER_TOOLBAR_FAVORITES_BTN_LBL.toString());
				
				XUICommand showUserFavsCmd = (XUICommand) this
				.findComponent("showFavoritesCmd" +"_" + getId());

				if (showUserFavsCmd != null) {
					String comandExecute = XVWScripts.getCommandScript("tab",showUserFavsCmd,2);
					arrayChildFavorites
					.add(
							"handler",
					"function() {"+comandExecute+"}");
				}
				
				
			}
		} catch (boRuntimeException e) {
			e.printStackTrace();
			log.severe("Could not load user preferences",e);
		}
		
		//Add the history menu
		if (getShowHistory()){
			
			//CHeck if the XUICommand exists
			String historyCmdExecute = "";
			XUICommand showUserHistoryCmd = (XUICommand) this
			.findComponent("showHistoryCmd");

			//Generate the Ext Menu Configuration
			ExtConfig arrayHistory = arrayMenu.addChild();// userProps
			arrayHistory.addJSString("xtype", "button");
			arrayHistory.addJSString("icon", "ext-xeo/icons/history.png");
			arrayHistory.addJSString("cls", "x-btn-text-icon");
			arrayHistory.addJSString("text", ComponentMessages.USER_TOOLBAR_HISTORY_BTN_LBL.toString());
			
			//Generate the Handler for the button
			if (showUserHistoryCmd != null) {
				historyCmdExecute = XVWScripts.getCommandScript("tab",showUserHistoryCmd,2);
				arrayHistory
				.add(
						"handler",
				"function() {"+historyCmdExecute+"}");
			}
			
		}
		
		//Add remaining menus child of the component
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
		            XVWScripts.getCommandScript( current.getTarget(), current, XVWServerActionWaitMode.DIALOG.ordinal() )+"}" 
		           );
			}
		}
	}

	@Override
	public List<UIComponent> getComponentList() {
		//Implement the interface so that this component
		//can be rendered in a toolbar
		EboContext ctx = boApplication.currentContext().getEboContext();
		List<UIComponent> list = new LinkedList<UIComponent>();
		
		Menu groupComponents = new Menu();
		groupComponents.setText(getGroupText());
		groupComponents.setIcon("ext-xeo/admin/users.gif");
		
		if (getRenderAsGroup()){
			list = groupComponents.getChildren();
		}
		
		final Menu SEPARATOR = new Menu();
		SEPARATOR.setText("-");
		
		if (getShowLogout()){
			Menu menuLogout = new Menu();
			menuLogout.setText(USER_TOOLBAR_LOGOUT_BTN_LBL.toString());
			menuLogout.setIcon("ext-xeo/images/menus/logout.gif");
			menuLogout.setServerAction("#{" + getBeanId() + ".openLink}");
			menuLogout.setTarget("top");
			menuLogout.setValue("LogoutXVW.jsp");
			
			list.add(menuLogout);
			list.add(SEPARATOR);
		}
		
		if (getShowUserProperties()){
			Menu menuUserProps = new Menu();
			menuUserProps.setText(USER_TOOLBAR_PROPERTIES_BTN_LBL.toString());
			menuUserProps.setIcon("ext-xeo/admin/users.gif");
			menuUserProps.setServerAction("#{" + getBeanId() + ".showUserProperties}");
			menuUserProps.setServerActionWaitMode( XVWServerActionWaitMode.DIALOG.toString( ));
			
			list.add(menuUserProps);
			list.add(SEPARATOR);
		}
		
		if (getShowFavorites()){
			Menu showUserFavsCmd = new Menu();
			showUserFavsCmd.setId("showFavoritesCmd"  +"_" + getId());
			showUserFavsCmd.setText(USER_TOOLBAR_FAVORITES_BTN_LBL.toString());
			showUserFavsCmd.setIcon("ext-xeo/icons/favorite.png");
			showUserFavsCmd.setServerAction("#{" + getBeanId() + ".openViewer}");
			showUserFavsCmd.setValue("{viewerName:'netgest/bo/xwc/xeo/viewers/UserFavorites.xvw',boui:'"+ctx.getBoSession().getPerformerBoui()+"'}");
			showUserFavsCmd.setTarget("tab");
			showUserFavsCmd.setServerActionWaitMode( XVWServerActionWaitMode.DIALOG.toString( ));
			
			list.add(showUserFavsCmd);
			list.add(SEPARATOR);
		}
		
		
		if (getShowUserProfiles()){
			Map<Object, String> mapProfiles = getProfileLovMap();
			Collection<String> valuesColl = mapProfiles.values();
			Iterator<String> valuesIt = valuesColl.iterator();
			Set<Object> keySet = mapProfiles.keySet();
			Iterator<Object> keyIt = keySet.iterator();
	
			boolean groupProfiles = getGroupProfiles();
			Menu parentProfilesMenu = new Menu();
			parentProfilesMenu.setText(USER_TOOLBAR_PROFILES_GRP_BTN_LBL.toString());
			parentProfilesMenu.setIcon("ext-xeo/admin/profiles.gif");
			
			// repeat for profiles
			if (valuesColl.size() > 1) {
				if (valuesColl.size() == keySet.size()) {
					while (valuesIt.hasNext()) {
	
						Menu currentProfile = new Menu();
						currentProfile.setText(valuesIt.next());
						currentProfile.setIcon("ext-xeo/admin/profiles.gif");
						currentProfile.setTarget("top");
						currentProfile.setServerAction("#{" + getBeanId() + ".openLink}");
						currentProfile.setValue("Login.xvw?action=change_profile&boui="+keyIt.next());
						if (!groupProfiles){
							list.add(currentProfile);
							list.add(SEPARATOR);
						}
						else{
							parentProfilesMenu.getChildren().add(currentProfile);
							parentProfilesMenu.getChildren().add(SEPARATOR);
						}
					}
				}
			}
			
			if (groupProfiles && (valuesColl.size() > 1)){
				list.add(parentProfilesMenu);
				list.add(Menu.getMenuSpacer());
			}
			
		}
		
		if (getShowHistory()){
			Menu historyMenu = new Menu();
			historyMenu.setText(ComponentMessages.USER_TOOLBAR_HISTORY_BTN_LBL.toString());
			historyMenu.setIcon("ext-xeo/icons/history.png");
			historyMenu.setTarget("tab");
			historyMenu.setServerAction("#{" + getBeanId() + ".openViewer}");
			historyMenu.setValue("{viewerName:'netgest/bo/xwc/xeo/viewers/UserHistory.xvw'}");
			historyMenu.setServerActionWaitMode( XVWServerActionWaitMode.DIALOG.toString( ));
			
			list.add(historyMenu);
			list.add(Menu.getMenuSpacer());
		}
		
		if (getRenderAsGroup()){
			List<UIComponent> res = new LinkedList<UIComponent>();
			res.add(groupComponents);
			return res;
		}
		
		return list;
	}

	
	
	
	
	
}
