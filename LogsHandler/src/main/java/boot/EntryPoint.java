package boot;

import client.AccountsServiceClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import pojos.account.Account;
import pojos.account.CreateAccountRequest;
import util.InfraUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@Path("entry-point")
public class EntryPoint {
    private final String  X_ACCOUNT_TOKEN = "X-ACCOUNT-TOKEN";
    public static Logger logger = LogManager.getLogger(EntryPoint.class);
    private final ElasticSearchHandler elasticSearchClient;
    private final Producer<String, String> producer;
    private final AccountsServiceClient accountsServiceClient;

    @Inject
    @Singleton
    public EntryPoint(ElasticSearchHandler elasticSearchClient, Producer<String, String> producer, AccountsServiceClient accountsServiceClient) { //WebTarget accountsServiceWebTarget
        this.elasticSearchClient = requireNonNull(elasticSearchClient);
        this.producer = requireNonNull(producer);
        this.accountsServiceClient = requireNonNull(accountsServiceClient);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("create-account")
    public Response createAccount(String jsonString) {
        //JsonObject jsonObject = InfraUtil.stringToJson(jsonString);
        CreateAccountRequest createAccountRequest = InfraUtil.stringToObject(jsonString, CreateAccountRequest.class);
        Account account = accountsServiceClient.createAccount(createAccountRequest);
        if(account!= null) {
            return Response.status(HttpURLConnection.HTTP_CREATED).entity(account).build();
        }
        return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity("Fail to create account").build();
    }

    // index message (from post request json) to index
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("index")
    public Response index(String message, @HeaderParam(X_ACCOUNT_TOKEN) String accountToken) {

        Map<String, Object>  map = InfraUtil.stringToObject(message, Map.class);
        map.put("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X)");
        map.put(X_ACCOUNT_TOKEN,accountToken);
        ObjectMapper objectMapper = new ObjectMapper();
        Account account = accountsServiceClient.getAccountByToken(accountToken);
        if (account != null) {
            try {
                return sendToKafka(objectMapper.writeValueAsString(map));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).entity("fail to index: unauthorized token").build();
    }

    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@HeaderParam(X_ACCOUNT_TOKEN) String accountToken) {
        Account account = accountsServiceClient.getAccountByToken(accountToken);
        if (account != null) {
            return searchByIndexName(account.getEsIndexName());
        }
        return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).entity("No such account found").build();
    }

    private Response sendToKafka(String message ) {
        RecordMetadata metadata = null;
        try {
            metadata = producer.send(new ProducerRecord<String, String>("my-topic", message)).get();
        } catch (Exception e) {
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).build();
        }
        logger.debug(" to partition " + metadata.partition() + " with offset " + metadata.offset());
        return Response.status(HttpURLConnection.HTTP_OK).entity(metadata.topic()).build();
    }

    private Response searchByIndexName(String esIndexNam) {
        SearchRequest searchRequest = elasticSearchClient.buildSearchQuery(esIndexNam);
        String result = elasticSearchClient.search(searchRequest);
        return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
    }
}