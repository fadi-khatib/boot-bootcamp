package Consumer;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

public class ConsumerModule extends AbstractModule {

    ConsumerConfiguration configuration;

    public ConsumerModule() {
        try {
            configuration = Util.load(ConsumerConfiguration.configFilePath, ConsumerConfiguration.class);
        }catch(Exception e){
            System.out.println(e.getMessage());
            try {
                configuration = Util.load(ConsumerConfiguration.configFilePath, ConsumerConfiguration.class);
            }catch(Exception exception){
                System.out.println(exception.getMessage());
            }
        }
    }

    @Override
    protected void configure() {
        bind(KafkaReceiver.class);
    }

    @Provides
    public RestHighLevelClient providesRestHighLevelClient() {
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost(configuration.getElasticHost(), configuration.getElasticPort(), "http"), new HttpHost(configuration.getElasticHost(), configuration.getElasticPort(), "http")));
        return client;
    }

    @Provides
    public Consumer<String, String> providesKafkaConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.getKafkaHost() + ":" + configuration.getKafkaPort());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, configuration.getGROUP_ID_CONFIG());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, configuration.getMAX_POLL_RECORDS());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, configuration.getOFFSET_RESET_EARLIER());
        Consumer<String, String> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Collections.singletonList(configuration.getTOPIC_NAME()));
        consumer.subscribe(Arrays.asList("my-topic"));
        return consumer;
    }
}
