package netgest.bo.xwc.components.template.wrappers;

import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * Wraps the XWV Scripts methods 
 *
 */
public class XVWScriptsWrapper {

	public String getAjaxCommand(XUIComponentBase component){
		return XVWScripts.getAjaxCommandScript( component, XVWScripts.WAIT_DIALOG );
	}
	
	public String getFormId(XUIComponentBase component){
		return component.getNamingContainerId();
	}
	
	
	
}
