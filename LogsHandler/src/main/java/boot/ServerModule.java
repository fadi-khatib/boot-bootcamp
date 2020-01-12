package boot;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.logz.guice.jersey.JerseyModule;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import org.apache.http.HttpHost;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;
import util.InfraUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.Properties;


public class ServerModule extends AbstractModule {
    private final JerseyConfiguration configuration;
    private final ServerConfiguration serverConfiguration;

    @Inject
    public ServerModule() {
        serverConfiguration = InfraUtil.load(ServerConfiguration.DOCKER_CONFIG_FILEPATH, ServerConfiguration.class);
        configuration = JerseyConfiguration.builder()
                .addPackage("boot")
                .addPort(serverConfiguration.getPort())
                .build();
    }

    @Override
    protected void configure() {
        binder().requireExplicitBindings();
        install(new JerseyModule(configuration));
        bind(EntryPoint.class);
        bind(ElasticSearchHandler.class);
    }

    @Provides
    public Client providesClient() {
        ClientConfig config = new ClientConfig();
        config.register(JacksonJsonProvider.class);
        Client client = ClientBuilder.newClient(config);
        return client;
    }

    @Provides
    @Singleton
    public Producer<String, String> providesKafkaProducer() {
        Properties props = new Properties();
        String kafkaBrokers = serverConfiguration.getKafkaHost() + ":" + serverConfiguration.getKafkaPort();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, serverConfiguration.getClientId());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        Producer<String, String> producer = new KafkaProducer<>(props);
        return producer;
    }

    @Provides
    public RestHighLevelClient providesRestHighLevelClient() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(serverConfiguration.getElasticHost(), serverConfiguration.getElasticPort(), "http"),
                        new HttpHost(serverConfiguration.getElasticHost(), serverConfiguration.getAdditionalElasticPort(), "http")));
        return client;
    }
}
