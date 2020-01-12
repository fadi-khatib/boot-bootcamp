package boot;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.logz.guice.jersey.JerseyServer;

public class MainClass {

    public static void main(String[] args) throws Exception {
        ServerModule serverModule = new ServerModule();
        Injector injector = Guice.createInjector(serverModule);
        injector.getInstance(JerseyServer.class).start();
    }

}


