package netgest.bo.xwc.xeo.advancedSearch;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.Logger;
import netgest.bo.system.boApplication;
import netgest.utils.LovUtils;

/**
 * Class that loads (and caches) XEOLovs as Map to be used in AttributeLov components 
 *
 */
public class OperatorMapLoader {

	
	
	/**
	 * Store the maps to cache with operators and stuff
	 */
	private Map<String,Map<String,String>> mapsCache = new HashMap<String, Map<String,String>>();
	
	public OperatorMapLoader(){}
	
	/**
	 * Name of XEO Lov containing operators for the initial row
	 */
	public static final String JOIN_OPERATORS_INITIAL = "op.initial";
	/**
	 * Name of XEO Lov containing operators for every other row (all operators)
	 */
	public static final String JOIN_OPERATORS_ALL = "op.all";
	
	/**
	 * Name of XEO Lov containing operators for a text attribute
	 */
	public static final String VALUE_OPERATORS_TEXT = "adv.text";
	/**
	 * Name of XEO Lov containing operators for a boolean/binary attribute
	 */
	public static final String VALUE_OPERATORS_BOOLEAN = "adv.boolean";
	/**
	 * Name of XEO Lov containing operators for a date/numeric attribute
	 */
	public static final String VALUE_OPERATORS_DATE = "adv.date";
	/**
	 * Name of XEO Lov containing operators for a LongT
	 */
	public static final String VALUE_OPERATORS_LONG_TEXT = "adv.ltext";
	/**
	 * Name of XEO Lov containing all operators
	 */
	public static final String VALUE_OPERATORES_ALL = "adv.all";
	
	
	
	/**
	 * Name of XEO Lov containing operators for object/lov/bridge attributes
	 */
	public static final String VALUE_OPERATORS_OBJ = "adv.obj";
	
	
	/**
	 * 
	 * Retrieve a certain lov as a map (caches the results)
	 * 
	 * @param name The lov name
	 * @return The map representing the lov
	 */
	public Map<String,String> get(String name){
		
		if (mapsCache.containsKey( name ))
			return mapsCache.get( name );
		
		return LovUtils.createMapFromLov( boApplication.currentContext().getEboContext(), name );
		
	}
	
	
}
