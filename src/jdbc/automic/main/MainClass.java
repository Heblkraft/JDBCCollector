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

import static jdbc.automic.configuration.ConfigLoader.config;

public class MainClass {

    public static void main(String[] args) {


        if (args.length < 2) {
            System.err.println("Only " + args.length + " arguments were given, but 2 are required");
            System.exit(-1);
        }

        System.setErr(System.err);
        System.setOut(System.out);

        File dbConfig = new File(args[0]);
        File restConfig = new File(args[1]);

        if (!dbConfig.exists() || !restConfig.exists()) {
            System.err.println("Cannot find or load .properties file in directory " + dbConfig.getAbsolutePath());
            System.exit(-1);
        }

        System.out.println(Arrays.toString(args));
        System.out.println(args.length);

        ConfigLoader.load("./dbconnection.properties", "./restconnection.properties");

        for (Map.Entry<String, String> entry : config.entrySet()) {
            System.out.println(entry.getKey() + "     " + entry.getValue());
        }

        RestConnector restConnector = new RestConnector();
        DBConnector connector = new DBConnector(restConnector);
    }
}
