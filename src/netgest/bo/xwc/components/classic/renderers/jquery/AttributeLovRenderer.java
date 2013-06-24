package netgest.bo.xwc.components.classic.renderers.jquery;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLAttr.SELECTED;
import static netgest.bo.xwc.components.HTMLAttr.STYLE;
import static netgest.bo.xwc.components.HTMLAttr.VALUE;
import static netgest.bo.xwc.components.HTMLTag.DIV;
import static netgest.bo.xwc.components.HTMLTag.OPTION;
import static netgest.bo.xwc.components.HTMLTag.SELECT;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import netgest.bo.xwc.components.classic.AttributeLov;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.JQueryBuilder;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.utils.StringUtils;

public class AttributeLovRenderer extends JQueryBaseRenderer {
	
	@Override
    public void encodeBegin(XUIComponentBase component) throws IOException {
		XUIResponseWriter w = getResponseWriter();
		AttributeLov lovComponent = (AttributeLov) component;
		
		if (!lovComponent.isRenderedOnClient()){
			w.startElement( DIV );
			w.writeAttribute( ID, lovComponent.getClientId() );
			
			w.startElement( SELECT );
			w.writeAttribute( STYLE, "width:100%" );
			w.writeAttribute( ID, lovComponent.getId() );
			Map<Object,String> options = lovComponent.getLovMap();
			Set<Entry<Object, String>> entries = options.entrySet();
			for (Entry<Object,String> current : entries){
				w.startElement( OPTION );
					w.writeAttribute( VALUE, current.getKey() );
					checkAndSetSelectedValue( w, lovComponent, current.getKey() );
					
					w.write( current.getValue() );
				w.endElement( OPTION );
			}
		}
		if (shouldUpdate( lovComponent ))
			updateComponent(lovComponent);
	}

	private void checkAndSetSelectedValue( XUIResponseWriter w, AttributeLov lovComponent, Object value ) throws IOException {
		Object valueComponent = lovComponent.getValue(); 
		if (valueComponent != null && StringUtils.hasValue( valueComponent.toString()) ){
			if (valueComponent.equals( value ))
				w.writeAttribute( SELECTED, "selected" );
		}
	}
	
	private void updateComponent( AttributeLov lovComponent ) {
		JQueryBuilder b = new JQueryBuilder();
		b.componentSelectorById( lovComponent.getClientId() );
		if (lovComponent.isVisible())
			b.show();
		else
			b.hide();
		addScriptFooter( lovComponent.getClientId(), b.build() );
		
		String result = "";
		if ( lovComponent.getValue() != null )
			result = lovComponent.getValue().toString();
		JQueryBuilder valueBuilder = new JQueryBuilder();
		valueBuilder.selectorById( lovComponent.getId() ).setInputValue( result );
		addScriptFooter( lovComponent.getClientId() + "_val", valueBuilder.build() );
		
		
	}

	@Override
    public void encodeEnd(XUIComponentBase component ) throws IOException {
    	XUIResponseWriter w = getResponseWriter();
    	if (!component.isRenderedOnClient()){
	    	w.endElement( SELECT );
	    	w.endElement( DIV );
    	}
	}
	
	@Override
	public void decode(XUIComponentBase component) {
		
	}

}
