package netgest.bo.xwc.framework.messages;

import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.localization.XUILocalizedMessage;


/**
 * 
 * Sends XUIMessages to the current context, using a default title
 * (depending on the type of message - a WARNING message will have Warning as
 * title (properly localized) and a random identifier.
 * If you need more control see {@link XUIMessageBuilder} to create everything
 * 
 * 
 * @author PedroRio
 *
 */
public class XUIMessageSender {

	
	//--------
	//Alert Messages
	//--------
	public static void alertWarning( String message ){
		XUIRequestContext.getCurrentContext().addMessage( generateId() , 
				XUIAlertMessageFactory.createWarning( message ) );
	}
	
	public static void alertWarning( String title, String message ){
		XUIRequestContext.getCurrentContext().addMessage( generateId() , 
				XUIAlertMessageFactory.createWarning( title, message ) );
	}
	
	public static void alertWarning( XUILocalizedMessage message ){
		XUIRequestContext.getCurrentContext().addMessage( generateId() , 
				XUIAlertMessageFactory.createWarning( message ) );
	}
	
	public static void alertWarning( XUILocalizedMessage title, XUILocalizedMessage message ){
		XUIRequestContext.getCurrentContext().addMessage( generateId() , 
				XUIAlertMessageFactory.createWarning( title, message ) );
	}
	
	//Info Messages
	public static void alertInfo( String message ){
		XUIRequestContext.getCurrentContext().addMessage( generateId() , 
				XUIAlertMessageFactory.createInfo( message ) );
	}
	
	public static void alertInfo( String title, String message ){
		XUIRequestContext.getCurrentContext().addMessage( generateId() , 
				XUIAlertMessageFactory.createInfo( title, message ) );
	}
	
	public static void alertInfo( XUILocalizedMessage message ){
		XUIRequestContext.getCurrentContext().addMessage( generateId() , 
				XUIAlertMessageFactory.createInfo( message ) );
	}
	
	public static void alertInfo( XUILocalizedMessage title, XUILocalizedMessage message ){
		XUIRequestContext.getCurrentContext().addMessage( generateId() , 
				XUIAlertMessageFactory.createInfo( title, message ) );
	}
	
	//Critical Messages
	public static void alertCritical( String message ){
		XUIRequestContext.getCurrentContext().addMessage( generateId() , 
				XUIAlertMessageFactory.createCritical( message ) );
	}
	
	public static void alertCritical( String title, String message ){
		XUIRequestContext.getCurrentContext().addMessage( generateId() , 
				XUIAlertMessageFactory.createCritical( title, message ) );
	}
	
	public static void alertCritical( XUILocalizedMessage message ){
		XUIRequestContext.getCurrentContext().addMessage( generateId() , 
				XUIAlertMessageFactory.createCritical( message ) );
	}
	
	public static void alertCritical( XUILocalizedMessage title, XUILocalizedMessage message ){
		XUIRequestContext.getCurrentContext().addMessage( generateId() , 
				XUIAlertMessageFactory.createCritical( title, message ) );
	}
	
	//Error Messages
	public static void alertError( String message ){
		XUIRequestContext.getCurrentContext().addMessage( generateId() , 
				XUIAlertMessageFactory.createError( message ) );
	}
	
	public static void alertError( String title, String message ){
		XUIRequestContext.getCurrentContext().addMessage( generateId() , 
				XUIAlertMessageFactory.createError( title, message ) );
	}
	
	public static void alertError( XUILocalizedMessage message ){
		XUIRequestContext.getCurrentContext().addMessage( generateId() , 
				XUIAlertMessageFactory.createError( message ) );
	}
	
	public static void alertError( XUILocalizedMessage title, XUILocalizedMessage message ){
		XUIRequestContext.getCurrentContext().addMessage( generateId() , 
				XUIAlertMessageFactory.createError( message ) );
	}
	
	
	
	// ----------------
	//PopUp Messages
	// ----------------
	public static void popupWarning( String title, String message ){
		XUIRequestContext.getCurrentContext().addMessage( generateId() , 
				XUIPopupMessageFactory.createWarning( title, message ) );
	}
	
	public static void popupInfo( String title, String message ){
		XUIRequestContext.getCurrentContext().addMessage( generateId() , 
				XUIPopupMessageFactory.createInfo( title, message ) );
	}
	
	public static void popupCritical( String title, String message ){
		XUIRequestContext.getCurrentContext().addMessage( generateId() , 
				XUIPopupMessageFactory.createCritical( title, message ) );
	}
	
	public static void popupError( String title, String message ){
		XUIRequestContext.getCurrentContext().addMessage( generateId() , 
				XUIPopupMessageFactory.createError( title, message ) );
	}
	
	
	protected static String generateId(){
		return "A" + System.currentTimeMillis();
	}
	
	

}
