//main class (i.e. App.java), which will start the server:
package boot;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.logz.guice.jersey.JerseyServer;



public class MainClass {
    private static int count = 0;

    public static void main(String[] args) throws Exception{
        System.out.println("hi server");
        ServerModule serverModule =  new ServerModule();
        Injector injector = Guice.createInjector(serverModule);
        // start Jerseyserver
        injector.getInstance(JerseyServer.class).start();

    }

}


