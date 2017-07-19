package jdbc.automic.main;

import jdbc.automic.configuration.ConfigLoader;
import jdbc.automic.dbconnector.DBConnector;
import jdbc.automic.restconnector.RestConnector;
import sun.security.krb5.Config;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

import static jdbc.automic.configuration.ConfigLoader.config;
import org.apache.log4j.Logger;

public class MainClass {

	public static void main(String[] args) {

		final Logger logger = Logger.getLogger(DBConnector.class);

		if(args.length < 2){
			logger.error("Only " + args.length + " arguments were given, but 2 are required");
			System.exit(-1);
		}

        System.setErr(System.err);
        System.setOut(System.out);

		File dbConfig = new File(args[0]);
		File restConfig = new File(args[1]);

		if(!dbConfig.exists() || !restConfig.exists()){
			logger.error("Cannot find or load .properties file in directory " + dbConfig.getAbsolutePath());
			System.exit(-1);
		}

		logger.debug("Loaded arguments: " +Arrays.toString(args));


		ConfigLoader.load("./dbconnection.properties", "./restconnection.properties");

		for(Map.Entry<String, String> entry : config.entrySet()){
			logger.debug(entry.getKey() + "     " + entry.getValue());
		}

		RestConnector restConnector = new RestConnector();
		new DBConnector(restConnector);
	}
}
