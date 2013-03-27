package netgest.bo.xwc.framework;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;

import netgest.bo.xwc.framework.components.XUIComponentBase;

public class XUIBindProperty<V> extends XUIBaseProperty<Object> {
    
	private 			Class<?>        cValueType    = null;
    private transient 	UIViewRoot   viewContainer = null;
    
    public UIViewRoot getViewContainer() {
    	if( this.viewContainer == null ) {
    		UIComponent parent = this.getComponent().getParent();
    		while( parent != null ) {
	    		if( parent instanceof UIViewRoot ) {
	    			this.viewContainer  = (UIViewRoot)parent;
	    			break;
	    		}
	    		parent = parent.getParent();
    		}
    	}
    	return this.viewContainer;
    }
    
    public XUIBindProperty( String sPropertyName, XUIComponentBase oComponent, Class<?> cValueType ) {
        super( sPropertyName, oComponent );
        this.cValueType = cValueType;
    }

    public XUIBindProperty( String sPropertyName, XUIComponentBase oComponent, Class<?> cValueType, String sExpressionString ) {
        super( sPropertyName, oComponent, sExpressionString==null?null:oComponent.createValueExpression( sExpressionString, cValueType ) );
        this.cValueType = cValueType;
    }

    public XUIBindProperty( String sPropertyName, XUIComponentBase oComponent, V oDefaultValue, Class<?> cValueType ) {
        super( sPropertyName, oComponent, oDefaultValue );
        this.cValueType = cValueType;
    }
    
    public void setExpressionText( String sExpression ) {
        super.setValue( sExpression==null?null:getComponent().createValueExpression( sExpression, this.cValueType ) );
    }

    public boolean isLiteral() {
    	Object value = super.getValue();
    	if( value != null ) {
	    	if( value instanceof ValueExpression ) {
		    		return ((ValueExpression)value).isLiteralText();
	    	}
	    	return true;
    	}
    	return false;
    }
    
    
    public String getExpressionString() {
    	Object value = super.getValue();
    	if( value != null ) {
	    	if( value instanceof ValueExpression ) {
		    		return ((ValueExpression)value).getExpressionString();
	    	}
	    	else {
	    		return String.valueOf( value );
	    	}
    	}
    	return null;
    }
    
    public boolean isNull() {
    	return super.getValue() == null;
    }
    
    @SuppressWarnings("unchecked")
	public V getEvaluatedValue() {
    	Object			oValue;
        ValueExpression oValExpr;
        
        V               oRetValue;

        oRetValue = null;
        
        oValue = getValue();

        if( oValue != null ) {
        	if( oValue instanceof ValueExpression ) {
        		oValExpr = (ValueExpression)oValue;
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
	                }
	            }
	            else {
	            	ELContext elCtx = getComponent().getELContext();
	                oRetValue = (V)oValExpr.getValue( elCtx );
	                if (elCtx instanceof XUIELContextWrapper){
	                	if (!((XUIELContextWrapper) elCtx).wasPropertyEvaluated()){
	                		wasEvaluated = false;
	                	}
	                }
	            }
        	}
        	else {
        		oRetValue = (V)oValue;
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
            wasEvaluated = false;
        }
        return oRetValue;
    }
    
    
    
    public boolean isLiteralText() {
        Object oValue = getValue();
        if( oValue == null ) {
        	return true;
        }
    	if( oValue instanceof ValueExpression ) {
    		return ((ValueExpression)oValue).isLiteralText();
    	}
    	return true;
    }
    
    
    
    
}
