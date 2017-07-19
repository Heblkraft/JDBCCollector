package jdbc.automic.restconnector;

import jdbc.automic.restconnector.RestCaller.Method;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;

import static jdbc.automic.configuration.ConfigLoader.config;

public class RestConnector implements IRestAction {
    //Finished
    Logger logger = Logger.getLogger(RestConnector.class);

    private final RestCaller restCaller = new RestCaller(config.get("rest.url"), Method.POST);

    //Initializes the RestCaller
    public RestConnector() {
        try {
            restCaller.addHeader("Authorization", "73f62553-bec9-46e9-b89c-9ab14cd18277");
            restCaller.addHeader("Content-Type", "application/json");
            restCaller.addHeader("Accept", "application/json");
            restCaller.build();
        } catch (IOException | URISyntaxException e) {
            logger.error("Cannot create Request Header");
            logger.trace("", e);
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
                restCaller.execute();
                restCaller.closeResponse();
                logger.debug("RestCaller sent Request: "+ jsonSent.toString());
            } catch (IOException e) {
                logger.error("Cannot connect to Rest Service: "+ config.get("rest.url"));
                logger.trace("", e);
            }

        }
    }
}