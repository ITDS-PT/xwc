package netgest.bo.xwc.framework.messages;

import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.localization.XUILocalizedMessage;
import netgest.utils.StringUtils;

/**
 * 
 * XUIMessage creator class (implementing the builder pattern)
 * 
 * When creating a new message the defaults are:
 * Severy - Error
 * Type - Alert
 * Title - Error
 * Message - ""
 * 
 *  
 * 
 * @author PedroRio
 *
 */
public class XUIMessageBuilder {

	private XUIRequestContext ctx;
	private SEVERITY messageSeverity = SEVERITY.ERROR;
	private TYPE messageType = TYPE.ALERT;
	private String title = "";
	private String message = "";
	private String id = "";
	
	/**
	 * 
	 * Retrieves the id set, or generates one
	 * 
	 * @return
	 */
	public String getId(){
		if (StringUtils.isEmpty( id ))
			return "A" + System.currentTimeMillis();
		return id;
	}
	
	public enum SEVERITY{
		WARNING(XUIMessage.SEVERITY_WARNING, ComponentMessages.XUIMESSAGE_WARNING),
		ERROR(XUIMessage.SEVERITY_ERROR, ComponentMessages.XUIMESSAGE_ERROR),
		CRITICAL(XUIMessage.SEVERITY_CRITICAL, ComponentMessages.XUIMESSAGE_CRITICAL),
		INFO(XUIMessage.SEVERITY_INFO, ComponentMessages.XUIMESSAGE_INFO);
		
		private int severity;
		private String defaultTitle;
		
		private SEVERITY(int type, XUILocalizedMessage title){
			this.severity = type;
			this.defaultTitle = title.toString();
		}
		
		public int getSeverity(){
			return severity;
		}
		
		public String getTitle(){
			return defaultTitle;
		}
	}
	
	public enum TYPE{
		ALERT(XUIMessage.TYPE_ALERT),
		POPUP(XUIMessage.TYPE_POPUP_MESSAGE),
		MESSAGE(XUIMessage.TYPE_MESSAGE);
		
		private int type;
		
		private TYPE(int type){
			this.type = type;
		}
		
		public int getType(){
			return type;
		}
		
	}
	
	public XUIMessageBuilder(XUIRequestContext ctx){
		this.ctx = ctx;
	}
	
	/**
	 * 
	 * Sets the id to send the message with
	 * 
	 * @param id The id
	 * 
	 */
	public XUIMessageBuilder id(String id){
		this.id = id;
		return this;
	}
	
	/**
	 * Sets the message's defaultTitle
	 * 
	 * @param defaultTitle
	 * @return
	 */
	public XUIMessageBuilder title(String title){
		this.title = title;
		return this;
	}
	
	public XUIMessageBuilder title(XUILocalizedMessage title){
		this.title = title.toString();
		return this;
	}
	
	public XUIMessageBuilder title(XUILocalizedMessage title, Object... args){
		this.title = title.toString(args);
		return this;
	}
	
	public XUIMessageBuilder message(String message){
		this.message = message;
		return this;
	}
	
	public XUIMessageBuilder message(XUILocalizedMessage message){
		this.message = message.toString();
		return this;
	}
	
	public XUIMessageBuilder message(XUILocalizedMessage message, Object... args){
		this.message = message.toString(args);
		return this;
	}
	
	public XUIMessageBuilder severity(SEVERITY severity){
		this.messageSeverity = severity;
		return this;
	}
	
	public XUIMessageBuilder type(TYPE type){
		messageType = type;
		return this;
	}
	
	private String getTitle(){
		if (StringUtils.isEmpty( title ))
			return messageSeverity.getTitle();
		return title;
	}
	
	public void send(){
		ctx.addMessage( getId(), new XUIMessage( 
				messageType.getType(), messageSeverity.getSeverity(), 
				getTitle(), message ) );
	}

}
