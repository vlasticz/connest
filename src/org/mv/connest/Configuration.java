package org.mv.connest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Configuration {	
		
	private static final String VERSION = "0.1.1";
	private static final String CONF_PATH_PARAM = "connest.config.path";
	private static final String URL_PROPERTY_NAME = "url";
	private static final String USER_PROPERTY_NAME = "user";
	private static final String PASS_PROPERTY_NAME = "pass";
	
	private static boolean writeHeader = true;
	private static Properties props;
	
	
	public static Connection getNewConnection() {
		try {
			Connection c = DriverManager.getConnection(props.getProperty(URL_PROPERTY_NAME),
					props.getProperty(USER_PROPERTY_NAME), props.getProperty(PASS_PROPERTY_NAME));			
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
		
		
	public static void save() throws FileNotFoundException, IOException {
		
		Properties p = new Properties();		
		p.setProperty("url", props.getProperty(URL_PROPERTY_NAME));		
		p.setProperty("user", props.getProperty(USER_PROPERTY_NAME));		
		p.setProperty("pass", props.getProperty(PASS_PROPERTY_NAME));
		
		if(writeHeader) {
			p.store(new FileOutputStream(getConfigPath()), "Connest " + getVersion() + " default properties file");
		} else {
			p.store(new FileOutputStream(getConfigPath()), null);
		}
		
	}
	
	public static void load() {
		
		try(InputStream is = new FileInputStream(getConfigPath())) {
			props = new Properties();
			props.load(is);
			System.out.println("Properties loaded - " + props.toString());
			
		} catch(Exception e) {
			System.out.println("Properties not loaded.");
			e.printStackTrace();
		}
		
	}
		
	
	public static void printVMParams() {		
		// get a RuntimeMXBean reference
		RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();

		// get the jvm's input arguments as a list of strings
		List<String> args = new ArrayList<>();
		args = runtimeMxBean.getInputArguments();
		
		// print the arguments using my logger
		for (String arg : args) System.out.println("ARG: " + arg);
		System.out.println("Properties file: " + getConfigPath());
	}
	
	
	private static String getConfigPath() {		
		return System.getProperty(CONF_PATH_PARAM);
	}
	
	public static String getVersion() {
		return VERSION;
	}
	
	public static String getURL() {
		return props.getProperty("url");
	}
	
	public static Properties getProperties() {
		return props;
	}
	
	
}
