package jdbc.automic.dbconnector;

import java.sql.*;

import jdbc.automic.restconnector.IRestAction;
import jdbc.automic.restconnector.RestConnector;


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
		try {
			query = query + " WHERE ID > ?";
			PreparedStatement ps = getConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ps.setInt(1,lastID);
			resultset = ps.executeQuery();
			IRestAction.fetchData(resultset);
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}


		//resultset = stmt.executeQuery(query + " WHERE ID = ?");

		//resultset = stmt.executeQuery(query + " WHERE TIMESTAMP = ?");
		return null;
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