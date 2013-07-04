package netgest.bo.xwc.components.classic.grid.utils;

import netgest.bo.xwc.components.model.Column;

/**
 * 
 * Helper Class to decode data field values (see {@link Column#getDataField()})
 * in valid values (due to the use of special characters like '.' and '_' which have
 * special meaning in BOQL Expressions and ExtJS Grids 
 * 
 * @author PedroRio
 *
 */
public class DataFieldDecoder {
	
	public static String convertForBOQL(String dataField){
		return dataField.replaceAll("__", ".").trim();
	}
	
	public static String convertForGridPanel(String dataField){
		return dataField.replaceAll("\\.", "__").trim();
	}
	
}
