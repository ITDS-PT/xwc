package netgest.bo.xwc.components.html;

import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;

import netgest.utils.StringUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GenericTag extends XUIComponentBase {

	private String 						textContent;
	private Map<String,String>	 		properties; 
	private XUIBaseProperty<Boolean> 	rawContent = new XUIBaseProperty<Boolean>( "rawContent",  this, false );
	
	public boolean isRawContent() {
		return this.rawContent.getValue();
	}
	public void setRawContent(boolean rawContent) {
		this.rawContent.setValue( rawContent );
	}
	
	public void setProperties( Map<String, String> properties ) {
		if( this.properties == null )
			this.properties = new LinkedHashMap<String, String>();
		this.properties.putAll( properties );
	}

        public Map<String, String> getProperties() {
                return this.properties;
        }        
	
	public void setTextContent( String textContext ) {
		this.textContent = textContext;
	}
        
        public String getTextContent() {
                return this.textContent;
        }

	@Override
	public void restoreState(Object state) {
		this.properties = new LinkedHashMap<String, String>();

		Object[] oState = (Object[])state;
		super.restoreState(oState[0]);
		
		this.textContent = (String)oState[1];

		int keysCntr = (Integer)oState[2];
		for( int i=0; i < keysCntr; i++ ) {
			Object[] keyPar = (Object[])oState[ i + 3 ];
			this.properties.put( (String)keyPar[0], (String)keyPar[1] );   
		}
	}

	@Override
	public Object saveState() {
		
		Object[] saveObj = new Object[ this.properties.size() + 3 ];
		saveObj[0] = super.saveState();
		saveObj[1] = this.textContent;
		saveObj[2] = this.properties.size();
		Set<String> keyNames = this.properties.keySet();
		
		int iPos = 3;
		for( String keyName : keyNames ) {
			saveObj[ iPos ] = new Object[] { keyName, this.properties.get( keyName ) };
			iPos++;
		}
		return saveObj;
	}
	
	public String serialize(){
		StringBuilder b = new StringBuilder(200);
		String tagName = properties.get("__tagName");
		b.append( "<" + tagName );
		if( tagName != null ) {
			Set<String> props = properties.keySet();
			for( String propName : props ) {
				if( !propName.equals( "__tagName" ) && !propName.equals( "beanId" ) ) {
					b.append( " " + propName+"='"+properties.get( propName )+"' " );
				}
			}
		}
		b.append(" >");
		if (StringUtils.hasValue( textContent) )
			b.append(textContent);
		
		b.append( "</" + tagName + ">");
		
		return b.toString();
	}
	

	public static class XEOHTMLRenderer extends XUIRenderer {

		@Override
		public void encodeBegin(XUIComponentBase component) throws IOException {
			super.encodeBegin(component);
			
			XUIResponseWriter w = getResponseWriter();
			GenericTag t = (GenericTag)component;
			
			if( t.isRawContent() ) {
				if( t.textContent != null ) {
					w.write( t.textContent );
				}
			}
			else {
				String			  tagName;
				tagName = t.properties.get("__tagName");
				if( tagName != null ) {
					w.startElement( tagName, component );
					Set<String> props = t.properties.keySet();
					for( String propName : props ) {
						if( !propName.equals( "__tagName" ) && !propName.equals( "beanId" ) ) {
							w.writeAttribute( propName , t.properties.get( propName ), null );
						}
					}
				}
				if( t.textContent != null ) {
					w.writeText( t.textContent, null );
				}
			}

		}

		@Override
		public void encodeEnd(XUIComponentBase component) throws IOException {
			GenericTag t = (GenericTag)component;

			String			  tagName;
			tagName = t.properties.get("__tagName");

			XUIResponseWriter w = getResponseWriter();
			if( tagName != null ) {
				w.endElement( t.properties.get("__tagName"));
			}

			super.encodeEnd(component);
			
		}
		
		@Override
		public StateChanged wasStateChanged(XUIComponentBase component,
				List< XUIBaseProperty< ? >> updateProperties) {
			return super.wasStateChanged( component , updateProperties );
		}
		
	}
	
	@Override
	public String toString() {
		return getProperties( ).get("__tagName");
	}
}
