package netgest.bo.xwc.components.template.wrappers;

import netgest.bo.xwc.framework.localization.XUIMessagesLocalization;
import netgest.utils.StringUtils;

public class LocalizationWrapper {

	private String[] defaultBundles;
	
	public LocalizationWrapper(String[] bundles) {
		this.defaultBundles = bundles;
	}
	
	public String get(String message){
		if (defaultBundles == null)
			throw new RuntimeException( "Cannot retrieve " + message + " when there're no default bundles " );
		for (String bundle : defaultBundles){
			String result = XUIMessagesLocalization.getMessage( bundle, message );
			if (StringUtils.hasValue( result )) return result;
		}
		return "";
	}
	
	public String get(String bundle, String message){
		return XUIMessagesLocalization.getMessage( bundle , message );
	}
	
}
