package netgest.bo.xwc.components.classic.renderers.xml;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;

import netgest.bo.xwc.components.classic.AttributeNumberLookup;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.xeo.components.SplitedLookup;

public class XMLSplitLookupRenderer extends XUIRenderer {
	
	@Override
	public void encodeBegin(XUIComponentBase component) throws IOException {
		
			SplitedLookup attribute = (SplitedLookup) component;
			XUIResponseWriter rw = getResponseWriter();
			rw.startElement( "attribute", component );
			//Encode Label
			attribute.getLabelComponent().encodeAll();
			//Encode Lookup
			List<UIComponent> children = attribute.getChildren();
			for (UIComponent child : children){
				if (child instanceof AttributeNumberLookup){
					((AttributeNumberLookup)child).encodeAll();
				}
			}
	}

	@Override
	public void encodeEnd(XUIComponentBase component) throws IOException {
		XUIResponseWriter rw = getResponseWriter();
		rw.endElement( "attribute" );
	}
	
	@Override
	public boolean getRendersChildren() {
		return true;
	}
	
	@Override
	public void encodeChildren( XUIComponentBase component ) throws IOException {
		
	}

}
