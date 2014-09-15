package com.basus.fins.util;

import static com.basus.fins.PortfolioConstants.*;

import com.basus.fins.PortfolioException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 
 * @author sambit
 *
 */
public class DbUtil {
	private static Connection conn = null;
	
	/**
	 * Establishes a connection to the database specified by <code>url</code>
	 * @param url JDBC url specifying the database to connect
	 * @return Connection object if connection is established
	 * @throws PortfolioException If an error happens or connection cannot be established
	 */
	public static Connection connect(String url) throws PortfolioException {		
		try {
			Class.forName(JDBC_DRIVER_CLASS).newInstance();
			conn = DriverManager.getConnection(url);
		}
		catch(Exception ex) {
			throw new PortfolioException(ex);
		}
		
		if (null == conn) {
			throw new PortfolioException("Cannot obtain Connection object");
		}
		
		return conn;
	}
	
	/**
	 * Returns the connection Object create in the <code>connect()</code> call
	 * @return Connection object
	 */
	public static Connection getConnection() {
		return conn;
	}
	
	public static void shutdown(String url) throws SQLException {
		String sdUrl = url + ";shutdown=true;";
		
		DriverManager.getConnection(sdUrl);
	}
	
	public static void shutdownAll() throws SQLException {
		DriverManager.getConnection("jdbc:derby:;shutdown=true");
	}
}
