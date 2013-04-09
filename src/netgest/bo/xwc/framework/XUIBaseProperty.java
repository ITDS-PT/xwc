package netgest.bo.xwc.framework;

import javax.el.ValueExpression;

import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.xeo.workplaces.admin.localization.ExceptionMessage;

public class XUIBaseProperty<V> {
    private V       		 oPropertyValue;
    private boolean 		 bDefaultValue = true;
    private String  		 sPropertyName;
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
    
    public boolean wasEvaluated(){
    	return wasEvaluated;
    }
    
    protected boolean wasEvaluated = true;

    private final void validate() {
        if( sPropertyName == null ) throw new IllegalArgumentException(ExceptionMessage.PROPERTY_NAME_CANNOT_BE_NULL.toString());
        if( oComponent == null ) throw new IllegalArgumentException(ExceptionMessage.COMPONENT_CANNOT_BE_NULL.toString());
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
        return new Object[] { 
        		bDefaultValue? null:oPropertyValue, 
        		bDefaultValue 
        	};
    }
    
    public void restoreState( Object oStateValue ) {
        Object[] oStateValues =(Object[])oStateValue; 
    	bDefaultValue = (Boolean)(oStateValues)[1];
    	if( !bDefaultValue )
    		oPropertyValue = (V)(oStateValues)[0];
    }
    
    public String getName() {
        return sPropertyName;
    }
    
    public boolean wasChanged() {
        return false;
    }

    public boolean isDefaultValue() {
    	return bDefaultValue; 
    }
    
    public XUIComponentBase getComponent() {
    	return this.oComponent;
    }
    
}
