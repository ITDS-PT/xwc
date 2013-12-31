package netgest.bo.xwc.components.classic.renderers.xml;

import java.io.IOException;

import netgest.bo.xwc.components.classic.OutputHtml;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class XMLOutputHtmlRenderer extends XUIRenderer {
	
	@Override
	public void encodeBegin(XUIComponentBase component) throws IOException {
		
			OutputHtml output = (OutputHtml) component;
			XUIResponseWriter rw = getResponseWriter();
			rw.startElement( component.getRendererType(), component );
			Object value = output.getValue();
			if (value != null){
				rw.writeCDATA(value.toString());
			}
			
			
	}

	@Override
	public void encodeEnd(XUIComponentBase component) throws IOException {
		XUIResponseWriter rw = getResponseWriter();
		rw.endElement( component.getRendererType() );
	}

}
