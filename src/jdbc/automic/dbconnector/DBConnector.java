package jdbc.automic.dbconnector;

import java.sql.*;

import jdbc.automic.restconnector.IRestAction;
import jdbc.automic.restconnector.RestConnector;


public class DBConnector {

	private Connection conn = null;
    private Statement statement = null;
    private ResultSet resultset = null;

    private int lastID;
    private Timestamp lastTimestamp = null;
	
	private RestConnector restConnector;
	private MainQueryThread mainQueryThread;

	
	public DBConnector(RestConnector restConnector) {
		this.restConnector = restConnector;
		this.mainQueryThread = new MainQueryThread(this);
		System.out.println("DB Connector");
	}
	
	public Connection getConnection() {
		try {
			if(conn == null) {
			    String jdbcstring = "jdbc:sqlserver://192.168.216.25:1433;DatabaseName=jdbc_test;user=jdbc_user;password=123;"; // TESTSTRING
		    	return conn = DriverManager.getConnection(jdbcstring);
	    	} else {
	    		return conn;
	    	}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			close();
		}
		return null;
	}

	public ResultSet sendQuery(String query){
		try {
			PrepareStatement ps = getConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		resultset = stmt.executeQuery(query + " WHERE ID = ?");

		resultset = stmt.executeQuery(query + " WHERE TIMESTAMP = ?");
		return null;
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