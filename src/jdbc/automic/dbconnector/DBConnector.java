package jdbc.automic.dbconnector;

import java.sql.*;

import jdbc.automic.restconnector.IRestAction;
import jdbc.automic.restconnector.RestConnector;
import static jdbc.automic.configuration.ConfigLoader.config;

public class DBConnector {
	private Connection conn = null;
    private Statement statement = null;
    private ResultSet resultset = null;

    private int lastID = 3;
    private Timestamp lastTimestamp = null;
	
	private RestConnector restConnector;
	private MainQueryThread mainQueryThread;
	
	public DBConnector(RestConnector restConnector) {
		this.restConnector = restConnector;
		this.mainQueryThread = new MainQueryThread(this);
		sendQuery(MainQueryThread.QUERY);
		System.out.println("DB Connector");
	}
	
	public Connection getConnection() {
		try {
			if(conn == null) {
			    String jdbcstring = "jdbc:sqlserver://192.168.216.33:1433;DatabaseName=jdbc_test;user=jdbc_user;password=123;"; // TESTSTRING
		    	return conn = DriverManager.getConnection(jdbcstring);
	    	} else {
	    		return conn;
	    	}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ResultSet sendQuery(String query){
		String query2 = query;
		try {
			if(config.get("incremenet.id") != null){
				query = query + " WHERE ID > ?";
                PreparedStatement ps = getConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				ps.setInt(1, lastID);
				resultset = ps.executeQuery();
                IRestAction.fetchData(resultset);
			}
			else if (config.get("increment.timestamp") != null){
				query2 = query2 + " WHERE TIMESTAMP > ?";
				PreparedStatement ps = getConnection().prepareStatement(query2, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				ps.setTimestamp(1,lastTimestamp);
				resultset = ps.executeQuery();
                IRestAction.fetchData(resultset);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultset;
	}

	private boolean isEmpty(ResultSet resultSet){
		boolean returnvalue = false;
		try {
			if(!resultSet.next()){
				returnvalue= true;
			}
			resultSet.beforeFirst();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnvalue;
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