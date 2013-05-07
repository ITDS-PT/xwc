package netgest.bo.xwc.framework.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author PedroRio
 *
 */
public class ConnectionCleanup {
	
	public void closeStatement(Statement statement){
		try {
			if (statement != null) statement.close();
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
	}
	
	public void closeStatementAndResult(Statement statement, ResultSet rs) {
		try {
			if (rs != null) rs.close();
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
		try {
			if (statement != null) statement.close();
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
		
	}

	public void closeAll(Connection db, Statement statement,
			ResultSet rs) {
		try {
			if (rs != null) rs.close();
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
		try {
			if (statement != null) statement.close();
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
		try {
			if (db != null) db.close();
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
	}
	
	
	public void closeConnection(Connection db) {
		try {
			if (db != null) db.close();
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
	}
	
}
