package netgest.bo.xwc.components.template;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.swing.tree.TreeNode;

import org.apache.fop.fo.RecursiveCharIterator;

import freemarker.core.TemplateElement;
import freemarker.core.TemplateObject;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateDirectiveModel;

import netgest.bo.xwc.components.template.loader.TemplateLoaderFactory;
import netgest.bo.xwc.components.template.preprocessor.CommandsPreProcessor;
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
	
	@Override
	public void initComponent() {
		super.initComponent( );
		if (!this.template.isDefaultValue( )){
			CommandsPreProcessor p = new CommandsPreProcessor( getTemplate(), this );
			List<UIComponent> list = p.createComponents( );
				getChildren( ).addAll( list );
		}
	}
	

	public void setProperties( Map<String, String> properties ) {
		if ( this.properties == null )
			this.properties = new LinkedHashMap<String, Object>();

		Iterator<String> it = properties.keySet().iterator();
		while ( it.hasNext() ) {
			String propName = it.next();
			String value = properties.get( propName );

			FacesContext context = FacesContext.getCurrentInstance();
			ExpressionFactory oExFactory = context.getApplication().getExpressionFactory();
			ValueExpression expression = oExFactory.createValueExpression( getELContext(), value, Object.class );
			if ( expression.isLiteralText() )
				this.properties.put( propName, value );
			else {
				try {
					Object evaluatedValue = expression.getValue( getELContext() );
					this.properties.put( propName,  evaluatedValue );
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
