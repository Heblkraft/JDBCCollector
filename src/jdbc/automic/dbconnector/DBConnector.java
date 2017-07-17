package jdbc.automic.dbconnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBConnector {

	private Connection conn = null;
    private Statement statement = null;
    private ResultSet resultset = null;
	
    private String host;
    private String databasename;
	private String username;
	private String password;
	
	
	
	
	public DBConnector() throws SQLException {
		System.out.println("DB Connector");
		this.host = host;
		this.databasename = databasename;
		this.username = username;
		this.password = password;
	
		getConnection();
	}
	
	public Connection getConnection() throws SQLException {
		try {
			host = ("jdbc:sqlserver://localhost:1433");
			username = "TestConnection";
	    	password = "123";
	    	databasename = "TestConnector";
		    
		    if(username == "" && password == "") {
		    	conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;DatabaseName=TestConnector;user=TestConnection;password=123;");
		    	statement = conn.createStatement();
		    	resultset = statement.executeQuery("SELECT * FROM testTable");
		    	writeResultSet(resultset);
		    
		    	System.out.println("Connected I to database");
		    	return conn;
		    }
		    
		    else {
		    	conn = DriverManager.getConnection(host+";DatabaseName="+databasename,username,password);
		    	statement = conn.createStatement();
		    	resultset = statement.executeQuery("SELECT * FROM testTable");
		    	writeResultSet(resultset);
		    	
		    	System.out.println("Connected II to database");
		    	return conn;
		    }
		}catch (Exception e) {
			throw e;
		}finally {
			close();
		}
	}
	
	private void writeResultSet(ResultSet resultSet) throws SQLException {
        // ResultSet is initially before the first data set
        while (resultSet.next()) {
            
            String ID = resultSet.getString("ID");
            String name = resultSet.getString("name");
           
            System.out.println("User: " + ID);
            System.out.println("name: " + name);
        }
    }
	
	private void close() {
        try {
            if (resultset != null) {
                resultset.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {

        }
    }

	
}
