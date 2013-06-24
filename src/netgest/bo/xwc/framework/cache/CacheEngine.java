package netgest.bo.xwc.framework.cache;

/**
 * Provides means to configure a CacheEngine and provide access to the Cache Content (by extending the {@link CacheAPI} interface)
 */
public interface CacheEngine extends CacheAPI {

	/**
	 * 
	 * Set the default expiration date (if changed after being set first time
	 * will only affect new resources)
	 * 
	 * @param seconds The number of seconds for the expiration date
	 */
	public void setDefaultExpirationTime(int seconds);
	
	/**
	 * Initializes the Cache Engine
	 */
	public void init();
	
	
}
