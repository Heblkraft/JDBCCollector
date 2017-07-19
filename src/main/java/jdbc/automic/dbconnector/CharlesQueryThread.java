package jdbc.automic.dbconnector;

import static jdbc.automic.configuration.ConfigLoader.config;

import jdbc.automic.restconnector.IRestAction;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CharlesQueryThread extends Thread{
	private final Logger logger = Logger.getLogger(CharlesQueryThread.class);
	private final DBConnector dbConnector;

	public CharlesQueryThread(String name, DBConnector dbConnector) {
		super(name);
		this.dbConnector = dbConnector;
	}

	@Override
	public void run() {
		logger.debug("Polling");
		ResultSet rs = dbConnector.sendQuery(config.get("query"));
		if(isEmpty(rs)) dbConnector.lastIDChanged(0);
		else {
			try {
				logger.debug("Change Detected");
				JSONArray array = IRestAction.fetchData(rs);
				if(config.get("increment.id") != null) dbConnector.lastIDChanged(Integer.parseInt(((JSONObject)array.get(array.size()-1)).get(config.get("increment.id")).toString())); //Returns the last Id in the Query and gives it to the dbConnector
				dbConnector.getRestConnector().getRestAction().action(array);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
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
}
