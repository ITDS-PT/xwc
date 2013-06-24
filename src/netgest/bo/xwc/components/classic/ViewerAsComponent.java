package netgest.bo.xwc.components.classic;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.el.ValueExpression;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.annotations.AcceptsIncludeValue;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;

public class ViewerAsComponent extends XUIComponentBase implements NamingContainer {
	
	/**
	 * Name of the viewer to include
	 */
	protected XUIBindProperty< String > viewerName = new XUIBindProperty< String >(
			"viewerName" , this , String.class );

	public String getViewerName() {
		return viewerName.getEvaluatedValue();
	}

	public void setViewerName(String newValExpr) {
		viewerName.setExpressionText( newValExpr );
	}
	
	protected Map<String,Object>	 		properties;
	
	public void setProperties( Map<String, Object> properties ) {
		if( this.properties == null )
			this.properties = new LinkedHashMap<String, Object>();
		this.properties.putAll( properties );
	}

	public Map<String, Object> getProperties() {
		return this.properties;
	}   

	@Override
	public void restoreState(Object state) {
		this.properties = new LinkedHashMap<String, Object>();

		Object[] oState = (Object[])state;
		super.restoreState(oState[0]);

		int keysCntr = (Integer)oState[1];
		for( int i=0; i < keysCntr; i++ ) {
			Object[] keyPar = (Object[])oState[ i + 2 ];
			this.properties.put( (String)keyPar[0], (String)keyPar[1] );   
		}
	}

	@Override
	public Object saveState() {

		Object[] saveObj = new Object[ this.properties.size() + 2 ];
		saveObj[0] = super.saveState();
		saveObj[1] = this.properties.size();
		Set<String> keyNames = this.properties.keySet();

		int iPos = 2;
		for( String keyName : keyNames ) {
			saveObj[ iPos ] = new Object[] { keyName, this.properties.get( keyName ) };
			iPos++;
		}
		return saveObj;
	}
	
	protected transient XUIViewRoot 			viewRoot;
	
	@Override
	public void initComponent() {

		String viewerName = resolveViewer( getViewerName() );

		viewRoot = getRequestContext().getSessionContext().createChildView( viewerName );
		
		this.getChildren().clear();
		this.getChildren().add( viewRoot );
		
		UIComponent r = this;
        while( r.getParent() != null )
               r = r.getParent();
        
        for( String s : viewRoot.getBeanIds() ) {
        		Object bean = viewRoot.getBean( s );
        		if ( bean instanceof XEOBaseBean ){
        			XEOBaseBean castBean = ( XEOBaseBean ) bean;
        			castBean.setViewRoot( ((XUIViewRoot) r ).getViewState() );
        		}	
    			for (String propName : properties.keySet()){
    				String propSetter = "set" + propName.toLowerCase();
    				Object propValue = properties.get( propName );
    				try {
    					for (Method m : bean.getClass().getMethods()){
    						if (m.isAnnotationPresent( AcceptsIncludeValue.class ) && propertyNameIsSameAsMethodName( propSetter , m ) ){
    							ValueExpression exprt = createValueExpression( propValue.toString() , Object.class );
    							if (exprt.isLiteralText())
    								m.invoke( bean , propValue );
    							else
    								m.invoke( bean , exprt );
    						}
    					}
    				} catch ( Exception e ) {
						e.printStackTrace(); //In production remove printStacktrace
					}
        			
        		}
    			
    			//FIXME: Add an annotation for the onAfterCreateEvent and call the method
    			//annotation by it
        }
        
	}

	protected boolean propertyNameIsSameAsMethodName(String propSetter, Method m) {
		return m.getName().equalsIgnoreCase( propSetter );
	}
	
		
	public String toString() {
		return super.toString();
	}
	
	protected String resolveViewer(String viewerName){
		return viewerName;
	}
	
	public static class XEOHTMLRenderer extends XUIRenderer {
		
		@Override
		public void encodeBegin( XUIComponentBase component ) throws IOException {
			super.encodeBegin(component);
			XUIResponseWriter w = getResponseWriter();
			w.startElement( HTMLTag.DIV , component );
			w.writeAttribute( HTMLAttr.ID , component.getClientId(), "id" );
		}
	
		@Override
		public void encodeEnd( XUIComponentBase component ) throws IOException {
			XUIResponseWriter w = getResponseWriter();
			w.endElement( HTMLTag.DIV );
			super.encodeEnd(component);
		}
	
	}
}
