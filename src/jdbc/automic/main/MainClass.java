package jdbc.automic.main;

import jdbc.automic.configuration.DummyConfiguration;
import jdbc.automic.dbconnector.DummyConnector;
import jdbc.automic.restconnector.DummyRestConnector;

public class MainClass {
	public static void main(String[] args) {
		new DummyConfiguration();
		new DummyConnector();
		new DummyRestConnector();
	}
}