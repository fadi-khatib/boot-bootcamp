package Producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.google.gson.Gson;

import javax.inject.Singleton;
import javax.inject.Inject;
import java.util.Map;
import java.util.Properties;

@Singleton
public class KafkaPublisher {

    private final Properties props;

    @Inject
    @Singleton
    public KafkaPublisher(Properties props) {
        this.props = props;
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

}
