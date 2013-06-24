package netgest.bo.xwc.components.classic;

import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class Layouts {

	public static final String LAYOUT_FIT_PARENT 	= "fit-parent";
	public static final String LAYOUT_FIT_WINDOW 	= "fit-window";
	public static final String LAYOUT_FORM_LAYOUT 	= "form";

	public static final void registerComponent( XUIResponseWriter w, XUIComponentBase oComp, String sLayoutType ) {
        w.getScriptContext().add(
        		XUIScriptContext.POSITION_FOOTER, 
        		oComp.getClientId()+"_layoutReg", 
        		"ExtXeo.layoutMan.register('" + XUIRequestContext.getCurrentContext().getViewRoot().getClientId() + "','" + oComp.getClientId() + "','" + sLayoutType + "');"
        	);
	}
	
	public static final void registerComponent( XUIResponseWriter w, String clientId, String sLayoutType ) {
		w.getScriptContext().add(
        		XUIScriptContext.POSITION_FOOTER, 
        		clientId + "_layoutReg", 
        		"ExtXeo.layoutMan.register('" + XUIRequestContext.getCurrentContext().getViewRoot().getClientId() + "','" + clientId + "','" + sLayoutType + "');"
        	);
	}
	
	public static final void registerComponent( XUIScriptContext context, String clientId, String viewId, String sLayoutType  ) {
        context.add(
        		XUIScriptContext.POSITION_FOOTER, 
        		clientId+"_layoutReg", 
        		"ExtXeo.layoutMan.register('" + viewId + "','" + clientId + "','" + sLayoutType + "');"
        	);
	}

	public static final void doLayout( XUIResponseWriter w ) {
		w.getScriptContext().add(
        		XUIScriptContext.POSITION_FOOTER, 
        		"doLayout_0", 
        		"Ext.onReady( function() { " +
        		"ExtXeo.layoutMan.doLayout('" + XUIRequestContext.getCurrentContext().getViewRoot().getClientId() + "');" +
        		"});"
        	);
	}
}
