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
import java.util.Map;

import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.context.FacesContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.classic.AttributeAutoComplete;
import netgest.bo.xwc.components.classic.AttributeAutoComplete.SearchType;
import netgest.bo.xwc.components.classic.Layouts;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.JQueryBuilder;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.JQueryWidget;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.WidgetFactory;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.WidgetFactory.JQuery;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;
import netgest.bo.xwc.components.util.ComponentRenderUtils;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIForm;
import netgest.utils.StringUtils;

import org.json.JSONArray;


public class AttributeAutoCompleteRenderer extends JQueryBaseRenderer implements XUIRendererServlet {

	private static final int NO_ITEMS_ALLOWED = 0;

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
		
		setupIncludes( component );
		createMarkup( component, w );
		Layouts.registerComponent( 
				getRequestContext().getScriptContext(), 
				component.getClientId(), 
				getRequestContext().getViewRoot().getViewId(), 
		"form" );
		
		if (shouldUpdate( component )){
			
			widget
			.componentSelectorById( component.getId() + "_complete" )
			.createAndStartOptions()
			.addOption( "json_url", 
					ComponentRenderUtils.getServletURL( getContext(), component.getClientId() ) )
					.addOption( "addontab" , true )
					.addOption( "formId" , component.findParentComponent( XUIForm.class ).getClientId() )
					.addOption( "componentId" , component.getOpenCommand().getClientId() )
					.addOption( "enableCardIdLink" , component.getEnableCardIdLink() )
					.addOption( "height" , 5)
					.addOption( "width", "100%" );
					if (component.isUsable())
						widget.addOption("maxitems", component.getMaxItems());
					else
						widget.addOption("maxitems", NO_ITEMS_ALLOWED);
					
			
					if (component.getSearchType() == SearchType.WORD){
						widget.addOption("searchType", "word");
					} else if (component.getSearchType() == SearchType.CHARACTER){
						widget.addOption("searchType", "character");
					}
					
					String template = component.getTemplateForSearch();
					if (StringUtils.hasValue( template )){
						widget.addOption("template", template);
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
			status.selectorById( component.getId() + "_complete" );
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
				setExistingValues( component );
			}
			
			Layouts.doLayout( w );
			
		}
	}



	private void setExistingValues( AttributeAutoComplete component ) {
		AttributeHandler handler = ((XEOObjectAttributeConnector) component.getDataFieldConnector()).getAttributeHandler();
		AutoCompleteBuilder builder = new AutoCompleteBuilder();
		//Object selectedObjects =  component.getValue() ;
		try {
			String selectedObjects =  handler.getValueString() ;
			if ( StringUtils.hasValue( selectedObjects) ){
				String[] values = selectedObjects.toString().split( "," );
				String append = "";
				StringBuilder result = new StringBuilder("[");
				for (String object : values){
						boObject loadedObject = boObject.getBoManager().loadObject( handler.getEboContext() , Long.valueOf( object ));
						String displayValue = loadedObject.getTextCARDID().toString();
						result.append(append);
						result.append("{")
							.append("'title':").append("'").append(displayValue).append("'").append(",")
							.append("'value':").append("'").append(object).append("'")
							
							.append("}");
						append = ",";
				}
				result.append("]");
				builder.componentSelectorById( component.getId() + "_complete" ).openTrigger().addItem( result.toString() ).endTrigger();
				addScriptFooter( component.getId() + "_update", builder.build() );
			}
		} catch ( boRuntimeException e ) {
			e.printStackTrace();
		}
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
		            		w.writeAttribute( ID, component.getId() + "_complete", null);
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
			
			
			w.startElement( SELECT );
				w.writeAttribute( NAME, component.getId() + "_input" );
				w.writeAttribute( STYLE, "display:none" );
				w.writeAttribute( ID, component.getId() );
			w.endElement( SELECT );
			
		w.endElement( DIV );
		
		
		
	}

	private void setupIncludes( XUIComponentBase component ) {
		if (!component.isRenderedOnClient()){
			
			includeHeaderScript( "autoComplete_js", 
					"ext-xeo/autocomplete/jquery.fcbkcomplete.js" );
			includeHeaderCss( "autoComplete_css", 
			"ext-xeo/autocomplete/style.css" );
		}
	}
	
	@Override
	public void encodeEnd(XUIComponentBase component) throws IOException { }
	
	
	
	public JSONArray retrieveFilteredListValue( AttributeAutoComplete component,  String beanId, String method,  String filter ){
		final Class<?>[] QUERY_ARGUMENTS = { String.class, String.class, String.class };
		
		String query = component.getLookupQuery();
		
		JSONArray result = new JSONArray();
		try {
			String sql = ""; 
        	FacesContext context = FacesContext.getCurrentInstance();
            ExpressionFactory oExFactory = context.getApplication().getExpressionFactory();
            MethodExpression m = oExFactory.createMethodExpression( 
            		component.getELContext(), 
            		"#{" + beanId + "." + method + "}", 
            		String.class,
            		QUERY_ARGUMENTS                    		
            		
            );
    		sql = ( String ) m.invoke(
    				component.getELContext() , 
    					new Object[] {
    						query,
    						filter,
    						component.getTemplateForSearch()
    					} 
    			);
    		return new JSONArray( sql );
		}
		catch ( Exception e ) {
    		e.printStackTrace();
		}
		return result;
	}

	@Override
	public void service( ServletRequest oRequest, ServletResponse oResponse, XUIComponentBase oComp )
			throws IOException {
		
		AttributeAutoComplete auto = (AttributeAutoComplete) oComp;
		
		//The FBCK script sends a tag attribute in the request
		String filter = oRequest.getParameter( "tag" );
		String result = retrieveFilteredListValue( auto, auto.getBeanId(), auto.getLookupMethod(),  filter ).toString();
		System.out.println( result );
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
		String value = parameters.get( component.getId() + "_input[]" );
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
