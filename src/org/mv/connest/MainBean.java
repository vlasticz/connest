package org.mv.connest;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

@ManagedBean
@ViewScoped
public class MainBean {

	private Configuration conf;
	private ArrayList<ConnectionThread> threads;
	
	
	public ArrayList<ConnectionThread> getThreads() {
		return threads;
	}
	

	@PostConstruct
	public void init() {		
		conf = new Configuration();
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
		if(index > -1) {
			try {
				if(threads.get(index) != null) {
					threads.get(index).getConn().close();
					threads.get(index).interrupt();
					threads.remove(index);				
				}			
				
			} catch(SQLException sqle) {
				System.out.println(sqle.getMessage());
				sqle.printStackTrace();
			}
		}
	}
	
	// Cleanup
	@PreDestroy
	public void destroy() {
		threads = null;
		conf = null;
	}
	
	/*
	private boolean sendTimestamp() {
		
		Calendar calendar = Calendar.getInstance();
		java.sql.Date timestamp = new java.sql.Date(calendar.getTime().getTime());
		
		try {
			// the mysql insert statement
			String query = " insert into users (first_name, last_name, date_created, is_admin, num_points) values (?, ?, ?, ?, ?)";
			// prepared statement
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setDate(1, timestamp);
			stmt.setString(2, "we're here");
			// execute
			stmt.execute();
			return true;
		} catch(Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	      
	}
	*/
	
	public String getVersion () {
		return Configuration.getVersion();
	}
	
	
}