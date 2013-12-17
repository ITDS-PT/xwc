package netgest.bo.xwc.components.classic.renderers.xml;

import java.io.IOException;

import netgest.bo.xwc.components.classic.Cell;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class XMLCellRenderer extends XUIRenderer {
	
	@Override
	public void encodeBegin(XUIComponentBase component) throws IOException {
		
			Cell cell = (Cell) component;
			XUIResponseWriter rw = getResponseWriter();
			rw.startElement( component.getRendererType(), component );
			int colSpan = cell.getColSpan();
			if (colSpan > 1){
				rw.writeAttribute("colSpan", colSpan);
			}
			
	}

	@Override
	public void encodeEnd(XUIComponentBase component) throws IOException {
		XUIResponseWriter rw = getResponseWriter();
		rw.endElement( component.getRendererType() );
	}

}
