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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class Configuration {	
		
	private static final String VERSION = "0.1.1";
	private static final String DATASOURCE_CONTEXT_BASE = "java:comp/env/";
	
	// Command line arguments
	private static final String CONF_PATH_PARAM = "connest.config.path";	
	private static final String DEBUG_PARAM = "connest.debug";
	
	// Properties file
	private static final String CONN_TYPE_PARAM = "datasource.type";
	private static final String DATASOURCE_CONTEXT_PARAM = "datasource.context";		
	private static final String URL_PROPERTY_NAME = "url";
	private static final String USER_PROPERTY_NAME = "user";
	private static final String PASS_PROPERTY_NAME = "pass";
	
	// Inner finals
	private static final String JDBC = "JDBC";
	private static final String JNDI = "JNDI";
	
	private static boolean writeHeader = true;
	private static Properties props;
	
	
	public static Connection getNewConnection() {		
		
		// JDBC
		if(getConnectionType().equals(JDBC)) {
			try {
				return DriverManager.getConnection(props.getProperty(URL_PROPERTY_NAME),
						props.getProperty(USER_PROPERTY_NAME), props.getProperty(PASS_PROPERTY_NAME));			
				
			// Can be anything here
			} catch(Exception e) {
				e.printStackTrace();		
				return null;
			}
			
		}
		
		// JNDI
		if(getConnectionType().equals(JNDI)) {
			try {
				Context ctx = new InitialContext();
			    DataSource ds = (DataSource)ctx.lookup(getDatasourceContext());
			    return ds.getConnection();
			    
			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}
			
		}

		
		// None of the above.
		System.out.println("Connection type not recognized.");
		return null;
		
	}	
	
			
	public static void save() throws FileNotFoundException, IOException {
		
		Properties p = new Properties();		
		p.setProperty("url", props.getProperty(URL_PROPERTY_NAME));		
		p.setProperty("user", props.getProperty(USER_PROPERTY_NAME));		
		p.setProperty("pass", props.getProperty(PASS_PROPERTY_NAME));
		
		if(writeHeader) {
			p.store(new FileOutputStream(getConfigPath()), "Connest " + getVersion() + " default properties file");
		} else {
			p.store(new FileOutputStream(getConfigPath()), "");
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
	
	
	// Getters & setters
	public static boolean isDebug() {
		return Boolean.getBoolean(DEBUG_PARAM); // Works as System.getProperty		
	}
	
	private static String getDatasourceContext() {
			
		// Default values custom handled due to not relevant exceptions
		// when context is defined as "datasource.context=<null>".
		String context = "<none>";
		if(props.getProperty(DATASOURCE_CONTEXT_PARAM) != null && 
				!props.getProperty(DATASOURCE_CONTEXT_PARAM).equals("")) {
			
			context = String.format("%s", DATASOURCE_CONTEXT_BASE +
					props.getProperty(DATASOURCE_CONTEXT_PARAM));			
		}
		return context;		
				
	}
	
	private static String getConfigPath() {		
		return System.getProperty(CONF_PATH_PARAM, System.getProperty("user.home").toString() +
				System.getProperty("file.separator") + "connest.properties");
	}
	
	private static String getConnectionType() {
		// return System.getProperty(CONN_TYPE_PARAM, "JDBC");
		return props.getProperty(CONN_TYPE_PARAM, "JDBC");
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
