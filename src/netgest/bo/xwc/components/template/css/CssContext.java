package netgest.bo.xwc.components.template.css;

import netgest.bo.xwc.components.template.javascript.JavaScriptContext.Position;

/**
 * Css Context interface to include css files and inline styles
 *
 */
public interface CssContext {

	/**
	 * 
	 * Adds a css to execute
	 * 
	 * @param script The script
	 * @param scriptId The script identifier
	 * @param position The position of the script
	 */
	public void add(String script, String scriptId, Position position);
	
	/**
	 * 
	 * Includes a css file
	 * 
	 * @param url The script url
	 * @param scriptId The script identifier
	 * @param position The position of the script
	 */
	public void include(String url, String scriptId, Position position);
	
}
