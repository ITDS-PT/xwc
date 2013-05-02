package netgest.bo.xwc.framework.cache.provider;

import java.util.HashMap;
import java.util.Map;

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
		BACK_BEAN
	}
	
	private static Map<CacheType,CacheEngine> engines = new HashMap<CacheType,CacheEngine>();
	
	public static CacheEngine getCacheProvider(){
		if (engines.containsKey( CacheType.TRIGGER_BASED ))
			return engines.get( CacheType.TRIGGER_BASED );
		
		CacheEngine defaultProvider = new TriggerBasedCacheProvider(
				  TimeProviderFactory.getTimeProvider()
				, ConnectionProviderFactory.getConnectionProvider()
		);
		engines.put( CacheType.TRIGGER_BASED , defaultProvider );
		return defaultProvider;
	}
	
	public static CacheEngine getCacheProvider(CacheType type){
		if (engines.containsKey( type ))
			return engines.get( type );
		
		if (CacheType.TRIGGER_BASED == type){
			CacheEngine newProvider = new TriggerBasedCacheProvider( 
					TimeProviderFactory.getTimeProvider()
					, ConnectionProviderFactory.getConnectionProvider() ); 
			engines.put( CacheType.TRIGGER_BASED , newProvider );
			newProvider.init();
			return newProvider;
		}
		
		CacheEngine defaultProvider = new TriggerBasedCacheProvider( 
				TimeProviderFactory.getTimeProvider()
				, ConnectionProviderFactory.getConnectionProvider() );
		defaultProvider.init();
		engines.put( type , defaultProvider );
		return defaultProvider; 
	}
	
	
	
	
}
