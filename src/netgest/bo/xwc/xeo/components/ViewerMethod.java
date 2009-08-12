package netgest.bo.xwc.xeo.components;

import javax.el.MethodExpression;
import javax.faces.event.ActionEvent;

import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.framework.XUIBindProperty;

public class ViewerMethod extends Menu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private XUIBindProperty<String> 	targetMethod 	= new XUIBindProperty<String>("targetMethod", this, String.class );

	@Override
	public String getRendererType() {
		return "menu";
	}
	
	public void setTargetMethod( String expressionString ) {
		this.targetMethod.setExpressionText( expressionString );
	}
	
	public String getTargetMethod() {
		return this.targetMethod.getEvaluatedValue();
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		try {
			MethodExpression m = super.createMethodBinding( "#{viewBean." + getTargetMethod() + "}" );
			m.invoke( getELContext(), null );
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
