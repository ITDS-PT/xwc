package netgest.bo.xwc.components.template.renderers;

import static netgest.bo.xwc.components.HTMLAttr.CLASS;
import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIComponent;

import netgest.bo.xwc.components.classic.Tab;
import netgest.bo.xwc.components.classic.Tabs;
import netgest.bo.xwc.components.template.base.TemplateRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * Renderer for the Tabs component, tabs are responsible for their own children
 *
 */
public class TabsRenderer extends TemplateRenderer {
	
	@Override
	public boolean getRendersChildren() {
		return true;
	}
	
	@Override
    public void encodeChildren(XUIComponentBase component) throws IOException {
    }

	@Override
	public void templateEncodeChildren( XUIComponentBase component ) throws IOException  {
		XUIResponseWriter w = getResponseWriter();
        
        Tabs oTabs = (Tabs)component;
        Iterator<UIComponent> oChildsIt = component.getChildren().iterator();
        while( oChildsIt.hasNext() ) {
            Tab oChildTab = (Tab)oChildsIt.next();
            if( oChildTab.getId().equals( oTabs.getActiveTab() ) && oChildTab.isVisible() ) {
            	
            	w.startElement( DIV );
            		w.writeAttribute( ID, "#"+oChildTab.getId() );
            		w.writeAttribute( CLASS, "xwc-active-tab" );
            		
	            	if( oTabs.isRenderedOnClient() ) {
	            		oChildTab.forceRenderOnClient();
	            	}
            	
            	oChildTab.encodeAll();
            	
            	w.endElement( DIV );
                
            }
        }
	}
	
}
