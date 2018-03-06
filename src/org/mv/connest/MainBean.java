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
			
	private final String LATENCY_MASK = "Latency: %dms";
	
	private ArrayList<ConnectionThread> threads;	
	private int refreshRate;
	
	
	@PostConstruct
	private void init() {
		threads = new ArrayList<>();
		refreshRate = 1; // In seconds
		
	}
	
	
	// Create connection threads
	public void establishConnections() {
		for(int i = 0; i < 10; i++) {
			establishConnection();
		}
			
	}
	
	public void establishConnection() {
		threads.add(new ConnectionThread());			
	}
	
	
	// Cleanup stopped threads
	public void cleanup() {
		for(int i = threads.size() - 1; i > -1; i--) {
			if(!threads.get(i).isAlive()) threads.remove(i);
		}
	}
	
	
	// Kill last thread
	public void kill() {
		kill(threads.get(threads.size() - 1));
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
			threads.remove(thread);
		}
		
	}
	
	
	// Echo
	public void echo(String msg) {
		System.out.println(msg);		
	}
	
	
	// Cleanup
	@PreDestroy
	private void destroy() {
		threads = null;
	}
	
	
	// Getters, setters and stuff
	public int getRefreshRate() {
		return refreshRate;
	}
	
	public String getLatency(ConnectionThread thread) {
		if(thread.getLatency() > -1) {
			return String.format(LATENCY_MASK, thread.getLatency());
		} else {
			return "N/A";
		}
	}
	
	public int getThreadsCount() {		
		if(threads != null && threads.size() > -1) {
			return threads.size();
		} else return 0;
	}
	
	public int getConnectionsCount() throws SQLException {
		int count = 0;
		for(ConnectionThread thread : threads) {
			
			if(thread.getConn() != null) {
				if(thread.getConn().isValid(ConnectionThread.VALIDATION_TIMEOUT)) {
					count++;
				}
			}
		}
		return count;
	}
	
	public ArrayList<ConnectionThread> getThreads() {
		return threads;
	}
	
	public String getVersion () {
		return Configuration.getVersion();
	}
	
	
}