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
	private StopWatch latencySW;
	private StopWatch totalSW;
	private boolean log = true; // Console logging turned off by default.
	private boolean logDb = true; // Database logging turned on.
		
	// Constructor
	public ConnectionThread() {

		currThread = Thread.currentThread(); 
		latencySW = new StopWatch();
		totalSW = new StopWatch();
		
	}
	
	
	/**
	 * 		RUN
	 */
	public void run() {
		
		// Starting without connection
		
		// Measuring elapsed time
		totalSW.start();
		
		// Main loop start
		while(!terminate) {				
			
			// Main sleep sequence
	        try {
	        		        	
	        	if(conn != null) {
	        		if(!conn.isClosed()) {
	        			//Debug
	        			System.out.println(conn.getClass().toString());
	        			
	        			// If we got a connection
		        		
			        	Thread.sleep(5000);
			        	if(log) System.out.println(currThread.toString() + " running");
				        if(logDb) sendTimestamp("[RUNNING]");
				        
				        // Latency measuring
				        
				        latencySW.start();
				        if(conn.isValid(VALIDATION_TIMEOUT)) {
				        	latencySW.stop();
				        	latency = latencySW.getTime();
				        	latencySW.reset();
				        } else {
				        	latencySW.stop();
				        	latencySW.reset();
				        	latency = -1;
				        }
	        		} else {
	        			terminate = true;
	        		}
	        		
		        }
	        	
	        	if(conn == null) {
		        	// Create connection if can be obtained, if not, will wait until
	        		// a connection is available or will timeout (maxWaitMilis) and quit.
		        	
		        	if((conn = Configuration.getNewConnection()) != null) {
		    			if(log) System.out.println("Connection " + conn.toString() + " created");

		    		} else {		    			
		    			terminate = true;		    			    			
		    		}
		        	
		        }
	        	
		    // SQLException
	        } catch(SQLException sqle) {
	        	sqle.printStackTrace();
	        	
	        // ConfigurationNotLoaded
	        } catch(ConfigurationNotLoadedException cnle) {
	        	cnle.printStackTrace();
	        	terminate = true;
	        	
	        // Interruption
	       	} catch(InterruptedException ie) {
	       		if(log) System.out.println(currThread.toString() + " interrupted");		       		
	       		if(logDb && conn != null) sendTimestamp("[INTERRUPTED]");
	       		terminate = true;
			}
		}
		
		// TODO: put into destroy() and create init()
		totalSW.stop();
		totalSW = null;
		destroy();
				
		
    }
	
	
	@PreDestroy	
	public void destroy() {
				
		// Close connection
		try {					
			if(conn != null) {
				if(!conn.isClosed()) {
					if(logDb) sendTimestamp("[CLOSING]");
					conn.close();
				}
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
			stmt.close();
			
		} catch(Exception e) {			
			e.printStackTrace();
		}		
		
	}
	
	
	// Setters & Getters
	public void setLogDb(Boolean log) {
		logDb = log;
	}
	
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
	
	public long getElapsed() {
		// May be null and asked for result both at exiting.
		if(totalSW != null) {
			return totalSW.getTime();
		} else return -1;
	}
	
}


class ConnectionTimeoutException extends Exception
{
    /**
	* 
	*/
	private static final long serialVersionUID = 1L;

	// Parameterless Constructor
    public ConnectionTimeoutException() {}

    // Constructor that accepts a message
    public ConnectionTimeoutException(String message)
    {
       super(message);
    }
}
