package netgest.bo.xwc.components.classic.renderers.jquery;

import static netgest.bo.xwc.components.HTMLAttr.CELLPADDING;
import static netgest.bo.xwc.components.HTMLAttr.CELLSPACING;
import static netgest.bo.xwc.components.HTMLAttr.CLASS;
import static netgest.bo.xwc.components.HTMLAttr.HREF;
import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLAttr.NAME;
import static netgest.bo.xwc.components.HTMLAttr.ONCLICK;
import static netgest.bo.xwc.components.HTMLAttr.STYLE;
import static netgest.bo.xwc.components.HTMLTag.A;
import static netgest.bo.xwc.components.HTMLTag.COL;
import static netgest.bo.xwc.components.HTMLTag.COLGROUP;
import static netgest.bo.xwc.components.HTMLTag.DIV;
import static netgest.bo.xwc.components.HTMLTag.SELECT;
import static netgest.bo.xwc.components.HTMLTag.SPAN;
import static netgest.bo.xwc.components.HTMLTag.TABLE;
import static netgest.bo.xwc.components.HTMLTag.TD;
import static netgest.bo.xwc.components.HTMLTag.TR;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.classic.AttributeAutoComplete;
import netgest.bo.xwc.components.classic.AttributeBase;
import netgest.bo.xwc.components.classic.AttributeAutoComplete.SearchType;
import netgest.bo.xwc.components.classic.Layouts;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.JQueryBuilder;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.JQueryWidget;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.WidgetFactory;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.WidgetFactory.JQuery;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;
import netgest.bo.xwc.components.util.ComponentRenderUtils;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIELContextWrapper;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIForm;
import netgest.bo.xwc.framework.components.XUIComponentBase.StateChanged;
import netgest.utils.StringUtils;


public class AttributeAutoCompleteRenderer extends JQueryBaseRenderer implements XUIRendererServlet {

	private static final int NO_ITEMS_ALLOWED = 0;

	@Override
	public StateChanged wasStateChanged(XUIComponentBase component,
			List< XUIBaseProperty< ? >> updateProperties) {
		XUIBaseProperty<?> readOnly = component.getStateProperty( "readOnly" );
	 	if (readOnly != null){
	 			if (readOnly.wasChanged())
	 				return StateChanged.FOR_RENDER; 
	 	}
	 
	 	AttributeBase base = ( AttributeBase ) component;
        if( super.wasStateChanged(component, updateProperties) == StateChanged.NONE ) {
        	Object value;
        	ValueExpression ve = component.getValueExpression("value");
        	if (ve != null) {
        	    try {
        	    	XUIELContextWrapper context = new XUIELContextWrapper( getFacesContext().getELContext() , component );
        			value = (ve.getValue(context));
        		}
    		    catch (ELException e) {
        			throw new FacesException(e);
    		    }
        	}
        	else {
        		value = base.getValue();
        	}
        	
            if (!XUIStateProperty.compareValues( base.getRenderedValue(), value )) {
                return StateChanged.FOR_UPDATE;
            }
        }
        else {
            return StateChanged.FOR_UPDATE;
        }
        return StateChanged.NONE;
	}
	
	@Override
	public void encodeBegin(XUIComponentBase component) throws IOException {
		encodeBegin( (AttributeAutoComplete) component, 
				getResponseWriter(), 
				WidgetFactory.createWidget( JQuery.AUTO_COMPLETE ) );
		
	}
	
	
	
	/**
	 * 
	 * Renders the component
	 * 
	 * @param component
	 * @param w
	 * @param widget
	 * 
	 * @throws IOException
	 */
	public void encodeBegin(AttributeAutoComplete component, XUIResponseWriter w, JQueryWidget widget) throws IOException {
		
		createMarkup( component, w );
		Layouts.registerComponent( 
				getRequestContext().getScriptContext(), 
				component.getClientId(), 
				getRequestContext().getViewRoot().getViewId(), 
		"form" );
		
		if (shouldUpdate( component )){
			
			widget
			.componentSelectorById( getComponentId( component ) )
			.createAndStartOptions()
			.addOption( "json_url", 
					ComponentRenderUtils.getServletURL( getContext(), component.getClientId() ) )
					.addOption( "addontab" , true )
					.addOption( "formId" , component.findParentComponent( XUIForm.class ).getClientId() )
					.addOption( "componentId" , component.getOpenCommand().getClientId() )
					.addOption( "enableCardIdLink" , component.getEnableCardIdLink() )
					.addOption( "input_name" , component.getClientId( ) +"_input" )
					.addOption( "input_min_size" , component.getMinSearchChars( ) )
					.addOption( "initialTextClass" , component.getInitialTextClass( ) )
					.addOption( "resultTextClass" , component.getResultTextClass( ) )
					.addOption( "selectedElementClass" , component.getSelectedElementClass( ) )
					.addOption( "delay" , component.getSearchDelay( ) )
					.addOption( "height" , 5 )
					.addOption( "width", "100%" )
					.addOption( "firstselected", true );
			
			
					if (component.isUsable())
						widget.addOption("maxitems", component.getMaxItems());
					else
						widget.addOption("maxitems", NO_ITEMS_ALLOWED);
					
			
					if (component.getSearchType() == SearchType.WORD){
						widget.addOption("searchType", "word");
					} else if (component.getSearchType() == SearchType.CHARACTER){
						widget.addOption("searchType", "character");
					}
					
					String typeText = component.getTypeMessage();
					if (StringUtils.hasValue( typeText ))
						widget.addOption( "complete_text", typeText);
			
					widget.endOptions();
			
			addScriptFooter( component.getClientId() + "_a", widget.build() );
			
			JQueryBuilder updater = new JQueryBuilder();
			updater.selectorById( component.getId() + "_tbl" );
			if (component.isVisible())
				updater.show();
			else
				updater.hide();
			
			JQueryBuilder status = new JQueryBuilder();
			status.selectorById( getComponentId( component ) );
			if (component.isUsable())
				status.command( "trigger(\"enable\")" );
			else
				status.command( "trigger(\"disable\")" );
			
			
			addScriptFooter( component.getClientId() + "_visibility" , updater.build() );
			addScriptFooter( component.getClientId() + "_status" , status.build() );
			
			
			
			JQueryBuilder updaterUsage = new JQueryBuilder();
			updaterUsage.selectorById( component.getId() + "_span" );
			if (component.isUsable()){
				updaterUsage.removeClass( "x-item-disabled" );
				updaterUsage.removeClass( "search-lookup-trigger-disabled-hover" );
				updaterUsage.addClass( "search-lookup-trigger" );
			}
			else{
				updaterUsage.removeClass( "search-lookup-trigger" );
				updaterUsage.addClass( "x-item-disabled" );
				updaterUsage.addClass( "search-lookup-trigger-disabled-hover" );
			}
			addScriptFooter( component.getClientId() + "_disabled", updaterUsage.build() );
			
			if (component.isXEOEnabled()){
				setExistingValuesXEOConnector( component );
			} else {
				setExistingValues( component );
			}
			
			Layouts.doLayout( w );
			
		}
	}


	

	private void setExistingValues(AttributeAutoComplete component) {
		AutoCompleteBuilder builder = new AutoCompleteBuilder();
		Object sourceValue = component.getValue();
		if (sourceValue == null)
			return;
		
		String value = String.valueOf( sourceValue );
		String[] values = value.split( "," ); 
		
		String displayValue = component.getDisplayValue();
		if (StringUtils.isEmpty( displayValue ))
			displayValue = value;
		String[] displayValues = displayValue.split( "," );
		
		int k = 0;
		JSONArray[] itemsToAdd = new JSONArray[values.length];
		for (String object : values){
				JSONArray itemHolder = new JSONArray();
				JSONObject newItem = new JSONObject();
				String currDisplay = displayValues[k];
				try {
					newItem.put( "title" , currDisplay );
					newItem.put( "value" , object );
				} catch ( JSONException e ) {
					e.printStackTrace();
				}
				itemHolder.put( newItem );
				itemsToAdd[k] = itemHolder;
				k++;
		}
		
		builder.componentSelectorById( getComponentId( component ) );
		for (JSONArray current : itemsToAdd){
			builder.openTrigger().addItem( current.toString() ).endTrigger();
		}
		addScriptFooter( component.getId() + "_updateVals", builder.build() );
	}



	private void setExistingValuesXEOConnector( AttributeAutoComplete component ) {
		AttributeHandler handler = ((XEOObjectAttributeConnector) component.getDataFieldConnector()).getAttributeHandler();
		AutoCompleteBuilder builder = new AutoCompleteBuilder();
		//Object selectedObjects =  component.getValue() ;
		try {
			String selectedObjects =  handler.getValueString() ;
			if ( StringUtils.hasValue( selectedObjects) ){
				String[] values = selectedObjects.toString().split( "," );
				
				JSONArray[] itemsToAdd = new JSONArray[values.length];
				int k = 0;
				for (String object : values){
						JSONArray itemHolder = new JSONArray();
						JSONObject newItem = new JSONObject();
						boObject loadedObject = boObject.getBoManager().loadObject( handler.getEboContext() , Long.valueOf( object ));
						String currDisplay = loadedObject.getTextCARDID().toString();
						try {
							newItem.put( "title" , currDisplay );
							newItem.put( "value" , object );
						} catch ( JSONException e ) {
							e.printStackTrace();
						}
						itemHolder.put( newItem );
						itemsToAdd[k] = itemHolder;
						k++;
				}
				
				builder.componentSelectorById( getComponentId( component ) );
				for (JSONArray current : itemsToAdd){
					builder.openTrigger().addItem( current.toString() ).endTrigger();
				}
				addScriptFooter( component.getId() + "_updateVals", builder.build() );
				
				
				
			}
		} catch ( boRuntimeException e ) {
			e.printStackTrace();
		}
	}
	
	public String getComponentId(AttributeAutoComplete comp){
		return comp.getClientId() + "_input";
	}

	public void createMarkup( AttributeAutoComplete component, XUIResponseWriter w ) throws IOException {
		w.startElement( DIV );
			w.writeAttribute( ID, component.getClientId() );
			
			String tableStyle = "table-layout:fixed;height:21px;margin:0px 0xp 0px 0px; padding:0px 0px 0px 0px;width:100%";
			
			w.startElement( TABLE );
	        	w.writeAttribute( CELLPADDING, "0", null );
	        	w.writeAttribute( CELLSPACING, "0", null );
	        	w.writeAttribute( STYLE, tableStyle, null );
				w.writeAttribute( ID, component.getId() + "_tbl" );
				
				
				w.startElement( COLGROUP);
	            
	            w.startElement( COL);
	            w.writeAttribute( STYLE, "width:100%", null );
	            
	            w.endElement(COL);
	            
	            w.startElement( COL );
	        	String width = "17px;";
	        	w.writeAttribute( STYLE, "vertical-align:top;width:" + width);
	            w.endElement(COL);
	            
	            w.endElement( COLGROUP );
	            
				
				
				w.startElement( TR );
					w.writeAttribute( ID, component.getId() + "_row" );
					w.startElement( TD );
						w.writeAttribute( ID, component.getId() + "_column" );
						w.writeAttribute( STYLE, "width:100%" );
						w.startElement( DIV);
		            		w.writeAttribute( ID, component.getClientId() + "_div");
		            		w.writeAttribute( CLASS, "xwc-attribute-autoComplete");
		            		w.startElement( SELECT );
			    				w.writeAttribute( NAME, component.getClientId( ) + "_input" );
			    				w.writeAttribute( STYLE, "display:none" );
			    				w.writeAttribute( ID, component.getClientId( ) + "_input" );
		    			w.endElement( SELECT );
		            	w.endElement( DIV );	
					w.endElement( TD );
					w.startElement( TD );
						w.writeAttribute( ID, component.getId() + "_lookup" );
						w.startElement( DIV);
		            	
	            		w.writeAttribute(ID, component.getClientId() + "_addButton", null);
	            		w.writeAttribute(STYLE, "display:inline;margin:0px 0px 0px 0px;padding:0px 0px 0px 0px;", null);
	            		
	            		w.startElement( SPAN );
	            		w.writeAttribute(ID, component.getId()+"_span", null);
	            		w.writeAttribute(CLASS, "search-lookup-trigger", null);
	            		
	            			w.startElement(A, null);
		            			w.writeAttribute(ID, component.getId()+"_add", null);
				    			w.writeAttribute(HREF, "javascript:void(0)", null);
				    			w.writeAttribute(CLASS, "search-lookup-trigger", null);
				    			//if (component.isUsable()) //FIXME: Tenho dúvidas se isto funcionará se começar disabled e depois for "enabled"
			    				w.writeAttribute( ONCLICK, XVWScripts.getAjaxCommandScript( component.getLookupCommand(),XVWScripts.WAIT_DIALOG ) );
			    			w.endElement(A);
			    			
			    			w.endElement(SPAN);		
			    			
	        	    w.endElement(DIV);
					w.endElement( TD );
				w.endElement( TR );
			w.endElement( TABLE );
			
			
			
			
		w.endElement( DIV );
		
		
		
	}

	
	
	@Override
	public void encodeEnd(XUIComponentBase component) throws IOException { }
	

	@Override
	public void service( ServletRequest oRequest, ServletResponse oResponse, XUIComponentBase oComp )
			throws IOException {
		
		AttributeAutoComplete auto = (AttributeAutoComplete) oComp;
		
		//The FBCK script sends a tag attribute in the request
		String filter = oRequest.getParameter( "tag" );
		String result = auto.getLookupResults ( filter );
		OutputStream os = oResponse.getOutputStream();
		os.write( result.getBytes() );
		
	}
	
	
	@Override
    public void decode(XUIComponentBase component) {

        AttributeAutoComplete oAttrComp = (AttributeAutoComplete)component;
        Map<String,String> parameters = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        decodeValue( oAttrComp , parameters);
    }
	
	public void decodeValue(AttributeAutoComplete component, Map<String,String> parameters){
		String value = parameters.get( component.getClientId() + "_input[]" );
		if( "NaN".equals( value ) ) {
			component.setSubmittedValue( "" );
		}
		else {
			component.setSubmittedValue( value );
		}
		
		super.decode(component);
		
	}
	
	private static class AutoCompleteBuilder extends JQueryBuilder{
		
		public AutoCompleteBuilder openTrigger(){
			b.append( ".trigger(" );
			return this;
		}
		
		public AutoCompleteBuilder addItem(String item){
			b.append( "\"addItem\"," + item );
			return this;
		}
		
		public AutoCompleteBuilder endTrigger(){
			b.append( ")" );
			return this;
		}
		
		public AutoCompleteBuilder componentSelectorById( String clientId ){
			super.componentSelectorById( clientId );
			return this;
		}
		
		
		
		
		
	}
	
}
