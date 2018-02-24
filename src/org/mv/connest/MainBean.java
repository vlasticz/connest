package org.mv.connest;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;

@ManagedBean
@ApplicationScoped
public class MainBean {
	
	private static final int VALIDATION_TIMEOUT = 10000;
	
	private ArrayList<ConnectionThread> threads;
	
	
	public ArrayList<ConnectionThread> getThreads() {
		return threads;
	}
	

	@PostConstruct
	public void init() {
		threads = new ArrayList<>();
	}
	
	
	// Create connection threads
	public void establishConnections() {
		for(int i = 0; i < 10; i++) {
			establishConnection();
		}
			
	}
	
	public void establishConnection() {
		threads.add(new ConnectionThread());
		threads.get(threads.size() - 1).start();
	}
	
	// Kill last connection
	public void kill() {
		kill(threads.size() - 1);		
	}
	
	// Kill all threads
	public void killAll() {
		for(int i = threads.size() - 1; i > -1; i--) {
			kill(i);
		}
		threads.clear();		
	}
	
	// Kill thread (connection) by index in connections
	private void kill(int index) {
		if(index > -1 && threads.get(index) != null) {						
			threads.get(index).interrupt();					
			threads.remove(index);			
		}
		
	}
	
	// Cleanup
	@PreDestroy
	public void destroy() {
		threads = null;
	}
	
	
	// Getters, setters and stuff
	public int getThreadsCount() {		
		if(threads != null && threads.size() > -1) {
			return threads.size();
		} else return 0;
	}
	
	public int getConnectionsCount() throws SQLException {
		int count = 0;
		for(ConnectionThread thread : threads) {
			if(thread.getConn().isValid(VALIDATION_TIMEOUT)) {
				count++;
			}
		}
		return count;
	}
	
	public String getVersion () {
		return Configuration.getVersion();
	}
	
	
}