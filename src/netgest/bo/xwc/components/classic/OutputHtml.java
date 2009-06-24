package netgest.bo.xwc.components.classic;

import java.io.IOException;

import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class OutputHtml extends ViewerOutputSecurityBase {

    private XUIStateProperty<String> valueExpression = new XUIStateProperty<String>( "valueExpression", this );
    private XUIStateProperty<Object> renderedValue  = new XUIStateProperty<Object>( "renderedValue", this );

    @Override
    public boolean wasStateChanged() {
        if( !super.wasStateChanged() ) {
            if (!XUIStateProperty.compareValues( this.renderedValue.getValue(), getValue() )) {
                return true;
            }
        }
        else {
            return true;
        }
        return false;    
    }

    @Override
    public Object saveState() {
        this.renderedValue.setValue( getValue() );
        return super.saveState();
    }

    public void setValueExpression(String valueExpression) {
        this.valueExpression.setValue( valueExpression ); 
        super.setValueExpression( "value", createValueExpression( valueExpression, String.class ) );
    }

    public String getValueExpression() {
        return valueExpression.getValue();
    }

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
            w.startElement( "SPAN", component );
            
            if ( oOut.getEffectivePermission(SecurityPermissions.READ) ) {
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
