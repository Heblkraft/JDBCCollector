package jdbc.automic.dbconnector;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.Clock;

import jdbc.automic.restconnector.IRestAction;
import jdbc.automic.restconnector.RestConnector;
import static jdbc.automic.configuration.ConfigLoader.config;

public class DBConnector {
	private Connection conn = null;
    private Statement statement = null;
    private ResultSet resultset = null;

    private int lastID = 0;
    private Timestamp lastTimestamp = null;
	
	private RestConnector restConnector;
	private MainQueryThread mainQueryThread;

	FileOutputStream fos = null;
	File file;
	String content;
	
	public DBConnector(RestConnector restConnector) {
		try {
			lastID = Integer.parseInt(Files.readAllLines(Paths.get(".id")).get(0));
		}
		catch(IOException | IndexOutOfBoundsException e) {
			e.printStackTrace();
			System.err.println("Can not open file or file is empty.");
		}

		this.restConnector = restConnector;
		this.mainQueryThread = new MainQueryThread(this);
		this.mainQueryThread.start();
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
			if(config.get("increment.id") != null){
				query = query + " WHERE ID > ?";
                PreparedStatement ps = getConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				ps.setInt(1, lastID);
				resultset = ps.executeQuery();

				System.out.println(" : "+IRestAction.fetchData(resultset));
			}
			else if (config.get("increment.timestamp") != null){
				query2 = query2 + " WHERE TIMESTAMP > ?";
				PreparedStatement ps = getConnection().prepareStatement(query2, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				ps.setTimestamp(1,lastTimestamp);
				resultset = ps.executeQuery();

				System.out.println(" : "+IRestAction.fetchData(resultset));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultset;
	}

	public void lastIDChanged (int newID) {
		if(lastID < newID) {
			try {
				lastID = newID;
				content = Integer.toString(lastID);
				file = new File(".id");
				fos = new FileOutputStream(file);

				// if file doesnt exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}

				byte[] contentInBytes = content.getBytes();

				fos.write(contentInBytes);
				fos.flush();
				fos.close();

				System.out.println("Done");

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fos != null) {
						fos.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public RestConnector getRestConnector(){
		return this.restConnector;
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