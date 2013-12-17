package netgest.bo.xwc.components.classic.renderers.xml;

import static netgest.bo.xwc.components.HTMLAttr.CELLPADDING;
import static netgest.bo.xwc.components.HTMLAttr.CELLSPACING;

import java.io.IOException;

import netgest.bo.xwc.components.classic.Rows;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class XMLRowsRender extends XUIRenderer {
	
	@Override
	public void encodeBegin(XUIComponentBase component) throws IOException {
		
			Rows rows = (Rows) component;
			XUIResponseWriter rw = getResponseWriter();
			rw.startElement( component.getRendererType(), component );
			int padding = rows.getCellPadding();
			int spacing = rows.getCellSpacing();
			
			rw.writeAttribute(CELLPADDING, padding);
			rw.writeAttribute(CELLSPACING, spacing);
			
		
	}

	@Override
	public void encodeEnd(XUIComponentBase component) throws IOException {
		XUIResponseWriter rw = getResponseWriter();
		rw.endElement( component.getRendererType() );
	}

}
