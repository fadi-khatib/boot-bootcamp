package boot;

public class ServerConfiguration {

    public ServerConfiguration() {
    }

    public static final String DOCKER_CONFIG_FILEPATH = "src/main/resources/server.config";
    public static final String LOCAL_CONFIG_FILEPATH = "logsHandler/src/main/resources/server.config";

    private int port;
    private int elasticPort;
    private int additionalElasticPort;
    private String elasticHost;
    private int kafkaPort;
    private String kafkaHost;
    private String clientId;

    public String getClientId() {
        return clientId;
    }

    public int getPort() {
        return port;
    }

    public int getElasticPort() {
        return elasticPort;
    }

    public int getAdditionalElasticPort() {
        return additionalElasticPort;
    }

    public String getElasticHost() {
        return elasticHost;
    }

    public int getKafkaPort() {
        return kafkaPort;
    }

    public String getKafkaHost() {
        return kafkaHost;
    }

}
