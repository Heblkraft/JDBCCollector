package jdbc.automic.configuration;

public interface ConfigModel {
    /**
     * Represents the required properties which have to be set in the .properties files.
     */
    String[] requiredFieldModels = {
        "increment.column|text",
        "increment.mode|text",
        "query|text",
        "increment.file|text"
    };
}
