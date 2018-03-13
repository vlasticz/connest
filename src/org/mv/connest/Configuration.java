package org.mv.connest;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class Configuration {
	
	
	private static String VERSION = "0.0.0";
	private static String URL = "jdbc:mysql://46.101.240.246:3306/connest";
	private static String USER = "connest";
	private static String PASS = "connest";
	
	private Properties props;
	private Writer writer;
	
	//private RuntimeMxBean runtimeMxBean;
	
	
	public static Connection getNewConnection() {
		try {
			Connection c = DriverManager.getConnection(URL, USER, PASS);			
			return c;
			
		// Can be anything here
		} catch(Exception e) {
			e.printStackTrace();		
			return null;
		}
	}	
	
	
	public static boolean canGetNewConnection() {
		Connection c = getNewConnection();
		
		if(c != null) {
			try {
				c.close();
			} catch(SQLException sqle) {
				sqle.printStackTrace();
			}
			return true;
			
		} else {
			return false;
		}
	}
	
	public static String getVersion() {
		return VERSION;
	}
	
	public static String getURL() {
		return URL;
	}
	
	
	public static void save() {
		//writer = new FileWriter("");
		
		// get a RuntimeMXBean reference
		RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();

		// get the jvm's input arguments as a list of strings
		List<String> args = new ArrayList<>();
		args = runtimeMxBean.getInputArguments();
		
		// print the arguments using my logger
		for (String arg : args) System.out.println("ARG: " + arg);
		
		
		/*
		props = new Properties();
		props.setProperty("url", URL);
		props.setProperty("user", USER);
		props.setProperty("pass", PASS);
		*/
		
	}
	
	
}
