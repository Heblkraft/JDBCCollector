package jdbc.automic.dbconnector;

import static jdbc.automic.configuration.ConfigLoader.config;


import jdbc.automic.restconnector.IRestAction;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class CharlesQueryThread extends Thread{
	//Worker Thread for the MainQueryThread
	private final Logger logger = Logger.getLogger(CharlesQueryThread.class);
	private final DBConnector dbConnector;

	/**
	 * <P>Executes the Query for the {@link MainQueryThread} in a new Thread</P>
	 * @param name Name of the Thread
	 * @param dbConnector DbConnector instance is needed
	 */
	public CharlesQueryThread(String name, DBConnector dbConnector) {
		super(name);
		this.dbConnector = dbConnector;
	}

	/**
	 * <P>Implementation of the Runnable Interface for the Thread</P>
	 * <P>Executes the Query when the Thread has been called</P>
	 */
	@Override
	public void run() {
		logger.debug("Polling");
		ResultSet rs = dbConnector.sendQuery(config.get("query"));
		if(rs == null) return;
		if(isEmpty(rs)) dbConnector.lastIDChanged(0);
		else {
			try {
				logger.debug("Change Detected");
				JSONArray array = IRestAction.fetchData(rs);
				//Next Statement gets last Item of the array and sends the increment item to the dbConnector
				if(config.get("increment.mode").equals("id")) dbConnector.lastIDChanged(Integer.parseInt(((JSONObject)array.get(array.size()-1)).get(config.get("increment.column")).toString())); //Returns the last Id in the Query and gives it to the dbConnector
				else dbConnector.lastTimestampChanged(Timestamp.valueOf(((JSONObject)array.get(array.size()-1)).get(config.get("increment.column")).toString()));

				dbConnector.getRestConnector().getRestAction().action(array);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * <P>Tests if the given Resultset is empty</P>
	 * @param resultSet Resultset of the Query
	 * @return <tt>true</tt> if the Resultset is empty
	 */
	private boolean isEmpty(ResultSet resultSet){
		boolean returnvalue = false;
		if(resultSet == null){
			logger.error(config.get("increment.column") + " does not exist");
			System.exit(-2);
		}
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
}
