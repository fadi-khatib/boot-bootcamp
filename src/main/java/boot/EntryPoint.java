package boot;//Now we need to create a simple test REST service class boot.bootcamp.EntryPoint:
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import boot.ServerConfiguration;

//import com.google.inject.Inject;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("entry-point")
public class EntryPoint {
    public static Logger logger = LogManager.getLogger(EntryPoint.class);
    private static int count = 0;
    private static String logMessage = "";

    @Inject
    public EntryPoint(ServerConfiguration config) {
        logMessage = config.getLogMessage();
    }
//
//    public EntryPoint() {
//        logMessage = "no config ";
//    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        System.out.println(logMessage + String.valueOf(count));
        String Log = logMessage+ String.valueOf(count);
        count += 1;
        logger.info(Log);
        return logMessage;
    }
}
