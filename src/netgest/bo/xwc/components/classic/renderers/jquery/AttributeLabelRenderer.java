package netgest.bo.xwc.components.classic.renderers.jquery;

import static netgest.bo.xwc.components.HTMLAttr.STYLE;
import static netgest.bo.xwc.components.HTMLTag.LABEL;

import java.io.IOException;

import netgest.bo.xwc.components.classic.AttributeLabel;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class AttributeLabelRenderer extends JQueryBaseRenderer {

	@Override
    public void encodeBegin(XUIComponentBase component) throws IOException {
		XUIResponseWriter w = getResponseWriter();
		AttributeLabel labelComponent = (AttributeLabel) component;
		w.startElement( LABEL );
			w.writeAttribute( STYLE, "font-family:verdana; font-size:12px" );
			w.write( labelComponent.getText() );
	}
	
	@Override
    public void encodeEnd(XUIComponentBase component ) throws IOException {
    	XUIResponseWriter w = getResponseWriter();
    	w.endElement( LABEL );
	}
	
}
