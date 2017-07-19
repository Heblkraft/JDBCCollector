package jdbc.automic.restconnector;

import jdbc.automic.restconnector.RestCaller.Method;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.plaf.nimbus.State;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.sql.*;

import static jdbc.automic.configuration.ConfigLoader.config;

public class RestConnector implements IRestAction {
    Logger logger = Logger.getLogger(RestConnector.class);

    private final RestCaller restCaller = new RestCaller(config.get("rest.url"), Method.POST);

    //Initializes the RestCaller
    public RestConnector() {
        logger.error("RestConnector Error");
        logger.debug("RestConnector Debug");
        try {
            restCaller.addHeader("Authorization", "73f62553-bec9-46e9-b89c-9ab14cd18277");
            restCaller.addHeader("Content-Type", "application/json");
            restCaller.addHeader("Accept", "application/json");
            restCaller.build();
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
            JSONObject jsonSent = new JSONObject();
            jsonSent.put("values", (JSONObject) obj);
            jsonSent.put("eventname", config.get("rest.eventname"));
            restCaller.setBody(jsonSent.toString());

            try {
                restCaller.build();
                restCaller.execute();
                BufferedReader reader = new BufferedReader(new InputStreamReader(restCaller.getResponse()));
                String s = null;
                while((s = reader.readLine())!= null){
                    System.out.println("Returned: "+s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            System.out.println("RestCaller sent: " + jsonSent);
        }
    }
}