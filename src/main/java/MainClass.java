//main class (i.e. App.java), which will start the server:

import java.io.IOException;
import java.io.*;
import java.nio.file.*;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/*import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
*/

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class MainClass {
    private static int count = 0;
    public static void main(String[] args) throws Exception {
        System.out.println("hi server");
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        Server jettyServer = new Server(8080);
        jettyServer.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/*");
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
    }

}

/*import java.io.IOException;
import java.io.*;
import java.nio.file.*;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;


public class MainClass {
    private static int count = 0;
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        HttpContext context = server.createContext("/");
        context.setHandler(MainClass::handleRequest);
        server.start();
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        //File file = new File("src/main/resources/log4j2.xml");
        //System.out.println(new String(Files.readAllBytes(Paths.get("src/main/resources/log4j2.xml"))));
        String response = "Hi there! " + String.valueOf(count);

        String Log = "boot boot fadi" + String.valueOf(count);
        count += 1;
        exchange.sendResponseHeaders(200, response.getBytes().length);//response code and length
        OutputStream os = exchange.getResponseBody();
        ((OutputStream) os).write(response.getBytes());
        os.close();
        System.out.println("hi");
        Logger logger;
        logger = LogManager.getLogger(MainClass.class);

        logger.info(Log);
        //logger.warn("Winter is coming");
    }


}*/