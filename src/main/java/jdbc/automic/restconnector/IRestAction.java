package jdbc.automic.restconnector;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;

import static jdbc.automic.configuration.ConfigLoader.config;

public interface IRestAction {
	Logger logger = Logger.getLogger(IRestAction.class);

	/**
	 * <P>Gets called if there is a change in the Datasource</P>
	 * <P>Gets implemented by {@link RestConnector}</P>
	 * @param array Changed Data
	 */
	JSONArray action(JSONArray array);

	/**
	 * <P>Helps the Datasource to convert the Resultset into an JSONArray</P>
	 * <P>One Row represents one {@link JSONObject} in the {@link JSONArray}</P>
	 * @param resultSet that should get converted
	 * @return {@link JSONArray} that represents the Resultset
	 * @throws SQLException Problems that might occur during the processing of the Resultset.
	 */
	static JSONArray fetchData(ResultSet resultSet) throws SQLException {
		JSONArray array = new JSONArray();
		JSONObject tableEntry;
		int rowCount = 0;

		while(resultSet.next() && rowCount++ < Integer.parseInt(config.get("max.entries"))) {
			tableEntry = new JSONObject();
			int columnCount = 1;
			while(columnCount <= resultSet.getMetaData().getColumnCount()) {
				tableEntry.put(resultSet.getMetaData().getColumnLabel(columnCount), resultSet.getObject(columnCount++));
			}
			array.add(tableEntry);
		}
		resultSet.beforeFirst();

		logger.debug("Fetching Resultset to JSONArray: ");
		logger.debug(array);
		return array;
	}
}
