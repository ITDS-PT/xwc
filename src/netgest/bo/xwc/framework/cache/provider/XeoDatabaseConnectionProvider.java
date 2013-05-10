package netgest.bo.xwc.framework.cache.provider;

import java.sql.Connection;

import netgest.bo.system.boApplication;

public class XeoDatabaseConnectionProvider implements ConnectionProvider {

	@Override
	public Connection getConnection() {
		return boApplication.currentContext().getEboContext().getConnectionData();
	}
	
}
