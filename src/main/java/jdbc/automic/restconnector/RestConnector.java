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
    private final Logger logger = Logger.getLogger(RestConnector.class);
    private final RestCaller restCaller = new RestCaller(config.get("rest.url"), Method.POST);

    /**
     * Initializes the {@link RestCaller}
     */
    public RestConnector() {
        try {
            restCaller.addHeader("Authorization", config.get("rest.authorization"));
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
    @Override
    public JSONArray action(JSONArray array) {
        JSONArray returnArray = new JSONArray();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        for (Object obj : array) {
            JSONObject jsonSent = new JSONObject();
            jsonSent.put("values", obj);
            jsonSent.put("eventtype", config.get("rest.eventtype"));
            if(config.get("increment.mode").equals("timestamp")){
                Object incrementValue = ((JSONObject) obj).get(config.get("increment.column"));
                Timestamp timestamp = Timestamp.valueOf(incrementValue.toString());

                jsonSent.put("eventtime", dateFormat.format(timestamp));

                //Removes the timestamp value from the values
                ((JSONObject)jsonSent.get("values")).remove(config.get("increment.column"));
            }
            returnArray.add(jsonSent);
            restCaller.setBody(jsonSent.toString());
            try {
                restCaller.addParametersToRequest();
                restCaller.execute();
                logger.debug("RestCaller sent Request: "+ jsonSent.toString());
                if(restCaller.getResponseCode() == 200){
                    logger.debug("Responsecode: "+ restCaller.getResponseCode());
                }else {
                    logger.error("Responsecode: "+ restCaller.getResponseCode());
                }
                restCaller.closeResponse();
            } catch (IOException e) {
                logger.error("Cannot connect to Rest Service: "+ config.get("rest.url"));
                logger.trace("", e);

            } catch (URISyntaxException e) {
                logger.error("Cannot resolve URL: "+ config.get("rest.url"));
                logger.trace("", e);
            }
        }
        return returnArray;
    }
}