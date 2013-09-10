package netgest.bo.xwc.framework.localization;

import netgest.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Formatter;
import java.util.Hashtable;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

public class XUIMessagesLocalization {

	private static UTF8Control control = new UTF8Control();
	
	
	

	public static String getMessage(String lang, Locale local, String bundle,
			String key, Object... args) {
		Locale language;
		if (StringUtils.hasValue( lang )) {
			if (lang.charAt(2) == '_') {

				String s1 = lang.substring(0, 2);
				String s2 = lang.substring(3, 5);
				language = new Locale(s1, s2);
			} else {
				language = new Locale(lang);
			}
			return getMessage(language, bundle, key, args);
		}
		return getMessage(local, bundle, key, args);
	}

	

	private static final Hashtable<Locale, Hashtable<String, ResourceBundle>> resourceBundles = new Hashtable<Locale, Hashtable<String, ResourceBundle>>();

	public static String getMessage(Locale locale, String bundle, String key) {
		return getMessage(locale, bundle, key, (Object[]) null);
	}

	private enum HandleMissingResource{
		RETURN_NULL,
		RETURN_MESSAGE
	}
	
	public static String getMessage(Locale locale, String bundle, String key,
			Object... args) {
		return getMessage(locale,bundle,key,HandleMissingResource.RETURN_MESSAGE, args);
	}
	public static String getMessage(Locale locale, String bundle, String key, HandleMissingResource missingResource,
			Object... args ) {
		
		String localizedMessage = null;

		Hashtable<String, ResourceBundle> localeBundles;
		ResourceBundle resourceBundle;

		try {

			resourceBundle = null;
			localeBundles = resourceBundles.get(locale);

			if (localeBundles != null) {
				resourceBundle = localeBundles.get(bundle);
			}

			if (resourceBundle == null) {
				resourceBundle = ResourceBundle.getBundle(bundle, locale, control );
				if (resourceBundle != null) {
					if (localeBundles == null) {
						localeBundles = new Hashtable<String, ResourceBundle>();
						resourceBundles.put(locale, localeBundles);
					}
					localeBundles.put(bundle, resourceBundle);
				}
			}

			if (resourceBundle != null) {
				localizedMessage = resourceBundle.getString(key);
				if (args != null && args.length > 0) {
					Formatter formatter = new Formatter();
					formatter.format(localizedMessage, args);
					localizedMessage = formatter.toString();
				}
			}

		} catch (java.util.MissingResourceException e) {
		}
		if (localizedMessage == null) {
			if (missingResource == HandleMissingResource.RETURN_NULL)
				return null;
			
			localizedMessage = bundle + "_" + locale.getLanguage() + "[" + key
					+ "]";

			if (args != null && args.length > 0) {
				boolean first = true;
				localizedMessage += " (";
				for (Object arg : args) {
					if (!first)
						localizedMessage += ", ";
					first = false;
					localizedMessage += arg;
				}
				localizedMessage += ")";
			}
		}
		return localizedMessage;
	}
	
	/**
	 * 
	 * Attemps to retrieve the message if it exits, if it does not exist return null
	 * 
	 * @param bundle
	 * @param id
	 * @return
	 */
	public static String getMessageOptional(String bundle, String id) {
		return getMessage(getThreadCurrentLocale(), bundle, id, HandleMissingResource.RETURN_NULL, (Object[]) null);
	}

	public static String getMessage(String bundle, String id) {

		return getMessage(getThreadCurrentLocale(), bundle, id, (Object[]) null);
	}

	public static String getMessage(String bundle, String id, Object... args) {
		return getMessage(getThreadCurrentLocale(), bundle, id, args);
	}

	public static void setThreadCurrentLocale(Locale local) {
		XUILocalization.setCurrentLocale( local );
	}

	public static Locale getThreadCurrentLocale() {
		return XUILocalization.getCurrentLocale();
	}
	
	public static class UTF8Control extends Control {
    public ResourceBundle newBundle
        (String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
            throws IllegalAccessException, InstantiationException, IOException
    {
        // The below is a copy of the default implementation.
        String bundleName = toBundleName(baseName, locale);
        String resourceName = toResourceName(bundleName, "properties");
        ResourceBundle bundle = null;
        InputStream stream = null;
        
        
        if (reload) {
            URL url = loader.getResource(resourceName);
            if (url != null) {
                URLConnection connection = url.openConnection();
                if (connection != null) {
                    connection.setUseCaches(false);
                    stream = connection.getInputStream();
                }
            }
        } else {
            stream = loader.getResourceAsStream(resourceName);
        }

        
        if (stream != null) {
            try {
                // Only this line is changed to make it to read properties files as UTF-8.
                bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
            } finally {
                stream.close();
            }
        }
        return bundle;
    }
}
	

}
