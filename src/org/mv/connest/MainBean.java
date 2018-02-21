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

	// Just held open
	private Connection conn;
	private String connMsg;
	private Util util;
	private ArrayList<Util> utils = new ArrayList<>();
	
	@PostConstruct
	public void init() {
		util = new Util();
	}
	
	@PreDestroy
	public void destroy() {
		try {
			if(!conn.isClosed()) {
				conn.close();				
				if(conn.isClosed()) addConnMsg("Connection killed");
				conn = null;
				util = null;
			}			
		} catch(SQLException sqle) {
			System.out.println(sqle.getMessage());
		}		
	}
	
	private void addConnMsg(String msg) {
		connMsg = connMsg + "\n" +  msg;
	}
	
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
	
	
	public String getVersion () {
		return Util.getVersion();
	}
	
	public void getConn() {
		// create util
		init();
		conn = util.getNewConnection();
		addConnMsg(util.getConnMsg());
		// insert into db
		//sendTimestamp();
	}
	
	public void killConn() {
		destroy();
	}
	
	public String getConnMsg() {
		return connMsg;
	}
	
	public Connection getDb() {
		return conn;
	}
}
