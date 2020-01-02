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

    //for internal server test
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("test")
    public String test() {
        return "logshandler . my server is runing fine";
    }

    // indexing constant massage to "index"
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("index")
    public String sendToProducerConstant() {
        Map<String, String> map = new HashMap<>();
        map.put("message", "my new message post ");
        map.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X)");
        String res = kafkaPublisher.produce(map);
        return res;
    }

    // index message (from post request json) to index
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/send/index")
    public String send(String jsonString, @Context UriInfo uriInfo, @HeaderParam("user-agent") String userAgent) {
        JsonObject jsonObject = InfraUtil.StringToJson(jsonString);
        Map<String, String> map = new HashMap<>();
        map.put("message", jsonObject.get("message").toString());
        map.put("User-Agent", userAgent);
        elasticsearchClient.setMap(map);
        elasticsearchClient.setIndex("index");
        IndexResponse res = elasticsearchClient.index();
        return res.status().toString();
    }

    @GET
    @Path("search")
    @Produces(MediaType.TEXT_PLAIN)
    public String search(@Context UriInfo uriInfo) {
        MultivaluedMap<String, String> query = uriInfo.getQueryParameters();
        Map<String, String> map = new HashMap<>();
        map.put("message", query.get("message").get(0));
        SearchRequest searchRequest = elasticsearchClient.buildSearchQuery(map, "index");
        String result = elasticsearchClient.search(searchRequest);
        return result;
    }

}