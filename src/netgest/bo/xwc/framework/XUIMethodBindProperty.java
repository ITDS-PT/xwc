package netgest.bo.xwc.framework;

import javax.el.ELContext;
import javax.el.MethodExpression;

import netgest.bo.xwc.framework.components.XUIComponentBase;

public class XUIMethodBindProperty extends XUIBaseProperty<MethodExpression> {

    
    private XUIComponentBase   oComp         = null;

    public XUIMethodBindProperty( String sPropertyName, XUIComponentBase oComponent ) {
        super( sPropertyName, oComponent );
        this.oComp = oComponent;
    }

    public XUIMethodBindProperty( String sPropertyName, XUIComponentBase oComponent, String sExpressionString ) {
        super( sPropertyName, oComponent, oComponent.createMethodBinding( sExpressionString ) );
        this.oComp = oComponent;
    }
    
    public void setExpressionText( String sExpression ) {
        super.setValue( oComp.createMethodBinding( sExpression ) );
    }

    public String getExpressionString() {
        return super.getValue().getExpressionString();
    }

    public void invoke() {

    	MethodExpression oMethod = super.getValue();

    	if( oMethod != null ) {
        	ELContext oElContext = XUIRequestContext.getCurrentContext().getELContext();
        	oMethod.invoke( oElContext, null );
    	}
    }
	
}
