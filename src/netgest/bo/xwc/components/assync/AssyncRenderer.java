package netgest.bo.xwc.components.assync;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class AssyncRenderer extends XUIRenderer {
	
	@Override
	public boolean getRendersChildren() {
		return true;
	}
	
	@Override
	public void encodeChildren(XUIComponentBase component) throws IOException {
		//Do nothing on purpose
	}

	
	@Override
	public void encodeBegin(XUIComponentBase component) throws IOException {
		XUIResponseWriter w = getResponseWriter();
		w.startElement( HTMLTag.DIV );
		w.writeAttribute( HTMLAttr.ID , component.getClientId() );
	}
	
	
	@Override
	@Deprecated
	public void encodeEnd(FacesContext context, UIComponent component)
			throws IOException {
		
		XUIResponseWriter w = getResponseWriter();
		w.endElement( HTMLTag.DIV );
	}
	

	
	
}
