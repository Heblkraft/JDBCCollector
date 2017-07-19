package jdbc.automic.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigLoader implements ConfigModel {

    public static final HashMap<String, String> config = new HashMap<>();

    // better name than dbConfigFile du fgt

    public static void load(String dbConfigFile, String restConfigFile){

        config.putAll(parseConfigFile(Paths.get(dbConfigFile)));
        config.putAll(parseConfigFile(Paths.get(restConfigFile)));

        if(validateAndOptimizeConfiguration()) {
            System.err.println("Configuration successfully loaded.");

        }
        else{
            System.err.println("Configuration could not be loaded because of some unresolved errors. ");
        }
    }

    public static void overwriteConfig(){

    }

    private static boolean validateAndOptimizeConfiguration(){

        List<String> missingKeys = new ArrayList<>();

        for(String requiredKey : requiredFieldModels){
          String[] configLineTokens = requiredKey.split("\\|");
          String[] attributeTokens = configLineTokens[1].split(":");

          if(config.get(configLineTokens[0]) == null){
              missingKeys.add(configLineTokens[0]);
          }


        }

        if(missingKeys.size() != 0){
            for(String missingKey : missingKeys){
               System.err.println("Attribute " + missingKey.toUpperCase() + " is required but not set.");
            }
            return false;
        }
        return true;
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

    private static HashMap<String, String> parseConfigFile(Path path) {

        HashMap<String, String> config = new HashMap<>();

        for (String line : readConfigFile(path)) {
            if(!line.contains("=")) continue;
            String[] pairs = line.split("=", 2);

            String key = pairs[0].trim();
            String value = pairs[1].trim();

            System.out.println(value);

            // hier fehlt noch der teil wenn es kein = zeichen gibt.

            config.put(key, value.isEmpty() ? null : value);
        }

        return config;
    }
}




















