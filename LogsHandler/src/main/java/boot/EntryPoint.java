package boot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import util.InfraUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;


@Path("entry-point")
public class EntryPoint {
    public static Logger logger = LogManager.getLogger(EntryPoint.class);
    private final ElasticSearchHandler elasticSearchClient;
    private final Producer<String, String> producer;
    private WebTarget accountsServiceWebTarget;

    @Inject
    @Singleton
    public EntryPoint(ElasticSearchHandler elasticSearchClient, Producer<String, String> producer, WebTarget accountsServiceWebTarget) {
        this.elasticSearchClient = requireNonNull(elasticSearchClient);
        this.producer = requireNonNull(producer);
        this.accountsServiceWebTarget = requireNonNull(accountsServiceWebTarget);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("create-account")
    public Response createAccount(String jsonString) {
        JsonObject jsonObject = InfraUtil.stringToJson(jsonString);
        Response response = accountsServiceWebTarget.path("create-account")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .post(Entity.json(jsonString));
        return response;
    }

    // index message (from post request json) to index
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("index")
    public Response index(String jsonString, @HeaderParam("X-ACCOUNT-TOKEN") String accountToken) {
        RecordMetadata metadata = null;
        JsonObject jsonObject = InfraUtil.stringToJson(jsonString);
        Response response = accountsServiceWebTarget.path("account/token")
                .request(MediaType.APPLICATION_JSON)
                .header("X-ACCOUNT-TOKEN", accountToken)
                .get();
        System.out.println(response.readEntity(String.class));//*local*
        if (response.getStatus() == 200) {
            response = sendToKafka(jsonString, "Mozilla/5.0 (Macintosh; Intel Mac OS X)", accountToken);
        }
        return response;
    }

    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@HeaderParam("X-ACCOUNT-TOKEN") String accountToken) {
        System.out.println(accountToken);//*local*
        Response response = accountsServiceWebTarget.path("account/token")
                .request(MediaType.APPLICATION_JSON)
                .header("X-ACCOUNT-TOKEN", accountToken)
                .get();

        if (response.getStatus() == 200) {
            JsonObject JSONObject = InfraUtil.stringToJson(response.readEntity(String.class));
            System.out.println(JSONObject);//*local*
            response = elasticSearch(JSONObject.get("esIndexName").getAsString());
        }
        return response;
    }

    public Response sendToKafka(String message, String userAgent, String accountToken) {
        RecordMetadata metadata = null;
        Map<String, String> map = new HashMap<>();
        map.put("message", message);
        map.put("User-Agent", userAgent);
        map.put("X-ACCOUNT-TOKEN", accountToken);
        try {
            metadata = producer.send(new ProducerRecord<String, String>("my-topic", new Gson().toJson(map))).get();
            logger.debug(" to partition " + metadata.partition() + " with offset " + metadata.offset());
        } catch (Exception e) {
            Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).build();
        }
        return Response.status(HttpURLConnection.HTTP_OK).entity(metadata.topic()).build();
    }

    public Response elasticSearch(String esIndexNam) {
        SearchRequest searchRequest = elasticSearchClient.buildSearchQuery(esIndexNam);
        String result = elasticSearchClient.search(searchRequest);
        System.out.println(result);//*local*
        return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
    }
}