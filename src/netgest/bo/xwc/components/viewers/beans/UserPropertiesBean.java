package netgest.bo.xwc.components.viewers.beans;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.system.boSession;
import netgest.bo.system.boSessionUser;
import netgest.bo.utils.IProfileUtils;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.xeo.beans.XEOEditBean;


public class UserPropertiesBean extends XEOEditBean {
	private String profile = "";
	public boSession sess;

	public UserPropertiesBean() {
		boSession session = getEboContext().getBoSession();
		Properties prop = new Properties();
		setPofilesMap();
		setProfile(session.getPerformerIProfileBouiAsString());
		prop.putAll(getProfilesLovMap());
	}

	public Map<Object, String> profileLovMap = new LinkedHashMap<Object, String>();

	public String getLabels() {

		boDefHandler defHandler = boDefHandler.getBoDefinition("Ebo_Perf");
		String objLabel = defHandler.getLabel();
		String descc = defHandler.getDescription();
		setPofilesMap();

		return objLabel + " ____ " + descc;
	}

	public Map<Object, String> getProfilesLovMap() {
		Map<Object, String> oProfilesMap = new LinkedHashMap<Object, String>();

		boSession session = getEboContext().getBoSession();
		sess = session;
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
		/**
		 * try { EboContext cont=null; boObjectList
		 * proflist=boObjectList.list(cont, "Ebo_Perf",
		 * "profile",session.getPerformerBoui());
		 * System.out.println(proflist.next()); } catch (boRuntimeException e) {
		 * 
		 * e.printStackTrace(); }
		 **/
		for (String profileString : map) {
			String[] currProfile = profileString.split(";");
			oProfilesMap.put(currProfile[0], currProfile[1]);
		}}
		return oProfilesMap;
	}

	public void setPofilesMap() {
		profileLovMap = getProfilesLovMap();
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getProfile() {
		return profile;
	}

	public void updateUser() {
		boObject user = getXEOObject();
		try {
			if (sess.getPerformerIProfileBouiAsString() != profile) {
				sess.setPerformerIProfileBoui(profile);
				boSessionUser bouser = sess.getUser();
				AttributeHandler l = user.getAttribute("user_language");
				EboContext cntxt = this.getEboContext();

				if (l != null && l.toString() != "") {
					boObjectList list = boObjectList.list(cntxt,
							"select xeoapplicationlanguage where boui=" + l);
					list.beforeFirst();
					boObject languageObj = list.getObject();
					AttributeHandler codeHandler = languageObj
							.getAttribute("code");
					String language = codeHandler.getValueString();
					bouser.setLanguage(language);
				} else
					bouser.setLanguage(boApplication.getDefaultApplication()
							.getApplicationLanguage());

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
				oRequestContext.renderResponse();
			} else {
				saveAndClose();
			}
		} catch (boRuntimeException e) {
			e.printStackTrace();
		}
	}

	public String getMainViewer(boSession oXeoSession) {

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
					// workPlace =
					// boObject.getBoManager().loadObject(loginCtx,"uiWorkPlace"
					// ,"name='default'");
				} else {
					boObjectList proflist = boObjectList.list(loginCtx,
							"SELECT uiWorkPlace WHERE profile=?",
							new Object[] { boui });
					if (proflist.next())
						workPlace = proflist.getObject();

					// workPlace = boObject.getBoManager().loadObject(loginCtx,
					// "SELECT uiWorkPlace WHERE profile=?",
					// new Object[] { boui }
					// );
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
