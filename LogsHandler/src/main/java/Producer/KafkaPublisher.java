package Producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Singleton;
import javax.inject.Inject;
import java.util.Map;
import java.util.Properties;

@Singleton
public class KafkaPublisher {
    private static Logger logger = LogManager.getLogger(KafkaPublisher.class);
    private final Properties props;

    @Inject
    @Singleton
    public KafkaPublisher(Properties props) {
        this.props = props;
    }

    public String produce(Map<String, String> map) {
        String input = new Gson().toJson(map).toString();
        Producer<String, String> producer = new KafkaProducer<>(props);
        try {
            RecordMetadata metadata = producer.send(new ProducerRecord<String, String>("my-topic", input)).get();
            logger.debug(" to partition " + metadata.partition() + " with offset " + metadata.offset());
        } catch (Exception e) {
            logger.error(e);
            return e.getMessage();
        }
        return "sent to kafka";
    }

}
