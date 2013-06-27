package netgest.bo.xwc.components.classic.grid;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import netgest.bo.def.boDefAttribute;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.connectors.DataFieldMetaData;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeMetaData;
import netgest.bo.xwc.components.model.Column;
import netgest.utils.StringUtils;

public class ColumnFilterLovAdapter {
	
	private Column column;
	private DataFieldMetaData metadata;
	
	public ColumnFilterLovAdapter(DataFieldMetaData metadata, Column col){
		this.metadata = metadata;
		this.column = col;
	}
	
	public boolean requiresConversion(){
		if (metadata instanceof XEOObjectAttributeMetaData){
			XEOObjectAttributeMetaData objectMetadata = (XEOObjectAttributeMetaData) metadata;
			boDefAttribute attributeDefinition = objectMetadata.getBoDefAttribute();
			return StringUtils.hasValue( attributeDefinition.getLOVName() ) && !column.useValueOnLov(); 
		}
		return false;
	}

	public Map< Object , String > convertMapToColumnFilterFormat(Map<Object,String> lovMap) {

		if (metadata instanceof XEOObjectAttributeMetaData){
			Map<Object,String> result = new LinkedHashMap< Object , String >();
			XEOObjectAttributeMetaData objectMetadata = (XEOObjectAttributeMetaData) metadata;
			boDefAttribute attributeDefinition = objectMetadata.getBoDefAttribute();
			EboContext ctx = boApplication.currentContext().getEboContext();
			Map<String,String> rawMap = createRawMapFromLov( ctx , attributeDefinition.getLOVName() );

			Iterator<String> rawIterator = rawMap.keySet().iterator();
			while ( rawIterator.hasNext() ){
				String key = rawIterator.next();
				String translatedDescription = lovMap.get( key );
				String rawTranslation = rawMap.get( key );
				result.put( rawTranslation , translatedDescription );

			}
			return result;
				
			}
		
		return lovMap;
		
	}
		
		
	
	
	/**
	 * 
	 * Creates a Map from an existing Lov with the original descriptions (ignoring translations)
	 * 
	 * @param ctx Context to load the map
	 * @param lovName The name of the lov
	 * 
	 * @return A map with the entries of the lov
	 */
	static Map<String, String> createRawMapFromLov( EboContext ctx,  String lovName ) {
		Map<String,String> result = new LinkedHashMap<String, String>();
		try {
			lovObject lov = LovManager.getLovObject( ctx, lovName );
			if( lov != null ) {
				lov.beforeFirst();
				while (lov.next()){
					result.put(lov.getCode(), lov.getRawDescription());
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return result;
	}
	
}
