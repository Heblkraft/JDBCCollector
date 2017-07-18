package jdbc.automic.configuration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigLoader {

    private Path dbConfigFile;
    private Path restConfigFile;
    private HashMap<String, String> config;

    public ConfigLoader(String dbConfigFile, String restConfigFile) {
        this.dbConfigFile = Paths.get(dbConfigFile);
        this.restConfigFile = Paths.get(restConfigFile);
    }

    public ConfigLoader(String dbConfigFile, String restConfigFile, boolean autoload) {
        this.dbConfigFile = Paths.get(dbConfigFile);
        this.restConfigFile = Paths.get(restConfigFile);
        if(autoload) load();
    }

    public void load() {
        this.config = new HashMap<>();

        this.config.putAll(parseConfigFile(dbConfigFile));
        this.config.putAll(parseConfigFile(restConfigFile));
    }

    public HashMap<String, String> getConfiguration(){
        return this.config;
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




















