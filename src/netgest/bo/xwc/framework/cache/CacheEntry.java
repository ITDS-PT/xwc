package netgest.bo.xwc.framework.cache;

import java.util.Date;

/**
 * 
 * Represents a entry in the cache mechanism
 * 
 *
 */
public interface CacheEntry {
	
	
	/**
	 * 
	 * Key representing the cache element
	 * 
	 * @return a string representing the key
	 */
	public String getKey();
	
	/**
	 * 
	 * The Cache content
	 * 
	 * @return The cache content
	 * 
	 */
	public Object getContent();
	
	
	/**
	 * 
	 * Date when the content was added to the cache
	 * 
	 * @return The date when the content was added to the cache
	 * 
	 */
	public Date getDateAdded();
	
	/**
	 * 
	 * Gets the date when this resource expires its validity in the cache
	 * 
	 * @return The date when the resource expires
	 */
	public Date getExpiredDate();
	
	/**
	 * 
	 * Whether or not the resource is expired
	 * 
	 * @return True if the resource is expired and false otherwise
	 */
	public boolean isExpired();
	
}
