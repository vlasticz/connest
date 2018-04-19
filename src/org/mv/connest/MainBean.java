package org.mv.connest;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import org.apache.commons.lang3.time.StopWatch;


@ManagedBean
@ApplicationScoped
public class MainBean {
	
	private ArrayList<ConnectionThread> threads;
	// Refresh rate of the main page.
	private int refreshRate;
	// Formatter for date and time on the main page
	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");	
	
	private StopWatch sw1;
	
	private final String LATENCY_MASK = "Latency: %dms";
	private final String ELAPSED_MASK = "Elapsed: %ds";
	
	
	@PostConstruct
	private void init() {
		threads = new ArrayList<>();
		refreshRate = 1; // In seconds.
		sw1 = new StopWatch();
		
		Configuration.printVMParams(); // Print start to console.
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch(ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
		
		loadConfiguration();		
		
	}
	
	
	public String getDebugSidebarVisibility() {
		if(Configuration.isDebug()) {
			return "";
		} else return "invisible";
	}
	
	
	public void loadConfiguration() {
		try {
			Configuration.load();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveConfiguration() {
		try {
			Configuration.save();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// Create connection threads
	public void establishConnections() {
		for(int i = 0; i < 10; i++) {
			establishConnection();
		}
			
	}
	
	public void establishConnection() {
		ConnectionThread ct = new ConnectionThread();
		threads.add(ct);
		// If connection cannot be established it's going to be
		// in terminated state.
		if(!ct.isTerminated()) ct.start();
	}
	
	
	// Cleanup stopped threads
	public void cleanup() {
		if(threads != null) {
			for(int i = threads.size() - 1; i > -1; i--) {
				if(threads.get(i).isTerminated()) threads.remove(i);
			}
		}
	}
	
	
	// Kill last thread
	public void kill() {
		if(threads.size() > 0) {
			kill(threads.get(threads.size() - 1));
		}
	}
	
	// Kill all threads
	public void killAll() {
		for(int i = threads.size() - 1; i > -1; i--) {
			kill(threads.get(i));
		}
		
	}
		
	// Kill thread - the main
	public void kill(ConnectionThread thread) {
		if(thread != null) {			
			thread.interrupt();
		}
		
	}
	
	// Abandon all connections testing method.
	public void abandonAll() {
		for(ConnectionThread ct : threads) {
			ct.setLogDb(false);
		}
	}
	
	
	// StopWatch operations
	
	public String getSW() {
		return String.valueOf(sw1.getTime());
	}
	
	public void startSW() {
		if (sw1.isStarted()) {
			sw1.resume();
		} else {
			sw1.start();
		}
	}
	
	public void stopSW() {
		sw1.stop();
	}
	
	public void clearSW() {
		stopSW();
		sw1.reset();
	}
	
	
	// Getters, setters and stuff
	
	// Echo
	public void echo(String msg) {
		System.out.println(msg);		
	}
	
	
	// Get latency full string returned.
	public String getLatency(ConnectionThread thread) {
		if(thread.getLatency() > -1) {
			return String.format(LATENCY_MASK, thread.getLatency());
		} else {
			return "N/A";
		}
	}
	
	
	// Get locat time
	public String getLocalTime() {		
		return dtf.format(LocalDateTime.now());		
	}
	
	
	// Get total elapsed time.
	public String getElapsed(ConnectionThread thread) {		
		if(thread.getElapsed() > -1) {
			return String.format(ELAPSED_MASK, thread.getElapsed() / 1000);
		} else {
			return "N/A";
		}		
	}
	
	// Thread count.
	public int getThreadsCount() {		
		if(threads != null && threads.size() > -1) {
			return threads.size();
		} else return 0;
	}
	
	// Connection count.
	public int getConnectionsCount() {
		int count = 0;
		if(threads != null) {
			for(ConnectionThread thread : threads) {				
				try {
					if(thread.getConn() != null && !thread.getConn().isClosed() &&
							thread.getConn().isValid(ConnectionThread.VALIDATION_TIMEOUT)) {
						count++;
					}
				} catch(SQLException sqle) {
					sqle.printStackTrace();
				}
			}
		}
		return count;
	}
	
	
	public int getRefreshRate() {
		return refreshRate;
	}
	
	public ArrayList<ConnectionThread> getThreads() {
		return threads;
	}


	public String getVersion () {
		return Configuration.getVersion();
	}
	
	
	// Cleanup
		@PreDestroy
		private void destroy() {
			threads = null;
			System.out.println("Exiting");
		}
	
	
}