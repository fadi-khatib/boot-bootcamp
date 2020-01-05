package bootTest;


import org.apache.commons.lang3.RandomStringUtils;

import javax.ws.rs.client.WebTarget;
import java.time.Duration;

import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.awaitility.Awaitility.await;

import javax.ws.rs.client.Entity;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class IntegrationTest {
    private static Logger logger = LogManager.getLogger(IntegrationTest.class);
    private String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X)";
    private String REST_API_URI = "http://localhost:8080/entry-point/";
    private WebTarget webTarget;

    @Before
    public void beforeAllTests() {
        webTarget = ClientBuilder.newClient().target(REST_API_URI);
    }

    @Test
    public void testEndToEnd() {
        String key = RandomStringUtils.random(15, false, true);
        String jsonObjectAsString = "{\"message\":\"" + key + "\"}";

        Response postResponse = webTarget.path("index")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.USER_AGENT, userAgent)
                .post(Entity.json(jsonObjectAsString));
        if (postResponse.getStatus() != 200) {
            logger.error("index fail Status: ");
            logger.error(postResponse.getStatus());
            logger.error(postResponse.toString());
        }

        String result = postResponse.toString() + "\n" + postResponse.readEntity(String.class);
        logger.debug(result);


        String header = "Macintosh";
        await().atMost(Duration.ofSeconds(7)).until(() -> {
            Response searchResponse1 = webTarget.path("search")
                    .queryParam("message", key)
                    .queryParam("header", header)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            assertNotNull(searchResponse1);
            String entity1 = searchResponse1.readEntity(String.class);
            boolean isMessageIndexed1 = searchResponse1.getStatus() == HttpURLConnection.HTTP_OK;
            boolean isMessageFound1 = entity1.indexOf(key) > -1;

            return isMessageIndexed1 && isMessageFound1;
        });

    }

    @After
    public void afterTest() {
    }

}
