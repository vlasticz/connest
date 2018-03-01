package org.mv.connest;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class Configuration {
	
	
	private static String VERSION = "1.0.0";
	private static String URL = "jdbc:mysql://asus-eee:3306/connest";
	private static String USER = "connest";
	private static String PASS = "connest";
	
	private Properties props;
	private Writer writer;
	
	//private RuntimeMxBean runtimeMxBean;
	
	
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
	
	
	public void save() {
		//writer = new FileWriter("");
		
		// get a RuntimeMXBean reference
		RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();

		// get the jvm's input arguments as a list of strings
		List<String> args = new ArrayList<>();
		args = runtimeMxBean.getInputArguments();
		
		// print the arguments using my logger
		for (String arg : args) System.out.println("ARG: " + arg);
		
		
		props = new Properties();
		props.setProperty("url", URL);
		props.setProperty("user", USER);
		props.setProperty("pass", PASS);
		
		
	}
	
	
}
