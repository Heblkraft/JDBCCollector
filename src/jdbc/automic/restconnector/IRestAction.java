package jdbc.automic.restconnector;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface IRestAction {
	public void action(JSONArray array);
	
	@SuppressWarnings("unchecked")
	public static JSONArray fetchData(ResultSet set) throws SQLException {
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
		return array;
	}
}
