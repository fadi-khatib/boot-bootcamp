package boot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.util.EntityUtils;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
//import org.json.simple.JSONObject;
import org.json.simple.JSONObject;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

@Path("entry-point")
public class EntryPoint {
    public static Logger logger = LogManager.getLogger(EntryPoint.class);
    private static int count = 0;
    private final ElasticSearchHandler elasticSearchClient;
    private final Producer<String, String> producer;

    @Inject
    @Singleton
    public EntryPoint(ElasticSearchHandler elasticSearchClient, Producer<String, String> producer) {
        this.elasticSearchClient = elasticSearchClient;
        this.producer = producer;
    }

    // index message (from post request json) to index
//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Path("index")
    public Response sendToKafka(String message, String userAgent, String accountToken) {
        RecordMetadata metadata = null;
        //JsonObject jsonObject = InfraUtil.stringToJson(jsonString);
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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("create-account")
    public Response createAccount(String jsonString) {
        RecordMetadata metadata = null;
        JsonObject jsonObject = InfraUtil.stringToJson(jsonString);
        Map<String, String> map = new HashMap<>();
        map.put("accountName", jsonObject.get("accountName").getAsString());
        return postHttpRequest(HttpHeaders.CONTENT_TYPE ,"application/json","create-account",jsonString);
    }

    // index message (from post request json) to index
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("index")
    public Response index(String jsonString, @HeaderParam("X-ACCOUNT-TOKEN") String accountToken) {
        RecordMetadata metadata = null;
        JsonObject jsonObject = InfraUtil.stringToJson(jsonString);
        Map<String, String> map = new HashMap<>();
        map.put("message", jsonObject.get("message").getAsString());
        WebTarget webTarget;
        webTarget = ClientBuilder.newClient().target("http://accounts-service:8083/");
        // return User
        Response response = null;
        response = webTarget.path("account/token")
                .request(MediaType.APPLICATION_JSON)
                .header("X-ACCOUNT-TOKEN", accountToken)
                .get();
        System.out.println(response.readEntity(String.class));//*local*
        if (response.getStatus() == 200){
            response= sendToKafka(jsonString,"Mozilla/5.0 (Macintosh; Intel Mac OS X)",accountToken );
        }
        return response;
    }

    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@HeaderParam("X-ACCOUNT-TOKEN") String accountToken) {
        System.out.println(accountToken);//*local*

        WebTarget webTarget;
        webTarget = ClientBuilder.newClient().target("http://accounts-service:8083/");
        // return User
        Response response = webTarget.path("account/token")
                .request(MediaType.APPLICATION_JSON)
                .header("X-ACCOUNT-TOKEN", accountToken)
                .get();

        if (response.getStatus() == 200){
            JsonObject JSONObject =  InfraUtil.stringToJson(response.readEntity(String.class));
//            Gson gson = new Gson();
//            String json = gson.toJson(response.getEntity().toString());
            System.out.println(JSONObject);//*local*
            response = elasticSearch(JSONObject.get("esIndexName").getAsString());
        }
        return response;
    }

    private Response postHttpRequest(String header ,String headerValue,String path ,String jsonString) {
        WebTarget webTarget;
        webTarget = ClientBuilder.newClient().target("http://accounts-service:8083/");
        Response response = webTarget.path(path)
                .request(MediaType.APPLICATION_JSON)
                .header(header, headerValue)
                .post(Entity.json(jsonString));
        System.out.println(response.getEntity());
        return response;
    }
}