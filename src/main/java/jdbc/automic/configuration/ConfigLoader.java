package jdbc.automic.configuration;

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

    // better name than dbConfigFile du fgt

    public static void load(String dbConfigFile, String restConfigFile){

        config.putAll(parseConfigFile(Paths.get(dbConfigFile)));
        config.putAll(parseConfigFile(Paths.get(restConfigFile)));

        if(validateAndOptimizeConfiguration()) logger.info("Configuration loaded successfully.");
        else logger.error("Failed to load configuration file. Check your .properties files.");

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
               logger.error(String.format("Attribute [ {0} ] is required but not set.", missingKey));
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

            if(value.contains(".sql")){
                try {
                    value = Files.readAllLines(Paths.get(value)).get(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            config.put(key, value.isEmpty() ? null : value);
        }

        return config;
    }
}




















