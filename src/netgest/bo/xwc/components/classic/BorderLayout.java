package netgest.bo.xwc.components.classic;

import java.io.IOException;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class BorderLayout extends XUIComponentBase {



	public static class XEOHTMLRenderer extends XUIRenderer {

		@Override
		public void encodeBegin(XUIComponentBase c) throws IOException {
			XUIResponseWriter w = getResponseWriter();
			w.startElement( HTMLTag.DIV, c );
			w.writeAttribute(HTMLAttr.ID, c.getClientId(), null );
			w.writeText("Hello", null);
		}

		@Override
		public void encodeEnd(XUIComponentBase component) throws IOException {
			XUIResponseWriter w = getResponseWriter();
			w.endElement( HTMLTag.DIV );
	
			ExtConfig c = new ExtConfig("Ext.Panel");
			c.addJSString("layout", "border" );
			c.addJSString("renderTo", XUIRequestContext.getCurrentContext().getViewRoot().getClientId() );
			ExtConfig i = c.addChild("items");
			i.add("title", "'Hello'");
			i.add("region", "'center'");
			i.addJSString("contentEl", component.getClientId() );
			
			w.getScriptContext().add( XUIScriptContext.POSITION_FOOTER,
					component.getClientId(), 
					"Ext.onReady( function() { " + c.renderExtConfig().toString() + "});" 
				);
			
			/*
			StringBuilder sb = new StringBuilder();
			sb.append( "var l = new Ext.BorderLayout(document.body, center:{});" ).append('\n');
			sb.append( "l.beginUpdate();" ).append('\n');
			sb.append( "l.endUpdate();" ).append('\n');
*/			
			

		}

		@Override
		public boolean getRendersChildren() {
			return true;
		}

		@Override
		public void encodeChildren(XUIComponentBase component)
				throws IOException {
			
		
		}
		
	}
}
