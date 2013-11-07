package netgest.bo.xwc.framework;

import javax.el.ValueExpression;

import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.jsf.XUIViewHandler;


public class XUIStateProperty<V> extends XUIBaseProperty<V> {
    
    private boolean bWasChanged;
   	private Object  lastEvalValue;
    private boolean lastEvalValueWasSet;
    private Object	savedValue;
    
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
    
    protected void setLastEvaluatedValue( Object lastEvalValue ) {
    	this.lastEvalValue = lastEvalValue;
    	lastEvalValueWasSet = true;
    }
    
	public Object saveState() {
    	if( !lastEvalValueWasSet ) {
	        Object oValue = getValue();
	        if( this instanceof XUIStateBindProperty ) {
	        	if (XUIViewHandler.isSavingInCache()  || !XUIViewHandler.evaluateStateProperties())
	        		lastEvalValue = oValue;
	        	else
	        		lastEvalValue = ((XUIStateBindProperty<?>)this).evaluateValue( (ValueExpression)oValue );
	        }
	        else {
	        	lastEvalValue = oValue;
	        }
    	}
        return new Object[] { lastEvalValue, super.saveState() };
    }
    
    @SuppressWarnings("unchecked")
	public void restoreState( Object oStateValue ) {
    	savedValue = (V)((Object[])oStateValue)[0];
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

    	if( this.isDefaultValue() ) {
    		return false;
    	}
    	
    	if( this.bWasChanged ) {
        	return true;
        }
        
        Object oNewValue = super.getValue();
        if( this instanceof XUIStateBindProperty<?> ) {
        	oNewValue = ((XUIStateBindProperty<?>)this).evaluateValue( (ValueExpression)oNewValue );
        }
        
        return !compareValues( this.savedValue , oNewValue );
        
    }
    
    public void setChanged( boolean changed ) {
    	this.bWasChanged = changed;
    }
    
    public V getSavedValue() {
        Object oNewValue = super.getValue();
        if( this instanceof XUIStateBindProperty<?> ) {
        	oNewValue = ((XUIStateBindProperty<?>)this).evaluateValue( (ValueExpression)oNewValue );
        }
        return (V)oNewValue;
    	
    }
    
}


