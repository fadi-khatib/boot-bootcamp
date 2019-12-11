package bootTest;


import boot.Msg;
import boot.ServerConfiguration;
import boot.ServerModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.logz.guice.jersey.JerseyServer;
import org.apache.commons.lang3.RandomStringUtils;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;


import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;


public class IntegrationTest {
    public HttpRequestHandler httpRequestHandler;
    public static Injector injector;




    @Before
    public void beforeAllTests(){
        System.out.println("Hi Test");
        ServerConfiguration SConfig = new ServerConfiguration();
        injector = Guice.createInjector(new ServerModule(SConfig));
        try{
            injector.getInstance(JerseyServer.class).start();
        }catch(Exception e){
            System.out.println(e.toString());
        }
    }

    @Test
    public void testEndToEnd() {
        String key = RandomStringUtils.random(15, false, true);
        httpRequestHandler = new HttpRequestHandler(injector.getInstance(Client.class) , new Msg(key) ,"http://localhost:8080" );
        httpRequestHandler.setPath("/entry-point/send/index");
        Response postResponse = httpRequestHandler.postRequest();
        if (postResponse.getStatus() == 500){
            System.out.println("index fail");
        }

        String result = postResponse.toString()+ "\n" + postResponse.readEntity(String.class);
        System.out.println(result);

        httpRequestHandler.setPath("/entry-point/search?message="+key+"&header=Macintosh");
        Response searchRes = httpRequestHandler.getRequest();

        assertNotNull(searchRes);
        assertTrue(searchRes.getStatus() == HttpURLConnection.HTTP_OK);
    }
    @Before
    public void beforTest() {
        System.out.println("befor each test");

    }
    @After
    public void afterTest() {
        System.out.println("after each test");

    }


}
