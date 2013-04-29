package netgest.bo.xwc.framework.cache;

/**
 * Cache API for getting and setting objects
 *
 */
public interface CacheAPI {

	public boolean contains(final String key);
	
	public void add(final String key, final CacheEntry entry);
	
	public void add(final String key, final Object entry);
	
	public void invalidate(final String key);
	
	/**
	 * 
	 * Retrieves a cache entry for a given key
	 * 
	 * @param key The key for the cache entry
	 * @return The cache entry if it's in the cache and valid, returns null if the item
	 * is not in cache or if it's been invalidated
	 */
	public CacheEntry get(final String key);
	
}
