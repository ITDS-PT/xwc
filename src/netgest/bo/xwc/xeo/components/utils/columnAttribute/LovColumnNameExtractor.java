package netgest.bo.xwc.xeo.components.utils.columnAttribute;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import netgest.bo.builder.boBuildDB;

public class LovColumnNameExtractor {

	private static final int ORACLE_SIZE_LIMIT_WITH_PREFIX_ADDED = 24;

	public static final String LOV_ID_PREFIX = "xeoLov";
	
	private String dataField = "";
	
	private static ConcurrentMap<String, String> decodeMap = new ConcurrentHashMap<String, String>();
	
	public LovColumnNameExtractor(String name){
		assert name != null : "Name cannot be null";
		this.dataField = name;
	}
	
	public String extractName(){
		if (decodeMap.containsKey(dataField)){
			return decodeMap.get(dataField);
		}
		if ( dataField.startsWith( LOV_ID_PREFIX ) )
			dataField = dataField.substring( LOV_ID_PREFIX.length(), dataField.length() );
		return dataField;
	}
	
	public static boolean isXeoLovColumn(String dataField){
		if (decodeMap.containsKey(dataField))
			return true;
		if (dataField.length() > LOV_ID_PREFIX.length()){
			String name = dataField.substring( 0, LOV_ID_PREFIX.length() - 1 );
			return name.equalsIgnoreCase(LOV_ID_PREFIX);
		}
		return false;
	}
	
	public String prefixColumnName(){
		if (dataField.length() > ORACLE_SIZE_LIMIT_WITH_PREFIX_ADDED){
			String name = decodeMap.get(dataField);
			if (name == null){
				name = createCRCFromField(dataField);
				decodeMap.putIfAbsent(name, dataField);
			}
			return name;
		} else {
			return LOV_ID_PREFIX + dataField;
		}
	}

	private String createCRCFromField(String dataField) {
		return boBuildDB.encodeObjectName(LOV_ID_PREFIX + dataField);
	}
	

}
