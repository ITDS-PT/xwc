package netgest.bo.xwc.framework.cache.provider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;

import netgest.bo.xwc.framework.cache.CacheElement;
import netgest.bo.xwc.framework.cache.CacheEngine;
import netgest.bo.xwc.framework.cache.CacheEntry;
import netgest.bo.xwc.framework.cache.ConnectionCleanup;
import netgest.bo.xwc.framework.cache.time.TimeProvider;


/**
 * 
 * Implementation of a cache engine that relies on a database table 
 *
 */
public class TriggerBasedCacheProvider implements CacheEngine {
	
	/**
	 * Name of the table to contain the cache information 
	 */
	public static final String CACHE_TABLE_NAME = "XEO_CACHE_TABLE";
	/**
	 * Name of the column representing the key
	 */
	public static final String CACHE_TABLE_KEY_COLUMN = "\"KEY\"";
	/**
	 * Name of the column representing the date
	 */
	public static final String CACHE_TABLE_DATE_COLUMN = "LAST_UPDATE";
	
	/**
	 * The default time to expire when none is provided (in seconds)
	 */
	public static final int DEFAULT_EXPIRE_TIME = 60;
	
	/**
	 * Default cache size 
	 */
	private static final int DEFAULT_CACHE_SIZE = 100;

	/**
	 * Default max cache size in bytes 
	 */
	private static final int DEFAULT_CACHE_MAX_MEM = 40000000;
	
	/**
	 * The cache
	 */
	private static LRUCache<String,CacheEntry> cache;// = new LRUCache<String,CacheEntry>( CACHE_DEFAULT_SIZE ); 
	/**
	 * Provider for the Time
	 */
	private TimeProvider time;
	/**
	 * Provider for a Database Connection
	 */
	private ConnectionProvider dbConnectionProvider;
	/**
	 * A Database stuff cleaner
	 */
	private ConnectionCleanup cleanup;
	
	private int maxCacheSize;
	
	private int maxCacheItens;
	
	private int actualSize;
	
	public TriggerBasedCacheProvider(TimeProvider provider, ConnectionProvider dbConnectionProvider, int maxCacheBytes, int maxCacheItens) {
		
		// Not implemented
		this.maxCacheSize = maxCacheBytes < 0 ? DEFAULT_CACHE_MAX_MEM : maxCacheBytes;
		
		this.maxCacheItens = maxCacheItens < 0 ? DEFAULT_CACHE_SIZE : maxCacheItens;
		
		cache = new LRUCache<String, CacheEntry>( this.maxCacheItens );
		
		this.time = provider;
		this.dbConnectionProvider = dbConnectionProvider;
		cleanup = new ConnectionCleanup();
	}
	
	

	@Override
	public void invalidate(String key) {
		CacheEntry entry = cache.get( key );
		if (entry != null){
			Connection conn = this.dbConnectionProvider.getConnection();
			PreparedStatement deleteKeyStatement = null;
			PreparedStatement deleteOtherKeysStatement = null;
			try {
				 deleteKeyStatement = conn.prepareStatement( String.format( "DELETE FROM %s WHERE \"KEY\" = ?", CACHE_TABLE_NAME )  );
				 deleteOtherKeysStatement = conn.prepareStatement( String.format( "DELETE FROM %s WHERE \"KEY\" LIKE ?", CACHE_TABLE_NAME )  );
				 deleteKeyStatement.setString( 1 , key );
				 deleteOtherKeysStatement.setString( 1 , key + ".%" );
				 deleteKeyStatement.execute();
				 deleteOtherKeysStatement.execute();
				 cache.removeKeysMatching( key );
			} catch ( SQLException e ) {
				e.printStackTrace();
			} finally {
				cleanup.closeStatement( deleteKeyStatement );
				cleanup.closeStatement( deleteOtherKeysStatement );
			}
		}
	}
	
	@Override
	public CacheEntry get(String key) {
		CacheEntry object = cache.get( key );
		if (object == null)
			return CacheElement.NULL_ENTRY;
		
		TimePresence cacheElementDate = getDateForKey(key);
		if (cacheElementDate.present){
			if (cacheElementDate.value != null){
				if ( cacheElementDate.value.compareTo( time.getCurrentTime() ) <= 0 ){
					return CacheElement.NULL_ENTRY; 
				}
			}
		}
		return object;
	}

	private class TimePresence {
		
		private boolean present;
		private Timestamp value;
		
		public TimePresence(boolean present, Timestamp value) {
			this.present = present;
			this.value = value;
		}
		
		
	}
	
	private TimePresence getDateForKey(String key) {
		Connection con = dbConnectionProvider.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement( String.format( "SELECT * FROM %S WHERE \"KEY\" = ?" , CACHE_TABLE_NAME ) );
			ps.setString( 1 , key );
			rs = ps.executeQuery();
			if (rs.next()){
				Timestamp date = rs.getTimestamp( CACHE_TABLE_DATE_COLUMN );
				return new TimePresence( true , date );
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
		} finally {
			cleanup.closeAll( con , ps , rs );
		}
		return new TimePresence( false , null );
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
			addToTableAndCache( key , entry , expiresDate );
		} else {
			addToTableAndCache( key , entry , null );
		}
		
	}
	
	private void addToTableAndCache(String key, Object entry, Date expiresDate){
		CacheElement newElement = new CacheElement( key , entry , expiresDate, time );
		Connection con = dbConnectionProvider.getConnection();
		if (!isKeyInStore( con , key ))
			putElementInTable( con, newElement );
		else
			updateElementInTable( con, newElement );
		
		cleanup.closeConnection( con );
		
		cache.put( key , newElement );
	}
	
	@Override
	public void add(String key, Object entry) {
		Date expiresDate = getExpiratioDate( defaultExpirationTime );
		addToTableAndCache( key , entry , expiresDate );
	}
	
	
	void updateElementInTable(Connection con, CacheElement newElement) {
		PreparedStatement s = null;
		try {
			con = this.dbConnectionProvider.getConnection();
			s = con.prepareStatement(
					String.format("UPDATE %s SET %s = ? WHERE %s = ?",CACHE_TABLE_NAME, CACHE_TABLE_DATE_COLUMN, CACHE_TABLE_KEY_COLUMN ) );
			if ( newElement.getExpiredDate() != null)
				s.setTimestamp( 1 , new Timestamp( newElement.getExpiredDate().getTime() ) );
			else
				s.setNull( 1 , Types.TIMESTAMP );
			s.setString( 2 , newElement.getKey() );
			s.execute();
		} catch ( SQLException e ) {
			e.printStackTrace();
		} finally {
			cleanup.closeStatement( s);
		}
	}



	protected boolean isKeyInStore(Connection con, String key){
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement( "SELECT \"KEY\" FROM " + CACHE_TABLE_NAME + " WHERE \"KEY\" = ?" );
			st.setString( 1 , key );
			rs = st.executeQuery();
			if (!rs.next() ) {
				return false;
			} else
				return true;
		} catch ( SQLException e ) {
			e.printStackTrace();
		} finally {
			cleanup.closeStatementAndResult( st , rs );
		}
		return false;
		
	}
	
	
	
	void putElementInTable(Connection con, CacheElement newElement) {
		PreparedStatement s = null;
		try {
			con = this.dbConnectionProvider.getConnection();
			s = con.prepareStatement( String.format("INSERT INTO %s VALUES(?,?)",CACHE_TABLE_NAME) );
			s.setString( 1 , newElement.getKey() );
			if ( newElement.getExpiredDate() == null)
				s.setNull( 2 , java.sql.Types.TIMESTAMP );
			else
				s.setTimestamp( 2 , new Timestamp( newElement.getExpiredDate().getTime() ) );
			s.execute();
		} catch ( SQLException e ) {
			throw new RuntimeException( String.format("Attempting to put %s with content %s ",newElement.getKey(), String.valueOf( newElement.getContent() ) ), e );
		} finally {
			cleanup.closeStatement( s );
		}
		
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
		addToTableAndCache( key , entry , expires );
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
			ResultSet set = conn.getMetaData().getTables(null, null, CACHE_TABLE_NAME, null);
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
			statement.execute( "CREATE TABLE " + CACHE_TABLE_NAME + " (\"KEY\" VARCHAR(50) UNIQUE, LAST_UPDATE TIMESTAMP)"  );
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
	}



	@Override
	public void setExpiresDate(String key, Date newExpirationDate) {
		
	}

	

	
	
	
	
	
}
