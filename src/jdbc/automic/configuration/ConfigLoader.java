package jdbc.automic.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigLoader {
	
	private Path configFile;
	
	public ConfigLoader(String configFile) {
		this.configFile = Paths.get(configFile);
	}
	
	private List<String> readConfigFile() {
		List<String> configLines = null;
		
		try (Stream<String> stream = Files.lines(configFile)) {
			configLines = stream
					.filter(line -> !line.isEmpty())
					.collect(Collectors.toList());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return configLines;
	}
	
	public void load() {
		//readConfigFile();
		parseConfigFile();
	}
	
	// fuer die db connection entweder 1 attribute oder alle anderen attributes einzeln. 
	
	private HashMap<String, String> parseConfigFile() {
		
		HashMap<String, String> config = new HashMap<String, String>();
		
		for(String line : readConfigFile()) {
			
			String[] pairs = line.split("=");
			
			String key = pairs[0].trim();
			String value = pairs[1].trim();
		
			System.out.println(Arrays.toString(value.split(";")));
			
			config.put(key, value);
		}
		
		for(Entry<String, String> entry : config.entrySet()) {
			System.out.println(entry.getKey() + ", " + entry.getValue());
		}
		
		return config;
	}
	
	
}
