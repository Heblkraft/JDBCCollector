package jdbc.automic.configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.log4j.Logger;

public class ConfigLoader implements ConfigModel {

    public static final HashMap<String, String> config = new HashMap<>();

    private static Logger logger = Logger.getLogger(ConfigLoader.class);

    public static void load(String baseConfigFile, String restConfigFile){

        if(!Files.exists(Paths.get(baseConfigFile))){
            logger.error(String.format("Can not find or load %s", baseConfigFile));
            System.exit(-1);
        }

        if(!Files.exists(Paths.get(baseConfigFile))){
            logger.error(String.format("Can not find or load %s", baseConfigFile));
            System.exit(-1);
        }

        config.putAll(readConfigurationFile("dbconnection.properties"));
        config.putAll(readConfigurationFile("restconnection.properties"));

        if(assertConfigurationStatus()) logger.info("Loaded configuration is valid.");
        else {
            logger.error("Configuration could not be loaded. Check your .properties files. ");
            System.exit(-1);
        }
    }

    private static boolean assertConfigurationStatus(){

        LinkedList<String> ls = new LinkedList<>();

        for(String s : requiredFieldModels){
            if(config.get(s) == null) ls.add(s);
        }

        if(!ls.isEmpty()){
            ListIterator li = ls.listIterator();

            while(li.hasNext()){
                logger.error(String.format("Attribute %s is required but improperly/not set. ", li.next()));
            }

            return false;
        }
        return true;
    }

    private static HashMap<String, String> readConfigurationFile(String path){

        HashMap<String, String> configLines = new HashMap<>();

        try(BufferedReader br = new BufferedReader(new FileReader(path))){

            String line;

            while((line = br.readLine()) != null){

                if(line.isEmpty()) continue;
                if(!line.contains("=")) continue;

                String[] prop = line.split("=", 2);

                if(prop.length < 2) continue;

                String key = prop[0].trim();
                String value = prop[1].trim();

                if(value.contains(".sql")) {
                    BufferedReader sqlBr = new BufferedReader(new FileReader(value));

                    String sqlLine;

                    StringBuilder sql = new StringBuilder();

                    while((sqlLine = sqlBr.readLine()) != null) sql.append(sqlLine);

                    value = sql.toString();

                    sqlBr.close();
                }

                configLines.put(key, value);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return configLines;
    }
}




















