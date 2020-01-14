package main;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.logz.guice.jersey.JerseyModule;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import modules.MyBatisModule;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import util.InfraUtil;

public class ServerModule extends AbstractModule {
    private final JerseyConfiguration configuration;
    private final ServerConfiguration serverConfiguration;

    @Inject
    public ServerModule() {
        serverConfiguration = InfraUtil.load(ServerConfiguration.CONFIG_FILE_PATH, ServerConfiguration.class);
        configuration = JerseyConfiguration.builder()
                .addPackage("main")
                .addPort(serverConfiguration.getPort())
                .build();
    }

    @Override
    protected void configure() {
        binder().requireExplicitBindings();
        install(new JerseyModule(configuration));
        install(new MyBatisModule(serverConfiguration));
        bind(EntryPoint.class);
    }

    @Provides
    public Client providesClient() {
        ClientConfig config = new ClientConfig();
        config.register(JacksonJsonProvider.class);
        Client client = ClientBuilder.newClient(config);
        return client;
    }
}
