package jdbc.automic.restconnector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONArray;

import jdbc.automic.restconnector.RestCaller.Method;
import org.json.simple.JSONObject;

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
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public IRestAction getRestAction() {
		return this;
	}
	
	//Implementations of the IRestAction witch gets called by DbConnector;
	@Override
	public void action(JSONArray array) {
		for(Object obj : array){
			JSONObject jsonObject = (JSONObject) obj;
			try {
				restCaller.setBody(jsonObject.toString());
				System.out.println("RestCaller sent: "+jsonObject);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}