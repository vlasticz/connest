package org.mv.connest;

import java.sql.Connection;
import java.sql.DriverManager;

public class Util {
	
	private String connMsg;
	
	private static String VERSION = "0.01";
	private static String URL = "jdbc:postgresql://localhost:5432/connest";
	private static String USER = "connest";
	private static String PASS = "connest";
	
	
	public Connection getNewConnection() {
		try(Connection c = DriverManager.getConnection(URL, USER, PASS)) {
			connMsg = "Connection " + c.toString() + " established";
			return c;
		} catch(Exception e) {
			e.printStackTrace();
			connMsg = e.getMessage();
			return null;
		}
	}
	
	public static String getVersion() {
		return VERSION;
	}
	
	public String getConnMsg() {
		return connMsg;
	}

}
