package netgest.bo.xwc.components.connectors.helper;

import netgest.bo.xwc.components.connectors.DataFieldMetaData;
import netgest.bo.xwc.components.connectors.DataFieldTypes;

public class MetadataConverter {

	private DataFieldMetaData meta;
	
	public MetadataConverter(DataFieldMetaData metadata){
		this.meta = metadata;
	}
	
	public String getDataTypeAsString(){
		switch (meta.getDataType()){
			case DataFieldTypes.VALUE_CHAR: return "string";
			case DataFieldTypes.VALUE_BLOB: return "string";
			case DataFieldTypes.VALUE_CLOB: return "string";
			case DataFieldTypes.VALUE_BOOLEAN: return "boolean";
			case DataFieldTypes.VALUE_DATE: return "date";
			case DataFieldTypes.VALUE_DATETIME: return "date";
			case DataFieldTypes.VALUE_NUMBER: return decodeNumberAttribute();
			case DataFieldTypes.VALUE_UNKNOWN: return "string";
			
			default : return "string";	
		}
	}
	
	private String decodeNumberAttribute(){
		if (meta.getInputRenderType() == DataFieldTypes.RENDER_OBJECT_LOOKUP)
			return "object";
		if (meta.getIsLov()) //FIXME: Isto devia ser "list" mas quando é objecto como lista....
			return "object";
		return "numeric";
	}

}
