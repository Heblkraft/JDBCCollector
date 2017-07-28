package jdbc.automic.configuration;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public final class ConfigLoader implements ConfigModel {

    /**
     * Deny instance creation of this class.
     */
    private ConfigLoader() {
    }

    /**
     * Represents the configuration consisting of the .properties files.
     */
    public static final HashMap<String, String> config = new HashMap<>();

    /**
     * {@link org.apache.commons.logging.impl.Log4JLogger}
     */
    private static final Logger logger = Logger.getLogger(ConfigLoader.class);


    /**
     * This Method is the entry point to the ConfigLoader. It is used to validate the
     * commandline arguments and check if the given Paths exist. After the configuration
     * is loaded a quick status check is run that determines whether the program is allowed
     * to be further executed.
     *
     * @param baseConfigFile Path to dbconnection.properties file
     * @param restConfigFile Path to the restconnection.properties file
     */
    public static void load(String baseConfigFile, String restConfigFile){

        if(!Files.exists(Paths.get(baseConfigFile))){
            logger.error(String.format("Can not find or load %s", baseConfigFile));
            System.exit(-1);
        }

        if(!Files.exists(Paths.get(restConfigFile))){
            logger.error(String.format("Can not find or load %s", restConfigFile));
            System.exit(-1);
        }

        config.putAll(readConfigurationFile(baseConfigFile));
        config.putAll(readConfigurationFile(restConfigFile));

        if(assertConfigurationStatus()) logger.info("Loaded configuration is valid.");
        else {
            logger.error("Configuration could not be loaded. Check your .properties files. ");
            System.exit(-1);
        }
    }


    /**
     * This method determines whether program gets further executed or not.
     * It takes the list of required properties that should be defined in the configuration file from the {@link ConfigModel} Interface
     * and loops through the loaded configuration whilst also checking their types.
     * <p>
     * Missing properties get saved to a list which is later used to display the
     * error messages.
     *
     * @return True if the loaded configuration is valid or false otherwise.
     */
    private static boolean assertConfigurationStatus(){

        ArrayList<String> errors = new ArrayList<>();

        for(String requiredField : requiredFieldModels){

            String[] pair = requiredField.split("\\|");

            if(config.get(pair[0]) == null){
                errors.add(String.format("Attribute %s of type %s is required but not set", pair[0], pair[1]));
                continue;
            }

            if(pair[1].equals("numeric")){
                for(char c : config.get(pair[0]).toCharArray()){
                    if(!Character.isDigit(c)){
                        errors.add(String.format("Attribute %s is not of type %s", pair[0], pair[1]));
                        break;
                    }
                }
            }
        }

        if(!errors.isEmpty()){
            for(String errorMessage : errors){
                logger.error(errorMessage);
            }
            return false;
        }

        return true;
    }

    /**
     * This method parses the configuration file and maps the tokens into a map. A {@link BufferedReader}
     * was used because the loaded file is not needed in memory and can be discarded afterwards.
     * <p>
     * The query can also contain a path to an external .sql file that contains the query. In that case
     * a new BufferedReader takes care of that.
     * <p>
     * All lines that are either empty, do not contain [ = ], or do not have a value assigned to the defined key are ignored.
     *
     * @param path The path to the .properties file.
     * @return Returns the mapped
     */

    private static HashMap<String, String> readConfigurationFile(String path) {

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