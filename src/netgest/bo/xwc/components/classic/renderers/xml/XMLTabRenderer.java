package netgest.bo.xwc.components.classic.renderers.xml;

import java.io.IOException;

import netgest.bo.xwc.components.classic.Tab;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class XMLTabRenderer extends XUIRenderer {
	
	@Override
	public void encodeBegin(XUIComponentBase component) throws IOException {
		
		Tab tab = (Tab) component;
		XUIResponseWriter rw = getResponseWriter();
		if (tab.getEffectivePermission(SecurityPermissions.READ) && tab.isVisible()){
			rw.startElement( component.getRendererType(), component );
			rw.writeAttribute("label", tab.getLabel());
		}
		
	}
	
	@Override
	public void encodeChildren(XUIComponentBase component) throws IOException {
		Tab tab = (Tab) component;
		if (tab.getEffectivePermission(SecurityPermissions.READ) && tab.isVisible()){
			super.encodeChildren(component);
		}
	}
	
	@Override
	public boolean getRendersChildren() {
		return true;
	}

	@Override
	public void encodeEnd(XUIComponentBase component) throws IOException {
		Tab tab = (Tab) component;
		if (tab.getEffectivePermission(SecurityPermissions.READ) && tab.isVisible()){
			XUIResponseWriter rw = getResponseWriter();
			rw.endElement( component.getRendererType() );
		}
	}

}
