package jdbc.automic;

import jdbc.automic.configuration.ConfigLoader;
import jdbc.automic.dbconnector.DBConnector;
import jdbc.automic.restconnector.RestConnector;
import org.apache.log4j.Logger;

public class MainClass{
    private static final Logger logger = Logger.getLogger(MainClass.class);

    /**
     * <P>Entry Point for the Program</P>
     * <P>Takes the args and gives it io the {@link ConfigLoader}</P>
     * @param args Program arguments.
     */
    public static void main(String[] args) {
        if(args.length < 2){
            logger.error("Only "+args.length+" arguments were given, but 2 were expected. Exiting programm...");
            System.exit(-1);
        }

        ConfigLoader.load(args[0], args[1]);

        RestConnector restConnector = new RestConnector();
        new DBConnector(restConnector);
    }
}
