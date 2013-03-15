package netgest.bo.xwc.components.template;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import netgest.bo.xwc.components.template.preprocessor.CommandsPreProcessor;
import netgest.bo.xwc.components.template.util.TemplateMap;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * Generic Template Component to render a template inside a view
 * 
 * Any property that is placed in the XML definition
 * will be available in the properties map
 * 
 */
public class Template extends XUIComponentBase {

	private TemplateMap<String, Object> properties;
	
	/**
	 * Whether or not to reRender the Template component
	 */
	private XUIStateBindProperty< Boolean > reRender = 
			new XUIStateBindProperty< Boolean >( "reRender" , this , "false"	 , Boolean.class );
	
	public void setReRender(String reRenderExpr){
		reRender.setExpressionText( reRenderExpr );
	}
	
	/**
	 * 
	 * Whether or not to re-render the template (defaults to false but can be overriden)
	 * 
	 * @return 
	 */
	public Boolean getReRender(){
		return reRender.getEvaluatedValue( );
	}
	
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
			this.properties = new TemplateMap< String , Object >( getELContext() );

		Iterator<String> it = properties.keySet().iterator();
		while ( it.hasNext() ) {
			String propName = it.next();
			String value = properties.get( propName );

			ValueExpression expression = createValueExpression( value );
			this.properties.put( propName, expression );
		}
	}

	protected ValueExpression createValueExpression(String value) {
		FacesContext context = FacesContext.getCurrentInstance();
		ExpressionFactory oExFactory = context.getApplication().getExpressionFactory();
		ValueExpression expression = oExFactory.createValueExpression( getELContext(), value, Object.class );
		return expression;
	}

	public Map<String, Object> getProperties() {
		return this.properties;
	}

	
	@Override
	public void restoreState(Object state) {
		this.properties = new TemplateMap< String , Object >( getELContext() );

		Object[] oState = (Object[])state;
		super.restoreState(oState[0]);
		
		int keysCntr = (Integer)oState[1];
		for( int i=0; i < keysCntr; i++ ) {
			Object[] keyPar = (Object[])oState[ i + 2 ];
			ValueExpression expression = createValueExpression( (String)keyPar[1] );
			this.properties.put( (String)keyPar[0], expression );   
		}
	}

	@Override
	public Object saveState() {
		
		Object[] saveObj = new Object[ this.properties.size() + 3 ];
		saveObj[0] = super.saveState();
		saveObj[1] = this.properties.size();
		Set<String> keyNames = this.properties.keySet();
		
		int iPos = 2;
		for( String keyName : keyNames ) {
			saveObj[ iPos ] = new Object[] { keyName, this.properties.getExpressionText( keyName ) };
			iPos++;
		}
		return saveObj;
	}
	
	
	
}
