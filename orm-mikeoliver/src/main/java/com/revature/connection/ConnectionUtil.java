package com.revature.connection;

import java.sql.Connection; // JDBC (Java Database Connectivity) - API included in the JRE in the form of the java.sql package that is used to manage a connection
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.log4j.Logger;

import com.revature.data.Persist;

public class ConnectionUtil {
	
//	private static Logger logger = Logger.getLogger(ConnectionUtil.class);
	private static Connection conn = null;
	/**
	 * Singleton class - allows only ONE instance of this class at a time
	 * 
	 * To fetch the instance, you need to call the getInstance() method i.e. getConnection() for this class.
	 */
	
	// the constructor is private to prevent multiple instantiations of the class
	private ConnectionUtil() {
		
		super();
		
	}
	
	
	/*
	 * The following method is designed to return the ONE instance of this class if it exists, or instantiate it if it doesn't
	 */
	public static Connection getConnection(Persist persist) {
		
		try {
			if (conn != null && !conn.isClosed()) {
//				logger.info("Returned reused connection object");
				return conn;
			}
		} catch (SQLException e) {
//			logger.error("Failed to reuse a connection");
			return null;
		}
		
		
		String url = "";
		String username = "";
		String password = "";
		
		try {
			
//			logger.info("Establishing connection to database....");
			System.out.println("Establishing connection to database....");
			
			
			url = persist.url;
			username = persist.username;
			password = persist.pwd;
			
			conn = DriverManager.getConnection(url, username, password);
//			logger.info("Database Connection Established");
			System.out.println("Database Connection Established");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
//			logger.error("SQL Exception thrown - Database connection failed");
			System.out.println("SQL Exception thrown - Database connection failed");
		}
		
		return conn;
		
	}

}