package netgest.bo.xwc.framework.jsf.cache;

import java.sql.Timestamp;

/**
 * 
 * Represents a CacheEntry in the viewer cache
 * 
 * @author PedroRio
 *
 */
public class CacheEntry {

	/**
	 * Cache Entry Id
	 */
	private String id;
	/**
	 * Date of cache entry last update
	 */
	private Timestamp lastUpdateDate;
	/**
	 * The content to cache
	 */
	private Object[] cacheContent;
	
	public CacheEntry(String id, Timestamp lastUpdateDate, Object[] cacheContent) {
		this.id = id;
		this.lastUpdateDate = lastUpdateDate;
		this.cacheContent = cacheContent;
	}
	
	public String getId() {
		return id;
	}
	public Timestamp getLastUpdateDate() {
		return lastUpdateDate;
	}
	public Object[] getCacheContent() {
		return cacheContent;
	}
	
	
	
	
}
