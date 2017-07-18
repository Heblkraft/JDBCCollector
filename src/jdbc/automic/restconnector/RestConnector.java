package jdbc.automic.restconnector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.sun.jna.platform.win32.WinBase;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONArray;

import jdbc.automic.restconnector.RestCaller.Method;

public class RestConnector implements IRestAction{
	RestCaller restCaller = new RestCaller("https://postman-echo.com/post", Method.POST);
	
	//Initializes the RestCaller
	public RestConnector() {
		try {
			restCaller.addHeader("Authorization", "73f62553-bec9-46e9-b89c-9ab14cd18277");
			restCaller.addHeader("Content-Type", "application/json");
			restCaller.addHeader("Accept", "application/json");
			restCaller.build();
			restCaller.execute();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		String jdbcstring = "jdbc:sqlserver://192.168.216.33:1433;DatabaseName=jdbc_test;user=jdbc_user;password=123;"; // TESTSTRING

		try {
			Connection connection = DriverManager.getConnection(jdbcstring);
			Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = statement.executeQuery("select * from test_table_id where id < 1");
			if(!rs.next()){
				System.out.println("Rs is empty");
			}
			rs.beforeFirst();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public IRestAction getRestAction() {
		return this;
	}
	
	//Implementations of the IRestAction witch gets called by DbConnector;
	@Override
	public void action(JSONArray array) {
		try {
			restCaller.setBody(array.toString());
			BufferedReader br = new BufferedReader(new InputStreamReader(restCaller.getResponse()));
			String allStrings = "";
			String s;
			while((s = br.readLine())!= null) {
				allStrings += s;
			}
			
			
			System.out.println(allStrings);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}