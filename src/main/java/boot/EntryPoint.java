package boot;
//Now we need to create a simple test REST service class boot.bootcamp.EntryPoint:
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import boot.ServerConfiguration;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;


@Path("entry-point")
public class EntryPoint {
    public static Logger logger = LogManager.getLogger(EntryPoint.class);
    private static int count = 0;
    private static String logMessage = "";
    HttpRequestHandler  httpRequestHandler ;
    ServerConfiguration SConfeg;

    @Inject
    public EntryPoint(ServerConfiguration config , HttpRequestHandler  httpHandler) {
        this.SConfeg = config;
        logMessage = config.getLogMessage();
        this.httpRequestHandler = httpHandler;
        this.httpRequestHandler.setEndPoint("http://localhost:9200");
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/test")
    public String test() {
        System.out.println("EntryPoint.test");
        return "my server is runing fine";
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/send")
    public String send(@Context  UriInfo uriInfo) {
        String result = "no result (send) ";
        System.out.println("EntryPoint.send");
        MultivaluedMap<String, String> query = uriInfo.getQueryParameters();
        String messageId = "";
        for(String key : query.keySet()){
            String q = query.getFirst(key).toString();
            messageId = messageId  + q;
        }

        try {
            httpRequestHandler.setMsg(new Msg(messageId));
            httpRequestHandler.setPath("/index/fadi");
            Response res = httpRequestHandler.postRequest();
            result = res.toString();
            System.out.println(res.toString());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return result;
    }
    @GET
    @Path("search")
    @Produces(MediaType.TEXT_PLAIN)
    public String search(@Context  UriInfo uriInfo) {
        System.out.println("EntryPoint.search");
        String result = "no result (search) ";
        String newQuery = "/index/_search?q=";
        MultivaluedMap<String, String> query = uriInfo.getQueryParameters();

        for(String key : query.keySet()){
            String q = query.getFirst(key).toString();
            newQuery = newQuery + key + ":" + q;
        }
        try {
            httpRequestHandler.setPath(newQuery);
            Response res = httpRequestHandler.getRequest();
            MultivaluedMap<String, Object> headers = res.getHeaders();
            result = res.toString()+ "\n" + res.readEntity(String.class);
            System.out.println(res.toString());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return result;
    }


}