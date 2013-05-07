package netgest.bo.xwc.framework.cache.time;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import netgest.bo.data.DriverUtils;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.framework.cache.provider.ConnectionProvider;
import netgest.bo.xwc.framework.cache.provider.ConnectionProviderFactory;

public class TimeProviderFactory {
	
	public static final String DEFAULT = "default";
	public static final String DATABASE = "database";
	
	private static final Map<String,TimeProvider> timeProviders = new HashMap< String , TimeProvider >();
	
	public void registerTimeProvider( String time, TimeProvider provider ){
		timeProviders.put( time , provider );
	}
	
	public static TimeProvider getTimeProvider(String type){
		if (timeProviders.containsKey( type ))
			return timeProviders.get( type );
		
		if (DEFAULT.equals( type )){
			timeProviders.put( DEFAULT , new DefaultTimeProvider() );
		}
		
		if (DATABASE.equals( type ) ){
			DriverUtils utils = boApplication.currentContext().getEboContext().getDataBaseDriver().getDriverUtils();
			ConnectionProvider provider = ConnectionProviderFactory.getConnectionProvider();
			timeProviders.put( DATABASE , new DatabaseTimeProvider( utils, provider ) );
		}
		
		return timeProviders.get( type );
	}
	
	public static TimeProvider getTimeProvider(){
		return timeProviders.get( DEFAULT );
	}
	
	/**
	 * Time provider based on the application time
	 *
	 */
	private static class DefaultTimeProvider implements TimeProvider {

		@Override
		public Date getCurrentTime() {
			return new Date(System.currentTimeMillis());
		}
		
	}
}
