package netgest.bo.xwc.framework.localization;


public interface XUICoreMessages {

	public static final XUILocalizedMessage SESSION_EXPIRED = 
		new XUILocalizedMessage( XUICoreMessages.class.getName(), "SESSION_EXPIRED" );
	
	public static final XUILocalizedMessage SESSION_EXPIRED_REDIRECT = 
			new XUILocalizedMessage( XUICoreMessages.class.getName(), "SESSION_EXPIRED_REDIRECT" );
	
	public static final XUILocalizedMessage VIEWER_NOTFOUND = 
		new XUILocalizedMessage( XUICoreMessages.class.getName(), "VIEWER_NOTFOUND" );

	public static final XUILocalizedMessage COMPONENT_NOT_REGISTRED = 
		new XUILocalizedMessage( XUICoreMessages.class.getName(), "COMPONENT_NOT_REGISTRED" );

	public static final XUILocalizedMessage VIEWER_CLASS_NOT_FOUND = 
		new XUILocalizedMessage( XUICoreMessages.class.getName(), "VIEWER_CLASS_NOT_FOUND" );

	public static final XUILocalizedMessage REQUEST_ERROR = 
		new XUILocalizedMessage( XUICoreMessages.class.getName(), "REQUEST_ERROR" );
	
	public static final XUILocalizedMessage ERROR_DETAILS = 
		new XUILocalizedMessage( XUICoreMessages.class.getName(), "ERROR_DETAILS" );
}
