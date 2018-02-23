package org.mv.connest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

import javax.annotation.PreDestroy;


public class ConnectionThread extends Thread{
	
	private Thread currThread;
	private Connection conn;
		
	// Constructor
	public ConnectionThread() {
		currThread = Thread.currentThread();
		conn = Configuration.getNewConnection();
	}	
		
	
	/**
	 * 		RUN
	 */
	public void run() {
		
		sendTimestamp("[STARTED]");
		
		
		// Main loop start
		while(!currThread.isInterrupted()) {
			
			// Main sleep sequence
	        try {
	        	Thread.sleep(5000);	        	
	        	System.out.println(currThread.toString() + " running");
	        	sendTimestamp("[RUNNING]");
	        	
	        // Interruption
	       	} catch(InterruptedException ie) {	       		
	       		System.out.println(currThread.toString() + " interrupted");
	       		currThread.interrupt();	       		
	        }
		}
		
		sendTimestamp("[STOPED]");
		destroy();
				
		
    }
	
	
	@PreDestroy	
	public void destroy() {
				
		// Close connection
		try {
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
	
}
