package netgest.bo.xwc.framework.localization;

import java.util.Formatter;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;


public class XUIMessagesLocalization {

	public static void main( String[] args ) {
		// Test
		
		// Default languange
		System.out.println( getMessage("netgest.bo.xwc.framework.localization.messages1", "hwrld" ) );
		System.out.println( getMessage("netgest.bo.xwc.framework.localization.messages1", "hello","Pedro" ) );
		
		// English
		setThreadCurrentLocale( Locale.US );
		System.out.println( getMessage("netgest.bo.xwc.framework.localization.messages1", "hwrld" ) );
		System.out.println( getMessage("netgest.bo.xwc.framework.localization.messages1", "hello","Pedro" ) );
		
		// Japanese... may dont find it!!!
		setThreadCurrentLocale( Locale.JAPANESE );
		System.out.println( getMessage("netgest.bo.xwc.framework.localization.messages1", "hwrld" ) );
		System.out.println( getMessage("netgest.bo.xwc.framework.localization.messages1", "hello","Pedro" ) );
		
	}
	
	static ThreadLocal<Locale> threadLocal = new ThreadLocal<Locale>() {
		protected Locale initialValue() {
			return Locale.getDefault();
		};
	};
	
	private static final Hashtable<Locale,Hashtable<String, ResourceBundle>> resourceBundles = new Hashtable<Locale,Hashtable<String, ResourceBundle>>();
	
	public static String getMessage( Locale locale, String bundle, String key ) {
		return getMessage( locale, bundle, key );
	}

	public static String getMessage( Locale locale, String bundle, String key, Object ...args ) {
		
		String localizedMessage = null;
		
		Hashtable<String, ResourceBundle> localeBundles;
		ResourceBundle resourceBundle;
		
		resourceBundle = null;
		localeBundles = resourceBundles.get( locale );
		
		if( localeBundles != null ) {
			resourceBundle = localeBundles.get( bundle );
		}
		
		if( resourceBundle == null ) {
			resourceBundle = ResourceBundle.getBundle( bundle, locale );
			if ( resourceBundle != null ) {
				if( localeBundles == null ) {				
					localeBundles = new Hashtable<String, ResourceBundle>();
					resourceBundles.put( locale, localeBundles );
				}
				localeBundles.put( bundle, resourceBundle );
			}
		}
		
		if( resourceBundle != null ) {
			localizedMessage = resourceBundle.getString( key );
			if( args != null && args.length > 0 ) {
				Formatter formatter = new Formatter();
				formatter.format( localizedMessage , args );
				localizedMessage = formatter.toString();
			}
		}
		
		if ( localizedMessage == null ) 
		{
			localizedMessage = bundle + "_" + locale.getLanguage() + "[" + key + "]";
		}
		return localizedMessage;
	}

	public static String getMessage( String bundle, String id ) {
		return getMessage(getThreadCurrentLocale(), bundle, id, (Object[])null );
	}

	public static String getMessage( String bundle, String id, Object ...args ) {
		return getMessage(getThreadCurrentLocale(), bundle, id, args);
	}
	
	public static void setThreadCurrentLocale( Locale local ) {
		
		threadLocal.set( local );
		
	}
	
	public static Locale getThreadCurrentLocale() {
		
		return threadLocal.get();
		
	}

}


