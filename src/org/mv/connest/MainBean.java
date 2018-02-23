package org.mv.connest;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class MainBean {
	
	private ArrayList<ConnectionThread> threads;
	
	
	public ArrayList<ConnectionThread> getThreads() {
		return threads;
	}
	

	@PostConstruct
	public void init() {
		threads = new ArrayList<>();
	}
	
	/* THREAD
    public void startThread() {
        if(thread == null) {
    		thread = new ConnectionThread();
    		thread.start();
        }
    }
	*/
	
	
	public void establishConn() {
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
	
	
	public String getVersion () {
		return Configuration.getVersion();
	}
	
	
}