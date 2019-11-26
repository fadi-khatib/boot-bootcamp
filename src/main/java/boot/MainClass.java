package boot;//main class (i.e. App.java), which will start the server:

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.logz.guice.jersey.JerseyServer;


public class MainClass {
    private static int count = 0;
    private static ServerConfiguration serverConfigurations;

    public static void main(String[] args) throws Exception{
        System.out.println("hi server");
        //serverConfigurations = new ServerConfiguration();
        ServerConfiguration SConfig = new ServerConfiguration();
        Injector injector = Guice.createInjector(new ServerModule(SConfig));
        injector.getInstance(JerseyServer.class).start();
        //EntryPoint e =injector.getInstance(EntryPoint.class);
        //ServerConfiguration s =injector.getInstance(ServerConfiguration.class);
        //StamClass instance = injector.getInstance(StamClass.class);


        //com.google.inject.Injector guice = Guice.createInjector( new boot.bootcamp.ServerModule());

    }
/*    public static void main_multiInctances(String[] args) throws Exception {
        System.out.println("hi server");

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        Server jettyServer = new Server(8080);
        jettyServer.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        // Tells the Jersey Servlet which REST service/class to load.
        jerseyServlet.setInitParameter(
                "jersey.config.server.provider.classnames",
                EntryPoint.class.getCanonicalName());

        try {
            jettyServer.start();
            jettyServer.join();
        } finally {
            jettyServer.destroy();
        }
    }*/

}