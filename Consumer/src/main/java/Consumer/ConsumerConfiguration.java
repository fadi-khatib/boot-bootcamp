package Consumer;

import org.json.simple.JSONObject;

public class ConsumerConfiguration {

        // docker -java
        private static final String configFilePath = "src/main/resources/consumer.config" ;
        // local
        private static final String additionalConfigFilePath = "Consumer/src/main/resources/consumer.config" ;
        //elastic
        public static final int elasticPort;
        public static final int additionalElasticPort;
        public static final String elasticHost;
        //kafka
        public static final int kafkaPort;
        public static final String kafkaHost;


        //public static String KAFKA_BROKERS = ConsumerConfiguration.kafkaHost+":"+ConsumerConfiguration.kafkaPort;
        public static Integer MESSAGE_COUNT=1000;
        public static String CLIENT_ID="client1";
        public static String TOPIC_NAME="demo";
        public static String GROUP_ID_CONFIG="consumerGroup1";
        public static Integer MAX_NO_MESSAGE_FOUND_COUNT=100;
        public static String OFFSET_RESET_LATEST="latest";
        public static String OFFSET_RESET_EARLIER="earliest";
        public static Integer MAX_POLL_RECORDS=1;

        static{
            JSONObject config = util.fileToJson(configFilePath);
            if(config == null){
                System.out.println("fail to find consumer.config file at: " + configFilePath);
                config = util.fileToJson(additionalConfigFilePath);
            }
            System.out.println(config.toJSONString());
            //elasticSearch
            elasticPort = Integer.parseInt(config.get("elasticPort").toString());
            elasticHost = config.get("elasticHost").toString();
            additionalElasticPort =  Integer.parseInt(config.get("additionalElasticPort").toString());

            // kafka
            kafkaPort = Integer.parseInt(config.get("kafkaPort").toString());
            kafkaHost = config.get("kafkaHost").toString();


        }

        public ConsumerConfiguration(){
            System.out.println("creating consumer configuration");
        }
}
