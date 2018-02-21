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
	private ArrayList<Connection> conns = new ArrayList<>();
	
	@PostConstruct
	public void init() {
		util = new Util();		
	}
	
	private void kill(int index) {
		if(index > -1) {
			try {
				if(conns.get(index) != null) {
					conns.get(index).close();
					if(conns.get(index).isClosed()) addConnMsg("Connection killed");
					conns.remove(index);				
				}			
				
			} catch(SQLException sqle) {
				System.out.println(sqle.getMessage());
				sqle.printStackTrace();
			}
		}
	}
	
	@PreDestroy
	public void destroy() {
		try {
			if(!conn.isClosed()) {
				conn.close();				
				if(conn.isClosed()) addConnMsg("Connection dastroyed");
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
		conns.add(util.getNewConnection());
		addConnMsg(util.getConnMsg());
		// insert into db
		//sendTimestamp();
	}
	
	public void killConn() {
		if(conns.size() >= 0) {
			kill(conns.size() - 1);
		}
	}
	
	public String getConnMsg() {
		return connMsg;
	}
	
	public Connection getDb() {
		return conn;
	}
}
