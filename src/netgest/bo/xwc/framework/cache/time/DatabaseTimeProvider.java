package netgest.bo.xwc.framework.cache.time;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import netgest.bo.data.DriverUtils;
import netgest.bo.xwc.framework.cache.ConnectionCleanup;
import netgest.bo.xwc.framework.cache.provider.ConnectionProvider;

/**
 * 
 * TimeProvider based on a database implementation
 *
 */
public class DatabaseTimeProvider implements TimeProvider {
	
	private ConnectionCleanup cleanup;
	
	private DriverUtils dbUtils;
	private ConnectionProvider databaseProvider;
	
	public DatabaseTimeProvider(DriverUtils utils, ConnectionProvider provider){
		this.cleanup = new ConnectionCleanup();
		this.dbUtils = utils;
		this.databaseProvider = provider;
	}
	
	@Override
	public Date getCurrentTime() {
		Connection conn = databaseProvider.getConnection();
		
		String query = dbUtils.getSelectTimeQuery();
		
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery( query );
			if (rs.next()){
				Timestamp time = rs.getTimestamp( 1 );
				return new Date(time.getTime());
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
		} finally {
			cleanup.closeAll( conn , st , rs );
		}
		
		return null;
	}
	
	
	
}
