package Consumer;


import com.google.gson.JsonObject;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.elasticsearch.action.index.IndexResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map;


public class KafkaReceiver {
    private static String kafkaIp ;

    public static void main(String[] args) {
        kafkaIp = ConsumerConfiguration.kafkaHost+":"+ConsumerConfiguration.kafkaPort;
        ElasticSearchHandler elasticSearchHandler = new ElasticSearchHandler();
        Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaIp);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());


        props.put(ConsumerConfig.GROUP_ID_CONFIG, ConsumerConfiguration.GROUP_ID_CONFIG);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, ConsumerConfiguration.MAX_POLL_RECORDS);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,ConsumerConfiguration.OFFSET_RESET_EARLIER);



        Consumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(ConsumerConfiguration.TOPIC_NAME));
        consumer.subscribe(Arrays.asList("my-topic"));
        Map<String,String> map = new HashMap<>();
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("offset = %s , key = %s, value = %s \n", record.offset(), record.key(), record.value());
                System.out.println("Record partition " + record.partition());
                // call elastic search handler here
                JsonObject jObject = util.StringToJson(record.value());
                if(jObject == null){
                    System.out.println("failed to parse json file for: \n" + record.value());
                }
                else{
                    map.put(jObject.get("User-Agent").toString(),jObject.get("message").toString());
                    elasticSearchHandler.setMap(map);
                    elasticSearchHandler.setIndex("index");
                    IndexResponse indexResponse = elasticSearchHandler.index();
                    if(indexResponse == null){
                        System.out.println("fail to send to elastic search");
                    }
                    else{
                        System.out.println("indexResponse status: "+indexResponse.status().toString());
                    }
                }
            }
        }
    }

}

