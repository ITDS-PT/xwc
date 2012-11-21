package netgest.bo.xwc.framework.messages;

import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.localization.XUILocalizedMessage;

/**
 * 
 * Factory to create Alert XUIMessages
 * 
 * @author PedroRio
 *
 */
public class XUIAlertMessageFactory {

	//Warning Messages
	public static XUIMessage createWarning(String title, String message){
		return new XUIMessage(XUIMessage.TYPE_ALERT, 
				XUIMessage.SEVERITY_WARNING, 
				title, 
				message );
	}
	
	public static XUIMessage createWarning(String message){
		return new XUIMessage(XUIMessage.TYPE_ALERT, 
				XUIMessage.SEVERITY_WARNING, 
				ComponentMessages.XUIMESSAGE_WARNING.toString(), 
				message );
	}
	
	public static XUIMessage createWarning(XUILocalizedMessage title, XUILocalizedMessage message){
		return new XUIMessage( XUIMessage.TYPE_ALERT, 
				XUIMessage.SEVERITY_WARNING, 
				title.toString(), 
				message.toString() );
	}
	
	public static XUIMessage createWarning(XUILocalizedMessage message){
		return new XUIMessage( XUIMessage.TYPE_ALERT, 
				XUIMessage.SEVERITY_WARNING, 
				ComponentMessages.XUIMESSAGE_WARNING.toString(), 
				message.toString() );
	}
	
	//Info Messages
	public static XUIMessage createInfo(String title, String message){
		return new XUIMessage( XUIMessage.TYPE_ALERT, 
				XUIMessage.SEVERITY_INFO, 
				title, 
				message );
	}
	
	public static XUIMessage createInfo(String message){
		return new XUIMessage( XUIMessage.TYPE_ALERT, 
				XUIMessage.SEVERITY_INFO, 
				ComponentMessages.XUIMESSAGE_INFO.toString(), 
				message );
	}
	
	public static XUIMessage createInfo(XUILocalizedMessage title, XUILocalizedMessage message){
		return new XUIMessage( XUIMessage.TYPE_ALERT, 
				XUIMessage.SEVERITY_INFO, 
				title.toString(), 
				message.toString() );
	}
	
	public static XUIMessage createInfo(XUILocalizedMessage message){
		return new XUIMessage( XUIMessage.TYPE_ALERT, 
				XUIMessage.SEVERITY_INFO, 
				ComponentMessages.XUIMESSAGE_INFO.toString(), 
				message.toString() );
	}
	
	//Error Messages
	public static XUIMessage createError(String title, String message){
		return new XUIMessage( XUIMessage.TYPE_ALERT, 
				XUIMessage.SEVERITY_ERROR, 
				title, 
				message );
	}
	
	public static XUIMessage createError(String message){
		return new XUIMessage( XUIMessage.TYPE_ALERT, 
				XUIMessage.SEVERITY_ERROR, 
				ComponentMessages.XUIMESSAGE_ERROR.toString(), 
				message );
	}
	
	public static XUIMessage createError(XUILocalizedMessage title, XUILocalizedMessage message){
		return new XUIMessage( XUIMessage.TYPE_ALERT, 
				XUIMessage.SEVERITY_ERROR, 
				title.toString(), 
				message.toString() );
	}
	
	public static XUIMessage createError(XUILocalizedMessage message){
		return new XUIMessage( XUIMessage.TYPE_ALERT, 
				XUIMessage.SEVERITY_ERROR, 
				ComponentMessages.XUIMESSAGE_ERROR.toString(), 
				message.toString() );
	}
	
	//Critical Messages
	public static XUIMessage createCritical(String title, String message){
		return new XUIMessage( XUIMessage.TYPE_ALERT, 
				XUIMessage.SEVERITY_CRITICAL, 
				title, 
				message );
	}
	
	public static XUIMessage createCritical(String message){
		return new XUIMessage( XUIMessage.TYPE_ALERT, 
				XUIMessage.SEVERITY_CRITICAL, 
				ComponentMessages.XUIMESSAGE_CRITICAL.toString(), 
				message );
	}
	
	public static XUIMessage createCritical(XUILocalizedMessage title, XUILocalizedMessage message){
		return new XUIMessage( XUIMessage.TYPE_ALERT, 
				XUIMessage.SEVERITY_CRITICAL, 
				title.toString(), 
				message.toString() );
	}
	
	public static XUIMessage createCritical(XUILocalizedMessage message){
		return new XUIMessage( XUIMessage.TYPE_ALERT, 
				XUIMessage.SEVERITY_CRITICAL, 
				ComponentMessages.XUIMESSAGE_CRITICAL.toString(), 
				message.toString() );
	}
	
	
}
