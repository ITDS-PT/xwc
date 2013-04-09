package netgest.bo.xwc.components.classic.theme;

import java.io.IOException;

import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIStyleContext;
import netgest.bo.xwc.framework.XUITheme;
import netgest.bo.xwc.framework.components.XUIViewRoot;

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

	@Override
	public String getDocType() {
		return "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n";
	}

	@Override
	public void writeHeader(XUIResponseWriter writer) {
	}

	@Override
	public void writePostBodyContent(XUIRequestContext context,
			XUIResponseWriter writer, XUIViewRoot viewRoot) throws IOException  {
		
	}

	@Override
	public void writePreFooterContent(XUIRequestContext context,
			XUIResponseWriter writer, XUIViewRoot viewRoot) throws IOException {
	}
	
	
	
}
