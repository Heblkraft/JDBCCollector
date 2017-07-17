package jdbc.automic.dbconnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jdbc.automic.restconnector.IRestAction;
import jdbc.automic.restconnector.RestConnector;


public class DBConnector {

	private Connection conn = null;
    private Statement statement = null;
    private ResultSet resultset = null;
	
	private RestConnector restConnector;

	
	public DBConnector(RestConnector restConnector) {
		this.restConnector = restConnector;

		try {
			System.out.println("DB Connector");
			getConnection();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Connection getConnection() throws SQLException {
		try {
			if(conn == null) {
			    String jdbcstring = "jdbc:sqlserver://192.168.216.25:1433;DatabaseName=jdbc_test;user=jdbc_user;password=123;"; // TESTSTRING
		    	return conn = DriverManager.getConnection(jdbcstring);
	    	} else {
	    		return conn;
	    	}
		}catch (Exception e) {
			throw e;
		}finally {
			close();
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