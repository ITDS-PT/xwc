package netgest.bo.xwc.framework;

import javax.el.ELContext;
import javax.el.MethodExpression;

import netgest.bo.xwc.framework.components.XUIComponentBase;

public class XUIMethodBindProperty extends XUIBaseProperty<MethodExpression> {
	
	private Object returnValue;
	
    public XUIMethodBindProperty( String sPropertyName, XUIComponentBase oComponent ) {
        super( sPropertyName, oComponent );
    }

    public XUIMethodBindProperty( String sPropertyName, XUIComponentBase oComponent, String sExpressionString ) {
        super( sPropertyName, oComponent, oComponent.createMethodBinding( sExpressionString ) );
    }
    
    public void setExpressionText( String sExpression ) {
    	if( sExpression != null && sExpression.length() > 0 )
    		super.setValue( getComponent().createMethodBinding( sExpression ) );
    	else
    		super.setValue( null );
    }

    public String getExpressionString() {
        return super.getValue().getExpressionString();
    }

    @SuppressWarnings("unchecked")
	public void invoke() {
    	MethodExpression oMethod = super.getValue();
    	if( oMethod != null ) {
        	ELContext oElContext = XUIRequestContext.getCurrentContext().getELContext();
        	returnValue = (Object)oMethod.invoke( oElContext, null );
    	}
    }
    
    public Object getReturnValue() {
    	return returnValue;
    }
	
}
