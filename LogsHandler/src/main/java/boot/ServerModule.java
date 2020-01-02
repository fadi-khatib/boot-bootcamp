package boot;

import Producer.KafkaPublisher;
import com.google.inject.AbstractModule;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.Properties;

import com.google.inject.Provides;
import io.logz.guice.jersey.JerseyModule;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import org.apache.http.HttpHost;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;

import util.InfraUtil;


public class ServerModule extends AbstractModule {
    JerseyConfiguration configuration ;
    ServerConfiguration serverConfiguration;
    @Inject
    public ServerModule(){
        try {
            serverConfiguration = InfraUtil.load(ServerConfiguration.configFilePath, ServerConfiguration.class);
        }catch(Exception e){
            System.out.println(e.getMessage());
            try {
                serverConfiguration = InfraUtil.load(ServerConfiguration.configFilePath, ServerConfiguration.class);
            }catch(Exception exception){
                System.out.println(exception.getMessage());
            }
        }
        configuration = JerseyConfiguration.builder()
                .addPackage("boot")
                .addPort(serverConfiguration.getPort())
                .build();
    }
    @Override
    protected void configure(){
        binder().requireExplicitBindings();
        install(new JerseyModule(configuration));
        bind(EntryPoint.class);
        bind(ElasticSearchHandler.class);
        bind(KafkaPublisher.class);
    }

    @Provides
    public Client providesClient() {
        ClientConfig config = new ClientConfig();
        config.register(JacksonJsonProvider.class);
        Client client = ClientBuilder.newClient(config);
        return client;
    }
    @Provides
    public Properties providesProperties() {
        Properties props = new Properties();
        String KAFKA_BROKERS = serverConfiguration.getKafkaHost()+":"+serverConfiguration.getKafkaPort();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, serverConfiguration.getCLIENT_ID());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return props;
    }
    @Provides
    public RestHighLevelClient providesRestHighLevelClient(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(serverConfiguration.getElasticHost(), serverConfiguration.getElasticPort(), "http"),
                        new HttpHost(serverConfiguration.getElasticHost(), serverConfiguration.getAdditionalElasticPort(), "http")));
        return client;
    }
}
