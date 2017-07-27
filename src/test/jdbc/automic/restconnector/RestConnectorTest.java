package jdbc.automic.restconnector;

import com.mockrunner.mock.jdbc.MockResultSet;
import jdbc.automic.configuration.ConfigLoader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class RestConnectorTest {

    RestConnector restConnector = new RestConnector();

    @BeforeClass
    public static void loadConfig() {
        ConfigLoader.load("./dbconnection.properties", "./restconnection.properties");
    }

    public static ResultSet setupResultSet(){
        MockResultSet resultSet = new MockResultSet("hi");
        resultSet.addColumn("id");
        resultSet.addColumn("name");
        resultSet.addColumn("timestamp");
        resultSet.addRow(new Object[]{1, "Leanne Graham", new Timestamp(1501139662586l)});
        resultSet.addRow(new Object[]{2, "Ervin Howell", new Timestamp(1501142734709l)});
        return resultSet;
    }

    public static JSONArray setupExpectedFetchdataArray(){
        JSONArray array = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put("name", "Leanne Graham");
        obj.put("id", 1);
        obj.put("timestamp", new Timestamp(1501139662586l));
        array.add(obj);
        obj = new JSONObject();
        obj.put("name", "Ervin Howell");
        obj.put("id", 2);
        obj.put("timestamp", new Timestamp(1501142734709l));
        array.add(obj);
        return array;
    }

    @Test
    public void testFetchdata() {
        ResultSet resultSet = setupResultSet();
        JSONArray expectedArray = setupExpectedFetchdataArray();

        try {
            Assert.assertEquals(expectedArray, IRestAction.fetchData(resultSet));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray setupExpectedSendRequestArray(){
        JSONArray array = new JSONArray();
            JSONObject sentObject = new JSONObject();
                sentObject.put("eventtype", "buildReport");
                JSONObject valuesObject = new JSONObject();
                    valuesObject.put("name", "Leanne Graham");
                    valuesObject.put("id", 1);
                    valuesObject.put("timestamp", new Timestamp(1501139662586l));
                sentObject.put("values", valuesObject);
            array.add(sentObject);
            sentObject = new JSONObject();
                sentObject.put("eventtype", "buildReport");
                valuesObject = new JSONObject();
                    valuesObject.put("name", "Ervin Howell");
                    valuesObject.put("id", 2);
                    valuesObject.put("timestamp", new Timestamp(1501142734709l));
                sentObject.put("values", valuesObject);
            array.add(sentObject);
        return array;
    }

    @Test
    public void testSendRequest(){
        try {
            JSONArray array = IRestAction.fetchData(setupResultSet());
            JSONArray sentArray = restConnector.action(array);
            JSONArray expectedArray = setupExpectedSendRequestArray();

            Assert.assertEquals(expectedArray, sentArray);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
