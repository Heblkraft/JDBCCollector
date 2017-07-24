package jdbc.automic.restconnector;

import jdbc.automic.dbconnector.DBConnector;
import jdbc.automic.restconnector.RestCaller.Method;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static jdbc.automic.configuration.ConfigLoader.config;

public class RestConnector implements IRestAction {
    //Finished
    Logger logger = Logger.getLogger(RestConnector.class);

    private final RestCaller restCaller = new RestCaller(config.get("rest.url"), Method.POST);

    /**
     * Initializes the {@link RestCaller}
     */
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

    /**
     * <p>Getter for the {@link IRestAction} implementation</p>
     * @return {@link IRestAction} for the {@link DBConnector}
     */
    public IRestAction getRestAction() {
        return this;
    }

    /**
     * <P>Implementation of the {@link IRestAction}</P>
     * <P>this Method gets called by {@link DBConnector}</P>
     * @param array Changed Data
     */
    //Implementations of the IRestAction witch gets called by DbConnector;
    @Override
    public void action(JSONArray array) {
        for (Object obj : array) {
            JSONObject jsonSent = new JSONObject();
            jsonSent.put("values", obj);
            jsonSent.put("eventtype", config.get("rest.eventtype"));
            if(config.get("increment.mode").equals("timestamp")){
                Timestamp timestamp = Timestamp.valueOf(((JSONObject)obj).get(config.get("increment.column")).toString());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                jsonSent.put("eventtime", dateFormat.format(timestamp));
                ((JSONObject)jsonSent.get("values")).remove(config.get("increment.column"));
            }
            restCaller.setBody(jsonSent.toString());
            try {
                restCaller.addParametersToRequest();
                restCaller.execute();
                logger.debug("RestCaller sent Request: "+ jsonSent.toString());
                restCaller.closeResponse();
            } catch (IOException e) {
                logger.error("Cannot connect to Rest Service: "+ config.get("rest.url"));
                logger.trace("", e);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        }
    }
}