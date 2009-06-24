package netgest.bo.xwc.framework;

import java.util.Arrays;

import javax.el.ValueExpression;

import netgest.bo.xwc.framework.components.XUIComponentBase;


public class XUIStateProperty<V> extends XUIBaseProperty<V> {
    
    private boolean bWasChanged;
    private Object  oBindValue;
    
    private XUIComponentBase oComponent;
    
    public XUIStateProperty( String sPropertyName, 
                            XUIComponentBase oComponent ) {
        
        this( sPropertyName, oComponent, null );
    }
    public XUIStateProperty( String sPropertyName, XUIComponentBase oComponent, V oValue ) {

        super( sPropertyName, oComponent, oValue );

    }

    public void setValue( V oNewValue ) {
        
        // Compare Values
        if( !compareValues( getValue(), oNewValue ) ) {
            super.setValue( oNewValue );
            bWasChanged = true;
        }
    }
    
    public Object saveState() {
        Object oValue = getValue();
        if( oValue instanceof ValueExpression ) {
            try {
	        	ValueExpression oValExpr = (ValueExpression)oValue;
	            if( oValExpr != null )
	            {
	                if ( oValExpr.isLiteralText() ) {
	                    
	                    String sLiteralText = oValExpr.getExpressionString();
	                    if( oValExpr.getExpectedType() == String.class ) {
	                    	oBindValue = (V)sLiteralText;
	                    }
	                    else if( oValExpr.getExpectedType() == Double.class ) {
	                    	oBindValue = (V)Double.valueOf( sLiteralText );
	                    }
	                    else if( oValExpr.getExpectedType() == Integer.class ) {
	                    	oBindValue = (V)Integer.valueOf( sLiteralText );
	                    }
	                    else if( oValExpr.getExpectedType() == Long.class ) {
	                    	oBindValue = (V)Long.valueOf( sLiteralText );
	                    }
	                    else if( oValExpr.getExpectedType() == Boolean.class ) {
	                    	oBindValue = (V)Boolean.valueOf( sLiteralText );
	                    }
	                    else if( oValExpr.getExpectedType() == Byte.class ) {
	                    	oBindValue = (V)Byte.valueOf( sLiteralText );
	                    } else {
	                    	throw new RuntimeException( "Cannot conver expression text ["+sLiteralText+"] in " + oValExpr.getExpectedType().getName() );
	                    }
	                }
	                else {
	                	oBindValue = (V)oValExpr.getValue( getComponent().getELContext() );
	                }
	            }
            }
            catch( Exception e )
            {
                System.out.println( "Saving evaluated value on [" +
                			this.getComponent().getId()+"].["+
                			this.getComponent().getClass().getName()+"].[" + 
                			this.getName() + "]:" + e.getClass().getName() + 
                			" - " + e.getMessage() 
                );
            }
        }
        else {
        	oBindValue = oValue;
        }
        return new Object[] { oBindValue, super.saveState() };
    }
    
    public void restoreState( Object oStateValue ) {
        oBindValue = (V)((Object[])oStateValue)[0];
        super.restoreState( ((Object[])oStateValue)[1] );
    }

    public static final boolean compareValues( Object oValue1, Object oValue2 ) {
        
        // Check object reference and if both are null
        if( ( oValue1 == oValue2 ) 
            || 
            ( oValue1 == null && oValue2 == null ) 
        ) {
            return true;
        }
        
        // Check if left is null and right not null and viceversa
        if( ( oValue1 == null && oValue2 != null ) 
            ||
            ( oValue1 != null && oValue2 == null )
        ) {
            return false;
        }
        
        if( !oValue1.equals( oValue2 ) ) {
            
            if( oValue1 instanceof String[] && oValue2 instanceof String[] ) {
                boolean  bChanged = false;
                String[] sValue1 = (String[])oValue1;
                String[] sValue2 = (String[])oValue2;
                
                if( sValue1.length != sValue2.length ) {
                    return false;
                }
                for (int i = 0; i < sValue1.length; i++) {
                    bChanged = !compareValues( sValue1[i], sValue2[i] );
                    if( bChanged ) {
                        return false;
                    } 
                }
                return true;
            }
            return false;
        }
        
        return true;
    }

    public boolean wasChanged() {
        
        Object oValue = super.getValue();
        
        if( oValue instanceof ValueExpression ) {
            try {
                
                Object oNewValue = ((ValueExpression)oValue).getValue( XUIRequestContext.getCurrentContext().getELContext() );
                if( oBindValue == null ) {
                	return false;
                }
                return !compareValues( oBindValue, oNewValue );

            }
            catch( Exception e )
            {}
        }
        return this.bWasChanged;
    }

}
