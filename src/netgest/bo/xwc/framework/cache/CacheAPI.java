package netgest.bo.xwc.framework.cache;

import java.util.Date;

/**
 * Cache API for getting and setting objects
 *
 */
public interface CacheAPI {
	
	/**
	 * 
	 * Checks whether or not a given resource is in cache
	 * 
	 * @param key The key to identify the resource
	 * 
	 * @return True if the resource    
	 */
	public boolean contains(final String key);
	
	
	/**
	 * 
	 * Add new resource with default values for dates. Replaces the previous one if it exists
	 * Uses the default expiration configuration from the cache provider
	 * 
	 * @param key The key for the file
	 * @param entry The content to cache
	 * 
	 */
	public void add(final String key, final Object entry);
	
	/**
	 * 
	 * Add new resource with default values for dates. Replaces the previous one if it exists
	 * 
	 * @param key The key for the file
	 * @param entry The content to cache
	 * @param expiresDate Date when the resource expires
	 */
	public void add(final String key, final Object entry, Date expiresDate);
	
	/**
	 * 
	 * Add new resource with default values for dates. Replaces the previous one if it exists
	 * 
	 * @param key The key for the file
	 * @param entry The content to cache
	 * @param secondsUntilExpire Number of seconds until the resource expires
	 */
	public void add(final String key, final Object entry, int secondsUntilExpire);
	
	/**
	 * 
	 * Invalidate the cache for a given key
	 * 
	 * @param key The key to invalidate
	 */
	public void invalidate(final String key);
	
	/**
	 * 
	 * Sets a new expiration date for a given key (if it exists)
	 * 
	 * @param key The key to extend
	 * @param newExpirationDate The date to extend to
	 */
	public void setExpiresDate(final String key, final Date newExpirationDate);
	
	/**
	 * 
	 * Retrieves a cache entry for a given key
	 * 
	 * @param key The key for the cache entry
	 * @return The cache entry if it's in the cache and valid, returns {@link CacheElement#NULL_ENTRY} if the item
	 * is not in cache, you can check existence with the {@link CacheAPI#contains(String)} method 
	 */
	public CacheEntry get(final String key);
	
}
