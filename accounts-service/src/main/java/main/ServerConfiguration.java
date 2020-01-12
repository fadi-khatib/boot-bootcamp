package main;


public class ServerConfiguration {

    public ServerConfiguration() {
    }

    //public static final String CONFIG_FILE_PATH = "accounts-service/src/main/resources/server.config";
    public static final String CONFIG_FILE_PATH = "src/main/resources/server.config" ; //*local*

    // service configuration
    private int port;
    // data base connection configurations
    private int dataBasePort;//  3306
    private String dataBaseHost;// "localhost"
    private String dataBaseRootPassword;// "password"
    private String DataBaseName; // "db"
    private String dataBaseUser; // "user",
    private String dataBasePassword; //"password"

    public int getPort() {
        return port;
    }

    public int getDataBasePort() {
        return dataBasePort;
    }

    public String getDataBaseHost() {
        return dataBaseHost;
    }

    public String getDataBaseRootPassword() {
        return dataBaseRootPassword;
    }

    public String getDataBaseName() {
        return DataBaseName;
    }

    public String getDataBaseUser() {
        return dataBaseUser;
    }

    public String getDataBasePassword() {
        return dataBasePassword;
    }
}
