package netgest.bo.xwc.components.template.wrappers;

import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.classic.scripts.XVWScripts.ValueType;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * Wraps the XWV Scripts methods to use in a freemarker template
 *
 */
public class XVWScriptsWrapper {

	/**
	 * Retrieve the script to execute an ajax command
	 * 
	 * @param component The command to execute
	 * @return The Javascript function to invoke
	 */
	public String ajaxCommand(XUIComponentBase component){
		return XVWScripts.getAjaxCommandScript( component, XVWScripts.WAIT_DIALOG );
	}
	
	/**
	 * Retrieve the script to execute an ajax command with a parameter as literal
	 * 
	 * @param component The command to execute
	 * @param value The value to pass as a literal
	 * @return The Javascript function to invoke
	 */
	public String ajaxCommand(XUIComponentBase component, String value){
		return XVWScripts.getAjaxCommandScript( component, value, XVWScripts.WAIT_DIALOG, ValueType.LITERAL );
	}
	
	/**
	 * Retrieve the script to execute an ajax command with a parameter as literal
	 * 
	 * @param component The command to execute
	 * @param value The value to pass as a literal
	 * @param type The type of value (String LITERAL OR VAR)
	 * @return The Javascript function to invoke
	 */
	public String ajaxCommand(XUIComponentBase component, String value, String type){
		ValueType valueType = ValueType.fromString( type );
		return XVWScripts.getAjaxCommandScript( component, value, XVWScripts.WAIT_DIALOG, valueType );
	}
	
	
	public String command(String target, XUIComponentBase component){
		return XVWScripts.getCommandScript(target, component, XVWScripts.WAIT_DIALOG);
	}
	
	public String command(String target, XUIComponentBase component, String value, String type){
		ValueType valueType = ValueType.fromString( type );
		return XVWScripts.getCommandScript(target, component, XVWScripts.WAIT_DIALOG, valueType );
	}

	public String ajaxCommand(String containerId, String commandId){
		return XVWScripts.getAjaxCommandScript( containerId , commandId , null );
	}
	
	public String ajaxCommand(String containerId, String commandId, String value){
		return XVWScripts.getAjaxCommandScript( containerId , commandId , value );
	}
	
	public String command(String containerId, String commandId){
		return XVWScripts.getCommandScript( containerId , commandId );
	}
	
	public String command(String containerId, String commandId, String value){
		return XVWScripts.getCommandScript( containerId , commandId , value );
	}
	
	public String openWindowCommand(String containerId, String commandId, String value){
		return XVWScripts.getOpenCommandWindow( containerId , commandId , value );
	}
	
	
	/**
	 * 
	 * Returns the script to refresh a view given its form identifier
	 * 
	 * @param formId The form identifier
	 * @return The
	 */
	public String synchView(String formId){
		return XVWScripts.getSyncClientViewScript( formId );
	}
	
	
	
	
	
	
}
