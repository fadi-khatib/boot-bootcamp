package bootTest;


import org.apache.commons.lang3.RandomStringUtils;

import javax.ws.rs.client.WebTarget;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import util.InfraUtil;
import javax.ws.rs.client.Entity;



import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;


public class IntegrationTest {
    private String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X)";
    private String REST_API_URI = "http://localhost:8080/entry-point/";
    @Before
    public void beforeAllTests(){
        System.out.println("Hi Test");
    }

    @Test
    public void testEndToEnd()  {
        String key = RandomStringUtils.random(15, false, true);
        String jsonObjectAsString = "{\"message\":\"" + key + "\"}";

        WebTarget webTarget = ClientBuilder.newClient().target(REST_API_URI);
        Response postResponse =  webTarget.path("send/index")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.USER_AGENT, userAgent)
                .post(Entity.json(jsonObjectAsString));

        if (postResponse.getStatus() != 200){
            System.out.println("index fail Status: ");
            System.out.println(postResponse.getStatus());
            System.out.println(postResponse.toString());
        }

        String result = postResponse.toString()+ "\n" + postResponse.readEntity(String.class);
        System.out.println(result);

        try {
            Thread.sleep(1000 * 7);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        String header   = "Macintosh";
        Response searchResponse = webTarget.path("search")
                .queryParam("message", key)
                .queryParam("header", header)
                .request(MediaType.APPLICATION_JSON)
                .get();
                //.queryParam("header", header)
        assertNotNull(searchResponse);
        String entity = searchResponse.readEntity(String.class);
        boolean isMessageIndexed = searchResponse.getStatus() == HttpURLConnection.HTTP_OK;
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
