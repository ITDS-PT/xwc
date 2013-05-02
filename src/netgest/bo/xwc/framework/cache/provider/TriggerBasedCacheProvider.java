package netgest.bo.xwc.framework.cache.provider;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

import netgest.bo.xwc.framework.cache.CacheElement;
import netgest.bo.xwc.framework.cache.CacheEngine;
import netgest.bo.xwc.framework.cache.CacheEntry;
import netgest.bo.xwc.framework.cache.time.TimeProvider;
import netgest.bo.xwc.framework.cache.time.TimeProviderFactory;
import netgest.bo.xwc.framework.jsf.utils.LRUCache;

public class TriggerBasedCacheProvider implements CacheEngine {
	
	public static final String CACHE_TABLE = "XEO_CACHE_TABLE";
	public static final int DEFAULT_EXPIRE_TIME = 60;
	private static final int CACHE_DEFAULT_SIZE = 100;
	public static final Date EPOCH_TIME = new Date( 0 );
	private static LRUCache<String,CacheEntry> cache = new LRUCache<String,CacheEntry>( CACHE_DEFAULT_SIZE ); 
	private TimeProvider time;
	private ConnectionProvider dbConnectionProvider;
	
	public TriggerBasedCacheProvider(TimeProvider provider, ConnectionProvider dbConnectionProvider) { 
		this.time = provider;
		this.dbConnectionProvider = dbConnectionProvider;
	}
	
	

	@Override
	public void invalidate(String key) {
		CacheEntry entry = cache.get( key );
		if (entry != null){
			//entry.setExpiredDate( EPOCH_TIME );
			//FIXME - Isto assim não vai lá, preciso de remover da Cache?
		}
	}
	
	@Override
	public CacheEntry get(String key) {
		CacheEntry object = cache.get( key );
		if (object == null)
			return CacheElement.NULL_ENTRY;
		return object;
	}

	@Override
	public boolean contains(String key) {
		return cache.contains( key );
	}

	@Override
	public void add(String key, Object entry, int secondsUntilExpire) {
		Date expiresDate = null;
		if (secondsUntilExpire > 0){
			expiresDate = getExpiratioDate( secondsUntilExpire );
			CacheElement newElement = new CacheElement( key , entry , expiresDate, time );
			cache.put( key , newElement );
		} else {
			CacheElement newElement = new CacheElement( key , entry , null, time );
			cache.put( key , newElement );
		}
		
	}
	
	@Override
	public void add(String key, Object entry) {
		Date expiresDate = getExpiratioDate( defaultExpirationTime );
		CacheElement newElement = new CacheElement( key , entry , expiresDate, time );
		cache.put( key , newElement );
	}
	
	private Date getExpiratioDate(int expirationTime){
		Date currentTime = time.getCurrentTime();
		Calendar cal = Calendar.getInstance();
		cal.setTime( currentTime );
		cal.add( Calendar.SECOND , expirationTime );
		Date time = cal.getTime();
		return time;
	}
	
	@Override
	public void add(String key, Object entry, Date expires) {
		CacheElement newElement = new CacheElement( key , entry , expires, time );
		cache.put( key , newElement );
	}

	private int defaultExpirationTime = DEFAULT_EXPIRE_TIME;
	
	@Override
	public void setDefaultExpirationTime(int seconds) {
		this.defaultExpirationTime = seconds;
	}

	@Override
	public void init() {
		//Check for the Table, and create if needed
		Connection conn = dbConnectionProvider.getConnection();
		try {
			ResultSet set = conn.getMetaData().getTables(null, null, CACHE_TABLE, null);
			boolean found = false;
			while (set.next()){
				found = true;
			}
			
			if (!found){
				createInitialTable(conn);
			}
			
			
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
	}

	void createInitialTable(Connection conn) {
		try {
			Statement statement = conn.createStatement();
			statement.execute( "CREATE TABLE " + CACHE_TABLE + " (KEY VARCHAR(50) UNIQUE, date TIMESTAMP);"  );
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
	}



	@Override
	public void setExpiresDate(String key, Date newExpirationDate) {
		
	}

	

	
	
	
	
	
}
