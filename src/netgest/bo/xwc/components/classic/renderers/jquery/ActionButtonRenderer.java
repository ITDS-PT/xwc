package netgest.bo.xwc.components.classic.renderers.jquery;

import static netgest.bo.xwc.components.HTMLAttr.CLASS;
import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLAttr.STYLE;
import static netgest.bo.xwc.components.HTMLTag.BUTTON;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;

import netgest.bo.xwc.components.classic.ActionButton;
import netgest.bo.xwc.components.classic.ToolBar.IconPosition;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.JQueryBuilder;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.JQueryWidget;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.WidgetFactory;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.WidgetFactory.JQuery;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.utils.StringUtils;

/**
 * 
 * ActionButton renderer, each button as a css class (xwc-actionbutton) that
 * can be customized. ALso hover/active state can also be customized
 * 
 * @author PedroRio
 *
 */
public class ActionButtonRenderer extends JQueryBaseRenderer {

	    @Override
	    public void encodeBegin(XUIComponentBase component) throws IOException {
	        super.encodeBegin(component);
	    }
	    
	    private String getButtonDivId(ActionButton button){
	    	return button.getId() + "_btn";
	    }
	
	    @Override
	    public void encodeEnd(XUIComponentBase component ) throws IOException {
	        ActionButton    oActionButton = (ActionButton)component;
	        XUIResponseWriter  w = getResponseWriter();
	        JQueryWidget widget = WidgetFactory.createWidget( JQuery.BUTTON );
	        
	        encodeEnd( oActionButton, w, widget );
	        
	        addScriptFooter( oActionButton.getId() , widget.build() );
	        // Icons must be added after the command so that certain html
	        //exists when the image is added
	        addButtonIcon( oActionButton );
	        
	    }
	    
	    /**
	     * 
	     * Renders the component with all dependencies injected
	     * 
	     * @param component The component to render
	     * @param writer The response to write in
	     * @param widget The widget to generate the javascript
	     * @throws IOException
	     */
	    public void encodeEnd(ActionButton component, XUIResponseWriter writer, JQueryWidget widget) throws IOException {
	    	
	    	// Place holder for the element and the button markup
	    	if (!component.isRenderedOnClient()){
		    	writer.startElement( DIV );
		    	writer.writeAttribute( ID, component.getClientId(), "id" );
		    	
		    	writer.startElement( BUTTON );
		    		writer.writeAttribute( ID, component.getId() + "_btn" );
		    		writer.writeAttribute( CLASS, "xwc-actionbutton" );
		    		
		    		writer.writeAttribute( STYLE, "min-width:" + component.getWidth() + "px" );
		    	writer.write( component.getLabel() );
		    	writer.endElement( BUTTON );
		    	
		    	writer.endElement( DIV );
	    	}
	    	
	    	widget
	    	.selectorById( getButtonDivId( component ) )
	    	.create().click( XVWScripts.getAjaxCommandScript( component, 1 ) + "; return false;" );
	    	 
	    	
	    	toggleDisabled( component, widget );
	    	toggleVisibility( component, widget );
	    	
	    	
	    	
	    }

		protected void addButtonIcon( ActionButton component ) {
			if (StringUtils.hasValue( component.getImage() ) ){
	    		if (component.getId() != null){
	    			JQueryBuilder builder = new JQueryBuilder();
	    			builder.selectorById( getButtonDivId( component )  ).
	    				command( "children('.ui-button-text')" );
	    	
	    			IconPosition position = IconPosition.fromString( component.getIconPosition() );
	    			
	    			if (position == IconPosition.LEFT)
	    				builder.command( "prepend( '<img src=\""+component.getImage()+"\" style=\"display:inline;padding:2px;vertical-align:middle\" />' )");
	    			else if (position == IconPosition.TOP )
	    				builder.command( "prepend( '<img src=\""+component.getImage()+"\" style=\"display:block;padding:2px;vertical-align:middle;margin-left:auto; margin-right:auto\" />' )");
	    			
	    			addScriptFooter( component.getId() + "_xwc-image", builder.build() );
	    		}
	    	}
		}

		protected void toggleVisibility( ActionButton component, JQueryWidget widget ) {
			if (component.isVisible())
	    		widget.show();
	    	else
	    		widget.hide();
		}

		protected void toggleDisabled( ActionButton component, JQueryWidget widget ) {
			if (component.isDisabled()){
	    		widget.disable();
	    	} else
	    		widget.enable();
		}
	
    @Override 
	public void decode(XUIComponentBase component){
		super.decode( component );
	}

}
