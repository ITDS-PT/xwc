package netgest.bo.xwc.framework;

import javax.el.ELContext;
import javax.el.ValueExpression;

import netgest.bo.xwc.framework.components.XUIComponentBase;

public class XUIBindProperty<V> extends XUIBaseProperty<ValueExpression> {
    
    private XUIComponentBase   oComp         = null;
    private Class              cValueType    = null;
    private boolean			   propertyResolved = false;
    public XUIBindProperty( String sPropertyName, XUIComponentBase oComponent, Class cValueType ) {
        super( sPropertyName, oComponent );
        this.oComp = oComponent;
        this.cValueType = cValueType;
    }

    public XUIBindProperty( String sPropertyName, XUIComponentBase oComponent, String sExpressionString, Class cValueType ) {
        super( sPropertyName, oComponent, sExpressionString==null?null:oComponent.createValueExpression( sExpressionString, cValueType ) );
        this.oComp = oComponent;
        this.cValueType = cValueType;
    }
    
    public void setExpressionText( String sExpression ) {
        super.setValue( sExpression==null?null:oComp.createValueExpression( sExpression, this.cValueType ) );
    }

    public String getExpressionString() {
    	ValueExpression value = super.getValue();
    	if( value != null )
    		return value.getExpressionString();
    	return null;
    }

    public boolean isPropertyResolved() {
    	return propertyResolved;
    }
    
    public V getEvaluatedValue() {
        ValueExpression oValExpr;
        
        V               oRetValue;

        propertyResolved = false;
        
        oRetValue = null;
        
        oValExpr = getValue();
        
        if( oValExpr != null )
        {
            if ( oValExpr.isLiteralText() ) {
            	propertyResolved = true;
                String sLiteralText = oValExpr.getExpressionString();
                if( oValExpr.getExpectedType() == String.class ) {
                    oRetValue = (V)sLiteralText;
                }
                else if( oValExpr.getExpectedType() == Double.class ) {
                    oRetValue = (V)Double.valueOf( sLiteralText );
                }
                else if( oValExpr.getExpectedType() == Integer.class ) {
                    oRetValue = (V)Integer.valueOf( sLiteralText );
                }
                else if( oValExpr.getExpectedType() == Long.class ) {
                    oRetValue = (V)Long.valueOf( sLiteralText );
                }
                else if( oValExpr.getExpectedType() == Boolean.class ) {
                    oRetValue = (V)Boolean.valueOf( sLiteralText );
                }
                else if( oValExpr.getExpectedType() == Byte.class ) {
                    oRetValue = (V)Byte.valueOf( sLiteralText );
                }
            }
            else {
            	ELContext elCtx = oComp.getELContext();
                oRetValue = (V)oValExpr.getValue( elCtx );
                propertyResolved = elCtx.isPropertyResolved();
                
            }
        }
        
        // If it a simple type generate a default value
        if( oRetValue == null ) {
            if( this.cValueType == Double.class ) {
                oRetValue = (V)Double.valueOf( 0 );
            }
            else if( this.cValueType == Integer.class ) {
                oRetValue = (V)Integer.valueOf( 0 );
            }
            else if( this.cValueType == Long.class ) {
                oRetValue = (V)Long.valueOf( 0 );
            }
            else if( this.cValueType == Boolean.class ) {
                oRetValue = (V)Boolean.valueOf( false );
            }
            else if( this.cValueType == Byte.class ) {
                oRetValue = (V)Byte.valueOf( (byte)0 );
            }
        }
        return oRetValue;
    }

}
