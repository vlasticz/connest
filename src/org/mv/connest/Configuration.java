package org.mv.connest;

import java.sql.Connection;
import java.sql.DriverManager;

public class Configuration {
	
	private static String VERSION = "1.00";
	private static String URL = "jdbc:mysql://asus-eee:3306/connest";
	private static String USER = "connest";
	private static String PASS = "connest";
	
	
	public static Connection getNewConnection() {
		try {
			Connection c = DriverManager.getConnection(URL, USER, PASS);			
			return c;
		} catch(Exception e) {
			e.printStackTrace();			
			return null;
		}
	}	
	
	public static String getVersion() {
		return VERSION;
	}

}
