package netgest.bo.xwc.framework.cache.provider;

import netgest.bo.xwc.framework.cache.CacheAPI;
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
		TRIGGER_BASED
	}
	
	public static CacheAPI getCacheProvider(){
		return new TriggerBasedCacheProvider(TimeProviderFactory.getTimeProvider());
	}
	
	public static CacheAPI getCacheProvider(CacheType type){
		if (CacheType.TRIGGER_BASED == type){
			return new TriggerBasedCacheProvider(TimeProviderFactory.getTimeProvider());
		}
		return new TriggerBasedCacheProvider(TimeProviderFactory.getTimeProvider());
	}
	
	
	
	
}
