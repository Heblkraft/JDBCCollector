package jdbc.automic.restconnector;

import jdbc.automic.restconnector.RestCaller.Method;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
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
            restCaller.setBody(jsonObject.toString());
            System.out.println("RestCaller sent: " + jsonObject);
        }
    }
}