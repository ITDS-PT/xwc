package netgest.bo.xwc.framework.cache;

import java.util.Date;

import netgest.bo.xwc.framework.cache.provider.CacheProviderFactory;

public class ExampleUsage {
	
	public CacheEngine getCacheEngine(){
		return CacheProviderFactory.getCacheProvider();
	}

	public void get_content_from_cache(){
		CacheEngine cache = getCacheEngine();
		
		CacheEntry entry = cache.get( "asd.asd.asd" );
		if (entry != null){
			Object content = entry.getContent();
			System.out.println(content);
		}
	}
	
	public void check_cache_validity(){

		CacheEngine cache = getCacheEngine();
		
		CacheEntry entry = cache.get( "asd.asd.asd" );
		if (entry.getExpiredDate().after( new Date(System.currentTimeMillis()) )){
			Object content = entry.getContent();
			cache.add( "asd.asd.asd" , content );
		}
		
		
		
	}
}
