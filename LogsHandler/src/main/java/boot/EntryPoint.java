package boot;
//Now we need to create a simple test REST service class boot.bootcamp.EntryPoint:
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
import org.json.simple.JSONObject;
import util.InfraUtil;

@Path("entry-point")
public class EntryPoint {
    public static Logger logger = LogManager.getLogger(EntryPoint.class);
    private static int count = 0;
    private static String logMessage = new String(ServerConfiguration.logMessage);
    private final ElasticSearchHandler elasticsearchClient;
    private final KafkaPublisher kafkaPublisher;

    @Inject
    public EntryPoint(ElasticSearchHandler httpHandler ,KafkaPublisher kafkaPublisher) {
        System.out.println("new Entry point has created");
        this.elasticsearchClient = httpHandler;
        this.kafkaPublisher = kafkaPublisher;
    }

    //for internal server test
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("test")
    public String test() {
        System.out.println("logshandler .EntryPoint.test");
        return "logshandler . my server is runing fine";
    }

    // indexing constant massage to "index"
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("index")
    public String sendToProducerConstant() {

        System.out.println("EntryPoint.producer.index");
        String result = "";
        String messageId = "";
        String res = null ;

        Map<String, String> map = new HashMap<>();
        map.put("message", "my new message post ");
        map.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X)");
        try {
            res = kafkaPublisher.produce(map);
        } catch (Exception e){
            System.out.println(e.getMessage());
            return e.getMessage();
        }
        return res;
    }

    // index message (from post request json) to index
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/send/index")
    public String send(String jsonString , @Context  UriInfo uriInfo , @HeaderParam("user-agent") String userAgent) {
        JsonObject jsonObject = InfraUtil.StringToJson(jsonString);
        System.out.println("EntryPoint.send");
        String result = "";
        String messageId = "";
        IndexResponse res = null ;

        Map<String, String> map = new HashMap<>();
        map.put("message", jsonObject.get("message").toString());
        map.put("User-Agent", userAgent);

            elasticsearchClient.setMap(map);
            elasticsearchClient.setIndex("index");
            try {
                res = elasticsearchClient.index();
                System.out.println("entrypoint.send status:  "+res.status().toString());
            } catch (Exception e){
                System.out.println(e.getMessage());
                return e.getMessage();
            }
        return res.status().toString();
    }

    @GET
    @Path("search")
    @Produces(MediaType.TEXT_PLAIN)
    public String search( @Context  UriInfo uriInfo  ) {
        System.out.println("EntryPoint.search");
        String result = "no result (search) ";
        MultivaluedMap<String, String> query = uriInfo.getQueryParameters();
        Map<String, String> map = new HashMap<>();
        map.put("message",query.get("message").get(0) );
        //map.put("User-Agent", query.get("header").get(0));
        SearchRequest searchRequest = elasticsearchClient.buildSearchQuery(map,"index");
        //SearchResponse searchResponse = null;
        try {
            result = elasticsearchClient.search(searchRequest);
            System.out.println("elastic search response: "+result.toString());
        }catch(Exception e){
            System.out.println(e.getMessage());
            return e.getMessage();
        }
        return result;

    }

}