package jdbc.automic.dbconnector;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.ArrayList;

import jdbc.automic.restconnector.IRestAction;
import jdbc.automic.restconnector.RestConnector;
import static jdbc.automic.configuration.ConfigLoader.config;

public class DBConnector {
	private Connection conn;

    private int lastID = 0;
    private Timestamp lastTimestamp;

	private static final String VIA_ID = ".id";
	private static final String VIA_TIMESTAMP = ".timestamp";

	private static final int ID_START_VALUE = 0;
	private static final String TIMESTAMP_START_VALUE = "2013-03-31 13:13:13.131";

	private final RestConnector restConnector;


	public DBConnector(RestConnector restConnector) {
		try {
            /*---------------Testing---------------------------------------------
            Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
            lastTimestamp = Timestamp.valueOf("2013-03-31 13:13:13.131");
            System.out.println(currentTimeStamp);
            lastTimestampChanged(currentTimeStamp);*/

            initTempFiles();

            if(config.get("increment.id") != null) {
                lastID = Integer.parseInt(Files.readAllLines(Paths.get(VIA_ID)).get(0));
            }
            else if(config.get("increment.timestamp") != null) {
                lastTimestamp = Timestamp.valueOf(Files.readAllLines(Paths.get(".timestamp")).get(0));
            }
		}
		catch(IOException | IndexOutOfBoundsException e) {
			System.err.println("Can not open file or file is empty.");
		}

		this.restConnector = restConnector;

		new MainQueryThread(this).start();

		System.out.println("DB Connector");
	}

	private void initTempFiles(){
		File idFile = new File(VIA_ID);
		File timestampFile = new File(VIA_TIMESTAMP);

		try {

			if(!idFile.exists() && idFile.createNewFile()){
				System.out.println(VIA_ID + " successfully created.");
			}

			if(!timestampFile.exists() && timestampFile.createNewFile()){
				System.out.println(VIA_TIMESTAMP + " successfully created.");
			}

			Files.write(Paths.get(VIA_ID), Integer.toString(ID_START_VALUE).getBytes());
			Files.write(Paths.get(VIA_TIMESTAMP), TIMESTAMP_START_VALUE.getBytes());

		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	private Connection getConnection() {
		try {
			if(conn == null) {
		    	return conn = DriverManager.getConnection(config.get("dbconnection"));
	    	} else {
	    		return conn;
	    	}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ResultSet sendQuery(String query){

		ResultSet resultset = null;
		PreparedStatement ps = null;

		try {

			if(config.get("increment.id") != null){
				query += " WHERE ID > ?";

				ps = getConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				ps.setInt(1, lastID);
			}

			else if (config.get("increment.timestamp") != null){
				query += " WHERE TIMESTAMP > ?";

				ps = getConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				ps.setTimestamp(1,lastTimestamp);
			}

			resultset = ps.executeQuery();
			System.out.println(": " + IRestAction.fetchData(resultset));

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return resultset;
	}

	public void lastIDChanged (int newID) {
		if(lastID < newID) {
			try {
				lastID = newID;
				String content = Integer.toString(lastID);
				writeToFile(".id", content);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public void lastTimestampChanged (Timestamp newTimeStamp) {
		if(lastTimestamp.before(newTimeStamp)) {
			try {
				lastTimestamp = newTimeStamp;
				String content = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS.ms").format(lastTimestamp);
				writeToFile(".timestamp", content);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void writeToFile(String filePath, String content) throws IOException {

		File file = new File(filePath);
		FileOutputStream fos = new FileOutputStream(file);

		if (!file.exists() && file.createNewFile()){
			System.out.println("File successfully created.");
		}

		fos.write(content.getBytes());
		fos.close();

		System.out.println("Done");
	}

	public RestConnector getRestConnector(){
		return this.restConnector;
	}
}