package jdbc.automic.restconnector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import jdbc.automic.restconnector.RestCaller.Method;

public class RestConnector implements IRestAction{
	
	RestCaller restCaller = new RestCaller("https://postman-echo.com/get?test=123&hi=456", Method.GET);
	
	public RestConnector() {
		try {
			restCaller.build();
			restCaller.execute();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(restCaller.getResponse()));
			String allStrings = "";
			String s;
			while((s = br.readLine())!= null) {
				allStrings += s;
			}
			JSONObject jsonObj = (JSONObject) JSONValue.parse(allStrings);
			System.out.println(jsonObj.get("args").toString());
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public IRestAction getRestAction() {
		return this;
	}

	@Override
	public void action(JSONArray array) {
		
	}
}