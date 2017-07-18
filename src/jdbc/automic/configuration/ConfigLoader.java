package jdbc.automic.configuration;

import org.omg.PortableServer.REQUEST_PROCESSING_POLICY_ID;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigLoader implements ConfigModel {

    public static HashMap<String, String> config = new HashMap<String, String>();

    public static void load(String dbConfigFile, String restConfigFile){
        if(validateConfiguration()) {
            config.putAll(parseConfigFile(Paths.get(dbConfigFile)));
            config.putAll(parseConfigFile(Paths.get(restConfigFile)));
            System.err.println("Configuration successfully loaded.");
        }else{
            System.err.println("Configuration could not be loaded because of some unresolved errors. ");
        }
    }

    private static List<String> readConfigFile(Path path) {
        List<String> configLines = null;

        try (Stream<String> stream = Files.lines(path)) {
            configLines = stream
                    .filter(line -> !line.isEmpty())
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return configLines;
    }

    public static boolean validateConfiguration(){

        List<String> missingKeys = new ArrayList<String>();

        for(String requiredKey : requiredFields){
            if(config.get(requiredKey) == null){
                missingKeys.add(requiredKey);
            }
        }

        if(!missingKeys.isEmpty()){
            for(String missingKey : missingKeys){
                System.err.println("Attribute " + missingKey.toUpperCase() + " is required but not set.");
            }
            return false;
        }
        return true;
    }

    private static HashMap<String, String> parseConfigFile(Path path) {

        HashMap<String, String> config = new HashMap<String, String>();

        for (String line : readConfigFile(path)) {
            String[] pairs = line.split("=", 2);

            String key = pairs[0].trim();
            String value = pairs[1].trim();

            config.put(key, value.isEmpty() ? null : value);
        }

        return config;
    }
}




















