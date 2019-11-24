//Now we need to create a simple test REST service class EntryPoint:
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/entry-point")
public class EntryPoint {
    public static Logger logger = LogManager.getLogger(EntryPoint.class);
    private static int count = 0;
    @GET
    @Path("test")
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        System.out.println("hi test "+ String.valueOf(count));
        String Log = "boot boot fadi"+ String.valueOf(count);
        count += 1;
        logger.info(Log);
        return "Test";
    }
}
