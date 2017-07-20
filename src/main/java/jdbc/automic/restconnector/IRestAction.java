package jdbc.automic.restconnector;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface IRestAction {
	Logger logger = Logger.getLogger(IRestAction.class);

	/**
	 * <P>Gets called if there is a change in the Datasource</P>
	 * <P>Gets implemented by {@link RestConnector}</P>
	 * @param array Changed Data
	 */
	void action(JSONArray array);

	/**
	 * <P>Helps the Datasource to convert the Resultset into an JSONArray</P>
	 * <P>One Row represents one {@link JSONObject} in the {@link JSONArray}</P>
	 * @param resultSet that should get converted
	 * @return {@link JSONArray} that represents the Resultset
	 * @throws SQLException
	 */
	static JSONArray fetchData(ResultSet resultSet) throws SQLException {
		JSONArray array = new JSONArray();
		JSONObject tableEntry = null;

		while(resultSet.next()) {
			tableEntry = new JSONObject();
			int i = 1;
			while(i <= resultSet.getMetaData().getColumnCount()) {
				tableEntry.put(resultSet.getMetaData().getColumnLabel(i), resultSet.getObject(i++));
			}
			array.add(tableEntry);
		}
		resultSet.beforeFirst();
		logger.debug("Fetching Resultset to JSONArray");
		logger.debug(array);
		return array;
	}
}
