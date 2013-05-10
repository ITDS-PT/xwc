package netgest.bo.xwc.framework.cache;

/**
 * 
 * Represents a Key for the cache engine
 * 
 * @author PedroRio
 * 
 */
public class CacheKey {
	
	private CacheKey child = null;
	private String topKey = null; 

	public static CacheKey toKey(String key){
		return new CacheKey( key );
	}
	
	public CacheKey(String key){
		
		if (key == null || key.length() == 0)
			throw new IllegalArgumentException( "Argument cannot be empty or null" );
		
		if (key.contains( "." )){
			topKey = key.substring( 0, key.indexOf( "." )  );
			child = new CacheKey( key.substring( key.indexOf( "." ) + 1 ,  key.length() ) );
		} else {
			topKey = key;
			child = null;		}
	}
	
	public String getComponent(){
		return topKey;
	}
	
	public CacheKey getChild(){
		return child;
	}
	
	public String seriazize(){
		return topKey;
	}
	
	
	
	
}
