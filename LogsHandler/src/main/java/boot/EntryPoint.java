package boot;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;

import java.util.HashMap;
import java.util.Map;

import Producer.KafkaPublisher;
import util.InfraUtil;

@Path("entry-point")
public class EntryPoint {
    public static Logger logger = LogManager.getLogger(EntryPoint.class);
    private static int count = 0;
    private final ElasticSearchHandler elasticsearchClient;
    private final KafkaPublisher kafkaPublisher;

    @Inject
    public EntryPoint(ElasticSearchHandler elasticsearchClient, KafkaPublisher kafkaPublisher) {
        this.elasticsearchClient = elasticsearchClient;
        this.kafkaPublisher = kafkaPublisher;
    }

    // index message (from post request json) to index
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("index")
    public String sendToProducerConstant(String jsonString, @Context UriInfo uriInfo, @HeaderParam("user-agent") String userAgent){
        JsonObject jsonObject = InfraUtil.stringToJson(jsonString);
        Map<String, String> map = new HashMap<>();
        map.put("message", jsonObject.get("message").toString());
        map.put("User-Agent", userAgent);
        String res = kafkaPublisher.produce(map);
        return res;
    }

    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public String search(@Context UriInfo uriInfo) {
        MultivaluedMap<String, String> query = uriInfo.getQueryParameters();
        Map<String, String> map = new HashMap<>();
        map.put("message", query.get("message").get(0));
        SearchRequest searchRequest = elasticsearchClient.buildSearchQuery(map, "index");
        String result = elasticsearchClient.search(searchRequest);
        return result;
    }

}