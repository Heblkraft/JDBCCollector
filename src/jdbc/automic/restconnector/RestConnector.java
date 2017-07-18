package jdbc.automic.restconnector;

import jdbc.automic.restconnector.RestCaller.Method;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

public class RestConnector implements IRestAction {

    private final RestCaller restCaller = new RestCaller("https://postman-echo.com/post", Method.POST);

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
        for (Object obj : array) {
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject jsonSent = new JSONObject();
            jsonSent.put("eventname", "buildRequest");
            restCaller.setBody(jsonObject.toString());
            try {
                restCaller.addParametersToRequest();
                restCaller.execute();
                BufferedReader reader = new BufferedReader(new InputStreamReader(restCaller.getResponse()));
                String s = null;
                while((s = reader.readLine())!= null){
                    System.out.println(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            System.out.println("RestCaller sent: " + jsonObject);
        }
    }
}