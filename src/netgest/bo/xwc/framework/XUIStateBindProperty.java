package netgest.bo.xwc.framework;

import javax.el.ValueExpression;

import netgest.bo.xwc.framework.components.XUIComponentBase;

public class XUIStateBindProperty<V> extends XUIStateProperty<ValueExpression> {
    
    private XUIComponentBase   oComp         = null;
    private Class              cValueType    = null;

    public XUIStateBindProperty( String sPropertyName, XUIComponentBase oComponent, Class cValueType ) {
        super( sPropertyName, oComponent );
        this.oComp = oComponent;
        this.cValueType = cValueType;
    }

    public XUIStateBindProperty( String sPropertyName, XUIComponentBase oComponent, String sExpressionString, Class cValueType ) {
        super( sPropertyName, oComponent, oComponent.createValueExpression( sExpressionString, cValueType ) );
        this.oComp = oComponent;
        this.cValueType = cValueType;
    }
    
    public void setExpressionText( String sExpression ) {
    	if( sExpression != null ) {
    		super.setValue( oComp.createValueExpression( sExpression, this.cValueType ) );
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
        return super.getValue().getExpressionString();
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
                oRetValue = (V)oValExpr.getValue( oComp.getELContext() );
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
