package netgest.bo.xwc.components.classic.renderers.jquery;

import static netgest.bo.xwc.components.HTMLAttr.CLASS;
import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;
import static netgest.bo.xwc.components.HTMLTag.FIELDSET;
import static netgest.bo.xwc.components.HTMLTag.LEGEND;

import java.io.IOException;

import netgest.bo.xwc.components.classic.Section;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.JQueryBuilder;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.utils.StringUtils;

public class SectionRenderer extends JQueryBaseRenderer {

	@Override
    public void encodeBegin(XUIComponentBase component) throws IOException {
        
		Section section = (Section) component;
		XUIResponseWriter w = getResponseWriter();
		
		if (!section.isRenderedOnClient()){
			createInitialMarkup( section, w );
		}
		
		if (shouldUpdate( section )	)
			updateComponent( section, w);
		

		
    }
	
	private void updateComponent( Section section, XUIResponseWriter w ) {
		JQueryBuilder builder = new JQueryBuilder();
		if (section.getVisible())
			builder.selectorById( section.getId() ).show();
		else
			builder.selectorById( section.getId() ).hide();
		
		addScriptFooter( section.getId() + "_update", builder.build() );
	}

	private void createInitialMarkup( Section section, XUIResponseWriter w ) throws IOException {

		w.startElement( DIV );
			w.writeAttribute( ID, section.getClientId() );
		w.startElement( FIELDSET );
			w.writeAttribute( ID, section.getId() );
			w.writeAttribute( CLASS, "ui-widget ui-widget-content xwc-section" );
			w.startElement( LEGEND );
				if (StringUtils.hasValue( section.getLabel() )){
					w.writeAttribute( CLASS, "ui-widget-header ui-corner-all xwc-legend" );
					w.write( section.getLabel() );
				}
				else
					w.writeAttribute( CLASS, "ui-widget-header ui-corner-all xwc-legend-empty" );
			w.endElement( LEGEND );
	}

	@Override
    public void encodeEnd(XUIComponentBase component ) throws IOException {
    	
    	Section tb = (Section) component;
    	XUIResponseWriter w = getResponseWriter();
    	if (!tb.isRenderedOnClient()){
    		w.endElement( FIELDSET );
    		w.endElement( DIV );
    	}
    }
	
}
