package bootTest;


import boot.Msg;
import com.google.inject.Injector;
import org.apache.commons.lang3.RandomStringUtils;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;

public class IntegrationTest {
    public HttpRequestHandler httpRequestHandler;
    public static Injector injector;

    @Before
    public void beforeAllTests(){
        System.out.println("Hi Test");
    }

    @Test
    public void testEndToEnd()  {
        ClientConfig config = new ClientConfig();
        config.register(JacksonJsonProvider.class);
        Client client = ClientBuilder.newClient(config);
        String key = RandomStringUtils.random(15, false, true);
        httpRequestHandler = new HttpRequestHandler(client , new Msg(key) ,"http://localhost:8080" );
        httpRequestHandler.setPath("/entry-point/send/index");
        Response postResponse = httpRequestHandler.postRequest();
        if (postResponse.getStatus() != 200){
            System.out.println("index fail Status: ");
            System.out.println(postResponse.getStatus());
            System.out.println(postResponse.toString());
        }

        String result = postResponse.toString()+ "\n" + postResponse.readEntity(String.class);
        System.out.println(result);

        httpRequestHandler.setPath("/entry-point/search?message="+key);//+"&header=Macintosh"

        try {
            Thread.sleep(1000 * 7);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        Response searchRes = httpRequestHandler.getRequest();
        assertNotNull(searchRes);
        String entity = searchRes.readEntity(String.class);
        boolean isMessageIndexed = searchRes.getStatus() == HttpURLConnection.HTTP_OK;
        boolean isMessageFound = entity.indexOf(key) > -1;
        System.out.println("key index");
        System.out.println(entity.indexOf(key));
        assertTrue(isMessageIndexed & isMessageFound);

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
