package netgest.bo.xwc.framework;

import javax.el.ValueExpression;

import netgest.bo.xwc.framework.components.XUIComponentBase;

public class XUIBaseProperty<V> {
    private V       oPropertyValue;
    private boolean bStateSaved;
    private boolean bDefaultValue = true;
    
    private String  sPropertyName;
    
    private XUIComponentBase oComponent;
    
    public XUIBaseProperty( String sPropertyName, 
                            XUIComponentBase oComponent ) {
        this.oComponent = oComponent;
        this.sPropertyName = sPropertyName;
        validate();
        oComponent.addStateProperty( this );
    }
    public XUIBaseProperty( String sPropertyName, XUIComponentBase oComponent, V oValue ) {

        oPropertyValue = oValue;
        this.oComponent = oComponent;
        this.sPropertyName = sPropertyName;
        validate();
        oComponent.addStateProperty( this );
        
        // If it is a value expression automatic add to component 
        // value expression collection.
        if( oValue instanceof ValueExpression )
            oComponent.setValueExpression( sPropertyName, (ValueExpression)oValue );

    }

    private final void validate() {
        if( sPropertyName == null ) throw new IllegalArgumentException("Property name cannot be null");
        if( oComponent == null ) throw new IllegalArgumentException("Component cannot be null");
    }

    public V getValue() {
        return oPropertyValue;
    }
    
    public void setValue( V oNewValue ) {
    	bDefaultValue = false;
        oPropertyValue = oNewValue;
        if( oNewValue instanceof ValueExpression ) {
            oComponent.setValueExpression( sPropertyName, (ValueExpression)oNewValue );
        }

    }
    
    public Object saveState() {
        bStateSaved = true;
        return new Object[] { oPropertyValue, bDefaultValue };
    }
    
    public void restoreState( Object oStateValue ) {
        Object[] oStateValues =(Object[])oStateValue; 
    	oPropertyValue = (V)(oStateValues)[0];
    	bDefaultValue = (Boolean)(oStateValues)[1];
    }
    
    public String getName() {
        return sPropertyName;
    }
    
    public boolean wasChanged() {
        return false;
    }

    public boolean isStateSaved() {
        return bStateSaved;
    }
    
    public boolean isDefaultValue() {
    	return bDefaultValue; 
    }
    
    public void setDefaultValue( boolean isDefaultValue ) {
    	this.bDefaultValue = isDefaultValue;
    }
    
    public XUIComponentBase getComponent() {
    	return this.oComponent;
    }
    
}
