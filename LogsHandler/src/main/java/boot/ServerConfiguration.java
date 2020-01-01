package boot;

import org.json.simple.JSONObject;

import java.io.File;
import util.InfraUtil;



public class ServerConfiguration {

    private static final String configFilePath = "src/main/resources/server.config" ;
    private static final String additionalConfigFilePath = "logsHandler/src/main/resources/server.config" ;

    public static final String logMessage;
    public static final int port;

    public static final int elasticPort;
    public static final int additionalElasticPort;
    public static final String elasticHost;

    public static final int kafkaPort;
    public static final String kafkaHost;

    static{
        JSONObject config = InfraUtil.fileToJson(configFilePath);
        String s = (new File(System.getProperty("user.dir"))).toString();
         s = (new File(System.getProperty("user.dir")).getParent().toString());
        if(config == null){
            config = InfraUtil.fileToJson(additionalConfigFilePath);
        }
        System.out.println(config.get("logMessage"));
        logMessage = config.get("logMessage").toString();

        //server
        port =  Integer.parseInt(config.get("port").toString());
        //elasticSearch
        elasticPort = Integer.parseInt(config.get("elasticPort").toString());
        elasticHost = config.get("elasticHost").toString();
        additionalElasticPort =  Integer.parseInt(config.get("additionalElasticPort").toString());

        // kafka
        kafkaPort = Integer.parseInt(config.get("kafkaPort").toString());
        kafkaHost = config.get("kafkaHost").toString();
    }

    public ServerConfiguration(){
        System.out.println("create ne ServerConfig");
    }
}
