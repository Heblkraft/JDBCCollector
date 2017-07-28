package jdbc.automic.dbconnector;
import jdbc.automic.restconnector.RestConnector;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

import static jdbc.automic.configuration.ConfigLoader.config;

public class DBConnector {
	private final Logger logger = Logger.getLogger(DBConnector.class);
	private Connection conn;

    private int lastID = 0;
    private Timestamp lastTimestamp;

    private static final String CURRENT_FILE = config.get("increment.file");

	private static final int ID_START_VALUE = 0;
	private static final String TIMESTAMP_START_VALUE = "2013-03-31 13:13:13.131";

	private final RestConnector restConnector;

	/**
	 * loads the increment.id or increment.timestamp config
	 * Starts MainQueryThread
	 * @param restConnector Instance of {@link RestConnector}
	 */
	public DBConnector(RestConnector restConnector) {
		try {
            /*---------------Testing---------------------------------------------
            Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
            lastTimestamp = Timestamp.valueOf("2013-03-31 13:13:13.131");
            System.out.println(currentTimeStamp);
            lastTimestampChanged(currentTimeStamp);*/

            initTempFiles();

            String inFile = Files.readAllLines(Paths.get(CURRENT_FILE)).get(0);

            if(config.get("increment.mode").equals("id")) lastID = Integer.parseInt(inFile);
            else if(config.get("increment.mode").equals("timestamp")) lastTimestamp = Timestamp.valueOf(inFile);
		}
		catch(IOException | IndexOutOfBoundsException e) {
			logger.error("Can not open file or file is empty.");
		}

		this.restConnector = restConnector;

		new MainQueryThread(this).start();

		logger.debug("Created DB Connector Instance");
	}

	/**
	 * <p>Creating idFile or timestampFile</p>
	 */
	private void initTempFiles(){
		try{
			if(!new File(CURRENT_FILE).exists()){
				Files.write(Paths.get(CURRENT_FILE), config.get("increment.mode").equals("timestamp") ? TIMESTAMP_START_VALUE.getBytes() : Integer.toString(ID_START_VALUE).getBytes());
				logger.info(CURRENT_FILE+ " successfully created");
			}else {
				logger.info(CURRENT_FILE+" already exists");
			}
		} catch (IOException e){
			logger.error("Can't create File: "+CURRENT_FILE);
			logger.trace("",e);
		}
	}

	 /**
	  * <p>Creates a Connection to the Database.</p>
	  * @return Returns the connection to the Database or null to try again
	  */
	private Connection getConnection() {
		try {

			if(conn == null) {
                logger.debug("Building Connection to Database...");
		    	conn = DriverManager.getConnection(config.get("dbconnection"));
		    	logger.info("Connected to " + conn.getMetaData().getDatabaseProductName());
	    	}
            return conn;
		}catch (SQLException e) {
			conn = null;
			logger.error("Connection could not be established");
			logger.trace(e.getMessage());
		}
		return null;
	}

	/**
	 * Adds a prepared Statement to a SQL_Query and Executes it
	 * @param query contains the sql-query command
	 * @return a Resultset is returned from the executed Query
	 */
	ResultSet sendQuery(String query){
		ResultSet resultset = null;
		try {
			if(getConnection() != null){
				query += " WHERE " + config.get("increment.column") + " > ?";
				PreparedStatement ps = getConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				ps.setObject(1, config.get("increment.mode").equals("id") ? lastID : lastTimestamp);
				resultset = ps.executeQuery();
			}
		} catch (SQLException e) {
			conn = null;
		}
		return resultset;
	}

	/**
	 * Saves the lastID to the file .id
	 * @param newID contains the latest ID
	 */
	void lastAttributeChanged (int newID) {
		if(lastID < newID) {
			try {
				logger.info("Id changed to: "+ newID);
				lastID = newID;
				String content = Integer.toString(lastID);
				writeToFile(content);
			} catch (IOException e) {
				logger.error("Can't write to file .id");
				logger.trace("",e);
			}
		}
	}
	/**
	 * Saves the lastTimestamp to the file .timestamp
	 * @param newTimeStamp contains the latest Timestamp
	 */
	void lastAttributeChanged (Timestamp newTimeStamp) {
		if(lastTimestamp.before(newTimeStamp)) {
			try {
				lastTimestamp = newTimeStamp;
				logger.info("Timestamp changed to: "+ newTimeStamp);
				writeToFile(lastTimestamp.toString());
			} catch (IOException e) {
				logger.error("Can't write to file .timestamp");
				logger.trace("",e);
			}
		}
	}

	/**
	 * Writes to a File or Creates the File
	 * @param content String to be written to the File
	 */
	private void writeToFile(String content) throws IOException {
		File file = new File(CURRENT_FILE);
		FileOutputStream fos = new FileOutputStream(file);

		if (!file.exists() && file.createNewFile()){
			logger.info("File successfully created!");
		}

		fos.write(content.getBytes());
		fos.close();

		logger.debug("Written to File " + CURRENT_FILE);
	}

	RestConnector getRestConnector(){
		return this.restConnector;
	}
}