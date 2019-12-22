package boot;

import Producer.KafkaPublisher;
import com.google.inject.AbstractModule;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import com.google.inject.Provides;
import io.logz.guice.jersey.JerseyModule;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;


public class ServerModule extends AbstractModule {
    JerseyConfiguration configuration ;

    @Inject
    public ServerModule(){
        int s = ServerConfiguration.port;
        configuration = JerseyConfiguration.builder()
                .addPackage("boot")
                .addPort(ServerConfiguration.port)
                .build();
    }
    @Override
    protected void configure(){
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
}
