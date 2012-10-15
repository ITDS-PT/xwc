package netgest.bo.xwc.components.template;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * Generic Template Component to render a template inside a view
 * 
 * Any property that is placed in the XML definition
 * will be available in the properties map
 * 
 */
public class Template extends XUIComponentBase {

	private Map<String, Object> properties;

	public void setProperties( Map<String, String> properties ) {
		if ( this.properties == null )
			this.properties = new LinkedHashMap<String, Object>();

		Iterator<String> it = properties.keySet().iterator();
		while ( it.hasNext() ) {
			String propName = it.next();
			String value = properties.get( propName );

			FacesContext context = FacesContext.getCurrentInstance();
			ExpressionFactory oExFactory = context.getApplication().getExpressionFactory();
			ValueExpression expression = oExFactory.createValueExpression( getELContext(), value, String.class );
			if ( expression.isLiteralText() )
				this.properties.put( propName, value );
			else {
				try {
					this.properties.put( propName, expression.getValue( getELContext() ) );
				} catch ( Exception e ) {
					e.printStackTrace();
					throw new RuntimeException( "Could not evaluate the expression " + value + " " + e.getMessage() );
				}
			}
		}
	}

	public Map<String, Object> getProperties() {
		return this.properties;
	}

}
