package netgest.bo.xwc.components.template.base;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class TemplateComponentBase extends XUIComponentBase {
	
	/**
	 * Content for the template (specified in a bean)
	 */
	private XUIBindProperty<String> templateContent =
			new XUIBindProperty<String>( "templateContent", this, String.class );
	
	/**
	 * 
	 * Sets the template for the component
	 * 
	 * @param templateNameExpr The template name or expression
	 */
	public void setTemplateForSearch(String templateNameExpr){
		this.template.setExpressionText( templateNameExpr );
	}
	
	/**
	 * 
	 * Retrieves the template name
	 * 
	 * @return The name of the template as a String
	 */
	public String getTemplateForSearch(){
		return template.getEvaluatedValue();
	}
	
	
	/**
	 * 
	 * Retrieve the content of an inline template
	 * 
	 * @return The template content
	 */
	public String getTemplateContent(){
		return templateContent.getEvaluatedValue();
	}

	public void setTemplateContent(String contentExpr){
		this.templateContent.setExpressionText( contentExpr );
	}
	private String textContent;
	
	/**
	 * Dynamic properties of the component 
	 */
	private Map<String, Object> properties;

	public void setProperties( Map<String, String> properties ) {
		if ( this.properties == null )
			this.properties = new LinkedHashMap<String, Object>();

		Iterator<String> it = properties.keySet().iterator();
		while (it.hasNext()){
			String propName = it.next();
			String value = properties.get( propName );
			
			FacesContext context = FacesContext.getCurrentInstance();
            ExpressionFactory oExFactory = context.getApplication().getExpressionFactory();
            ValueExpression expression = oExFactory.createValueExpression(getELContext(),  value, String.class );
            if (expression.isLiteralText())
				this.properties.put( propName, value );
			else{
				try{
					this.properties.put( propName, expression.getValue( getELContext() ) );
				} catch (Exception e){
					throw new RuntimeException( "Could not evaluate the expression " +  value + " " + e.getMessage() );
				}
			}
		}
		
		
		
	}

	public Map<String, Object> getProperties() {
		if (this.properties == null){
			this.properties = new LinkedHashMap<String, Object>();
		}
		return this.properties;
	}
	

	public void setTextContent( String textContext ) {
		this.textContent = textContext;
	}

	public String getTextContent() {
		return this.textContent;
	}
	

	@Override
	public boolean getRendersChildren() {
		//Overridden so that the component renders its children 
		return true;

	}

	@Override
	public void encodeChildren() throws IOException {
		//Overridden from the base component, so that it does nothing also (when called from
		//regular situations
	}

	/** 
	 * Encodes the children of component when inside a template
	 */
	public void templateEncodeChildren() throws IOException {
		super.encodeChildren();
	}

	@Override
	@Deprecated
	public void encodeChildren( FacesContext context ) throws IOException {
		//Overridden to do nothing on purpose, if we don't do this, the encodeChildren method
		//is always called when the template is processed and when the component is processed 
	}

}
