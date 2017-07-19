package jdbc.automic.restconnector;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface IRestAction {
	Logger logger = Logger.getLogger(IRestAction.class);

	void action(JSONArray array);
	
	static JSONArray fetchData(ResultSet set) throws SQLException {
		JSONArray array = new JSONArray();
		JSONObject tableEntry = null;

		while(set.next()) {
			tableEntry = new JSONObject();
			int i = 1;
			while(i <= set.getMetaData().getColumnCount()) {
				tableEntry.put(set.getMetaData().getColumnLabel(i), set.getObject(i++));
			}
			array.add(tableEntry);
		}
		set.beforeFirst();
		logger.debug("Fetching Resultset to JSONArray");
		logger.debug(array);
		return array;
	}
}
