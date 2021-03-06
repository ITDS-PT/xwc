package netgest.bo.xwc.components.classic.renderers.xml;

import java.io.IOException;

import javax.smartcardio.ATR;

import netgest.bo.xwc.components.classic.AttributeBase;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class XMLAttributeRenderer extends XUIRenderer {
	
	@Override
	public void encodeBegin(XUIComponentBase component) throws IOException {
		
			AttributeBase attribute = (AttributeBase) component;
			XUIResponseWriter rw = getResponseWriter();
			rw.startElement( component.getRendererType(), component );
			rw.writeCDATA(attribute.getDisplayValue());
		
	}

	@Override
	public void encodeEnd(XUIComponentBase component) throws IOException {
		XUIResponseWriter rw = getResponseWriter();
		rw.endElement( component.getRendererType() );
	}

}
