package netgest.bo.xwc.xeo.beans;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boSession;
import netgest.bo.system.boSessionUser;
import netgest.bo.utils.IProfileUtils;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.localization.ViewersMessages;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.annotations.XUIWebDefaultCommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.localization.XUICoreMessages;
import netgest.utils.StringUtils;

public class XEOLoginBean extends XEOSecurityLessBean {

	private String userName;
	private String password;
	private String profile = "";
	private boolean showProfiles = false;

	public XEOLoginBean() {
	}

	public String getUserName() {
		return getIsLoggedIn() ? getBoSession().getUser().getUserName()
				: this.userName;
	}

	public String getStatusMessage() {
		String message = "";

		XUIRequestContext oRequestContext = getRequestContext();
		String msg = oRequestContext.getRequestParameterMap().get( "msg" );

		if ( msg != null ) {
			if ( "1".equals( msg ) ) {
				message = XUICoreMessages.SESSION_EXPIRED.toString();
			}
		}
		return message;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return getIsLoggedIn() ? "*********************" : "";
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean getIsLoggedIn() {
		return getBoSession() != null;
	}

	public boolean getDisableLogout() {
		return getBoSession() == null;
	}

	public boSession getBoSession() {
		HttpSession session = getHttpSession( false );
		if ( session != null ) {
			boSession xeoSession = ( boSession ) session
					.getAttribute( "boSession" );
			return xeoSession;
		}
		return null;
	}

	public boolean getShowProfilesLov() {
		return showProfiles;
	}

	public String getProfile() {
		return this.profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public Map< Object , String > getProfileLovMap() {

		Map< Object , String > oProfilesMap = new LinkedHashMap< Object , String >();
		if ( getBoSession() != null ) {
			String[] iProfiles;

			// SYSUSER should have access to all profiles configured on XEO
			// Instance
			if ( getBoSession().getUser().getUserName().equals( "SYSUSER" ) )
				iProfiles = IProfileUtils.getAllIProfiles( getBoSession() );
			else
				iProfiles = IProfileUtils.getIProfiles( getBoSession() );

			if ( iProfiles != null ) {
				for ( String profileString : iProfiles ) {
					String[] currProfile = profileString.split( ";" );
					oProfilesMap.put( currProfile[0] , currProfile[1] );
				}
			}
		}
		return oProfilesMap;
	}

	public String getMainViewer(boSession oXeoSession) {

		String mainViewer;

		mainViewer = null;

		boDefHandler defH = boDefHandler.getBoDefinition( "uiWorkPlace" );
		if ( defH != null && defH.getAttributeRef( "defaultViewer" ) != null ) {
			EboContext loginCtx;
			loginCtx = null;
			if ( boApplication.currentContext().getEboContext() == null ) {
				loginCtx = oXeoSession
						.createRequestContext( null , null , null );
				boApplication.currentContext().addEboContext( loginCtx );
			} else {
				loginCtx = boApplication.currentContext().getEboContext();
			}
			try {
				boObject workPlace = null;
				long boui = oXeoSession.getPerformerIProfileBoui();
				if ( boui == 0 ) {
					boObjectList proflist = boObjectList.list( loginCtx ,
							"select uiWorkPlace where name='default'" );
					if ( proflist.next() )
						workPlace = proflist.getObject();
					// workPlace =
					// boObject.getBoManager().loadObject(loginCtx,"uiWorkPlace"
					// ,"name='default'");
				} else {
					boObjectList proflist = boObjectList.list( loginCtx ,
							"SELECT uiWorkPlace WHERE profile=?" ,
							new Object[] { boui } );
					if ( proflist.next() )
						workPlace = proflist.getObject();

					// workPlace = boObject.getBoManager().loadObject(loginCtx,
					// "SELECT uiWorkPlace WHERE profile=?",
					// new Object[] { boui }
					// );
				}
				if ( workPlace != null && workPlace.exists() ) {
					mainViewer = workPlace.getAttribute( "defaultViewer" )
							.getValueString();
					if ( mainViewer.length() == 0 ) {
						mainViewer = null;
					}
				}
			} catch ( Exception e ) {
				e.printStackTrace();
			} finally {
				if ( loginCtx != null )
					loginCtx.close();
			}

		}

		if ( mainViewer == null ) {
			mainViewer = ( String ) ( ( HttpServletRequest ) XUIRequestContext
					.getCurrentContext().getRequest() )
					.getAttribute( "__xwcMainViewer" );
			if ( mainViewer == null ) {
				return "Main.xvw";
			}
		}
		return mainViewer;
	}

	@XUIWebDefaultCommand
	public void login() {

		try {

			boApplication bApp = boApplication.getXEO();

			boSession oXeoSession = getBoSession();
			XUIRequestContext oRequestContext = getRequestContext();

			if (getIsLoggedIn()){
				if (hasSingleProfile()){
					if ( oRequestContext.isAjaxRequest() ) {
						redirectAjax();
					} else {
						redirectHttp();
					}
					return ;
				} else if (hasMultipleProfiles()){
					this.showProfiles = true;
				}
			}
			
			
			if ( this.getIsLoggedIn() && showProfiles && StringUtils.hasValue(this.profile) ) {
				showProfiles = false;
				oXeoSession.setPerformerIProfileBoui( this.profile );
				oXeoSession.loadUserLocaleSettings();

				if ( oRequestContext.isAjaxRequest() ) {
					redirectAjax();
				} else {
					redirectHttp();
				}
			} else {
				if ( this.userName != null && this.password != null ) {
					oXeoSession = bApp.boLogin( this.userName , this.password );
					oXeoSession.loadUserLocaleSettings();
					HttpSession session = getHttpSession( true );
					session.setAttribute( "boSession" , oXeoSession );
					
					showProfiles = false;

					Map< Object , String > profilesMap = getProfileLovMap();
					if ( profilesMap.size() > 0 ) {
						if ( profilesMap.size() > 1 ) {
							showProfiles = true;
						} else {
							setOnlyProfileUserHas( oXeoSession , profilesMap );
						}
					}

					Boolean showProfilesConfig = ( Boolean ) ( ( HttpServletRequest ) getRequestContext().getRequest() )
							.getAttribute( "__xwcShowUserProfiles" );
					if ( ( showProfilesConfig != null && !showProfilesConfig
							.booleanValue() ) || !showProfiles ) {
						if ( oRequestContext.isAjaxRequest() ) {
							redirectAjax();
						} else {
							redirectHttp();
						}
					}
				}
			}

		} catch ( boLoginException e ) {
			if ( "BO-4000".equals( e.getErrorCode() ) ) {
				getRequestContext().addMessage(
						"wrongUsernameOrPassword" ,
						new XUIMessage( XUIMessage.TYPE_ALERT ,
								XUIMessage.SEVERITY_ERROR ,
								ViewersMessages.LOGIN_TITLE_ERROR_LOGIN.toString() ,
								ViewersMessages.LOGIN_INVALID_CREDENCIALS
								.toString() ) );
			} else {

			}
		}

	}

	private boolean hasSingleProfile() {
		return getProfileLovMap().size() == 1;
	}
	
	private boolean hasMultipleProfiles(){
		return getProfileLovMap().size() > 1;
	}

	private void setOnlyProfileUserHas( boSession oXeoSession ,
			Map< Object, String > profilesMap ) {
		EboContext loginCtx = null;
		try {
			if ( boApplication.currentContext().getEboContext() == null ) {
				loginCtx = oXeoSession.createEboContext();
				boApplication.currentContext().addEboContext( loginCtx );
			}
			oXeoSession.setPerformerIProfileBoui( getFirstProfile( profilesMap ) );
		} finally {
			if ( loginCtx != null ) {
				loginCtx.close();
			}
		}
	}

	private String getFirstProfile( Map< Object, String > profilesMap ) {
		return String
				.valueOf( profilesMap.keySet()
						.iterator().next() );
	}

	private void redirectHttp() {
		boSession oXeoSession = getBoSession();
		XUIRequestContext oRequestContext  = XUIRequestContext.getCurrentContext();
		HttpServletResponse oHttpResponse = ( HttpServletResponse ) oRequestContext
				.getResponse();
		try {
			oHttpResponse.sendRedirect( oRequestContext
					.getActionUrl( getMainViewer( oXeoSession ) ) );
			oRequestContext.responseComplete();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}

	private void redirectAjax() {
		boSession oXeoSession = getBoSession();
		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
		oRequestContext
				.getScriptContext()
				.add( XUIScriptContext.POSITION_HEADER ,
						"Login_Logout" ,
						"document.location.href='"
								+ oRequestContext
										.getActionUrl( getMainViewer( oXeoSession ) )
								+ "'" );
		oRequestContext.renderResponse();
	}
	private XUIRequestContext getRequestContext(){
		return XUIRequestContext.getCurrentContext();
	}

	
	public boolean getConfigDisabled() {
		if (getIsLoggedIn() && StringUtils.isEmpty(getProfile()) ) {
			return true;
		}
		else {
			return false;
		}
			
	}
	
	public void logout() {
		HttpSession session = getHttpSession( true );
		XUIRequestContext oRequestContext = XUIRequestContext
				.getCurrentContext();
		HttpServletResponse oHttpResponse = ( HttpServletResponse ) oRequestContext
				.getResponse();
		if ( oRequestContext.isAjaxRequest() ) {
			oRequestContext.getScriptContext().add(
					XUIScriptContext.POSITION_HEADER ,
					"Login_Logout" ,
					XVWScripts.getCommandScript(
							( XUIComponentBase ) oRequestContext.getViewRoot()
									.findComponent( "login:logoutBtn" ) ,
							XVWScripts.WAIT_DIALOG ) );
			oRequestContext.renderResponse();
		} else {
			try {
				session.removeAttribute( "boSession" );
				session.invalidate();
				oHttpResponse.sendRedirect( oRequestContext
						.getActionUrl( oRequestContext.getViewRoot()
								.getViewId() ) );
			} catch ( IOException e ) {
				e.printStackTrace();
			}
			oRequestContext.responseComplete();
		}
	}

	private HttpSession getHttpSession(boolean createNew) {
		HttpServletRequest req = ( HttpServletRequest ) XUIRequestContext
				.getCurrentContext().getRequest();
		HttpSession session = req.getSession( createNew );
		return session;
	}

	/**
	 * Changes the user profile in the current session
	 */
	public void change_profile() {
		if ( getBoSession() == null ) {
			logout();
		} else {
			boSessionUser user = getBoSession().getUser();
			userName = user.getUserName();
			if ( StringUtils.hasValue( userName ) ) {

					XUIRequestContext oRequestContext = XUIRequestContext
							.getCurrentContext();
					String boui = oRequestContext.getRequestParameterMap().get(
							"boui" );
					boSession oXeoSession = getBoSession();

					Map< Object , String > profileMap = getProfileLovMap();

					if ( profileMap.containsKey( boui ) ) {
						setProfile( profileMap.get( boui ) );
						String currentProfileBoui = oXeoSession.getPerformerIProfileBouiAsString();
						if ( StringUtils.hasValue( currentProfileBoui ) && !currentProfileBoui.equals( boui )) {
							oXeoSession.setPerformerIProfileBoui( boui );
							if ( oRequestContext.isAjaxRequest() ) {
								redirectAjax();
							} else {
								redirectHttp();
							}
						}
					}
				}
		}
	}
}
