package netgest.bo.xwc.framework.cache.provider;

import java.sql.Connection;

/**
 * Provides a DatabaseConnection
 *
 */
public interface ConnectionProvider {
	
	public Connection getConnection();

}
