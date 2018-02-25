package org.mv.connest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import org.apache.commons.lang3.time.StopWatch;

import javax.annotation.PreDestroy;


public class ConnectionThread extends Thread{
	
	public static final int VALIDATION_TIMEOUT = 10000;
	
	private Thread currThread;
	private Connection conn;
	private long latency;
	private StopWatch sw;
	private boolean log = false; // Console logging turned off by default.
	private boolean logDb = true; // Database logging turned on.
		
	// Constructor
	public ConnectionThread() {
		currThread = Thread.currentThread();
		conn = Configuration.getNewConnection();
		sw = new StopWatch();
		if(log) System.out.println("Connection " + conn.toString() + " created");
	}	
		
	
	/**
	 * 		RUN
	 */
	public void run() {
		
		if(logDb) sendTimestamp("[STARTED]");
		
		
		// Main loop start
		while(!currThread.isInterrupted()) {
						
			
			// Main sleep sequence
	        try {
	        	Thread.sleep(5000);
	        	if(log) System.out.println(currThread.toString() + " running");
		        if(logDb) sendTimestamp("[RUNNING]");

		        // Latency measuring		        
		        sw.start();
		        if(conn.isValid(VALIDATION_TIMEOUT)) {
		        	sw.stop();
		        	latency = sw.getTime();
		        	sw.reset();
		        } else {
		        	latency = -1;
		        }
	        	
		    // SQLException
	        } catch(SQLException sqle) {
	        	System.out.println(sqle.getMessage());
	        	
	        // Interruption
	       	} catch(InterruptedException ie) {
	       		if(log) System.out.println(currThread.toString() + " interrupted");		       		
	       		if(logDb) sendTimestamp("[INTERRUPTED]");
	       		currThread.interrupt();
	        }
		}
				
		destroy();
				
		
    }
	
	
	@PreDestroy	
	public void destroy() {
				
		// Close connection
		try {			
			if(log) System.out.println("Connection " + conn.toString() + " closed.");
			if(logDb) sendTimestamp("[STOPED]");
			conn.close();	
						
		} catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	
	/**
	 * SEND TIMESTAMP
	 */
	@SuppressWarnings("unused")
	private void sendTimestamp() {
		sendTimestamp(null);
	}
	
	private void sendTimestamp(String msg) {
		sendTimestamp(msg, true); // Add info is on by default.
		
	}
	
	private void sendTimestamp(String msg, Boolean detail) {
		
		Calendar calendar = Calendar.getInstance();
		java.sql.Date timestamp = new java.sql.Date(calendar.getTime().getTime());
		
		try {
			// the mysql insert statement
			String query = " insert into connections (timestamp, connection, thread, msg) values (?, ?, ?, ?)";
			// prepared statement
			PreparedStatement stmt = conn.prepareStatement(query);
			if(detail) {
				stmt.setString(2, getConn().toString());
				stmt.setString(3, currThread.toString());
			}
			stmt.setDate(1, timestamp);			
			stmt.setString(4, msg);
			
			// execute
			stmt.execute();
			
		} catch(Exception e) {
			System.out.println(e.getMessage());			
		}		
		
	}
	
	
	// Getters
	public Connection getConn() {
		return conn;
	}
	
	public long getLatency() {
		return latency;
	}
	
}
