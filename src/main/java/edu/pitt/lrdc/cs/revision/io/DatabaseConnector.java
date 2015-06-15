package edu.pitt.lrdc.cs.revision.io;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSet;
import com.mysql.jdbc.Statement;

public class DatabaseConnector {
	  private Connection connection = null;
	 
	  private String driverName;
	  private String serverAddress;
	  private String databaseName;
	  private String user;
	  private String password;
	  private String port;
	  
	  public void init() {
		 //load properties later
		  driverName = "com.mysql.jdbc.Driver";
		  serverAddress = "sword.lrdc.pitt.edu:3306";
		  //serverAddress = "192.168.56.1";
		  user = "research1";
		  password = "sword8888";
		  port = "3306";
		  databaseName = "arrow";
	  }

	  public void buildConnection() throws SQLException, ClassNotFoundException {
		  Class.forName(driverName);
		  String connectionStr = "";
		  if(driverName.contains("mysql")) {
			  connectionStr = "jdbc:mysql://";
		  }
		  connectionStr += serverAddress;
		  //connectionStr += ":"+port;
		  connectionStr += "/"+databaseName;
		 
		  System.out.println(connectionStr);
		  connection = (Connection)DriverManager.getConnection(connectionStr,user, password);
	  }
	  public void testConnection() throws Exception {
	    try {
	     	buildConnection();    
	     	Statement stmt = (Statement) connection.createStatement();
	     	String sql = "SELECT * FROM assignment";
	     	ResultSet rs = (ResultSet) stmt.executeQuery(sql);
	     	while(rs.next()) {
	     		System.out.println(rs.getInt("assignment_id"));
	     	}
	    } catch (Exception e) {
	      //throw e;
	    	e.printStackTrace();
	    } finally {
	    	
	    }

	  }
	  public static void main(String[] args) throws Exception {
		  DatabaseConnector dc = new DatabaseConnector();
		  dc.init();
		  dc.testConnection();
	  }

}
