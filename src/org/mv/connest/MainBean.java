package org.mv.connest;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
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
	private ArrayList<Connection> conns;
	
	public ArrayList<Connection> getConns() {
		return conns;
	}

	@PostConstruct
	public void init() {
		conf = new Configuration();
		conns = new ArrayList<>();
	}
	
	// kill last connection
	public void kill() {
		kill(conns.size() - 1);		
	}
	
	// kill all connections
	public void killAll() {
		for(int i = conns.size() -1; i > -1; i--) {
			kill(i);
		}
		conns.clear();
	}
	
	// kill connection by index in connections
	private void kill(int index) {
		if(index > -1) {
			try {
				if(conns.get(index) != null) {
					conns.get(index).close();
					conns.remove(index);				
				}			
				
			} catch(SQLException sqle) {
				System.out.println(sqle.getMessage());
				sqle.printStackTrace();
			}
		}
	}
	
	@PreDestroy
	// cleanup
	public void destroy() {
		conns = null;
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
	
	public void getConn() {
		conns.add(conf.getNewConnection());		
		// insert into db
		//sendTimestamp();
	}
}
