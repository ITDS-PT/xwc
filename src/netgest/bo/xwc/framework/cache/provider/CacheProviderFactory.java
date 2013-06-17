package netgest.bo.xwc.framework.cache.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import netgest.bo.xwc.framework.cache.CacheEngine;
import netgest.bo.xwc.framework.cache.time.TimeProviderFactory;

/**
 * 
 * Factory to get CacheProviders
 * 
 * @author PedroRio
 *
 */
public class CacheProviderFactory {
	
	public enum CacheType{
		TRIGGER_BASED,
		MEMORY_BASED
	}
	
	private static Map<String,CacheEngine> engines = new HashMap<String,CacheEngine>();
	
	public static synchronized CacheEngine getCacheProvider(){
		if (engines.containsKey( CacheType.TRIGGER_BASED.name() ))
			return engines.get( CacheType.TRIGGER_BASED.name() );
		
		CacheEngine defaultProvider = new TriggerBasedCacheProvider(
				  TimeProviderFactory.getTimeProvider()
				, ConnectionProviderFactory.getConnectionProvider()
		);
		defaultProvider.init();
		engines.put( CacheType.TRIGGER_BASED.name() , defaultProvider );
		return defaultProvider;
	}
	
	public static synchronized void registerCacheProvider(CacheType type, CacheEngine engine){
		engines.put( type.name() , engine );
	}
	
	public static synchronized void registerCacheProvider(String type, CacheEngine engine){
		engines.put( type , engine );
	}
	
	public static Set<String> getListOfEngines(){
		return engines.keySet();
	}
	
	public static synchronized CacheEngine getCacheProvider(CacheType type){
		if (engines.containsKey( type.name() ))
			return engines.get( type.name() );
		
		if (CacheType.TRIGGER_BASED == type){
			CacheEngine newProvider = new TriggerBasedCacheProvider( 
					  TimeProviderFactory.getTimeProvider( TimeProviderFactory.DATABASE )
					, ConnectionProviderFactory.getConnectionProvider( ) ); 
			newProvider.init();
			engines.put( CacheType.TRIGGER_BASED.name() , newProvider );
			return newProvider;
		}
		
		CacheEngine defaultProvider = new TriggerBasedCacheProvider( 
				TimeProviderFactory.getTimeProvider()
				, ConnectionProviderFactory.getConnectionProvider() );
		defaultProvider.init();
		engines.put( type.name() , defaultProvider );
		return defaultProvider; 
	}
	
	
	
	
}
