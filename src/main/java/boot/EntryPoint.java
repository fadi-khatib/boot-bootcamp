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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;

import java.util.HashMap;
import java.util.Map;


@Path("entry-point")
public class EntryPoint {
    public static Logger logger = LogManager.getLogger(EntryPoint.class);
    private static int count = 0;
    private static String logMessage = "";
    private final ElasticSearchHandler elasticsearchClient;
    private final ServerConfiguration sConfeg;

    @Inject
    public EntryPoint(ServerConfiguration config, ElasticSearchHandler httpHandler) {
        this.sConfeg = config;
        logMessage = config.getLogMessage();
        this.elasticsearchClient = httpHandler;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("test")
    public String test() {
        System.out.println("EntryPoint.test");
        return "my server is runing fine";
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/send/index")
    public String send(Msg msg , @Context  UriInfo uriInfo , @HeaderParam("user-agent") String userAgent) {

        System.out.println("EntryPoint.send");
        String result = "";
        String messageId = "";
        IndexResponse res = null ;

        Map<String, String> map = new HashMap<>();
        map.put("message", msg.getMessage());
        map.put("User-Agent", userAgent);

            elasticsearchClient.setMap(map);
            elasticsearchClient.setIndex("index");
            try {
                res = elasticsearchClient.index();
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
        map.put("User-Agent", query.get("header").get(0));

        SearchRequest searchRequest = elasticsearchClient.buildSearchQuery(map,"index");
        return elasticsearchClient.search(searchRequest);

    }


}