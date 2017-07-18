package jdbc.automic.configuration;

public interface ConfigModel {

    String[] requiredFields = {
        "dbconnection", "query", "incremenet.id"
    };

    String[] optionalFields = {
        "poll.interval", "rest.api.key", "rest.api.event", "rest.url", "rest.get", "rest.post",
        "max.entries", "max.threadpool"
    };

}
