package netgest.bo.xwc.components.classic.renderers.jquery;

import static netgest.bo.xwc.components.HTMLAttr.CLASS;
import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLAttr.NAME;
import static netgest.bo.xwc.components.HTMLAttr.STYLE;
import static netgest.bo.xwc.components.HTMLAttr.TYPE;
import static netgest.bo.xwc.components.HTMLAttr.VALUE;
import static netgest.bo.xwc.components.HTMLTag.DIV;
import static netgest.bo.xwc.components.HTMLTag.INPUT;

import java.io.IOException;
import java.util.Map;

import netgest.bo.xwc.components.classic.AttributeBase;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.JQueryBuilder;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIComponentBase.StateChanged;

public class AttributeTextRenderer extends JQueryBaseRenderer {

	@Override
    public void encodeBegin(XUIComponentBase component) throws IOException {
		XUIResponseWriter w = getResponseWriter();
		AttributeBase  activeComponent = (AttributeBase) component;
		
		if (!activeComponent.isRenderedOnClient()){
			createInitialMarkup( w, activeComponent );
			addComponentScript( component );
		}
		
		if (activeComponent.wasStateChanged2() == StateChanged.FOR_RENDER)
			addComponentScript( component );
    }

	private void addComponentScript( XUIComponentBase component ) {
		AttributeBase base = (AttributeBase) component;
		JQueryBuilder builder = new JQueryBuilder();
			builder.componentSelectorById( component.getId() )
				.setInputValue( base.getDisplayValue() );
		addScriptFooter( component.getClientId(), builder.build() );
		
	}

	private void createInitialMarkup( XUIResponseWriter w, AttributeBase textComponent )
			throws IOException {
		w.startElement( DIV );
			w.writeAttribute( ID, textComponent.getClientId() );
		
		w.startElement( INPUT );
			w.writeAttribute( NAME, textComponent.getClientId() );
			w.writeAttribute( CLASS, "text ui-widget-content ui-corner-all" );
			w.writeAttribute( ID, textComponent.getId() );
			w.writeAttribute( TYPE, "text" );
			w.writeAttribute( VALUE, textComponent.getDisplayValue() );
			w.writeAttribute( STYLE, "width:100%" );
	}
    
    @Override
    public void encodeEnd(XUIComponentBase component ) throws IOException {
    	XUIResponseWriter w = getResponseWriter();
    	if (!component.isRenderedOnClient()){
	    	w.endElement( INPUT );
		    w.endElement( DIV );
    	}
    	
    } 
    
    @Override
    public void decode(XUIComponentBase component){
    	AttributeBase oAttrComp = (AttributeBase) component; 
    	if( !oAttrComp.isDisabled() && !oAttrComp.isReadOnly() && oAttrComp.isVisible() ) {
            Map<String,String> reqMap = getFacesContext().getExternalContext().getRequestParameterMap();
        	
            String clientId =  oAttrComp.getClientId();
        	        	
            if( oAttrComp.getSubmittedValue() == null && reqMap.containsKey( clientId ) ) {
                String value = reqMap.get( clientId );
                oAttrComp.setSubmittedValue( value );
            } 
        }
    }
	
}
