package org.mv.connest;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import java.sql.Connection;
import java.sql.SQLException;

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
				db = null;
				util = null;
				if(db.isClosed() || db == null)	connMsg = "Connection killed";
			}			
		} catch(SQLException sqle) {
			sqle.printStackTrace();
		}		
	}
	
	public String getVersion () {
		return Util.getVersion();
	}
	
	public void getConn() {		
		db = util.getNewConnection();
		connMsg = util.getConnMsg();
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
