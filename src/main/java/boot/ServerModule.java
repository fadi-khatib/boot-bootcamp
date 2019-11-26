package boot;

import boot.ServerConfiguration;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import io.logz.guice.jersey.JerseyModule;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import org.eclipse.jetty.server.Server;


public class ServerModule extends AbstractModule {
    JerseyConfiguration configuration ;
    public ServerModule(ServerConfiguration SConfig){
        configuration = JerseyConfiguration.builder()
                .addPackage("boot")
                .addPort(SConfig.getPort())
                .build();
    }
    @Override
    protected void configure(){
          binder().requireExplicitBindings();
          install(new JerseyModule(configuration));
          bind(ServerConfiguration.class);

    }

//    @Provides
//    public ServerConfiguration providesServerConfiguration() {
//        // Read file server.config
//        // convert to json
//        // return serverConfiguration;
//        return new ServerConfiguration();
//    }
}
