package jdbc.automic.dbconnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import jdbc.automic.restconnector.RestConnector;

public class DBConnector {

	private Connection conn = null;
    private Statement statement = null;
    private ResultSet resultset = null;
	private String jdbcstring;
	private RestConnector restConnector;
	
	
	
	public DBConnector(RestConnector restConnector) throws SQLException {
		System.out.println("DB Connector");
		
		
		this.restConnector = restConnector;
		getConnection();
	}
	
	public Connection getConnection() throws SQLException {
		try {
			
		    jdbcstring = "jdbc:sqlserver://localhost:1433;DatabaseName=TestConnector;user=TestConnection;password=123;"; // TESTSTRING
		    conn = DriverManager.getConnection(jdbcstring);
		   	statement = conn.createStatement();
		   	resultset = statement.executeQuery("SELECT * FROM testTable");
		   	restConnector.action(resultset);
		   	
		   	System.out.println("Connected I to database");
	    	return conn;
		    
		   
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
