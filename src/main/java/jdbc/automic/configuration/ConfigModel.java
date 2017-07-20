package jdbc.automic.configuration;

public interface ConfigModel {

    /**
     * Represents the required properties which have to be set in the .properties files.
     */
    String[] requiredFieldModels =
            {
                    "increment.column",
                    "increment.mode",
                    "query"
            };

    /**
     * Represents all optional properties which can be set in the .properties files.
     * If not set they get assigned a default value.
     */
    String[] optionalFieldModels =
            {
                    "rest.eventname",
                    "rest.url",
                    "rest.authentication.token",
                    "max.threadpool",
                    "max.entries",
                    "poll.interval"
            };

}
