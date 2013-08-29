package netgest.bo.xwc.components.viewers.beans;

import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.system.Logger;
import netgest.bo.system.XEO;
import netgest.bo.system.boApplication;
import netgest.bo.system.boSession;
import netgest.bo.system.boSessionUser;
import netgest.bo.system.locale.LocaleSettings;
import netgest.bo.system.login.LocalePreferenceSerialization;
import netgest.bo.utils.IProfileUtils;
import netgest.bo.xeomodels.system.Theme;
import netgest.bo.xeomodels.system.ThemeIncludes;
import netgest.bo.xwc.components.viewers.beans.localization.LocaleSettingsBean;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.localization.XUILocalization;
import netgest.bo.xwc.xeo.beans.XEOEditBean;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;


/**
 * 
 * Supports the User Properties viewer
 * 
 *
 */
public class UserPropertiesBean extends XEOEditBean {
	
	private static final Logger logger = Logger.getLogger(UserPropertiesBean.class);
	
	private String profile = "";
	
	public UserPropertiesBean() {
		boSession session = getEboContext().getBoSession();
		Properties prop = new Properties();
		setProfilesMap();
		setProfile(session.getPerformerIProfileBouiAsString());
		prop.putAll(getProfilesLovMap());
	}

	public Map<Object, String> profileLovMap = new LinkedHashMap<Object, String>();

	public String getLabels() {

		boDefHandler defHandler = boDefHandler.getBoDefinition("Ebo_Perf");
		String objLabel = defHandler.getLabel();
		String descc = defHandler.getDescription();
		setProfilesMap();

		return objLabel + " ____ " + descc;
	}

	public Map<Object, String> getProfilesLovMap() {
		Map<Object, String> oProfilesMap = new LinkedHashMap<Object, String>();

		boSession session = getEboContext().getBoSession();
		boSessionUser user = session.getUser();

		if (user.getUserName().equals("SYSUSER"))
		{
			String[] map = IProfileUtils.getAllIProfiles(session);
			for (String profileString : map) {
				String[] currProfile = profileString.split(";");
				oProfilesMap.put(currProfile[0], currProfile[1]);

			}
		}
		else{
		String[] map = IProfileUtils.getIProfiles(session);
		
		for (String profileString : map) {
			String[] currProfile = profileString.split(";");
			oProfilesMap.put(currProfile[0], currProfile[1]);
		}}
		return oProfilesMap;
	}

	public void setProfilesMap() {
		profileLovMap = getProfilesLovMap();
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getProfile() {
		if (profile == null) {
			String performerProfile = String.valueOf(getEboContext().getBoSession().getPerformerIProfileBoui());
			if (performerProfile != null && !"".equalsIgnoreCase(performerProfile) && Long.valueOf( performerProfile ) > 0) {
				profile = performerProfile;
			}
		}
		return profile;
		
	}

	public void updateUser() {
		boObject user = getXEOObject();
		try {
			
			boSession sess = getEboContext().getBoSession();
			boSessionUser bouser = sess.getUser();
			
			setTheme( user , bouser );
			
			if (!sess.getPerformerIProfileBouiAsString().equals( profile) ) {
				sess.setPerformerIProfileBoui(profile);
			}	
			
			AttributeHandler languageAtt = user.getAttribute("user_language");
			if ( languageAtt != null ) {
				
				boObject languageObj = XEO.loadWithQuery( 
						"select XeoApplicationLanguage where boui= ?" , languageAtt.getValueLong() );
				if (languageObj != null){
					String language = languageObj.getAttribute("code").getValueString();
					bouser.setLanguage(language);
					
				}
			} else{
				bouser.setLanguage(boApplication.getXEO().getApplicationLanguage());
			}
			
			LocaleSettingsBean localeSettingsBean = (LocaleSettingsBean) getViewRoot().getBean( "localeBean" );
			LocaleSettings settings = localeSettingsBean.createLocaleSettings();
			
			LocalePreferenceSerialization.save( settings , getEboContext() );
			XUILocalization.setCurrentLocale( settings.getLocale() );
			

			save();
			XUIRequestContext oRequestContext = getRequestContext();
			oRequestContext
					.getScriptContext()
					.add(
						XUIScriptContext.POSITION_HEADER,
						"sendToMain",
						"parent.document.location.href='"
								+ oRequestContext
										.getActionUrl(getMainViewer(sess))
								+ "'");
		} catch (boRuntimeException e) {
			logger.severe("Error setting a new profile", e);
		}
	}

	private void setTheme(boObject user, boSessionUser bouser) {
		boObject themeObj;
		try {
			if (user.getAttribute("theme").getValueLong()  > 0){
				themeObj = user.getAttribute("theme").getObject();

				bridgeHandler filesIncludeHandler = themeObj.getBridge(Theme.FILES);
				Map<String,String> files = new HashMap<String, String>();
				filesIncludeHandler.beforeFirst();
				while(filesIncludeHandler.next()){
					boObject currentFileInclude = filesIncludeHandler.getObject();
					String id = currentFileInclude.getAttribute(ThemeIncludes.ID).getValueString();
					String path = currentFileInclude.getAttribute(ThemeIncludes.FILEPATH).getValueString();
					files.put(id, path);
				}
				bouser.setThemeFiles(files);
			}
			
		}
		catch (boRuntimeException e) {
			logger.severe("Could not change the user theme", e);
		}
	}

	private String getMainViewer(boSession oXeoSession) {

		String mainViewer;

		mainViewer = null;

		boDefHandler defH = boDefHandler.getBoDefinition("uiWorkPlace");
		if (defH != null && defH.getAttributeRef("defaultViewer") != null) {
			EboContext loginCtx;
			loginCtx = null;
			if (boApplication.currentContext().getEboContext() == null) {
				loginCtx = oXeoSession.createRequestContext(null, null, null);
				boApplication.currentContext().addEboContext(loginCtx);
			} else {
				loginCtx = boApplication.currentContext().getEboContext();
			}
			try {
				boObject workPlace = null;
				long boui = oXeoSession.getPerformerIProfileBoui();
				if (boui == 0) {
					boObjectList proflist = boObjectList.list(loginCtx,
							"select uiWorkPlace where name='default'");
					if (proflist.next())
						workPlace = proflist.getObject();
				} else {
					boObjectList proflist = boObjectList.list(loginCtx,
							"SELECT uiWorkPlace WHERE profile=?",
							new Object[] { boui });
					if (proflist.next())
						workPlace = proflist.getObject();
				}
				if (workPlace != null && workPlace.exists()) {
					mainViewer = workPlace.getAttribute("defaultViewer")
							.getValueString();
					if (mainViewer.length() == 0) {
						mainViewer = null;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (loginCtx != null)
					loginCtx.close();
			}

		}

		if (mainViewer == null) {
			mainViewer = (String) ((HttpServletRequest) XUIRequestContext
					.getCurrentContext().getRequest())
					.getAttribute("__xwcMainViewer");
			if (mainViewer == null) {
				return "Main.xvw";
			}
		}
		return mainViewer;
	}
}
