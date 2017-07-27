package jdbc.automic.restconnector;

import com.mockrunner.mock.jdbc.MockResultSet;
import jdbc.automic.configuration.ConfigLoader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.sql.Timestamp;

public class RestConnectorTest {
    @Test
    public void testtest() {

    }

    @BeforeClass
    public static void loadConfig() {
        ConfigLoader.load("./dbconnection.properties", "./restconnection.properties");
    }

    @Test
    public void testFetchdata() {
        MockResultSet resultSet = new MockResultSet("hi");
        resultSet.addColumn("id");
        resultSet.addColumn("name");
        resultSet.addColumn("timestamp");
        resultSet.addRow(new Object[]{1, "Leanne Graham", new Timestamp(1501139662586l)});

        JSONArray array = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put("name", "Leanne Graham");
        obj.put("id", 1);
        obj.put("timestamp", new Timestamp(1501139662586l));
        array.add(obj);


        try {
            Assert.assertEquals(array, IRestAction.fetchData(resultSet));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
