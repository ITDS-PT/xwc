package netgest.bo.xwc.components.classic.renderers.jquery;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLAttr.NAME;
import static netgest.bo.xwc.components.HTMLAttr.STYLE;
import static netgest.bo.xwc.components.HTMLAttr.TYPE;
import static netgest.bo.xwc.components.HTMLAttr.VALUE;
import static netgest.bo.xwc.components.HTMLTag.DIV;
import static netgest.bo.xwc.components.HTMLTag.INPUT;

import java.io.IOException;
import java.util.Map;

import netgest.bo.xwc.components.classic.AttributeDate;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.JQueryWidget;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.WidgetFactory;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.WidgetFactory.JQuery;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIComponentBase.StateChanged;

public class AttributeDateRenderer extends JQueryBaseRenderer {
	
	@Override
    public void encodeBegin(XUIComponentBase component) throws IOException {
		XUIResponseWriter w = getResponseWriter();
		AttributeDate  dateComponent = (AttributeDate) component;
		
		if (!dateComponent.isRenderedOnClient()){
			createInitialMarkup( w, dateComponent );
			addComponentScript( component );
		}
		
		if (dateComponent.wasStateChanged2() == StateChanged.FOR_RENDER)
			addComponentScript( component );
    }

	private void addComponentScript( XUIComponentBase component ) {
		AttributeDate date = (AttributeDate) component;
		JQueryWidget widget = WidgetFactory.createWidget( JQuery.DATE_PICKER );
		widget.selectorById( component.getId() ).createAndStartOptions();
		widget.addOption( "showOn", "both" );
		//widget.addOption( "buttonImage", "images/calendar.gif" );
		widget.endOptions();
		widget.setInputValue( date.getDisplayValue() );
		
		String s = widget.build();
		
		
		addScriptFooter( component.getClientId(), s );
	}

	private void createInitialMarkup( XUIResponseWriter w, AttributeDate dateComponent )
			throws IOException {
		w.startElement( DIV );
			w.writeAttribute( ID, dateComponent.getClientId() );
		
			w.startElement( INPUT );
				w.writeAttribute( NAME, dateComponent.getClientId() );
				w.writeAttribute( ID, dateComponent.getId() );
				w.writeAttribute( TYPE, "text" );
				w.writeAttribute( VALUE, dateComponent.getDisplayValue() );
				w.writeAttribute( STYLE, "width:90%" );
	
	}
    
    @Override
    public void encodeEnd(XUIComponentBase component ) throws IOException {
    	
    } 
    
    @Override
    public void decode(XUIComponentBase component){
    	AttributeDate oAttrComp = (AttributeDate) component; 
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
