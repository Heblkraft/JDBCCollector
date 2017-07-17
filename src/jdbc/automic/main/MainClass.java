package jdbc.automic.main;

import jdbc.automic.configuration.ConfigLoader;
import jdbc.automic.dbconnector.DBConnector;
import jdbc.automic.restconnector.RestConnector;

public class MainClass {

	public static void main(String[] args) {
		new ConfigLoader("");
		RestConnector restConnector = new RestConnector();
		new DBConnector(restConnector);
	}
}
