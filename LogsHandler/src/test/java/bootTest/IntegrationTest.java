package bootTest;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.RandomStringUtils;

import javax.ws.rs.client.WebTarget;
import java.time.Duration;

import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;
import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.awaitility.Awaitility.await;

import javax.ws.rs.client.Entity;


import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.InfraUtil;


public class IntegrationTest {
    private static Logger logger = LogManager.getLogger(IntegrationTest.class);
    private static final String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X)";
    private static final String REST_API_URI = "http://localhost:8080/entry-point";
    private static WebTarget webTarget;

    @BeforeClass
    public static void beforeAllTests() {
        webTarget = ClientBuilder.newClient().target(REST_API_URI);
    }

    @Test
    public void testEndToEnd() {
        String key = RandomStringUtils.random(15, false, true);
        String jsonObjectAsString = "{\"message\":\"" + key + "\"}";
        String account = "{'accountName':'fadi'}";

        //create account
        Response postResponse = webTarget.path("/create-account")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.USER_AGENT, userAgent)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .post(Entity.json(account));
        assertEquals(200, postResponse.getStatus());

        String userJsonString = postResponse.readEntity(String.class);
        JsonObject userJson = InfraUtil.stringToJson(userJsonString);
        String esIndexName = userJson.get("esIndexName").getAsString();
        String token = userJson.get("token").getAsString();


        await().atMost(Duration.ofSeconds(7)).until(() -> {
            Response indexResponse = webTarget.path("/index")
                    .request(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.USER_AGENT, userAgent)
                    .header("X-ACCOUNT-TOKEN", token)
                    .post(Entity.json(jsonObjectAsString));
            assertNotNull(indexResponse);
            return (200 == indexResponse.getStatus());
        });
//        String result = postResponse.toString() + "\n" + postResponse.readEntity(String.class);
//        logger.debug(result);


        String header = "Macintosh";
        await().atMost(Duration.ofSeconds(7)).until(() -> {
            Response searchResponse = webTarget.path("search")
                    .request(MediaType.APPLICATION_JSON)
                    .header("X-ACCOUNT-TOKEN", token)
                    .get();
            assertNotNull(searchResponse);
            String entity1 = searchResponse.readEntity(String.class);
            boolean isMessageIndexed1 = searchResponse.getStatus() == HttpURLConnection.HTTP_OK;
            boolean isMessageFound1 = entity1.indexOf(key) > -1;
            System.out.println(entity1);
            return isMessageIndexed1 && isMessageFound1;
        });

    }

    @After
    public void afterTest() {
    }

}
