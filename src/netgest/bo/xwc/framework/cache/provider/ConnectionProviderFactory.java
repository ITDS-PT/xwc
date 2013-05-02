package netgest.bo.xwc.framework.cache.provider;

import java.util.HashMap;
import java.util.Map;

public class ConnectionProviderFactory  {
	
	public static final String DEFAULT = "default";
	
	private static Map<String,ConnectionProvider> providers = new HashMap< String , ConnectionProvider >();
	
	static {
		providers.put( DEFAULT , new XeoDatabaseConnectionProvider() );
	}
	
	public static void registerProvider(String type, ConnectionProvider provider){
		providers.put( type , provider );
	}
	
	public static ConnectionProvider getConnectionProvider(){
		return providers.get( DEFAULT );
	}
	
	public static ConnectionProvider getConnectionProvider(String type){
		return providers.get( type );
	}
	
}
