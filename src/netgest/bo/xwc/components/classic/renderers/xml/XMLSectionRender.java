package netgest.bo.xwc.components.classic.renderers.xml;

import static netgest.bo.xwc.components.HTMLAttr.LABEL;

import java.io.IOException;

import netgest.bo.xwc.components.classic.Section;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class XMLSectionRender extends XUIRenderer {
	
	@Override
	public void encodeBegin(XUIComponentBase component) throws IOException {
		
			Section section = (Section) component;
			XUIResponseWriter rw = getResponseWriter();
			rw.startElement( component.getRendererType(), component );
			rw.writeAttribute(LABEL, section.getLabel());
		
	}

	@Override
	public void encodeEnd(XUIComponentBase component) throws IOException {
		XUIResponseWriter rw = getResponseWriter();
		rw.endElement( component.getRendererType() );
	}

}
