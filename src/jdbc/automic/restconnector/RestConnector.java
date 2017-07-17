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
			restCaller.appendAttributes("{\"body\":\"hi\"}", "");
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