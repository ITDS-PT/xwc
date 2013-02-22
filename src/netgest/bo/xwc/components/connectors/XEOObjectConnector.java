package netgest.bo.xwc.components.connectors;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.Logger;
import netgest.bo.system.LoggerLevels;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.localization.ConnectorsMessages;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.xeo.workplaces.admin.localization.ExceptionMessage;

public class XEOObjectConnector implements DataRecordConnector, Map<String,Object> {

	private static final Logger log = Logger.getLogger( XEOObjectConnector.class.getName() );
    
    private long lBoObjectBoui;
    private boObject currObject=null;
    private int	 rowIndex; 
    
    /**
     * 
     * List of Constants when using an Object Connector
     * 
     * 
     * @author Pedro Pereira
     *
     */
    public static enum SystemConstants{
    	SYS_OBJECT_LABEL,
    	SYS_OBJECT_ICON_16,
    	SYS_ICON_COMPOSED_STATE,
    	SYS_CARDID,
    	SYS_ROWNUM
    };
    
    public static boolean isSystemConstant(String name){
    	for (SystemConstants p: SystemConstants.values())
    	{
    		if (name.equalsIgnoreCase(p.name()))
    			return true;
    	}
    	return false;
    }
    
    public XEOObjectConnector( long lBoObjectBoui, int rowIndex ) 
    {
    	this.rowIndex = rowIndex;
        this.lBoObjectBoui = lBoObjectBoui;
    }
    
    public XEOObjectConnector( boObject currObject, int rowIndex ) 
    {
    	this.rowIndex = rowIndex;
        this.currObject = currObject;
    }
    
    public byte getSecurityPermissions() {
		return SecurityPermissions.FULL_CONTROL;
	}

	public boObject getXEOObject() {
        boObject oBoObject = this.currObject;
        if( lBoObjectBoui != 0 && this.currObject==null) {
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
				return new GenericFieldConnector( ConnectorsMessages.OBJECT_LABEL.toString(), "<img style='height:16;width:16;align:abs-middle' src='" + getXEOObject().getSrcForIcon16()+"'/>", DataFieldTypes.VALUE_CHAR );

			if( name.equals("SYS_ICON_COMPOSED_STATE") ) 
				return new GenericFieldConnector( ConnectorsMessages.OBJECT_LABEL.toString(), getXEOObject().getICONComposedState(), DataFieldTypes.VALUE_CHAR );
			
			if( name.equals("SYS_CARDID") ) 
				return new GenericFieldConnector( ConnectorsMessages.OBJECT_LABEL.toString(), getXEOObject().getCARDID().toString(), DataFieldTypes.VALUE_CHAR );
			
			if (name.equals("SYS_ROWNUM"))
				return new GenericFieldConnector( ConnectorsMessages.OBJECT_LABEL.toString(), String.valueOf(getRowIndex()), DataFieldTypes.VALUE_CHAR );
			
		} catch (boRuntimeException e) {
			throw new RuntimeException( e );
		}
        
        AttributeHandler oAttrHandlr = getXEOAttribute( name );
        if( oAttrHandlr == null ) {
//            String sObjectName = getXEOObject().getName();
//            
//            XUIRequestContext requestContext = XUIRequestContext.getCurrentContext();
//            if( requestContext != null && requestContext.getViewRoot() != null ) {
//            	log.severe(
//            				"Error on viewer [%s] - XEOObjectConnector cannot found attribute [%s] on XEO Model [%s]",
//            				requestContext.getViewRoot().getViewId(),
//            				name,
//            				sObjectName
//            			);
//            }
//            else {
//            	log.severe("XEOObjectConnector cannot found attribute [%s]. Is missing on object [%s]",
//        				name,
//        				sObjectName
//            	);
//            }
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
            throw new NullPointerException(ExceptionMessage.KEY_CANNOT_BE_NULL.toString());

        if( !(key instanceof String) )
            throw new IllegalArgumentException(ExceptionMessage.KEY_MUST_BE_A_STRING.toString());

        return getAttribute( (String)key ) != null;
    }

    public boolean containsValue(Object value) { 
        return false;
    }

    public Object get(Object key) {
        if( key == null )
            throw new NullPointerException(ExceptionMessage.KEY_CANNOT_BE_NULL.toString() );

        if( !(key instanceof String) )
            throw new IllegalArgumentException(ExceptionMessage.KEY_MUST_BE_A_STRING.toString());
        
        return getAttribute( (String)key );
    }

    public Object put(String key, Object value) {
        throw new RuntimeException(ExceptionMessage.XEODATARECORD_IS_READONLY.toString());
    }

    public Object remove(Object key) {
        return null;
    }

    @SuppressWarnings("unchecked")
	public void putAll(Map t) {
        log.log( LoggerLevels.WARNING, LoggerMessageLocalizer.getMessage("METHOD_NOT_IMPLEMENTED"));
    }

    public void clear() {
        log.log( LoggerLevels.WARNING, LoggerMessageLocalizer.getMessage("METHOD_NOT_IMPLEMENTED") );
    }

    @SuppressWarnings("unchecked")
	public Set<String> keySet() {
        log.log( LoggerLevels.WARNING, LoggerMessageLocalizer.getMessage("METHOD_NOT_IMPLEMENTED") );
        return Collections.EMPTY_SET;
    }

    public Collection<Object> values() {
        log.log( LoggerLevels.WARNING, LoggerMessageLocalizer.getMessage("METHOD_NOT_IMPLEMENTED") );
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

		@Override
		public String getInvalidMessage() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean validate() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isValid() {
			return true;
		}
    	
    }

	@Override
	public int getRowIndex() {
		return this.rowIndex;
	}
	
	/**
	 * 
	 * Retrieve the attribute definition from an attribute name (could have references
	 * to another object (BOQL dot syntax)
	 * 
	 * @param attName The name of the attribute in the form of "att.att.att"
	 * 
	 * @return An attribute definition
	 */
	public static boDefAttribute getAttributeDefinitionFromName(String attName, boDefHandler parent){
		
		if ( attName.contains( "__" )) {
				//Split by "."
				String[] relationAttribute = attName.split( "__" );
				int size = relationAttribute.length;
				boDefAttribute targetAttributeDefinition = null;
				for (int i = 0 ; i < size ; i++){
					String parentAtt = relationAttribute[i];
					if (i+1 < size){
						String childAtt = relationAttribute[i+1];
						boDefAttribute defAttRel = parent.getAttributeRef( parentAtt );
						boDefHandler defModelRel = defAttRel.getReferencedObjectDef();
						targetAttributeDefinition = defModelRel.getAttributeRef(childAtt);
						parent = defModelRel;
					}
				}
				return targetAttributeDefinition;
		}
		else
			return parent.getAttributeRef(attName);
	}
	
	public static boDefAttribute getAttributeDefinitionFromNameWithDotSeparator(String attName, boDefHandler parent){
		
		if ( attName.contains( "." )) {
				//Split by "."
				String[] relationAttribute = attName.split( "\\." );
				int size = relationAttribute.length;
				boDefAttribute targetAttributeDefinition = null;
				for (int i = 0 ; i < size ; i++){
					String parentAtt = relationAttribute[i];
					if (i+1 < size){
						String childAtt = relationAttribute[i+1];
						boDefAttribute defAttRel = parent.getAttributeRef( parentAtt );
						boDefHandler defModelRel = defAttRel.getReferencedObjectDef();
						targetAttributeDefinition = defModelRel.getAttributeRef(childAtt);
						parent = defModelRel;
					}
				}
				return targetAttributeDefinition;
		}
		else
			return parent.getAttributeRef(attName);
	}
    
}
