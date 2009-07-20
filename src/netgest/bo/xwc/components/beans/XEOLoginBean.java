package netgest.bo.xwc.components.beans;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boSession;
import netgest.bo.utils.IProfileUtils;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.localization.ViewersMessages;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.localization.XUICoreMessages;

public class XEOLoginBean extends XEOSecurityLessBean {
	
	private String userName;
	private String password;
	private String profile;
	private boolean showProfiles = false;
	
	public String getUserName() {
		return getIsLoggedIn()?getBoSession().getUser().getUserName():this.userName;
	}
	
	public String getStatusMessage() {
		String message = "";
		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
		String msg = oRequestContext.getRequestParameterMap().get( "msg" );
		if( msg != null ) {
			if( "1".equals( msg ) ) {
				message = XUICoreMessages.SESSION_EXPIRED.toString();
			}
		}
		return message;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getPassword() {
		return getIsLoggedIn()?"*********************":"";
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean getIsLoggedIn() {
		return getBoSession()!=null;
	}

	public boolean getDisableLogout() {
		return getBoSession()==null;
	}

	public boSession getBoSession() {
		HttpSession session = getHttpSession( false );
		if( session != null ) {
			boSession xeoSession = (boSession)session.getAttribute("boSession");
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

	public void setProfile( String profile ) {
		this.profile=profile;
	}

	public Map<Object, String> getProfileLovMap() {
		
		Map<Object,String> oProfilesMap = new LinkedHashMap<Object,String>();
		if( getBoSession() != null ) {
            String[] iProfiles;
            
			iProfiles = IProfileUtils.getIProfiles( getBoSession() );
			
	        if(iProfiles.length <= 1)
	        {
	            this.profile = IProfileUtils.DEFAULT;
	            if(iProfiles.length > 0)
	            {
	                this.profile = iProfiles[0].split(";")[0];
	            }
	        }
	        else {
                for( String profileString : iProfiles ) {
                	String[] currProfile = profileString.split(";");
                	oProfilesMap.put( currProfile[0] , currProfile[1] );
                }
	        }
		}
		return oProfilesMap;
	}
	
	public String getMainViewer() {
		String mViewer = (String)((HttpServletRequest)XUIRequestContext.getCurrentContext().getRequest()).getAttribute("__xwcMainViewer");
		if( mViewer == null ) {  
			return "Main.xvw";
		}
		return mViewer;
	}
	
	public void login() {

		boolean invalidCredentials = true;

		try {
			
			boApplication bApp = boApplication.getApplicationFromStaticContext("XEO");
			
			boSession oXeoSession = getBoSession();

			if( this.getIsLoggedIn() && showProfiles && this.profile != null ) 
			{
				showProfiles = false;
				invalidCredentials = false;
				oXeoSession.setPerformerIProfileBoui( this.profile );
				XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
				HttpServletResponse oHttpResponse = (HttpServletResponse)oRequestContext.getResponse();
				
				if( oRequestContext.isAjaxRequest() ) {
					oRequestContext.getScriptContext().add(
						XUIScriptContext.POSITION_HEADER,
						"Login_Logout", 
						"document.location.href='" + oRequestContext.getActionUrl( getMainViewer() ) + "'"
					);
					oRequestContext.renderResponse();
				}
				else {
					try {
						oHttpResponse.sendRedirect( oRequestContext.getActionUrl( getMainViewer() ) );
						oRequestContext.responseComplete();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			else {
				if( this.userName != null && this.password != null ) {
					oXeoSession = bApp.boLogin( this.userName, this.password );
					HttpSession session = getHttpSession( true );
					XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
					HttpServletResponse oHttpResponse = (HttpServletResponse)oRequestContext.getResponse();
					session.setAttribute( "boSession", oXeoSession );
					showProfiles = false;
					
					if( getProfileLovMap().size() > 1 ) {
						showProfiles = true;
					}
					
					invalidCredentials = false;
					Boolean showProfilesConfig = (Boolean)((HttpServletRequest)XUIRequestContext.getCurrentContext().getRequest()).getAttribute("__xwcShowUserProfiles");
					if( (showProfilesConfig != null && !showProfilesConfig.booleanValue()) || !showProfiles  ) {
						if( oRequestContext.isAjaxRequest() ) {
							oRequestContext.getScriptContext().add(
								XUIScriptContext.POSITION_HEADER,
								"Login_Logout", 
								"document.location.href='" + oRequestContext.getActionUrl( getMainViewer() ) + "'"
							);
							oRequestContext.renderResponse();
						}
						else {
							try {
								oHttpResponse.sendRedirect( oRequestContext.getActionUrl( getMainViewer() ) );
								oRequestContext.responseComplete();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
			
		} catch (boLoginException e) {
			if( "BO-4000".equals( e.getErrorCode() ) ) {
				invalidCredentials = true;
			}
			else {
				
			}
		}
		
		if( invalidCredentials ) {

			XUIRequestContext.getCurrentContext().addMessage(
					"LoginBean",
					new XUIMessage( 
						XUIMessage.TYPE_ALERT,
						XUIMessage.SEVERITY_ERROR,
						ViewersMessages.LOGIN_TITLE_ERROR_LOGIN.toString(),
						ViewersMessages.LOGIN_INVALID_CREDENCIALS.toString()
					)
				);
		}

	
	}

	public void logout() {
		HttpSession session = getHttpSession( true );
		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
		HttpServletResponse oHttpResponse = (HttpServletResponse)oRequestContext.getResponse();
		if( oRequestContext.isAjaxRequest() ) {
			oRequestContext.getScriptContext().add(
				XUIScriptContext.POSITION_HEADER,
				"Login_Logout", 
				XVWScripts.getCommandScript( 
						(XUIComponentBase)oRequestContext.getViewRoot().findComponent("login:logoutBtn") , 
						XVWScripts.WAIT_STATUS_MESSAGE 
				)
			);
			oRequestContext.renderResponse();
		}
		else {
			try {
				session.removeAttribute("boSession");
				session.invalidate();
				oHttpResponse.sendRedirect( oRequestContext.getActionUrl( oRequestContext.getViewRoot().getViewId() ) );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			oRequestContext.responseComplete();
		}
	}

	private HttpSession getHttpSession( boolean createNew ) {
		HttpServletRequest req = (HttpServletRequest)XUIRequestContext.getCurrentContext().getRequest();
		HttpSession session = req.getSession( createNew );
		return session;
	}
}
