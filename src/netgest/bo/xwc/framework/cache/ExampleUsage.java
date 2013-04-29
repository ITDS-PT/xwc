package netgest.bo.xwc.framework.cache;

import netgest.bo.xwc.framework.cache.provider.CacheProviderFactory;

public class ExampleUsage {
	

	
	public static void main(String[] args) {

		CacheAPI cache = CacheProviderFactory.getCacheProvider();
		CacheEntry entry = cache.get( "asd.asd.asd" );
		Object content = entry.getContent();
		
		cache.add( "asd.asd.asd" , content );
		
	}
}
