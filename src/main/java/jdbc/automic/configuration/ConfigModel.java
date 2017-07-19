package jdbc.automic.configuration;

interface ConfigModel {

    String[] requiredFieldModels = {
            "dbconnection|text",
            "increment.column|text",
            "increment.mode|text",
            "query|text:file",
    };

    String[][] modelRequdiredFields = {

            {"dbconnection", "required", "text"},
            {"increment.column", "required", "text|numeric"},
            {"increment.mode", "required", "text"},
            {"query", "required", "text|file"},
    };

    String[][] modelOptionalFields = {
            {"dbconnection", "required", "text"},
            {"increment.column", "required", "text|numeric"},
            {"increment.mode", "required", "text"},
            {"query", "required", "text|file"},
    };


}
