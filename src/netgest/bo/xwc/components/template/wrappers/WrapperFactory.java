package netgest.bo.xwc.components.template.wrappers;

import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.bridgeHandler;

/**
 * Creates wrappers for the various types of
 * XEO elements to use inside templates
 *
 */
public class WrapperFactory {

	/**
	 * 
	 * Wraps a bridge 
	 * 
	 * @param bridge The bridge to wrap
	 * @return A wrapper to use inside a template
	 */
	public static BridgeWrapper wrapBridge(bridgeHandler bridge){
		return new BridgeWrapper( bridge );
	}
	
	/**
	 * 
	 * Wraps an object list 
	 * 
	 * @param list The list to wrap
	 * @return A wrapper to use inside a template
	 */
	public static ListWrapper wrapList(boObjectList list){
		return new ListWrapper( list );
	}
	
	/**
	 * 
	 * Wraps an object 
	 * 
	 * @param object The object to wrap
	 * @return A wrapper to use inside a template
	 */
	public static ObjectWrapper wrapObject(boObject object){
		return new ObjectWrapper( object );
	}
	
}
