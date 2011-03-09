package netgest.bo.xwc.components.classic.renderers;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.el.ValueExpression;
import javax.faces.component.ValueHolder;

import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.XUIViewBindProperty;
import netgest.bo.xwc.framework.XUIViewProperty;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.XUIViewStateProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

import org.w3c.tidy.Tidy;

public class XMLHtmlEditRenderer extends XUIRenderer{

	/* (non-Javadoc)
	 * @see netgest.bo.xwc.framework.XUIRenderer#encodeBegin(netgest.bo.xwc.framework.components.XUIComponentBase)
	 */
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
			
			value = ((ValueHolder)component).getValue();
			if( value != null ) {
				Tidy tidy = new Tidy();
				StringWriter w = new StringWriter();
				String valueString = value.toString();
				StringReader reader = new StringReader(valueString);
				tidy.setPrintBodyOnly(true);
				tidy.parseDOM(reader,w);
				rw.write(w.getBuffer().toString());
			}
		}
		
		
	}

	/* (non-Javadoc)
	 * @see netgest.bo.xwc.framework.XUIRenderer#encodeEnd(netgest.bo.xwc.framework.components.XUIComponentBase)
	 */
	@Override
	public void encodeEnd(XUIComponentBase component) throws IOException {
		XUIResponseWriter rw = getResponseWriter();
		rw.endElement( component.getRendererType() );
	}
	
}
