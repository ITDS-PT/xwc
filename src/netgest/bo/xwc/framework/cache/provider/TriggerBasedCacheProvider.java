package netgest.bo.xwc.framework.cache.provider;

import java.util.Date;

import netgest.bo.xwc.framework.cache.CacheAPI;
import netgest.bo.xwc.framework.cache.CacheEntry;
import netgest.bo.xwc.framework.cache.time.TimeProvider;
import netgest.bo.xwc.framework.jsf.utils.LRUCache;

public class TriggerBasedCacheProvider implements CacheAPI {
	
	private static LRUCache<String,CacheEntry> cache = new LRUCache<String,CacheEntry>( 100 ); 
	private TimeProvider time;
	
	public TriggerBasedCacheProvider(TimeProvider provider) { 
		this.time = provider;
	}

	
	
	@Override
	public void add(String key, CacheEntry element) {
		cache.put( key , element );
	}

	@Override
	public void invalidate(String key) {
		CacheEntry entry = cache.get( key );
		if (entry != null){
			entry.setExpiredDate( new Date( 100 ) );
		}
	}

	@Override
	public CacheEntry get(String key) {
		CacheEntry object = cache.get( key );
		if (object == null)
			return null;
		if (object.getExpiredDate().after( time.getCurrentTime() )){
			return null;
		}
		return object;
	}

	@Override
	public boolean contains(String key) {
		return cache.contains( key );
	}

	@Override
	public void add(String key, Object entry) {
		
	}
	
	
	
}
