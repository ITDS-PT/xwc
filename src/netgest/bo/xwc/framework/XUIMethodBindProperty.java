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
    
    public XUIMethodBindProperty( String sPropertyName, XUIComponentBase oComponent, String sExpressionString, Class<?>[] params ) {
        super( sPropertyName, oComponent, oComponent.createMethodBinding( sExpressionString, params ) );
    }
    
    public void setExpressionText( String sExpression ) {
    	if( sExpression != null && sExpression.length() > 0 )
    		super.setValue( getComponent().createMethodBinding( sExpression ) );
    	else
    		super.setValue( null );
    }
    
    public void setExpressionText( String sExpression, Class<?>[] args ) {
    	if( sExpression != null && sExpression.length() > 0 )
    		super.setValue( getComponent().createMethodBinding( sExpression, args ) );
    	else
    		super.setValue( null );
    }

    public String getExpressionString() {
    	MethodExpression m = super.getValue(); 
    	if( m != null ) 
    		return super.getValue().getExpressionString();
    	
    	return null;
    	
    }

	public void invoke() {
    	MethodExpression oMethod = super.getValue();
    	if( oMethod != null ) {
        	ELContext oElContext = XUIRequestContext.getCurrentContext().getELContext();
        	returnValue = (Object)oMethod.invoke( oElContext, null );
    	}
    }
	
	public void invoke(Object[] args) {
    	MethodExpression oMethod = super.getValue();
    	if( oMethod != null ) {
    		ELContext oElContext = getComponent().getELContext();
        	returnValue = (Object)oMethod.invoke( oElContext, args );
    	}
    }
    
    public Object getReturnValue() {
    	return returnValue;
    }
	
}
