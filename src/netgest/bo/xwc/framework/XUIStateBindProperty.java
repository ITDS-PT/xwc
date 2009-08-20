package netgest.bo.xwc.framework;

import javax.el.ValueExpression;

import netgest.bo.xwc.framework.components.XUIComponentBase;

public class XUIStateBindProperty<V> extends XUIStateProperty<ValueExpression> {
    
    private Class              cValueType    = null;

    public XUIStateBindProperty( String sPropertyName, XUIComponentBase oComponent, Class cValueType ) {
        super( sPropertyName, oComponent );
        this.cValueType = cValueType;
    }

    public XUIStateBindProperty( String sPropertyName, XUIComponentBase oComponent, String sExpressionString, Class cValueType ) {
        super( sPropertyName, oComponent, oComponent.createValueExpression( sExpressionString, cValueType ) );
        this.cValueType = cValueType;
    }
    
    public void setExpressionText( String sExpression ) {
    	if( sExpression != null ) {
    		if( !sExpression.equals( getExpressionString() ) ) {
    			super.setValue( getComponent().createValueExpression( sExpression, this.cValueType ) );
    		}
    	} else {
    		super.setValue( null );
    	}
    }

    public boolean isNull() {
    	return super.getValue() == null;
    }
    
    public boolean isLiteralText() {
    	if( super.getValue() != null ) {
    		return this.getValue().isLiteralText();
    	}
    	return false;
    }
    
    public String getExpressionString() {
    	ValueExpression ve = super.getValue(); 
    	if( ve != null )
    		return ve.getExpressionString();
    	
    	return null;
    }

    public V getEvaluatedValue() {
        ValueExpression oValExpr;
        
        V               oRetValue;
        
        oRetValue = null;
        
        oValExpr = getValue();
        
        if( oValExpr != null )
        {
            if ( oValExpr.isLiteralText() ) {
                
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
                } else {
                	throw new RuntimeException( "Cannot conver expression text ["+sLiteralText+"] in " + oValExpr.getExpectedType().getName() );
                }
            }
            else {
                oRetValue = (V)oValExpr.getValue( getComponent().getELContext() );
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
        setLastEvaluatedValue( oRetValue );
        return oRetValue;
    }
}
