package Consumer;

public class ConsumerConfiguration {

    public ConsumerConfiguration(){
    }

    // docker -java
    public static final String configFilePath = "src/main/resources/consumer.config" ;
    // local
    public static final String additionalConfigFilePath = "Consumer/src/main/resources/consumer.config" ;
    //elastic
    private int elasticPort;
    private int additionalElasticPort;
    private String elasticHost;
    //kafka
    private int kafkaPort;
    private String kafkaHost;

    private String CLIENT_ID;
    private String TOPIC_NAME;
    private String GROUP_ID_CONFIG;

    private String OFFSET_RESET_EARLIER;
    private Integer MAX_POLL_RECORDS;

    public String getConfigFilePath(){
        return  configFilePath;
    }
    public String getAdditionalConfigFilePath(){
        return  additionalConfigFilePath;
    }
    public String getElasticHost(){
        return  elasticHost;
    }
    public String getKafkaHost(){
        return  kafkaHost;
    }
    public String getCLIENT_ID(){
        return  CLIENT_ID;
    }
    public String getTOPIC_NAME(){
        return  TOPIC_NAME;
    }
    public String getGROUP_ID_CONFIG(){
        return  GROUP_ID_CONFIG;
    }
    public String getOFFSET_RESET_EARLIER(){
        return  OFFSET_RESET_EARLIER;
    }
    public Integer getMAX_POLL_RECORDS(){
        return  MAX_POLL_RECORDS;
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
