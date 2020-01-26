package main;

import com.google.inject.AbstractModule;
import io.logz.guice.jersey.JerseyModule;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import modules.MyBatisModule;

import javax.inject.Inject;
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

}
