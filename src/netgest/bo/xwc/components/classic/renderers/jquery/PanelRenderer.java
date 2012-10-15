package netgest.bo.xwc.components.classic.renderers.jquery;

import static netgest.bo.xwc.components.HTMLAttr.CLASS;
import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLAttr.TITLE;
import static netgest.bo.xwc.components.HTMLTag.DIV;
import static netgest.bo.xwc.components.HTMLTag.SPAN;

import java.io.IOException;

import netgest.bo.xwc.components.classic.Panel;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.JQueryBuilder;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.utils.StringUtils;

/**
 * 
 * JQuery Based renderer for the Panel component.
 * Can be customized via CSS by the following classes
 * xwc-panel : Container around the panel
 * xwc-panel-title : Container for the panel title
 * xwc-panel-content : The panel content goes here
 * 
 * @author PedroRio
 *
 */
public class PanelRenderer extends JQueryBaseRenderer {

	@Override
    public void encodeBegin(XUIComponentBase component) throws IOException {
		XUIResponseWriter writer = getResponseWriter();
		Panel panel = (Panel) component;
		encodeBegin( panel , writer);
	}
	
	public void encodeBegin(Panel panel, XUIResponseWriter w) throws IOException {
		//Place Holder
		createInitialMarkup( panel, w );
    		
    	if (shouldUpdate( panel )){
    		JQueryBuilder b = new JQueryBuilder();
    		b.selectorByCss( ".xwc-panel" );
    		
    		if (panel.getCollapsible())
    			b.command( "collapsiblePanel(true)" );
    		else
    			b.command( "collapsiblePanel(false)" );
    		
    		addScriptFooter( "panel", b.build() );
    	}
	}

	private void createInitialMarkup( Panel panel, XUIResponseWriter w ) throws IOException {
		w.startElement( DIV );
    	w.writeAttribute( ID, panel.getClientId() );
    	
    		//Panel holder
    		w.startElement( DIV );
    			w.writeAttribute( CLASS, "xwc-panel" );
    			
    			String panelTitle =  panel.getTitle(); 
    			if (StringUtils.isEmpty( panelTitle ))
    				w.writeAttribute( TITLE, "" );
    			else
    				w.writeAttribute( TITLE, panelTitle );
    			
    			w.startElement( SPAN );
    			w.writeAttribute( CLASS, "panelContent" );
	}
	
	@Override
    public void encodeEnd(XUIComponentBase component ) throws IOException {
    	XUIResponseWriter w = getResponseWriter();
    	Panel panel = (Panel) component;
    	encodeEnd(panel,w);
	}

	public void encodeEnd( Panel panel, XUIResponseWriter w ) throws IOException{
		
		w.endElement( SPAN ); //End content
		
		w.endElement( DIV ); //End panel content
		
		w.endElement( DIV ); //End placeholder
    	
	}
	
}
