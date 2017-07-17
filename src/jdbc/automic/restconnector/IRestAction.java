package jdbc.automic.restconnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface IRestAction {
	public void action(JSONArray array);
	
	@SuppressWarnings("unchecked")
	public static JSONArray fetchData(ResultSet set) throws SQLException {
		//Test
		String url = "jdbc:sqlserver://localhost:1433;DatabaseName=Test-Connector;user=test-user;password=hallo1;";
		Connection connection;
		try {
			connection = DriverManager.getConnection(url);
		    System.out.println("Database connected!");
		    Statement stmt = connection.createStatement();
		    set = stmt.executeQuery("select * from accounts;");
		} catch (SQLException e) {
		    throw new IllegalStateException("Cannot connect the database!", e);
		}
		
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
