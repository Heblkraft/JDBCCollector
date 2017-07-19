package jdbc.automic.main;

import jdbc.automic.configuration.ConfigLoader;
import jdbc.automic.configuration.ConfigModel;
import jdbc.automic.dbconnector.DBConnector;
import jdbc.automic.restconnector.RestConnector;
import sun.security.krb5.Config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static jdbc.automic.configuration.ConfigLoader.config;

import org.apache.log4j.Logger;


public class MainClass {

    private static final Logger logger = Logger.getLogger(MainClass.class);

    public static void main(String[] args) {

        long beforeUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

        if(args.length < 2){
            logger.error(String.format("Only %n arguments were given, but 2 were expected. Exiting programm...", args.length));
            System.exit(-1);
        }

        ConfigLoader.load(args[0], args[1]);

        //RestConnector restConnector = new RestConnector();
        //DBConnector connector = new DBConnector(restConnector);

        long afterUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        long actualMemUsed=afterUsedMem-beforeUsedMem;

        logger.debug(String.format("%d Bytes of Memory used.", actualMemUsed));

    }
}
