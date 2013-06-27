package netgest.bo.xwc.xeo.components.utils.columnAttribute;

import netgest.bo.def.boDefAttribute;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.Logger;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataFieldTypes;
import netgest.bo.xwc.components.connectors.XEOObjectConnector;
import netgest.utils.StringUtils;

public class LovValueGridDisplay {
	
	private static final Logger logger = Logger.getLogger( LovValueGridDisplay.class );

	private boDefAttribute attributeDefinition;
	
	public LovValueGridDisplay(boDefAttribute attribute){
		this.attributeDefinition = attribute;
	}
	
	public DataFieldConnector getConnectorForValue(Object value){
		String attributeName = attributeDefinition.getName();
		String description = value.toString();
		String lovValue = getLovValue(attributeName, description);
		if (lovValue != null) {
			String translation = null;
			try {
				translation = lovObject.getTranslation( getLovName( attributeName ) , lovValue , String.valueOf( description ) );
			} catch ( boRuntimeException e ) {
				logger.warn( e );
			}
			return new XEOObjectConnector.GenericFieldConnector(
					attributeName, lovValue, DataFieldTypes.VALUE_CHAR,
					translation != null ? String.valueOf(translation) : null);
		} else {
			return new XEOObjectConnector.GenericFieldConnector(
					attributeName, description != null ? String.valueOf(description)
							: null, DataFieldTypes.VALUE_CHAR);
		}
	}
	
	
	
	private String getLovValue(String name,Object value) {
		String toRet=null;
		try {
			String lovName = null;
			if (attributeDefinition!=null && !StringUtils.isEmpty(attributeDefinition.getLOVName()))
				lovName=attributeDefinition.getLOVName();
			
			if (value != null && lovName != null) {
				EboContext ctx = boApplication.currentContext().getEboContext();
				lovObject lovObj = LovManager.getLovObject( ctx , lovName);
				lovObj.beforeFirst();
				boolean found = lovObj.findLovItemByDescription(String.valueOf( value ));
				
				if (found) {							
					toRet = lovObj.getCode();
				}
			}
		} catch (boRuntimeException e) {
			logger.warn( e );
		}
		return toRet;
	}	
	
	private String getLovName(String attributeName){
		String lovName = "";
		
		if (attributeDefinition != null && StringUtils.hasValue( attributeDefinition.getLOVName()) )
			lovName = attributeDefinition.getLOVName();
		
		return lovName;
	}
	
	
}
