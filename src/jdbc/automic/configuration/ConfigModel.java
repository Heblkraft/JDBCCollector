package jdbc.automic.configuration;

interface ConfigModel {

    String[] requiredFields = {
        "dbconnection", "query", "incremenet.id|increment.timestamp"
    };

    String[] optionalFields = {
        "poll.interval", "rest.api.key", "rest.api.event", "rest.url", "rest.get", "rest.post",
        "max.entries", "max.threadpool"
    };

}
