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
	 * Date when the content was created
	 * 
	 * @return The date when the content was created
	 * 
	 */
	public Date getDateCreation();
	
	/**
	 * 
	 * Date when the content was last created
	 * 
	 * @return The date of last update
	 */
	public Date getDateLastUpdate();
	
	
	/**
	 * 
	 * Update the date of last update of this element with a new date
	 * 
	 * @param newDateOfLastUpdate 
	 */
	public void updateResource(Date newDateOfLastUpdate);
	
	/**
	 * 
	 * Gets the date when this resource expires its validity in the cache
	 * 
	 * @return The date when the resource expires
	 */
	public Date getExpiredDate();
	
	/**
	 * 
	 * Set a new expired date for the resource
	 * 
	 * @param newDate The expired date
	 */
	public void setExpiredDate(Date newDate);
	
}
