package main;


public class ServerConfiguration {

    public ServerConfiguration() {
    }

    //public static final String CONFIG_FILE_PATH = "accounts-service/src/main/resources/server.config";//*local*
    public static final String CONFIG_FILE_PATH = "src/main/resources/server.config" ;

    private int port;
    private int dataBasePort;
    private String dataBaseHost;
    private String dataBaseRootPassword;
    private String DataBaseName;
    private String dataBaseUser;
    private String dataBasePassword;

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
