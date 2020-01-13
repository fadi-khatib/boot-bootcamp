package Consumer;

public class ConsumerConfiguration {

    public ConsumerConfiguration() {
    }

    public static final String DOCKER_CONFIG_FILEPATH = "src/main/resources/consumer.config";
    //    public static final String LOCAL_CONFIG_FILEPATH = "Consumer/src/main/resources/consumer.config";
    private int elasticPort;
    private String elasticHost;
    private int kafkaPort;
    private String kafkaHost;
    private String topicName;
    private String groupIdConfig;
    private String offsetResetEarlier;
    private String accountsServiceHost;
    private int accountsServicePort;

    public String getAccountsServiceHost() {
        return accountsServiceHost;
    }

    public int getAccountsServicePort() {
        return accountsServicePort;
    }

    public String getElasticHost() {
        return elasticHost;
    }

    public String getKafkaHost() {
        return kafkaHost;
    }

    public String getTopicName() {
        return topicName;
    }

    public String getGroupIdConfig() {
        return groupIdConfig;
    }

    public String getOffsetResetEarlier() {
        return offsetResetEarlier;
    }

    public int getElasticPort() {
        return elasticPort;
    }


    public int getKafkaPort() {
        return kafkaPort;
    }
}
