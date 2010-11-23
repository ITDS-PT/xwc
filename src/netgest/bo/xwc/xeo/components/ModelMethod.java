package netgest.bo.xwc.xeo.components;

import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.faces.event.ActionEvent;

import netgest.bo.runtime.boObject;
import netgest.bo.xwc.components.annotations.Required;
import netgest.bo.xwc.components.annotations.RequiredAlways;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.xeo.localization.BeansMessages;

/**
 * 
 * A {@link Menu} component to invoke a Method defined in a given XEO Model
 * 
 * @author jcarreira
 *
 */
public class ModelMethod extends ViewerMethod {
	
	@Override
	public String getRendererType() {
		return "xvw:menu";
	}
	
	/**
	 * The {@link boObject} from which the method will be executed
	 */
	XUIBindProperty<boObject> 	targetObject = 
		new XUIBindProperty<boObject>("targetObject", this, boObject.class, "#{viewBean.XEOObject}" );
		
	/**
	 * The name of the method from the {@link boObject} to execute
	 */
	@RequiredAlways	
	XUIBindProperty<String> 	targetMethod = 
		new XUIBindProperty<String>("targetMethod", this, String.class );
	
	public void setTargetObject( String expressionString ) {
		this.targetObject.setExpressionText( expressionString );
	}
	
	public boObject getTargetObject() {
		return this.targetObject.getEvaluatedValue();
	}
	
	public void setTargetMethod( String expressionString ) {
		this.targetMethod.setExpressionText( expressionString );
	}
	
	public String getTargetMethod() {
		return this.targetMethod.getEvaluatedValue();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		boObject xeoObject = targetObject.getEvaluatedValue();
		try {
			Method m = xeoObject.getClass().getMethod( this.targetMethod.getEvaluatedValue(), (Class[])null );
			m.invoke( xeoObject, (Object[])null );			
			
			
			if( xeoObject.getObjectErrors() != null ) {
		    	StringBuffer sErros = new StringBuffer();
		        ArrayList oErrors = xeoObject.getObjectErrors();
				if( oErrors != null && oErrors.size() > 0 ) {
				    for( Object error : oErrors ) {
					sErros.append( (String)error ).append("<br>");
						}
				}
				this.getRequestContext().addMessage( "viewBean_erros", new XUIMessage(
					XUIMessage.TYPE_ALERT, 
					XUIMessage.SEVERITY_ERROR,
					BeansMessages.TITLE_ERRORS.toString(),
					sErros.toString()));
				xeoObject.clearObjectErrors();
			}		
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
