package jdbc.automic.configuration;

public interface ConfigModel {


    /**
     * Represents the required properties which have to be set in the .properties files.
     */
    String[] requiredFieldModels =
            {
                    "increment.column|text",
                    "increment.mode|text",
                    "query|text",
                    "increment.file|text"
            };

    /**
     * Represents all optional properties which can be set in the .properties files.
     * If not set they get assigned a default value.
     */
    String[] optionalFieldModels =
            {
                    "rest.eventtype|text",
                    "rest.url|text",
                    "rest.authentication.token|text",
                    "max.threadpool|numeric",
                    "max.entries|numeric",
                    "poll.interval|numeric"
            };

}
