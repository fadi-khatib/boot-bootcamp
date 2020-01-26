package bootTest;

import org.apache.commons.lang3.RandomStringUtils;
import javax.ws.rs.client.WebTarget;
import java.time.Duration;

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

import pojos.account.Account;
import pojos.account.CreateAccountRequest;
import util.GlobalParams;

public class IntegrationTest {
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
        CreateAccountRequest createAccountRequest = new CreateAccountRequest("fadi");

        Account account = createAccount(createAccountRequest);
        String token = account.getToken();

        indexAndAssert(token, jsonObjectAsString);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> searchAndAssertMessage(key, account.getToken()));
    }
    @Test
    public void testTwoAccounts() {
        String message1Key = RandomStringUtils.random(15, false, true);
        String message2Key = RandomStringUtils.random(15, false, true);

        String message1 = "{\"message\":\"" + message1Key + "\"}";
        String message2 = "{\"message\":\"" + message2Key + "\"}";

        CreateAccountRequest createAccount1Request = new CreateAccountRequest("Fadi1");//{'accountName':'fadi'}";
        CreateAccountRequest createAccount2Request = new CreateAccountRequest("Fadi2");//{'accountName':'fadi'}";

        Account account1 = createAccount(createAccount1Request);
        Account account2 = createAccount(createAccount2Request);

        String token1 = account1.getToken();
        String token2 = account2.getToken();

        indexAndAssert(token1, message1);
        indexAndAssert(token2, message2);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> verifyAccountDoesNotHaveMessage(message2Key, account1.getToken()));
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> verifyAccountDoesNotHaveMessage(message1Key, account2.getToken()));
    }

    @Test
    public void testInvalidToken() {
        String key = RandomStringUtils.random(15, false, true);
        String token  = RandomStringUtils.random(15, false, true);
        String jsonObjectAsString = "{\"message\":\"" + key + "\"}";

        Response indexResponse = webTarget.path("/index")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.USER_AGENT, userAgent)
                .header(GlobalParams.X_ACCOUNT_TOKEN, token)
                .post(Entity.json(jsonObjectAsString));
        assertNotNull(indexResponse);
        assertTrue(401 == indexResponse.getStatus());
        searchWithInvalidToken(token);
    }

    @After
    public void afterTest() {
    }

    private Account createAccount(CreateAccountRequest createAccountRequest){
        Response response = webTarget.path("/create-account")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.USER_AGENT, userAgent)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .post(Entity.json(createAccountRequest));
        assertEquals(HttpURLConnection.HTTP_CREATED, response.getStatus());
        Account account = response.readEntity(Account.class);
        assertNotNull(account);
        return account;
    }

    private void indexAndAssert(String token, String message){
        Response response = webTarget.path("/index")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.USER_AGENT, userAgent)
                .header(GlobalParams.X_ACCOUNT_TOKEN, token)
                .post(Entity.json(message));
        assertNotNull(response);
        assertTrue(200 == response.getStatus());
    }

    private void verifyAccountDoesNotHaveMessage(String message, String accountToken) {
        Response searchResponse = webTarget.path("search")
                .request(MediaType.APPLICATION_JSON)
                .header(GlobalParams.X_ACCOUNT_TOKEN, accountToken)
                .get();
        assertNotNull(searchResponse);
        String entity = searchResponse.readEntity(String.class);
        assertTrue(!entity.contains(message));
    }

    private void searchAndAssertMessage(String key, String token) {
        Response searchResponse = webTarget.path("search")
                .request(MediaType.APPLICATION_JSON)
                .header(GlobalParams.X_ACCOUNT_TOKEN, token)
                .get();
        assertNotNull(searchResponse);
        String entity = searchResponse.readEntity(String.class);
        assertTrue(searchResponse.getStatus() == HttpURLConnection.HTTP_OK);
        assertTrue(entity.contains(key));
    }

    private void searchWithInvalidToken(String token) {
        Response searchResponse = webTarget.path("search")
                .request(MediaType.APPLICATION_JSON)
                .header(GlobalParams.X_ACCOUNT_TOKEN, token)
                .get();
        assertNotNull(searchResponse);
        assertTrue(searchResponse.getStatus() == HttpURLConnection.HTTP_UNAUTHORIZED);
    }

}
