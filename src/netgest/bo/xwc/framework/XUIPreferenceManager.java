package netgest.bo.xwc.framework;

import netgest.bo.preferences.Preference;
import netgest.bo.preferences.PreferenceManager;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;

public class XUIPreferenceManager {
	
	private static final EboContext getEboContext() {
		return boApplication.currentContext().getEboContext();
	}
	
	private static final String getProfileName() {
		return getEboContext().getBoSession().getPerformerIProfileName();
	}
	
	private static final String getUserName() {
		return getEboContext().getSysUser().getUserName();
	}
	
	private static final PreferenceManager getManager() {
		return getEboContext().getApplication().getPreferencesManager();
	}
	
	public static final Preference getSystemPreference( String name ) {
		return getManager().getSystemPreference( name );
	}
	
	public static final Preference getSystemPreference( String name, String customContext ) {
		return getManager().getSystemPreference( name, customContext );
	}
	
	public static final Preference getProfilePreference( String name ) {
		return getManager().getProfilePreference( name, getProfileName() );
	}
	
	public static final Preference getProfilePreference( String name, String customContext ) {
		return getManager().getProfilePreference( name, getProfileName(), customContext );
	}
	
	public static final Preference getUserPreference( String name ) {
		return getManager().getUserPreference( name, getUserName() );
	}
	
	public static final Preference getUserPreference( String name, String customContext ) {
		return getManager().getUserPreference( name, getUserName(), customContext );
	}
	
	public static final Preference getUserPreferenceInProfile( String name ) {
		return getManager().getUserPreferenceInProfile( name, getUserName(), getProfileName() );
	}
	
	public static final Preference getUserPreferenceInProfile( String name, String customContext ) {
		return getManager().getUserPreferenceInProfile( name, getUserName(), getProfileName(), customContext );
	}
	
}
