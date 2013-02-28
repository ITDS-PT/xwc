package netgest.bo.xwc.components.classic.theme;

import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIStyleContext;
import netgest.bo.xwc.framework.XUITheme;

public class NullTheme implements XUITheme {

	@Override
	public String getResourceBaseUri() {
		return "";
	}

	@Override
	public void addStyle(XUIStyleContext styleContext) {
		
	}

	@Override
	public void addScripts(XUIScriptContext styleContext) {
		
	}

	@Override
	public String getBodyStyle() {
		return "";
	}

	@Override
	public String getHtmlStyle() {
		return "";
	}
	
	
	
}
