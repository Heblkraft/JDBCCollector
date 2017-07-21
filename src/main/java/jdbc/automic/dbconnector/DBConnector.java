package jdbc.automic.dbconnector;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import jdbc.automic.restconnector.RestConnector;
import org.apache.log4j.Logger;
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
			}else logger.debug(CURRENT_FILE+" already exists");
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
		    	logger.debug("Connected to " + conn.getMetaData().getDatabaseProductName());
	    	}
            return conn;
		}catch (SQLException e) {
			conn = null;
			logger.info("Connection could not be established");
			logger.trace(e.getMessage());
		}
		return null;
	}


	public static <T extends Object> List<Class<T>> findClassesImplementing(Class<T> cls) throws IOException {
		List<Class<T>> classes = new ArrayList<Class<T>>();

		for (URL root : Collections.list(Thread.currentThread().getContextClassLoader().getResources(""))) {
			for (File file : findFiles(new File(root.getFile()), ".+\\.jar$")) {
				JarFile jarFile = new JarFile(file);
				for (JarEntry jarEntry : Collections.list(jarFile.entries())) {
					String name = jarEntry.getName();
					if (name.endsWith(".class")) try {
						Class<?> found = Class.forName(name.replace("/", ".").replaceAll("\\.class$", ""));
						if (cls.isAssignableFrom(found)) {
							classes.add((Class<T>) found);
						}
					} catch (Throwable ignore) {
						// No real class file, or JAR not in classpath, or missing links.
					}
				}
			}
		}

		return classes;
	}

	public static List<File> findFiles(File directory, final String pattern) throws IOException {
		File[] files = directory.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory() || file.getName().matches(pattern);
			}
		});

		List<File> found = new ArrayList<File>(files.length);

		for (File file : files) {
			if (file.isDirectory()) {
				found.addAll(findFiles(file, pattern));
			} else {
				found.add(file);
			}
		}

		return found;
	}

	/**
	 * Adds a prepared Statement to a SQL_Query and Executes it
	 * @param query contains the sql-query command
	 * @return a resultset is returned from the executed Query
	 */
	public ResultSet sendQuery(String query){
		ResultSet resultset = null;
		PreparedStatement ps = null;
		try {
			if(config.get("increment.mode").equals("id")){
				query += " WHERE "+config.get("increment.column")+" > ?";
				ps = getConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				ps.setInt(1, lastID);
			}

			else if (config.get("increment.mode").equals("timestamp")){
				query += " WHERE "+config.get("increment.column")+" > ?";
				ps = getConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				ps.setTimestamp(1,lastTimestamp);
			}
			resultset = ps.executeQuery();

		} catch (SQLException e) {
			conn = null;
		}

		return resultset;
	}

	/**
	 * Saves the lastID to the file .id
	 * @param newID contains the latest ID
	 */
	public void lastIDChanged (int newID) {
		if(lastID < newID) {
			try {
				lastID = newID;
				String content = Integer.toString(lastID);
				writeToFile(CURRENT_FILE, content);
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
	public void lastTimestampChanged (Timestamp newTimeStamp) {
		logger.debug("Timestamp changed to: "+ newTimeStamp);
		if(lastTimestamp.before(newTimeStamp)) {
			try {
				lastTimestamp = newTimeStamp;
				writeToFile(CURRENT_FILE, lastTimestamp.toString());
			} catch (IOException e) {
				logger.error("Can't write to file .timestamp");
				logger.trace("",e);
			}
		}
	}

	/**
	 * Writes to a File or Creates the File
	 * @param filePath Path to the File to write to
	 * @param content String to be written to the File
	 * @throws IOException
	 */
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