package jdbc.automic;

import jdbc.automic.configuration.ConfigLoader;
import jdbc.automic.dbconnector.DBConnector;
import jdbc.automic.restconnector.RestConnector;
import org.apache.log4j.Logger;

import java.net.URL;
import java.net.URLClassLoader;

public class MainClass{
    private static final Logger logger = Logger.getLogger(MainClass.class);

    /**
     * <P>Entry Point for the Program</P>
     * <P>Takes the args and gives it io the {@link ConfigLoader}</P>
     * @param args
     */
    public static void main(String[] args) {
        if(args.length < 2){
            logger.error(String.format("Only %n arguments were given, but 2 were expected. Exiting programm...", args.length));
            System.exit(-1);
        }

        ConfigLoader.load(args[0], args[1]);

        RestConnector restConnector = new RestConnector();
        new DBConnector(restConnector);
    }
}
