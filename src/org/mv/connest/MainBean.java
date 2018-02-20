package org.mv.connest;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

@ManagedBean
@ViewScoped
public class MainBean {

	// Just held open
	private Connection db;
	private String connMsg;
	private Util util;
	
	@PostConstruct
	public void init() {
		util = new Util();
	}
	
	@PreDestroy
	public void destroy() {
		try {
			if(!db.isClosed()) {
				db.close();				
				if(db.isClosed()) connMsg = "Connection killed";
				db = null;
				util = null;
			}			
		} catch(SQLException sqle) {
			System.out.println(sqle.getMessage());
		}		
	}
	
	
	private boolean sendTimestamp() {
		
		Calendar calendar = Calendar.getInstance();
		java.sql.Date timestamp = new java.sql.Date(calendar.getTime().getTime());
		
		try {
			// the mysql insert statement
			String query = " insert into users (first_name, last_name, date_created, is_admin, num_points) values (?, ?, ?, ?, ?)";
			// prepared statement
			PreparedStatement stmt = db.prepareStatement(query);
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
	
	
	public String getVersion () {
		return Util.getVersion();
	}
	
	public void getConn() {		
		db = util.getNewConnection();
		connMsg = util.getConnMsg();
		sendTimestamp();
	}
	
	public void killConn() {
		destroy();
	}
	
	public String getConnMsg() {
		return connMsg;
	}
	
	public Connection getDb() {
		return db;
	}
}
