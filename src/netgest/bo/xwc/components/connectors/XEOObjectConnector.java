package netgest.bo.xwc.components.connectors;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.localization.ConnectorsMessages;
import netgest.bo.xwc.components.security.SecurityPermissions;

public class XEOObjectConnector implements DataRecordConnector, Map<String,Object> {

	private static final Logger log = Logger.getLogger( XEOObjectConnector.class.getName() );
    
    private long lBoObjectBoui;
    
    public XEOObjectConnector( long lBoObjectBoui ) 
    {
        this.lBoObjectBoui = lBoObjectBoui;
    }
    
    public byte getSecurityPermissions() {
		return SecurityPermissions.FULL_CONTROL;
	}

	public boObject getXEOObject() {
        boObject oBoObject;
        EboContext oEboContext = boApplication.currentContext().getEboContext();
        try {
        	
        	//oBoObject = boObject.getBoManager().getObjectInContext( oEboContext , lBoObjectBoui);
        	
        	//if( oBoObject == null ) {
	            oBoObject = 
	                    boObject.getBoManager().loadObject(oEboContext, lBoObjectBoui );
        	//}
        	
        } catch (boRuntimeException e) {
            throw new RuntimeException(e);
        }
        return oBoObject;
    }
    
    public DataFieldConnector getAttribute( String name ) {
        XEOObjectAttributeConnector oRetAttribute;
        
        oRetAttribute = null;
        
        
        // Static Attributes
        try {
			if( name.equals("SYS_OBJECT_LABEL") ) 
				return new GenericFieldConnector( ConnectorsMessages.OBJECT_LABEL.toString(), getXEOObject().getLabel(), DataFieldTypes.VALUE_CHAR );
			
			if( name.equals("SYS_OBJECT_ICON_16") ) 
				return new GenericFieldConnector( ConnectorsMessages.OBJECT_LABEL.toString(), "<img src='" + getXEOObject().getSrcForIcon16()+"'>", DataFieldTypes.VALUE_CHAR );

			if( name.equals("SYS_ICON_COMPOSED_STATE") ) 
				return new GenericFieldConnector( ConnectorsMessages.OBJECT_LABEL.toString(), getXEOObject().getICONComposedState(), DataFieldTypes.VALUE_CHAR );
			
		} catch (boRuntimeException e) {
			throw new RuntimeException( e );
		}
        
        AttributeHandler oAttrHandlr = getXEOAttribute( name );
        if( oAttrHandlr == null ) {
            String sObjectName =  getXEOObject().getName();
            
            log.fine("Cannot found attribute [" + name + "]. Is missing on object [" + sObjectName + "]" );
        } else {
        	oRetAttribute = new XEOObjectAttributeConnector( this, oAttrHandlr );
        }

        return oRetAttribute;
        
    }
    
    protected AttributeHandler getXEOAttribute( String sAttributeName ) {
    	return decodeAttribute( sAttributeName , getXEOObject());
    }

    protected AttributeHandler decodeAttribute( String sAttributeName, boObject oObj  ) {
    	AttributeHandler oRet = null;
    	
    	
    	if( oObj != null ) {

    		if( sAttributeName.indexOf('.') == -1 ) {
        		return oObj.getAttribute( sAttributeName );
        	}
    		
        	String[] sAtt = sAttributeName.split("\\.");
    		for(int i=0;i < sAtt.length; i++ ) {
	    		oRet = oObj.getAttribute( sAtt[i] );
	    		if( i < sAtt.length - 1 && oRet != null && oRet.isObject() ) {
	    			try {
						oObj = oRet.getObject();
						if( oObj == null ) {
							oRet = null;
							break;
						}
					} catch (boRuntimeException e) {
						// TODO Auto-generated catch block
						throw new RuntimeException(e);
					}
	    		}
	    	}
    	}
        return oRet;
    }
    
    public int size() {
        return 0;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean containsKey(Object key) {
        if( key == null )
            throw new NullPointerException( "key cannot be null" );

        if( !(key instanceof String) )
            throw new IllegalArgumentException("key must be a string");

        return getAttribute( (String)key ) != null;
    }

    public boolean containsValue(Object value) { 
        return false;
    }

    public Object get(Object key) {
        if( key == null )
            throw new NullPointerException( "key cannot be null" );

        if( !(key instanceof String) )
            throw new IllegalArgumentException("key must be a string");
        
        return getAttribute( (String)key );
    }

    public Object put(String key, Object value) {
        throw new RuntimeException("XEODataRecord is readOnly" );
    }

    public Object remove(Object key) {
        return null;
    }

    @SuppressWarnings("unchecked")
	public void putAll(Map t) {
        log.log( Level.WARNING, "Mehtod not implemented!" );
    }

    public void clear() {
        log.log( Level.WARNING, "Mehtod not implemented!" );
    }

    @SuppressWarnings("unchecked")
	public Set<String> keySet() {
        log.log( Level.WARNING, "Mehtod not implemented!" );
        return Collections.EMPTY_SET;
    }

    public Collection<Object> values() {
        log.log( Level.WARNING, "Mehtod not implemented!" );
        return null;
    }

    @SuppressWarnings("unchecked")
	public Set entrySet() {
        return Collections.EMPTY_SET;
    }

    public static class GenericFieldConnector implements DataFieldConnector {
    	
    	private byte dataType;
    	private String label;
    	private String value;
    	
    	public GenericFieldConnector( String label, String value, byte dataType ) {
    		this.label = label;
    		this.value = value;
    		this.dataType = dataType;
    	}
    	
		public byte getDataType() {
			return dataType;
		}

		public int getDecimalPrecision() {
			return 0;
		}

		public byte getInputRenderType() {
			return DataFieldTypes.RENDER_DEFAULT;
		}

		public boolean getIsLov() {
			return false;
		}

		public String getLabel() {
			return label;
		}

		public Map<Object, String> getLovMap() {
			return null;
		}

		public int getMaxLength() {
			return 0;
		}

		public int getMinDecimals() {
			return 0;
		}

		public boolean getNumberGrouping() {
			return false;
		}

		public double getNumberMaxValue() {
			return 0;
		}

		public double getNumberMinValue() {
			return 0;
		}

		public DataListConnector getDataList() {
			return null;
		}

		public String[] getDependences() {
			return null;
		}

		public boolean getDisabled() {
			return false;
		}

		public String getDisplayValue() {
			return value;
		}

		public boolean getIsLovEditable() {
			return false;
		}

		public boolean getOnChangeSubmit() {
			return false;
		}

		public boolean getRecomended() {
			return false;
		}

		public boolean getRequired() {
			return false;
		}

		public boolean getValid() {
			return false;
		}

		public Object getValue() {
			return value;
		}

		public boolean getVisible() {
			return true;
		}

		public void setValue(Object newValue) {
			
		}
	    public byte getSecurityPermissions() {
			return SecurityPermissions.FULL_CONTROL;
		}
    	
    }
    
}
