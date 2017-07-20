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
import org.apache.log4j.Logger;

import static jdbc.automic.configuration.ConfigLoader.config;

public class DBConnector {
	private final Logger logger = Logger.getLogger(DBConnector.class);
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
			logger.error("Can not open file or file is empty.");
		}

		this.restConnector = restConnector;

		new MainQueryThread(this).start();

		logger.debug("Created DB Connector Instance");
	}

	private void initTempFiles(){
		File idFile = new File(VIA_ID);
		File timestampFile = new File(VIA_TIMESTAMP);

		try {

			if(!idFile.exists() && idFile.createNewFile()){
				logger.info(VIA_ID + " successfully created.");
                Files.write(Paths.get(VIA_ID), Integer.toString(ID_START_VALUE).getBytes());
			}

			if(!timestampFile.exists() && timestampFile.createNewFile()){
				logger.info(VIA_TIMESTAMP + " successfully created.");
                Files.write(Paths.get(VIA_TIMESTAMP), TIMESTAMP_START_VALUE.getBytes());
            }



		} catch (IOException e) {
			logger.error("Can't create File");
			logger.trace("",e);
		}


	}

	private Connection getConnection() {
		try {

			if(conn == null) {
                logger.debug("Building Connection to Database...");
		    	conn = DriverManager.getConnection(config.get("dbconnection"));
		    	logger.debug("Connected to " + conn.getMetaData().getDatabaseProductName());
	    	}
            return conn;
		}catch (Exception e) {
			conn = null;
			logger.info("Lost Connection to Database!");
		}
		return null;
	}

	public ResultSet sendQuery(String query){

		ResultSet resultset = null;
		PreparedStatement ps = null;

		try {

			if(config.get("increment.id") != null){
				query += " WHERE "+config.get("increment.id")+" > ?";

				ps = getConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				ps.setInt(1, lastID);
			}

			else if (config.get("increment.timestamp") != null){
				query += " WHERE TIMESTAMP > ?";

				ps = getConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				ps.setTimestamp(1,lastTimestamp);
			}
			resultset = ps.executeQuery();


		} catch (SQLException e) {
			conn = null;
		}

		return resultset;
	}

	public void lastIDChanged (int newID) {
		if(lastID < newID) {
			try {
				lastID = newID;
				String content = Integer.toString(lastID);
				writeToFile(VIA_ID, content);
			} catch (IOException e) {
				logger.error("Can't write to file .id");
				logger.trace("",e);
			}
		}
	}

	public void lastTimestampChanged (Timestamp newTimeStamp) {
		if(lastTimestamp.before(newTimeStamp)) {
			try {
				lastTimestamp = newTimeStamp;
				String content = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS.ms").format(lastTimestamp);
				writeToFile(VIA_TIMESTAMP, content);

			} catch (IOException e) {
				logger.error("Can't write to file .timestamp");
				logger.trace("",e);
			}
		}
	}

	private void writeToFile(String filePath, String content) throws IOException {

		File file = new File(filePath);
		FileOutputStream fos = new FileOutputStream(file);

		if (!file.exists() && file.createNewFile()){
			logger.info("File successfully created!");
		}

		fos.write(content.getBytes());
		fos.close();

		logger.info("Written to File " + filePath);
	}

	public RestConnector getRestConnector(){
		return this.restConnector;
	}
}