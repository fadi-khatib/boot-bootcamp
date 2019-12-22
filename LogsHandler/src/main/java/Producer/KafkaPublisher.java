package Producer;

import boot.ServerConfiguration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.google.gson.Gson;

import javax.inject.Singleton;
import javax.inject.Inject;
import java.util.Map;
import java.util.Properties;

@Singleton
public class KafkaPublisher {

    final static String kafkaIp = ServerConfiguration.kafkaHost+":"+ServerConfiguration.kafkaPort;
    private final Properties props;

    @Inject
    @Singleton
    public KafkaPublisher() {

        props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, IKafkaConstants.KAFKA_BROKERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, IKafkaConstants.CLIENT_ID);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    }

    public String produce(Map<String, String> map){
        String input = new Gson().toJson(map).toString();
        int count = 0;
        System.out.println("Message");
            try (Producer<String, String> producer = new KafkaProducer<>(props)) {
                RecordMetadata metadata = producer.send(new ProducerRecord<String, String>("my-topic", input+Integer.toString(count))).get();
                System.out.println( " to partition " + metadata.partition() + " with offset " + metadata.offset());
                producer.flush();
                return " message sent to kafka partition: "+ metadata.partition();
            }catch(Exception e){
                System.out.println("Error in sending record");
                System.out.println(e);
            }
            count = count+1;
            return "message fail to send to kafka";


    }

    public interface IKafkaConstants {
        public static String KAFKA_BROKERS = ServerConfiguration.kafkaHost+":"+ServerConfiguration.kafkaPort;
        public static Integer MESSAGE_COUNT=1000;
        public static String CLIENT_ID="client1";
        public static String TOPIC_NAME="demo";
        public static String GROUP_ID_CONFIG="consumerGroup1";
        public static Integer MAX_NO_MESSAGE_FOUND_COUNT=100;
        public static String OFFSET_RESET_LATEST="latest";
        public static String OFFSET_RESET_EARLIER="earliest";
        public static Integer MAX_POLL_RECORDS=1;
    }

}
