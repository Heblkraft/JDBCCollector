package jdbc.automic.restconnector;

import java.sql.ResultSet;

import org.json.simple.JSONArray;

public interface IRestAction {
	public void action(JSONArray array);
	
	public static JSONArray fetchData(ResultSet set) {
		JSONArray array = null;
		
		return array;
	}
}
