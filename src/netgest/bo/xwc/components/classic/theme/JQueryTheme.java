package netgest.bo.xwc.components.classic.theme;

import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIStyleContext;

public class JQueryTheme extends ExtJsTheme {

	
	public String getResourceBaseUriJquery() {
		return "jquery-xeo/";
	}

	@Override
	public void addStyle( XUIStyleContext styleContext ) {
		super.addStyle( styleContext );
		styleContext.addInclude(XUIStyleContext.POSITION_HEADER, "jquery_ui_css",
				composeUrl(getResourceBaseUriJquery() + "css/anacom/jquery-ui-1.8.20.custom.css"));
		/*DO NOT INCLUDE styleContext.addInclude(XUIStyleContext.POSITION_HEADER, "jquery_ui_css",
				composeUrl(getResourceBaseUriJquery() + "css/smoothness/jquery-ui-1.9.0.custom.css"));*/
		styleContext.addInclude(XUIStyleContext.POSITION_HEADER, "jquery_css_xeo",
				composeUrl("ext-xeo/css/jquery-xeo.css"));
		styleContext.addInclude(XUIStyleContext.POSITION_HEADER, "jqGrid_css",
				composeUrl("jquery-xeo/css/ui.jqgrid.css"));
		styleContext.addInclude(XUIStyleContext.POSITION_HEADER, "multiSelect_css",
				composeUrl("jquery-xeo/css/ui.multiselect.css"));
		
		
	}

	@Override
	public void addScripts( XUIScriptContext scriptContext ) {
		super.addScripts( scriptContext );
		
		
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "jquery",
				composeUrl("//ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"));
		String jQueryFallback = "!window.jQuery && document.write(unescape(\"%3Cscript src='jquery-xeo/js/jquery-1.8.2.min.js' type='text/javascript'%3E%3C/script%3E\"));";
		scriptContext.add( XUIScriptContext.POSITION_HEADER, "jquery_fallback",jQueryFallback );
		
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "jquery-ui",
				composeUrl("//ajax.googleapis.com/ajax/libs/jqueryui/1.8.23/jquery-ui.min.js"));
		String jQueryUIFallback = "!window.jQuery.ui && document.write(unescape(\"%3Cscript src='jquery-xeo/js/jquery-ui-1.8.20.custom.min.js' type='text/javascript'%3E%3C/script%3E\"));";
		scriptContext.add( XUIScriptContext.POSITION_HEADER, "jqueryUI_fallback",jQueryUIFallback );
		
		
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "jquery-blockui",
				composeUrl(getResourceBaseUriJquery() + "js/jquery.blockUI.js"));
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "jquery-jqGridLocale",
				composeUrl(getResourceBaseUriJquery() + "js/grid.locale-en.js"));
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "jquery-ui-multi-select",
				composeUrl(getResourceBaseUriJquery() + "js/ui.multiselect.js"));
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "jquery-jqGrid",
				composeUrl(getResourceBaseUriJquery() + "js/jquery.jqGrid.src.js"));
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "jquery-xeo",
				composeUrl(getResourceBaseUriJquery() + "jquery-xeo.js"));
				
				
	}
	
	

}
