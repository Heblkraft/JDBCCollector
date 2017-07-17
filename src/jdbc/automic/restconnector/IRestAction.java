package jdbc.automic.restconnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface IRestAction {
	public void action(ResultSet array);
	
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
		System.out.println(array);
		return array;
	}
}
