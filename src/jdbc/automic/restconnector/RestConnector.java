package jdbc.automic.restconnector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONArray;

import jdbc.automic.restconnector.RestCaller.Method;

public class RestConnector implements IRestAction{
	RestCaller restCaller = new RestCaller("https://postman-echo.com/post", Method.POST);
	
	public RestConnector() {
		try {
			restCaller.addHeader("Authorization", "73f62553-bec9-46e9-b89c-9ab14cd18277");
			restCaller.addHeader("Content-Type", "application/json");
			restCaller.addHeader("Accept", "application/json");
			restCaller.setBody("{\"body\":\"hi\"}");
			restCaller.build();
			restCaller.execute();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(restCaller.getResponse()));
			String allStrings = "";
			String s;
			while((s = br.readLine())!= null) {
				allStrings += s;
			}
			System.out.println(allStrings);
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
		System.out.println("action");
	}
}