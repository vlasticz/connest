package org.mv.connest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.commons.lang3.time.StopWatch;

import javax.annotation.PreDestroy;


public class ConnectionThread extends Thread{
	
	public static final int VALIDATION_TIMEOUT = 10000;
	
	private boolean terminate = false; // Telling the thread whether to terminate itself.
	private boolean finished = false; // Whether it's terminated already.
	private Thread currThread;
	private Connection conn;
	private long latency;
	private StopWatch sw;
	private boolean log = true; // Console logging turned off by default.
	private boolean logDb = true; // Database logging turned on.
		
	// Constructor
	public ConnectionThread() {
		currThread = Thread.currentThread();
		sw = new StopWatch();
		
		// Create connection if can be obtained and start thread
		if((conn = Configuration.getNewConnection()) != null) {
			if(log) System.out.println("Connection " + conn.toString() + " created");			
		} else {			
			terminate = true;
			finished = true;
		}
		
	}	
		
	
	/**
	 * 		RUN
	 */
	public void run() {
		
		// Could be terminating due to no db connection.
		if(!terminate && logDb) sendTimestamp("[STARTED]");
		
		
		// Main loop start
		while(!terminate) {						
			
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
		        	sw.stop();
		        	sw.reset();
		        	latency = -1;
		        }
	        	
		    // SQLException
	        } catch(SQLException sqle) {
	        	sqle.printStackTrace();	        	
	        	
	        // Interruption
	       	} catch(InterruptedException ie) {
	       		if(log) System.out.println(currThread.toString() + " interrupted");		       		
	       		if(logDb) sendTimestamp("[INTERRUPTED]");
	       		terminate = true;
	        }
		}
				
		destroy();
				
		
    }
	
	
	@PreDestroy	
	public void destroy() {		
				
		// Close connection
		try {					
			if(conn != null) {				
				if(logDb) sendTimestamp("[CLOSING]");
				conn.close();
				if(log) System.out.println("Connection " + conn.toString() + " closed.");
			}
						
		} catch(SQLException sqle) {
			System.out.println(sqle.getMessage());
		}
		
		finished = true;		
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
				
		try {
			// the mysql insert statement
			String query = "insert into connest.connections (connection, thread, msg) values (?, ?, ?)";
			// prepared statement
			PreparedStatement stmt = conn.prepareStatement(query);
			if(detail) {
				stmt.setString(1, getConn().toString());
				stmt.setString(2, currThread.toString());
			}		
			stmt.setString(3, msg);
			
			// execute
			stmt.execute();
			
		} catch(Exception e) {			
			e.printStackTrace();
		}		
		
	}
	
	
	// Getters
	public Connection getConn() {
		return conn;
	}
	
	public long getLatency() {
		return latency;
	}
	
	public boolean isTerminated() {
		if(terminate && finished) {
			return true;
		} else {
			return false;
		}
	}
	
}
