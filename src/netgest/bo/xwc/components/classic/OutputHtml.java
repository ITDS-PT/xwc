package netgest.bo.xwc.components.classic;

import java.io.IOException;

import javax.el.ValueExpression;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.annotations.Required;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * 
 * The {@link OutputHtml} components allow to directly output an HTML string
 * from a Bean property to the Viewer
 * 
 * @author jcarreira
 *
 */
public class OutputHtml extends ViewerOutputSecurityBase {

    /**
     * The HTML content to show
     */
	@Required
    private XUIStateProperty<String> valueExpression = new XUIStateProperty<String>( "valueExpression", this );
    /**
     * The value that was rendered in the viewer
     */
    private XUIStateProperty<Object> renderedValue  = new XUIStateProperty<Object>( "renderedValue", this );
    /**
     * If the content of the component is visible or not
     */
    private XUIViewStateBindProperty<Boolean> visible  = new XUIViewStateBindProperty<Boolean>( "visible", this, "true",Boolean.class );

    @Override
    public StateChanged wasStateChanged2() {
        if( super.wasStateChanged2() == StateChanged.NONE ) {
            if (!XUIStateProperty.compareValues( this.renderedValue.getValue(), getValue() )) {
                return StateChanged.FOR_RENDER;
            }
        }
        else {
            return StateChanged.FOR_RENDER;
        }
        return StateChanged.NONE;    
    }
    
    @Override
    public Object saveState() {
        this.renderedValue.setValue( getValue() );
        return super.saveState();
    }
    
    /**
     * Sets the visible.
     * 
     * @param sExpression the new EL Expression for visible
     */
    public void setVisible( String sExpression ) {
    	this.visible.setExpressionText( sExpression );
    }

    /**
     * Sets the visible.
     * 
     * @param bVisible the new visible
     */
    public void setVisible( boolean bVisible ) {
    	this.visible.setExpressionText( Boolean.toString( bVisible ) );
    }
    
    /**
     * Gets the visible.
     * 
     * @return the visible
     */
    public boolean getVisible() {
    	return this.visible.getEvaluatedValue();
    }
    
    /**
     * The value to be displayed
     * @param valueExpression {@link ValueExpression} or a literal String with the output html
     */
    public void setValueExpression(String valueExpression) {
        this.valueExpression.setValue( valueExpression ); 
        super.setValueExpression( "value", createValueExpression( valueExpression, String.class ) );
    }
    
    /**
     * The current expression string or literal representing the value to ouput
     * @return String
     */
    public String getValueExpression() {
        return valueExpression.getValue();
    }
    
    /**
     * The evaluated value of the valueExpression property.
     * @return Object with the current Value  
     */
    @Override
    public Object getValue() {
        return super.getValue();
    }

    @Override
    public boolean isRendered() {
    	if ( !getEffectivePermission(SecurityPermissions.READ) ) {
    		return false;
    	}
    	return super.isRendered();
    }
    
    public static class XEOHTMLRenderer extends XUIRenderer {

        @Override
		public void encodeBegin(XUIComponentBase component) throws IOException {
            OutputHtml oOut = (OutputHtml)component;
            
            XUIResponseWriter w = getResponseWriter();
            w.startElement( HTMLTag.SPAN, component );
            w.writeAttribute( HTMLAttr.ID , component.getClientId(), null );
            if ( oOut.getEffectivePermission(SecurityPermissions.READ) && oOut.getVisible() ) {
            	w.write( (String)((OutputHtml)component).getValue() );
            } else {
            	w.write( "" );            	
            }
        }

		@Override
        public void encodeEnd(XUIComponentBase component) throws IOException {
            XUIResponseWriter w = getResponseWriter();
            w.endElement( "SPAN" );
        }
    }

}
