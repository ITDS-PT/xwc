package netgest.bo.xwc.components.classic.renderers;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.el.ValueExpression;
import javax.faces.component.ValueHolder;

import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.XUIViewBindProperty;
import netgest.bo.xwc.framework.XUIViewProperty;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.XUIViewStateProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * 
 * Renders the xvw:attribute component as XML
 * 
 * @author PedroRio
 *
 */
public class XMLAttributeRenderer extends XMLBasicRenderer {
	@Override
	public void encodeBegin(XUIComponentBase component) throws IOException {
		XUIResponseWriter rw = getResponseWriter();
		rw.startElement( component.getRendererType(), component );
		Map<String,Object>  map = component.getAttributes();
		for( String s : map.keySet() ) {
			rw.writeAttribute( s, String.valueOf( map.get( s ) ), s);
		}
		
		Set<Entry<String, XUIBaseProperty<?>>>  props = component.getStateProperties();

		Object value;

		String name = component.getClientId();
		rw.writeAttribute("id", name, "id");
		
		
		for( Entry<String,XUIBaseProperty<?>> s : props ) {
			
			value = null;
			
			XUIBaseProperty<?> p = s.getValue();
			
			if( p instanceof XUIViewBindProperty<?> ) {
				value = ((XUIBindProperty<?>)p).getValue();
			}
			else if ( p instanceof XUIViewStateBindProperty<?> ) {
				value = ((XUIStateBindProperty<?>)p).getValue();
			}
			else if ( p instanceof XUIViewStateProperty<?> ) {
				value = ((XUIViewStateProperty<?>)p).getValue();
			}
			else if ( p instanceof XUIBaseProperty<?> ) {
				value = ((XUIBaseProperty<?>)p).getValue();
			}
			else if( p instanceof XUIViewProperty<?> ) {
				value = p.getValue();
			}
			
			if( value instanceof ValueExpression ) {
				value = ((ValueExpression)value).getValue( component.getELContext() );
			}
			if( value != null ) {
				rw.writeAttribute( s.getKey(), String.valueOf( value ), s.getKey() );
			}
		}
		
		if( component instanceof ValueHolder ) {
			rw.writeAttribute("name", name, "name");
			
		}
        
	}
	
	@Override
	public void encodeEnd(XUIComponentBase component) throws IOException {
		super.encodeEnd(component);
	}
}
