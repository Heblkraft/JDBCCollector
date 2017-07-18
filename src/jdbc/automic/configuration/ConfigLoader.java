package jdbc.automic.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigLoader {

    public static HashMap<String, String> config = new HashMap<String, String>();

    public static void load(String dbConfigFile, String restConfigFile){
        config.putAll(parseConfigFile(Paths.get(dbConfigFile)));
        config.putAll(parseConfigFile(Paths.get(restConfigFile)));
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

        HashMap<String, String> config = new HashMap<String, String>();

        for (String line : readConfigFile(path)) {
            String[] pairs = line.split("=", 2);

            String key = pairs[0].trim();
            String value = pairs[1].trim();

            config.put(key, value);
        }

        return config;
    }
}




















