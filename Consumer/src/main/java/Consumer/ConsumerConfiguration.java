package Consumer;

public class ConsumerConfiguration {

    public ConsumerConfiguration(){
    }

    public static final String DOCKER_CONFIG_FILEPATH = "src/main/resources/consumer.config" ;
    public static final String LOCAL_CONFIG_FILEPATH = "Consumer/src/main/resources/consumer.config" ;
    private int elasticPort;
    private int additionalElasticPort;
    private String elasticHost;
    private int kafkaPort;
    private String kafkaHost;
    private String clientId;
    private String topicName;
    private String groupIdConfig;
    private String offsetResetEarlier;
    private Integer maxPollRecords;

    public String getDOCKER_CONFIG_FILEPATH(){
        return  DOCKER_CONFIG_FILEPATH;
    }
    public String getAdditionalConfigFilePath(){
        return  LOCAL_CONFIG_FILEPATH;
    }
    public String getElasticHost(){
        return  elasticHost;
    }
    public String getKafkaHost(){
        return  kafkaHost;
    }
    public String getClientId(){
        return  clientId;
    }
    public String getTopicName(){
        return  topicName;
    }
    public String getGroupIdConfig(){
        return  groupIdConfig;
    }
    public String getOffsetResetEarlier(){
        return  offsetResetEarlier;
    }
    public Integer getMaxPollRecords(){
        return  maxPollRecords;
    }
    public int getElasticPort(){
        return  elasticPort;
    }
    public int getAdditionalElasticPort(){
        return  additionalElasticPort;
    }
    public int getKafkaPort(){
        return  kafkaPort;
    }
}
