package Consumer;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

import util.InfraUtil;


public class ConsumerModule extends AbstractModule {

    private final ConsumerConfiguration configuration;

    public ConsumerModule() {
        configuration = InfraUtil.load(ConsumerConfiguration.DOCKER_CONFIG_FILEPATH, ConsumerConfiguration.class);
    }

    @Override
    protected void configure() {
        binder().requireExplicitBindings();
        bind(KafkaReceiver.class);
        bind(BulkRequest.class);
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
        props.put(ConsumerConfig.GROUP_ID_CONFIG, configuration.getGroupIdConfig());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, configuration.getOffsetResetEarlier());
        Consumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(configuration.getTopicName()));
        return consumer;
    }
}
