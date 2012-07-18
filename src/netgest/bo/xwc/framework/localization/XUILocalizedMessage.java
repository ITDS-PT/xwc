package netgest.bo.xwc.framework.localization;

import java.util.Locale;


public class XUILocalizedMessage {
	
	
	private String 		bundle;
	private String 		id;
	
	public XUILocalizedMessage( String bundle, String id) {
		this.id = id;
		this.bundle = bundle;
	}
	
	public String toString() {
		try{
			return XUIMessagesLocalization.getMessage( bundle, id );
		} catch (Exception e){
			return "";
		}
	}

	public String toString( Locale locale ) {
		return XUIMessagesLocalization.getMessage( locale, bundle, id );
	}
	
	public String toString( Locale locale, Object ...args ) {
		return XUIMessagesLocalization.getMessage(locale, bundle, id, args );
	}

	public String toString( Object ...args ) {
		return XUIMessagesLocalization.getMessage( bundle, id, args );
	}
	
}
